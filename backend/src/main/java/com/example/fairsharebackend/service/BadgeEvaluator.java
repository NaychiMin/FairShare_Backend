package com.example.fairsharebackend.service;

import com.example.fairsharebackend.constant.BadgeRuleType;
import com.example.fairsharebackend.entity.Badge;
import com.example.fairsharebackend.entity.BadgeEvaluationContext;
import com.example.fairsharebackend.entity.User;

public interface BadgeEvaluator {
    BadgeRuleType supports();
    boolean qualifies(User user, Badge badge, BadgeEvaluationContext context);
}
