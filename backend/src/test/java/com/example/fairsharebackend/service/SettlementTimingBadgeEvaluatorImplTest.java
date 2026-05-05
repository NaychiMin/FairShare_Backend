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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettlementTimingBadgeEvaluatorImplTest {

    @Mock private SettlementRepository settlementRepository;
    @Mock private UserBadgeRepository userBadgeRepository;
    @Mock private ExpenseRepository expenseRepository;


    @InjectMocks
    private SettlementTimingBadgeEvaluatorImpl evaluator;

    private User user;
    private Badge badge;
    private Group group;
    private Settlement settlement;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUserId(UUID.randomUUID());
        user.setName("User");

        group = new Group();
        group.setGroupId(UUID.randomUUID());
        group.setGroupName("Group");

        settlement = new Settlement();
        settlement.setFromUser(user);
        settlement.setGroup(group);

        badge = new Badge();
        badge.setRuleConfig("{\"timingInMin\": 10}");
    }

    @Test
    @DisplayName("Return true when settlement is within timing threshold")
    void qualifies_shouldReturnTrue_whenWithinTime() {

        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 12, 0);

        Expense expense = new Expense();
        expense.setCreatedAt(now.minusMinutes(2));

        when(expenseRepository
                .findTopByGroup_GroupIdOrderByCreatedAtDesc(
                        group.getGroupId()))
                .thenReturn(Optional.of(expense));

        badge.setRuleConfig("{\"timingInMin\":5}");

        settlement.setCreatedAt(now);

        BadgeEvaluationContext ctx = new BadgeEvaluationContext();
        ctx.setGroup(group);
        ctx.setSettlement(settlement);

        boolean result = evaluator.qualifies(user, badge, ctx);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Return false when no balance exists")
    void qualifies_shouldReturnFalse_whenNoBalance() {

        when(expenseRepository
                .findTopByGroup_GroupIdOrderByCreatedAtDesc(
                        group.getGroupId()))
                .thenReturn(Optional.empty());

        BadgeEvaluationContext ctx = new BadgeEvaluationContext();
        ctx.setGroup(group);
        ctx.setSettlement(settlement);

        boolean result = evaluator.qualifies(user, badge, ctx);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Return false when badge config is invalid JSON")
    void qualifies_shouldReturnFalse_whenInvalidConfig() {

        badge.setRuleConfig("INVALID_JSON");

        BadgeEvaluationContext ctx = new BadgeEvaluationContext();
        ctx.setGroup(group);
        ctx.setSettlement(settlement);

        boolean result = evaluator.qualifies(user, badge, ctx);

        assertThat(result).isFalse();
    }
}