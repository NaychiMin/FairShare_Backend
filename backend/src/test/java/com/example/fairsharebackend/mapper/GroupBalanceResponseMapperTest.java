package com.example.fairsharebackend.dto;

import com.example.fairsharebackend.entity.dto.response.GroupBalanceResponseDto;
import com.example.fairsharebackend.entity.dto.response.UserBalanceDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GroupBalanceResponseDtoTest {

    @Test
    void shouldSetAndGetFieldsCorrectly() {
        GroupBalanceResponseDto dto = new GroupBalanceResponseDto();

        UserBalanceDto userBalance = new UserBalanceDto(); // assume default constructor exists

        dto.setNetBalance(new BigDecimal("50.00"));
        dto.setOwesYou(List.of(userBalance));
        dto.setYouOwe(List.of(userBalance));

        assertThat(dto.getNetBalance()).isEqualByComparingTo("50.00");
        assertThat(dto.getOwesYou()).hasSize(1);
        assertThat(dto.getYouOwe()).hasSize(1);
    }
}