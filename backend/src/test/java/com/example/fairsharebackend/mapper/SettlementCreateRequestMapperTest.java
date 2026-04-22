package com.example.fairsharebackend.dto;

import com.example.fairsharebackend.entity.dto.request.SettlementCreateRequestDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SettlementCreateRequestDtoTest {

    @Test
    void shouldSetAndGetFieldsCorrectly() {
        SettlementCreateRequestDto dto = new SettlementCreateRequestDto();

        UUID groupId = UUID.randomUUID();
        UUID fromUser = UUID.randomUUID();
        UUID toUser = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        dto.setGroupId(groupId);
        dto.setFromUserId(fromUser);
        dto.setToUserId(toUser);
        dto.setAmount(new BigDecimal("50.00"));
        dto.setSettlementDate(now);
        dto.setPaymentMethod("CASH");
        dto.setNotes("Dinner");

        assertThat(dto.getGroupId()).isEqualTo(groupId);
        assertThat(dto.getFromUserId()).isEqualTo(fromUser);
        assertThat(dto.getToUserId()).isEqualTo(toUser);
        assertThat(dto.getAmount()).isEqualByComparingTo("50.00");
        assertThat(dto.getSettlementDate()).isEqualTo(now);
        assertThat(dto.getPaymentMethod()).isEqualTo("CASH");
        assertThat(dto.getNotes()).isEqualTo("Dinner");
    }
}