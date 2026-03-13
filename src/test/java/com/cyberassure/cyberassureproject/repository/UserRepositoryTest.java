package com.cyberassure.cyberassureproject.repository;

import com.cyberassure.cyberassureproject.entity.Role;
import com.cyberassure.cyberassureproject.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @PersistenceContext
    EntityManager em;

    private Role role;

    @BeforeEach
    void setUp() {
        role = roleRepository.findByRoleName("ROLE_CUSTOMER")
                .orElseGet(() -> roleRepository.save(
                        Role.builder().roleName("ROLE_CUSTOMER").isActive(true).build()));
    }

    @Test
    void existsByEmail_returnsTrue_whenEmailExists() {
        userRepository.save(User.builder().fullName("Alice").email("alice@test.com")
                .passwordHash("h").role(role).build());
        assertTrue(userRepository.existsByEmail("alice@test.com"));
    }

    @Test
    void existsByEmail_returnsFalse_whenEmailAbsent() {
        assertFalse(userRepository.existsByEmail("nobody@test.com"));
    }

    @Test
    void findByEmail_returnsUser_whenExists() {
        userRepository.save(User.builder().fullName("Bob").email("bob@test.com")
                .passwordHash("h").role(role).build());
        Optional<User> result = userRepository.findByEmail("bob@test.com");
        assertTrue(result.isPresent());
        assertEquals("Bob", result.get().getFullName());
    }

    @Test
    void findByEmail_returnsEmpty_whenMissing() {
        Optional<User> result = userRepository.findByEmail("ghost@test.com");
        assertFalse(result.isPresent());
    }
}
