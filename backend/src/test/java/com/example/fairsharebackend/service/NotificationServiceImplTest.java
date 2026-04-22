package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.*;
import com.example.fairsharebackend.entity.dto.response.NotificationResponseDto;
import com.example.fairsharebackend.repository.NotificationRepository;
import com.example.fairsharebackend.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationServiceImpl service;

    private User user;
    private User actor;
    private Group group;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(UUID.randomUUID());
        user.setEmail("test@example.com");

        actor = new User();
        actor.setUserId(UUID.randomUUID());

        group = new Group();
        group.setGroupId(UUID.randomUUID());
    }

    @Test
    @DisplayName("Save notification when recipient is valid")
    void shouldSaveNotification_whenRecipientValid() {
        // ACT
        service.notifyUser(user, actor, group, "TYPE", "msg", UUID.randomUUID());

        // ASSERT
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Skip notification when recipient is null")
    void shouldSkipNotification_whenRecipientNull() {
        // ACT
        service.notifyUser(null, actor, group, "TYPE", "msg", UUID.randomUUID());

        // ASSERT
        verify(notificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Skip actor when notifying multiple users")
    void shouldSkipActor_whenNotifyingUsers() {
        // ARRANGE
        List<User> users = List.of(user, actor);

        // ACT
        service.notifyUsers(users, actor, group, "TYPE", "msg", UUID.randomUUID());

        // ASSERT
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Return notifications for user")
    void shouldReturnNotificationsForUser() {
        // ARRANGE
        Notification notification = new Notification();

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(notificationRepository.findByRecipient_EmailOrderByCreatedAtDesc(user.getEmail()))
                .thenReturn(List.of(notification));

        // ACT
        List<NotificationResponseDto> result =
                service.getMyNotifications(user.getEmail());

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Mark notification as read when user is authorized")
    void shouldMarkAsRead_whenAuthorized() {
        // ARRANGE
        Notification notification = new Notification();
        notification.setRecipient(user);

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(notificationRepository.findById(any()))
                .thenReturn(Optional.of(notification));

        UUID id = UUID.randomUUID();

        // ACT
        service.markAsRead(id, user.getEmail());

        // ASSERT
        verify(notificationRepository, times(1)).save(notification);
        assertThat(notification.isRead()).isTrue();
    }

    @Test
    @DisplayName("Throw exception when user is not authorized to mark notification as read")
    void shouldThrow_whenUnauthorizedToMarkAsRead() {
        // ARRANGE
        Notification notification = new Notification();

        User other = new User();
        other.setEmail("other@test.com");
        notification.setRecipient(other);

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(notificationRepository.findById(any()))
                .thenReturn(Optional.of(notification));

        // ACT & ASSERT
        assertThatThrownBy(() ->
                service.markAsRead(UUID.randomUUID(), user.getEmail()))
                .isInstanceOf(RuntimeException.class);
    }
}