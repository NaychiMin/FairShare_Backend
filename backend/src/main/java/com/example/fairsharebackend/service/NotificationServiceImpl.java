package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.Group;
import com.example.fairsharebackend.entity.Notification;
import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.entity.dto.response.NotificationResponseDto;
import com.example.fairsharebackend.entity.dto.response.UnreadGroupNotificationDto;
import com.example.fairsharebackend.repository.NotificationRepository;
import com.example.fairsharebackend.repository.UnreadGroupNotificationProjection;
import com.example.fairsharebackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            UserRepository userRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void notifyUser(
            User recipient,
            User actor,
            Group group,
            String type,
            String message,
            UUID referenceId
    ) {
        if (recipient == null) {
            return;
        }

        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setActor(actor);
        notification.setGroup(group);
        notification.setType(type);
        notification.setMessage(message);
        notification.setReferenceId(referenceId);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    @Override
    public void notifyUsers(
            List<User> recipients,
            User actor,
            Group group,
            String type,
            String message,
            UUID referenceId
    ) {
        if (recipients == null || recipients.isEmpty()) {
            return;
        }

        for (User recipient : recipients) {
            if (recipient == null) {
                continue;
            }

            // skip notifying the actor about their own action
            if (actor != null && recipient.getUserId().equals(actor.getUserId())) {
                continue;
            }

            Notification notification = new Notification();
            notification.setRecipient(recipient);
            notification.setActor(actor);
            notification.setGroup(group);
            notification.setType(type);
            notification.setMessage(message);
            notification.setReferenceId(referenceId);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setRead(false);

            notificationRepository.save(notification);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getMyNotifications(String email) {
        validateUserExists(email);

        return notificationRepository.findByRecipient_EmailOrderByCreatedAtDesc(email)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(String email) {
        validateUserExists(email);
        return notificationRepository.countByRecipient_EmailAndIsReadFalse(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnreadGroupNotificationDto> getUnreadCountsByGroup(String email) {
        validateUserExists(email);

        List<UnreadGroupNotificationProjection> rows =
                notificationRepository.countUnreadByGroup(email);

        return rows.stream()
                .map(row -> new UnreadGroupNotificationDto(
                        row.getGroupId(),
                        row.getUnreadCount()
                ))
                .toList();
    }

    @Override
    public void markAsRead(UUID notificationId, String email) {
        validateUserExists(email);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getRecipient().getEmail().equals(email)) {
            throw new RuntimeException("Not authorized to update this notification");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(String email) {
        validateUserExists(email);

        List<Notification> notifications =
                notificationRepository.findByRecipient_EmailAndIsReadFalseOrderByCreatedAtDesc(email);

        for (Notification notification : notifications) {
            notification.setRead(true);
        }

        notificationRepository.saveAll(notifications);
    }

    private void validateUserExists(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private NotificationResponseDto mapToDto(Notification notification) {
        return new NotificationResponseDto(
                notification.getNotificationId(),
                notification.getType(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt(),
                notification.getGroup() != null ? notification.getGroup().getGroupId() : null,
                notification.getActor() != null ? notification.getActor().getName() : null
        );
    }
}