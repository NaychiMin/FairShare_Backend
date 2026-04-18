package com.example.fairsharebackend.service;

import com.example.fairsharebackend.constant.BadgeRuleType;
import com.example.fairsharebackend.constant.BadgeScope;
import com.example.fairsharebackend.entity.*;
import com.example.fairsharebackend.exception.EvaluatorException;
import com.example.fairsharebackend.repository.PairwiseBalanceRepository;
import com.example.fairsharebackend.repository.SettlementRepository;
import com.example.fairsharebackend.repository.UserBadgeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
public class SettlementTimingBadgeEvaluatorImpl implements BadgeEvaluator {
    private final String TIMING_KEY = "timingInMin";
    private static final Logger log = LoggerFactory.getLogger(SettlementTimingBadgeEvaluatorImpl.class);
    private final SettlementRepository settlementRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final PairwiseBalanceRepository pairwiseBalanceRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public SettlementTimingBadgeEvaluatorImpl(
            SettlementRepository settlementRepository,
            UserBadgeRepository userBadgeRepository,
            PairwiseBalanceRepository pairwiseBalanceRepository
    ) {
        this.settlementRepository = settlementRepository;
        this.userBadgeRepository = userBadgeRepository;
        this.pairwiseBalanceRepository = pairwiseBalanceRepository;
    }

    @Override
    public BadgeRuleType supports() {
        return BadgeRuleType.SETTLEMENT_TIMING;
    }

    @Override
    public boolean qualifies(User user, Badge badge, BadgeEvaluationContext context) {
        try {
            this.validateContext(context);
            this.validateBagde(badge);
        } catch (Exception ex) {
            return false;
        }


        Settlement settlement = context.getSettlement();
        boolean alreadyEarned = hasUserEarnedBadge(
                settlement.getFromUser(),
                badge,
                badge.getBadgeScope() == BadgeScope.GROUP ? settlement.getGroup() : null
        );

        if (alreadyEarned) {
            log.info("User {} has already earned Badge {}", user.getName(), badge.getName());
            return false;
        }

        Group group = context.getGroup();

        Optional<PairwiseBalance> latestBalance = pairwiseBalanceRepository
                .findTopByGroup_GroupIdAndDebtor_UserIdOrderByLastUpdatedDesc(group.getGroupId(), context.getSettlement().getFromUser().getUserId());
        if (latestBalance.isEmpty()) {
            log.info("No latest balance found for User {} from group {}", user.getName(), group.getGroupName());
            return false;
        }

        LocalDateTime latestBalanceLastUpdate = latestBalance.get().getLastUpdated();
        LocalDateTime settlementTime = settlement.getCreatedAt();
        Integer timingInMin = this.getBadgeTiming(badge.getRuleConfig());

        log.info("Benchmark: timingInMin {}", timingInMin);
        log.info("Comparing: last balance last update {} VS settlement created at {}", latestBalanceLastUpdate, settlementTime);

        if (latestBalanceLastUpdate == null || settlementTime == null || timingInMin == null) {
            return false;
        }

        long diffInMinutes = ChronoUnit.MINUTES.between(latestBalanceLastUpdate, settlementTime);
        log.info("Comparing: diffInMinutes {}", diffInMinutes);
        return diffInMinutes < timingInMin;
    }

    private void validateContext(BadgeEvaluationContext context) {
        if (context.getGroup() == null) {
            throw new EvaluatorException("Missing group");
        }
        if (context.getSettlement() == null) {
            throw new EvaluatorException("Missing settlement");
        }
    }

    private void validateBagde(Badge badge) {
        if (badge.getRuleConfig() == null) {
            throw new EvaluatorException("Missing badge rule config");
        }
    }

    private Integer getBadgeTiming(String ruleConfig) {
        try {
            JsonNode config = objectMapper.readTree(ruleConfig);
            return config.has(TIMING_KEY) ?
                    config.get(TIMING_KEY).asInt() : 0;
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
