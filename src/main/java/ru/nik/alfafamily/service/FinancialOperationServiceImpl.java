package ru.nik.alfafamily.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.FinancialOperation;
import ru.nik.alfafamily.dto.FinancialOperationDto;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.exceptions.FamilyMemberDoesNotExistsException;
import ru.nik.alfafamily.exceptions.FinancialOperationException;
import ru.nik.alfafamily.repository.FinancialOperationRepository;
import ru.nik.alfafamily.util.Utilities;

@Service
@Slf4j
public class FinancialOperationServiceImpl implements FinancialOperationService {

    private final FinancialOperationRepository repository;

    private final FamilyMemberService familyMemberService;

    private final UserService userService;

    private final Mapper mapper;

    @Autowired
    public FinancialOperationServiceImpl(FinancialOperationRepository repository,
                                         FamilyMemberService familyMemberService, UserService userService, Mapper mapper) {
        this.repository = repository;
        this.familyMemberService = familyMemberService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @Override
    public List<FinancialOperation> createOrUpdate(String userId, String familyMemberId,
                                                   MultipartFile file) {
        if (!familyMemberService.isFamilyMemberExists(familyMemberId)) {
            throw new FamilyMemberDoesNotExistsException(
                    "Family familyMember with id " + familyMemberId + " doesn't exists.");
        }

        log.info("Parsing file: {} ..", file.getOriginalFilename());
        try {
            List<FinancialOperation> operations = Utilities.parseCsv(file);
            log.info("Parsing complete.");
            if (operations.size() > 0) {

                Map<String, Category> categoryMap = new HashMap<>();
                List<String> categoryList = new ArrayList<>();

                operations.forEach(o -> categoryMap.put(o.getCategory().getName(),
                        new Category(o.getCategory().getName(), null)));

                categoryMap.values().forEach(category -> categoryList.add(category.getName()));

                FamilyMember member = familyMemberService
                        .updateCategories(familyMemberId, categoryList);

                operations.forEach(operation -> member.getCategories().forEach(m -> {
                    if (operation.getCategory().getName().equals(m.getName())) {
                        operation.setCategory(m);
                    }
                }));
                return repository.saveAll(operations);
            }
            // TODO разобраться с ексепшнами
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public List<FinancialOperation> findAllForUser(String userId) {
        return userService.isUserExistsById(userId) ? repository
                .findAllByCategoryInOrderByDateDesc(getUserCategories(userId))
                : Collections.emptyList();
    }

    @Override
    public Boolean deleteAllForFamilyMember(String userId, String familyMemberId) {
        if (!userService.isUserExistsById(userId)) {
            return repository.deleteAllByCategoryIn(getFamilyMemberCategories(userId, familyMemberId)) != 0;
        }
        return false;
    }

    @Override
    public List<FinancialOperation> findAllForUserBetweenDates(String userId, Date start, Date end) {
        if (userService.isUserExistsById(userId)) {

            List<Category> categories = getUserCategories(userId);

            List<FinancialOperation> operations = repository.findAllByCategoryInOrderByDateDesc(categories);

            return Utilities.getByDateBetween(operations, start, end);
        } else return Collections.emptyList();
    }

    @Override
    public List<FinancialOperation> findAllForFamilyMemberBetweenDates(String userId, String familyMemberId, Date start, Date end) {
        if (userService.isUserExistsById(userId)) {

            List<Category> categories = getFamilyMemberCategories(userId, familyMemberId);
            List<FinancialOperation> operations = repository.findAllByCategoryInOrderByDateDesc(categories);

            return Utilities.getByDateBetween(operations, start, end);
        }
        return Collections.emptyList();
    }

    /**
     * Create financial operation "by hands".
     * Creation is possible only today or later.
     *
     * @param dto
     * @return FinancialOperation entity
     */
    @Override
    public FinancialOperation create(FinancialOperationDto dto) {
        FinancialOperation operation = mapper.fromFinancialOperationDto(dto);

        Date currentDate = removeTime(new Date());

        if (operation.getDate().after(new Date(currentDate.getTime() - 1000))) {
            return repository.save(operation);
        } else throw new FinancialOperationException("Wrong date of financial operation creation.");
    }

    @Override
    public FinancialOperation update(FinancialOperationDto dto) {
        FinancialOperation operation = mapper.fromFinancialOperationDto(dto);

        Date currentDate = removeTime(new Date());
        FinancialOperation oldOperation = findById(dto.getId());

        if (oldOperation.isPlanned() && !operation.getDate()
                .after(new Date(currentDate.getTime() - 1000))) {
            throw new FinancialOperationException("Wrong date of financial operation creation.");
        }
        return repository.save(operation);
    }

    @Override
    public Boolean delete(String operationId) {
        repository.deleteById(operationId);
        return !repository.existsById(operationId);
    }

    @Override
    public FinancialOperation findById(String operationId) {
        return repository.findById(operationId).orElse(null);
    }


    private List<Category> getUserCategories(String userId) {
        List<Category> allCategories = new ArrayList<>();
        List<FamilyMember> members = familyMemberService.findAll(userId);
        members.forEach(m -> allCategories.addAll(m.getCategories()));
        return allCategories;
    }

    private List<Category> getFamilyMemberCategories(String userId, String familyMemberId) {
        FamilyMember neededMember = null;
        List<FamilyMember> members = familyMemberService.findAll(userId);
        for (FamilyMember member : members) {
            if (member.getId().equals(familyMemberId)) {
                neededMember = member;
            }
        }
        if (neededMember != null) {
            return neededMember.getCategories();
        }
        return null;
    }

    private Date removeTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            date = sdf.parse(sdf.format(date));
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
