package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.*;
import com.example.fairsharebackend.entity.dto.response.UserBadgeResponseDto;
import com.example.fairsharebackend.repository.UserBadgeRepository;
import com.example.fairsharebackend.repository.UserRepository;
import com.example.fairsharebackend.constant.BadgeType;
import com.example.fairsharebackend.constant.BadgeScope;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserBadgeServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserBadgeRepository userBadgeRepository;

    @InjectMocks
    private UserBadgeServiceImpl userBadgeService;

    private User user;
    private String email;

    @BeforeEach
    void setUp() {
        email = "test@example.com";

        user = new User();
        user.setUserId(UUID.randomUUID());
        user.setEmail(email);
        user.setName("Test User");
    }

    @Test
    @DisplayName("Get user badges successfully")
    void shouldReturnUserBadges() {
        // ARRANGE
        Badge badge = new Badge();
        badge.setName("3 EXPENSES");
        badge.setDescription("Yay! got 3");
        badge.setBadgeType(BadgeType.EXPENSE);
        badge.setBadgeScope(BadgeScope.GROUP);

        Group group = new Group();
        group.setGroupName("Japan 2026");

        UserBadge userBadge = new UserBadge();
        userBadge.setBadge(badge);
        userBadge.setUser(user);
        userBadge.setGroup(group);
        userBadge.setCreatedAt(LocalDateTime.now());

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userBadgeRepository.findByUserOrderByCreatedAtDesc(user))
                .thenReturn(List.of(userBadge));

        // ACT
        List<UserBadgeResponseDto> result = userBadgeService.getUserBadges(email);

        // ASSERT
        assertThat(result).hasSize(1);

        UserBadgeResponseDto dto = result.get(0);
        assertThat(dto.getBadgeName()).isEqualTo("3 EXPENSES");
        assertThat(dto.getDescription()).isEqualTo("Yay! got 3");
        assertThat(dto.getBadgeType()).isEqualTo("EXPENSE");
        assertThat(dto.getBadgeScope()).isEqualTo("GROUP");
        assertThat(dto.getGroupName()).isEqualTo("Japan 2026");
        assertThat(dto.getEarnedAt()).isNotNull();
    }

    @Test
    @DisplayName("Return empty list when user has no badges")
    void shouldReturnEmptyListWhenNoBadges() {
        // ARRANGE
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userBadgeRepository.findByUserOrderByCreatedAtDesc(user))
                .thenReturn(List.of());

        // ACT
        List<UserBadgeResponseDto> result = userBadgeService.getUserBadges(email);

        // ASSERT
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Throw exception when user not found")
    void shouldThrowWhenUserNotFound() {
        // ARRANGE
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> userBadgeService.getUserBadges(email))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("Handle null badge gracefully")
    void shouldHandleNullBadge() {
        // ARRANGE
        UserBadge userBadge = new UserBadge();
        userBadge.setBadge(null); // important edge case
        userBadge.setUser(user);
        userBadge.setCreatedAt(LocalDateTime.now());

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userBadgeRepository.findByUserOrderByCreatedAtDesc(user))
                .thenReturn(List.of(userBadge));

        // ACT
        List<UserBadgeResponseDto> result = userBadgeService.getUserBadges(email);

        // ASSERT
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBadgeName()).isEqualTo("Unknown Badge");
    }

    @Test
    @DisplayName("Handle null group gracefully")
    void shouldHandleNullGroup() {
        // ARRANGE
        Badge badge = new Badge();
        badge.setName("Solo Badge");
        badge.setBadgeType(BadgeType.EXPENSE);
        badge.setBadgeScope(BadgeScope.USER);

        UserBadge userBadge = new UserBadge();
        userBadge.setBadge(badge);
        userBadge.setUser(user);
        userBadge.setGroup(null); // edge case
        userBadge.setCreatedAt(LocalDateTime.now());

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userBadgeRepository.findByUserOrderByCreatedAtDesc(user))
                .thenReturn(List.of(userBadge));

        // ACT
        List<UserBadgeResponseDto> result = userBadgeService.getUserBadges(email);

        // ASSERT
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getGroupName()).isNull();
    }
}