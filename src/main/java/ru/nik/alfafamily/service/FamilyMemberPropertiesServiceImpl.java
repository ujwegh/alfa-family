package ru.nik.alfafamily.service;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.FamilyMemberProperties;
import ru.nik.alfafamily.repository.FamilyMemberPropertiesRepository;

@Service
public class FamilyMemberPropertiesServiceImpl implements FamilyMemberPropertiesService {

	private final FamilyMemberPropertiesRepository repository;

	private final FamilyMemberService familyMemberService;

	@Autowired
	public FamilyMemberPropertiesServiceImpl(FamilyMemberPropertiesRepository repository,
		FamilyMemberService familyMemberService) {
		this.repository = repository;
		this.familyMemberService = familyMemberService;
	}

	@Override
	public FamilyMemberProperties createOrUpdate(String userId, String familyMemberId, Map<String, String> properties) {
		FamilyMember member = familyMemberService.findById(userId, familyMemberId);
		List<FamilyMemberProperties> propertiesList = repository.findAllByFamilyMember_Id(member.getId());
		FamilyMemberProperties property = propertiesList.size() != 0 ? propertiesList.get(0)
			: new FamilyMemberProperties(member, null);

		property.setColor(properties.get("color"));
		return repository.save(property);
	}

	@Override
	public Boolean delete(String familyMemberId) {
		return repository.deleteByFamilyMember_Id(familyMemberId) != 0;
	}
}
