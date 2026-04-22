package com.example.fairsharebackend.dto;

import com.example.fairsharebackend.entity.dto.request.SettlementEditRequestDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class SettlementEditRequestDtoTest {

    @Test
    void shouldSetAndGetFieldsCorrectly() {
        SettlementEditRequestDto dto = new SettlementEditRequestDto();

        LocalDateTime now = LocalDateTime.now();

        dto.setAmount(new BigDecimal("75.00"));
        dto.setSettlementDate(now);
        dto.setPaymentMethod("PAYNOW");
        dto.setNotes("Updated");

        assertThat(dto.getAmount()).isEqualByComparingTo("75.00");
        assertThat(dto.getSettlementDate()).isEqualTo(now);
        assertThat(dto.getPaymentMethod()).isEqualTo("PAYNOW");
        assertThat(dto.getNotes()).isEqualTo("Updated");
    }
}