package ru.nik.alfafamily.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.nik.alfafamily.domain.*;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.repository.FamilyMemberRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
        ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
@EnableMongoRepositories(basePackages = {"ru.nik.alfafamily.repository"})
@EnableAutoConfiguration
@ContextConfiguration(classes = {FamilyMemberPropertiesServiceImpl.class, FamilyMemberServiceImpl.class,
	UserServiceImpl.class, Mapper.class, CategoryServiceImpl.class, RoleServiceImpl.class})
class FamilyMemberPropertiesServiceImplTest {
    @Autowired
    private FamilyMemberPropertiesService service;

    @Autowired
    private MongoTemplate template;

    @Autowired
    private FamilyMemberService familyMemberService;

    @Autowired
    private FamilyMemberRepository memberRepository;

    @BeforeEach
    public void init() {
        User user = new User("firstName", "secondName",
                "admin@mail.com", "password");
        Role role = new Role("USER");
        template.save(role);
        user.setRole(role);
        template.save(user);

        FamilyMember member1 = new FamilyMember("test-1-familyMember", user);
        FamilyMember member2 = new FamilyMember("test-2-familyMember", user);
        template.save(member1);
        template.save(member2);
        FamilyMemberProperties familyMemberProperties = new FamilyMemberProperties(member1, "red");

        template.save(familyMemberProperties);

        Category category1 = new Category("бензин", member1);
        Category category2 = new Category("продукты", member1);
        template.save(category1);
        template.save(category2);
    }

    @AfterEach
    public void cleanup() {
        template.getDb().drop();
    }

    @Test
    void create() {
        FamilyMember familyMember = memberRepository.findAll().get(1);
        FamilyMemberProperties expected = new FamilyMemberProperties(familyMember, "red");

        Map<String, String> map = new HashMap<>();
        map.put("color", expected.getColor());

        FamilyMemberProperties actual = service.createOrUpdate(familyMember.getId(), map);

        assertNotNull(actual);
        assertEquals(expected.getFamilyMember().getId(), actual.getFamilyMember().getId());
        assertEquals(expected.getColor(), actual.getColor());
    }

    @Test
    void update() {
        FamilyMember familyMember = memberRepository.findAll().get(0);
        FamilyMemberProperties expected = new FamilyMemberProperties(familyMember, "green");

        Map<String, String> map = new HashMap<>();
        map.put("color", "blue");

        FamilyMemberProperties actual = service.createOrUpdate(familyMember.getId(), map);

        assertNotNull(actual);
        assertEquals(expected.getFamilyMember().getId(), actual.getFamilyMember().getId());
        assertEquals(expected.getColor(), actual.getColor());
    }

    @Test
    void delete() {
        FamilyMember member1 = memberRepository.findAll().get(0);
        FamilyMemberProperties properties = service.findByFamilyMemberId(member1.getId());
        boolean a = familyMemberService.delete(properties.getId());
        assertTrue(a);
    }

    @Test
    void findById() {
        FamilyMember member1 = memberRepository.findAll().get(0);
        FamilyMemberProperties expected = new FamilyMemberProperties(member1, "red");

        FamilyMemberProperties actual = service.findById(expected.getId());
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertNotNull(actual.getFamilyMember());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getFamilyMember().getId(), actual.getFamilyMember().getId());
        assertEquals(expected.getFamilyMember().getName(), actual.getFamilyMember().getName());
    }
}