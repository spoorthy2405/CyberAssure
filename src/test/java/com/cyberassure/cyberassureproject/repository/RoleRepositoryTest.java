package com.cyberassure.cyberassureproject.repository;

import com.cyberassure.cyberassureproject.entity.Role;
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
class RoleRepositoryTest {

    @Autowired
    RoleRepository roleRepository;

    @Test
    void findByRoleName_returnsRole_whenExists() {
        roleRepository.save(Role.builder().roleName("ROLE_TEST_ADMIN").isActive(true).build());
        Optional<Role> result = roleRepository.findByRoleName("ROLE_TEST_ADMIN");
        assertTrue(result.isPresent());
        assertEquals("ROLE_TEST_ADMIN", result.get().getRoleName());
    }

    @Test
    void findByRoleName_returnsEmpty_whenMissing() {
        Optional<Role> result = roleRepository.findByRoleName("ROLE_NON_EXISTENT");
        assertFalse(result.isPresent());
    }

    @Test
    void existsByRoleName_returnsTrue_whenExists() {
        roleRepository.save(Role.builder().roleName("ROLE_TEMP").isActive(true).build());
        assertTrue(roleRepository.existsByRoleName("ROLE_TEMP"));
    }

    @Test
    void existsByRoleName_returnsFalse_whenMissing() {
        assertFalse(roleRepository.existsByRoleName("ROLE_MISSING_XYZ"));
    }
}
