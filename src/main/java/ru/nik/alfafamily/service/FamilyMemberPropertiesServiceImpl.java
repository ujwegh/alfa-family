package ru.nik.alfafamily.service;

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
	public FamilyMemberProperties createOrUpdate(String email, String familyMemberId, String color) {
		FamilyMember member = familyMemberService.findById(email, familyMemberId);
		FamilyMemberProperties properties = new FamilyMemberProperties(member, color);
		return repository.save(properties);
	}

	@Override
	public Boolean delete(String familyMemberId) {
		return repository.deleteByFamilyMember_Id(familyMemberId) != 0;
	}
}
