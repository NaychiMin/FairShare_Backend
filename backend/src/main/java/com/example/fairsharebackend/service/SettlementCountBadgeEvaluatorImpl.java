package com.example.fairsharebackend.service;

import com.example.fairsharebackend.constant.BadgeRuleType;
import com.example.fairsharebackend.entity.Badge;
import com.example.fairsharebackend.entity.BadgeEvaluationContext;
import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.exception.EvaluatorException;
import org.springframework.stereotype.Component;

@Component
public class SettlementCountBadgeEvaluatorImpl implements BadgeEvaluator {
    public SettlementCountBadgeEvaluatorImpl() {
    }
    @Override
    public BadgeRuleType supports() {
        return BadgeRuleType.SETTLEMENT_COUNT;
    }

    @Override
    public boolean qualifies(User user, Badge badge, BadgeEvaluationContext context) {
        try {
            this.validateContext(context);
        } catch (Exception ex) {
            return false;
        }

        // Count number of settlements user has made for the group
        return false;
    }

    private void validateContext(BadgeEvaluationContext context) {
        if (context.getGroup() == null) {
            throw new EvaluatorException("Missing group");
        }
    }
}
