package com.example.fairsharebackend.service;

import com.example.fairsharebackend.constant.BadgeRuleType;
import com.example.fairsharebackend.constant.BadgeType;
import com.example.fairsharebackend.entity.*;
import com.example.fairsharebackend.entity.dto.response.UserBadgeDto;
import com.example.fairsharebackend.mapper.UserBadgeMapper;
import com.example.fairsharebackend.repository.BadgeRepository;
import com.example.fairsharebackend.repository.UserBadgeRepository;
import com.example.fairsharebackend.util.BadgeEvaluatorRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BadgeEngineImpl implements BadgeEngine {
    private static final Logger log = LoggerFactory.getLogger(BadgeEngineImpl.class);
    private final BadgeEvaluatorRegistry badgeEvaluatorRegistry;
    private final BadgeNotificationService badgeNotificationService;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserBadgeMapper userBadgeMapper;
    public BadgeEngineImpl(
            BadgeEvaluatorRegistry badgeEvaluatorRegistry,
            BadgeNotificationService badgeNotificationService,
            BadgeRepository badgeRepository,
            UserBadgeRepository userBadgeRepository,
            UserBadgeMapper userBadgeMapper
    ) {
        this.badgeEvaluatorRegistry = badgeEvaluatorRegistry;
        this.badgeNotificationService = badgeNotificationService;
        this.badgeRepository = badgeRepository;
        this.userBadgeRepository = userBadgeRepository;
        this.userBadgeMapper = userBadgeMapper;
    }

    public void evaluate(Settlement settlement) {
        log.info("Start evaluating Settlement :: {} ", settlement.getSettlementId());

        List<Badge> applicableBadges = badgeRepository.findByBadgeType(BadgeType.SETTLEMENT);

        BadgeEvaluationContext context = new BadgeEvaluationContext();
        context.setGroup(settlement.getGroup());
        context.setSettlement(settlement);

        for (Badge b : applicableBadges) {
            log.info("For Badge :: {}", b.getName());
            BadgeEvaluator evaluator = badgeEvaluatorRegistry.get(b.getBadgeRuleType());

            if (evaluator.qualifies(settlement.getFromUser(), b, context)) {
                this.awardBadge(settlement.getFromUser(), b, settlement.getGroup());
            }
        }
    }

    public void evaluate(Expense expense) {
        log.info("Start evaluating Expense :: {} ", expense.getExpenseId());
        List<Badge> applicableBadges = badgeRepository.findByBadgeType(BadgeType.EXPENSE);

        BadgeEvaluationContext context = new BadgeEvaluationContext();
        context.setGroup(expense.getGroup());
        context.setExpense(expense);

        for (Badge b : applicableBadges) {
            log.info("For Badge :: {}", b.getName());
            BadgeEvaluator evaluator = badgeEvaluatorRegistry.get(b.getBadgeRuleType());

            if (evaluator.qualifies(expense.getPaidBy(), b, context)) {
                this.awardBadge(expense.getPaidBy(), b, expense.getGroup());
            }
        }
    }

    private void awardBadge(User user, Badge badge, Group group) {
        log.info("Awarding Badge {} to User {}", badge.getName(), user.getName());
        UserBadge userBadge = new UserBadge();
        userBadge.setUser(user);
        userBadge.setBadge(badge);
        userBadge.setGroup(group); // Will be null for PERSONAL scope badges
        userBadge.setCreatedAt(LocalDateTime.now());
        userBadge.setUpdatedAt(null);

        UserBadgeDto dto = this.userBadgeMapper.toDto(userBadge);
        this.badgeNotificationService.notifyBadgeEarned(user.getUserId(), dto);
        userBadgeRepository.save(userBadge);
    }
}
