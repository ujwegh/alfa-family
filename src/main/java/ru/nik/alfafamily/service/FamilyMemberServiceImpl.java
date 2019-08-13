package ru.nik.alfafamily.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.exceptions.FamilyMemberDoesNotExistsException;
import ru.nik.alfafamily.repository.FamilyMemberRepository;

@Service
public class FamilyMemberServiceImpl implements FamilyMemberService {

	private final FamilyMemberRepository repository;

	private final UserService userService;


	@Autowired
	public FamilyMemberServiceImpl(FamilyMemberRepository repository, UserService service) {
		this.repository = repository;
		this.userService = service;
	}


	@Override
	public List<FamilyMember> findAll(String userId) {
		return repository.findAllByUser_Id(userId);
	}

	@Override
	public FamilyMember create(String userId, String name) {
		if (userService.isUserExistsById(userId)){
			User user = userService.findById(userId);
			FamilyMember familyMember = new FamilyMember();
			familyMember.setUser(user);
			familyMember.setName(name);

			return repository.save(familyMember);
		}
		return null;
	}

	@Override
	public FamilyMember update(String familyMemberId, String name) {
		FamilyMember familyMember = findById(familyMemberId);
		familyMember.setName(name);
		return repository.save(familyMember);
	}

	@Override
	public Boolean delete(String familyMemberId) {
		repository.deleteById(familyMemberId);

		return true;
	}

	@Override
	public FamilyMember findById(String familyMemberId) throws FamilyMemberDoesNotExistsException{
		if (!isFamilyMemberExists(familyMemberId))
			throw new FamilyMemberDoesNotExistsException(
				"Family familyMember with id " + familyMemberId + " doesn't exists.");
		return repository.findById(familyMemberId).orElse(null);
	}

	@Override
	public Boolean isFamilyMemberExists(String familyMemberId) {
		return repository.existsById(familyMemberId);
	}

}
