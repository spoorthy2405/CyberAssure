package com.cyberassure.cyberassureproject.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserTest {

    @Test
    void userBuilder_ShouldSetDefaultsCorrectly() {
        Role role = new Role();
        role.setRoleId(1L);
        role.setRoleName("ROLE_CUSTOMER");

        User user = User.builder()
                .userId(1L)
                .fullName("John Doe")
                .email("john@example.com")
                .passwordHash("hashedpassword")
                .role(role)
                .build();

        assertEquals(1L, user.getUserId());
        assertEquals("John Doe", user.getFullName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("hashedpassword", user.getPasswordHash());
        assertEquals("ACTIVE", user.getAccountStatus());
        assertNotNull(user.getCreatedAt());
        assertEquals(role, user.getRole());
    }
}
