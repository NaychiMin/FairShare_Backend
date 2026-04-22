package com.example.fairsharebackend.dto;

import com.example.fairsharebackend.entity.dto.response.SettlementResponseDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SettlementResponseDtoTest {

    @Test
    void shouldSetAndGetAllFields() {
        SettlementResponseDto dto = new SettlementResponseDto();

        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        dto.setSettlementId(id);
        dto.setGroupName("Trip");
        dto.setAmount(new BigDecimal("100.00"));
        dto.setSettlementDate(now);
        dto.setPaymentMethod("CASH");
        dto.setNotes("Test");
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);

        assertThat(dto.getSettlementId()).isEqualTo(id);
        assertThat(dto.getGroupName()).isEqualTo("Trip");
        assertThat(dto.getAmount()).isEqualByComparingTo("100.00");
        assertThat(dto.getSettlementDate()).isEqualTo(now);
        assertThat(dto.getPaymentMethod()).isEqualTo("CASH");
        assertThat(dto.getNotes()).isEqualTo("Test");
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.getUpdatedAt()).isEqualTo(now);
    }
}