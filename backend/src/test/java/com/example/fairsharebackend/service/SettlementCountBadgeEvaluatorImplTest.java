package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.*;
import com.example.fairsharebackend.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettlementCountBadgeEvaluatorImplTest {

    @Mock
    private SettlementRepository settlementRepository;

    @Mock
    private UserBadgeRepository userBadgeRepository;

    @InjectMocks
    private SettlementCountBadgeEvaluatorImpl evaluator;

    private User user;
    private Group group;
    private Badge badge;
    private BadgeEvaluationContext ctx;

    @BeforeEach
    void setUp() {
        user = new User();

        group = new Group();

        badge = new Badge();
        badge.setRuleConfig("{\"count\": 2}");

        ctx = new BadgeEvaluationContext();
        ctx.setGroup(group);
        ctx.setSettlement(new Settlement());
    }

    @Test
    @DisplayName("Return true when settlement count meets required threshold")
    void shouldReturnTrue_whenCountMet() {
        // ARRANGE
        when(settlementRepository.countByGroupAndFromUser(group, user))
                .thenReturn(3L);

        // ACT
        boolean result = evaluator.qualifies(user, badge, ctx);

        // ASSERT
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Return false when settlement count is below threshold")
    void shouldReturnFalse_whenCountInsufficient() {
        // ARRANGE
        when(settlementRepository.countByGroupAndFromUser(any(), any()))
                .thenReturn(1L);

        Badge lowThresholdBadge = new Badge();
        lowThresholdBadge.setRuleConfig("{\"count\": 5}");

        // ACT
        boolean result = evaluator.qualifies(new User(), lowThresholdBadge, ctx);

        // ASSERT
        assertThat(result).isFalse();
    }
}