package com.example.fairsharebackend.service;

import com.example.fairsharebackend.util.BadgeEvaluatorRegistry;
import com.example.fairsharebackend.constant.BadgeRuleType;
import com.example.fairsharebackend.entity.*;
import com.example.fairsharebackend.repository.*;
import com.example.fairsharebackend.mapper.*;
import com.example.fairsharebackend.entity.dto.response.UserBadgeDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BadgeEngineImplTest {

    @Mock private BadgeEvaluatorRegistry registry;
    @Mock private BadgeNotificationService notificationService;
    @Mock private BadgeRepository badgeRepository;
    @Mock private UserBadgeRepository userBadgeRepository;
    @Mock private UserBadgeMapper mapper;
    @Mock private ApplicationEventPublisher publisher;

    @Mock private BadgeEvaluator evaluator;

    @InjectMocks
    private BadgeEngineImpl engine;

    private User user;
    private Group group;
    private Badge badge;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(UUID.randomUUID());
        user.setName("Test User");

        group = new Group();

        badge = new Badge();
        badge.setName("Test Badge");
        badge.setBadgeRuleType(BadgeRuleType.SETTLEMENT_COUNT);
    }

    @Test
    @DisplayName("Award badge when evaluator qualifies")
    void shouldAwardBadge_whenEvaluatorReturnsTrue() {

        Settlement settlement = new Settlement();
        settlement.setFromUser(user);
        settlement.setGroup(group);

        when(badgeRepository.findByBadgeType(any()))
                .thenReturn(List.of(badge));

        when(registry.get(any(BadgeRuleType.class)))
                .thenReturn(evaluator);

        when(evaluator.qualifies(any(), any(), any()))
                .thenReturn(true);

        when(mapper.toDto(any(UserBadge.class)))
                .thenReturn(new UserBadgeDto());

        engine.evaluate(settlement);

        verify(userBadgeRepository).save(any(UserBadge.class));
        verify(publisher).publishEvent(any(UserBadge.class));
    }

    @Test
    @DisplayName("Do not award badge when evaluator does not qualify")
    void shouldNotAwardBadge_whenEvaluatorReturnsFalse() {

        Settlement settlement = new Settlement();
        settlement.setFromUser(user);
        settlement.setGroup(group);

        when(badgeRepository.findByBadgeType(any()))
                .thenReturn(List.of(badge));

        when(registry.get(any(BadgeRuleType.class)))
                .thenReturn(evaluator);

        when(evaluator.qualifies(any(), any(), any()))
                .thenReturn(false);

        engine.evaluate(settlement);

        verify(userBadgeRepository, never()).save(any());
        verify(publisher, never()).publishEvent(any());
    }
}