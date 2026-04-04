package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.dto.response.UserBadgeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BadgeNotificationServiceImpl implements BadgeNotificationService {
    private static final Logger log = LoggerFactory.getLogger(BadgeNotificationServiceImpl.class);
    private final SimpMessagingTemplate messagingTemplate;

    public BadgeNotificationServiceImpl(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void notifyBadgeEarned(UUID userId, UserBadgeDto badge) {
        log.info("Sending badge notification to user {}: {}", userId, badge.getBadge().getName());

        messagingTemplate.convertAndSend(
                "/topic/users/" + userId + "/badges",
                badge
        );
    }

    @Override
    public void notifyBadgeEarnedToGroup(UUID groupId, UserBadgeDto badge) {
        log.info("Sending badge notification to group {}: {}", groupId, badge.getBadge().getName());

        // Send to group-specific topic
        messagingTemplate.convertAndSend(
                "/topic/groups/" + groupId + "/badges",
                badge
        );
    }

    @Override
    public void notifyMultipleBadgesEarned(UUID userId, List<UserBadgeDto> badges) {
        log.info("Sending {} badge notifications to user {}", badges.size(), userId);

        for (UserBadgeDto badge : badges) {
            notifyBadgeEarned(userId, badge);
        }
    }
}