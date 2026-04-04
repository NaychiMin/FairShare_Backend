package com.example.fairsharebackend.service;

import com.example.fairsharebackend.constant.BadgeRuleType;
import com.example.fairsharebackend.constant.BadgeScope;
import com.example.fairsharebackend.entity.*;
import com.example.fairsharebackend.exception.EvaluatorException;
import com.example.fairsharebackend.repository.ExpenseRepository;
import com.example.fairsharebackend.repository.UserBadgeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExpenseCountBadgeEvaluatorImpl implements BadgeEvaluator {
    private final String COUNT_KEY = "count";
    private static final Logger log = LoggerFactory.getLogger(ExpenseCountBadgeEvaluatorImpl.class);
    private final ExpenseRepository expenseRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public ExpenseCountBadgeEvaluatorImpl(
            ExpenseRepository expenseRepository,
            UserBadgeRepository userBadgeRepository
    ) {
        this.expenseRepository = expenseRepository;
        this.userBadgeRepository = userBadgeRepository;
    }

    @Override
    public BadgeRuleType supports() {
        return BadgeRuleType.EXPENSE_COUNT;
    }

    @Override
    public boolean qualifies(User user, Badge badge, BadgeEvaluationContext context) {
        try {
            this.validateContext(context);
            this.validateBagde(badge);
        } catch (Exception ex) {
            return false;
        }

        Expense expense = context.getExpense();
        boolean alreadyEarned = hasUserEarnedBadge(
                expense.getCreatedBy(),
                badge,
                badge.getBadgeScope() == BadgeScope.GROUP ? expense.getGroup() : null
        );

        if (alreadyEarned) {
            log.info("User {} has alread earned Badge {}", expense.getCreatedBy().getName(), badge.getName());
            return false;
        }

        Group group = context.getGroup();
        Integer count = this.getBadgeCount(badge.getRuleConfig());

        long currentLong = this.expenseRepository.countByGroupAndCreatedBy(group, expense.getCreatedBy());

        return currentLong >= count;
    }

    private void validateContext(BadgeEvaluationContext context) {
        if (context.getGroup() == null) {
            throw new EvaluatorException("Missing group");
        }
        if (context.getExpense() == null) {
            throw new EvaluatorException("Missing expense");
        }
    }

    private void validateBagde(Badge badge) {
        if (badge.getRuleConfig() == null) {
            throw new EvaluatorException("Missing badge rule config");
        }
    }

    private Integer getBadgeCount(String ruleConfig) {
        try {
            JsonNode config = objectMapper.readTree(ruleConfig);
            return config.has("count") ?
                    config.get("count").asInt() : 0;
        } catch (JsonProcessingException e) {
            throw new EvaluatorException(e.getMessage());
        }
    }

    private boolean hasUserEarnedBadge(User user, Badge badge, Group group) {
        if (badge.getBadgeScope() == BadgeScope.GROUP && group != null) {
            // Group-scoped badge - check if user earned this badge for this specific group
            return userBadgeRepository.existsByUserAndBadgeAndGroup(user, badge, group);
        } else {
            // Personal-scoped badge - check if user earned this badge overall
            return userBadgeRepository.existsByUserAndBadgeAndGroupIsNull(user, badge);
        }
    }
}
