package ru.nik.alfafamily.service;

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
	public FamilyMemberProperties createOrUpdate(String familyMemberId, Map<String, String> properties) {
		FamilyMember member = familyMemberService.findById(familyMemberId);

		FamilyMemberProperties property = repository.findByFamilyMember_Id(familyMemberId);
		if (property == null) {
			property = new FamilyMemberProperties(member, null);
		}
		property.setColor(properties.get("color"));
		return repository.save(property);
	}

	@Override
	public Boolean delete(String familyMemberId) {
		return repository.deleteByFamilyMember_Id(familyMemberId) != 0;
	}

	@Override
	public FamilyMemberProperties findById(String propertiesId) {
		return repository.findById(propertiesId).orElse(null);
	}

	@Override
	public FamilyMemberProperties findByFamilyMemberId(String familyMemberId) {
		return repository.findByFamilyMember_Id(familyMemberId);
	}
}
