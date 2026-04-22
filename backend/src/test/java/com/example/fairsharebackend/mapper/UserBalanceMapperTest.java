package com.example.fairsharebackend.dto;

import com.example.fairsharebackend.entity.dto.response.UserBalanceDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserBalanceDtoTest {

    @Test
    void shouldCreateUsingConstructor() {
        UUID id = UUID.randomUUID();

        UserBalanceDto dto = new UserBalanceDto(
                id, "Alice", "alice@example.com", new BigDecimal("25.00")
        );

        assertThat(dto.getUserId()).isEqualTo(id);
        assertThat(dto.getName()).isEqualTo("Alice");
        assertThat(dto.getEmail()).isEqualTo("alice@example.com");
        assertThat(dto.getAmount()).isEqualByComparingTo("25.00");
    }

    @Test
    void shouldSetAndGetFields() {
        UserBalanceDto dto = new UserBalanceDto();
        dto.setName("Bob");

        assertThat(dto.getName()).isEqualTo("Bob");
    }
}