package com.example.fairsharebackend.service;

import com.example.fairsharebackend.repository.BadgeRepository;
import com.example.fairsharebackend.repository.UserBadgeRepository;
import com.example.fairsharebackend.util.BadgeEvaluatorRegistry;
import org.springframework.stereotype.Service;

@Service
public class BadgeEngineImpl implements BadgeEngine {
    private final BadgeEvaluatorRegistry badgeEvaluatorRegistry;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    public BadgeEngineImpl(
            BadgeEvaluatorRegistry badgeEvaluatorRegistry,
            BadgeRepository badgeRepository,
            UserBadgeRepository userBadgeRepository
    ) {
        this.badgeEvaluatorRegistry = badgeEvaluatorRegistry;
        this.badgeRepository = badgeRepository;
        this.userBadgeRepository = userBadgeRepository;
    }
}
