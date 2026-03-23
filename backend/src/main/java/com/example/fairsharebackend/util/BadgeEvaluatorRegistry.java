package com.example.fairsharebackend.util;

import com.example.fairsharebackend.constant.BadgeRuleType;
import com.example.fairsharebackend.constant.BadgeRuleType;
import com.example.fairsharebackend.constant.BadgeType;
import com.example.fairsharebackend.service.BadgeEvaluator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BadgeEvaluatorRegistry {
    private final Map<BadgeRuleType, BadgeEvaluator> registry = new HashMap<>();
    public BadgeEvaluatorRegistry(
            List<BadgeEvaluator> badgeEvaluatorList
    ) {
        for (BadgeEvaluator e : badgeEvaluatorList) {
            registry.put(e.supports(), e);
        }
    }

    public BadgeEvaluator get(BadgeRuleType badgeRuleType) {
        return registry.get(badgeRuleType);
    }
}
