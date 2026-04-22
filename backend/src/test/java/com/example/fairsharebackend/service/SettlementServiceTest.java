package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.*;
import com.example.fairsharebackend.entity.dto.request.SettlementCreateRequestDto;
import com.example.fairsharebackend.entity.dto.response.SettlementResponseDto;
import com.example.fairsharebackend.mapper.SettlementMapper;
import com.example.fairsharebackend.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettlementServiceTest {

    @Mock private SettlementRepository settlementRepository;
    @Mock private UserRepository userRepository;
    @Mock private GroupRepository groupRepository;
    @Mock private GroupMembershipRepository membershipRepository;
    @Mock private BalanceService balanceService;
    @Mock private BadgeEngine badgeEngine;
    @Mock private SettlementMapper mapper;
    @Mock private GroupActivityRepository activityRepository;
    @Mock private NotificationService notificationService;

    @InjectMocks
    private SettlementService service;

    private User creator;
    private User payer;
    private User receiver;
    private Group group;

    @BeforeEach
    void setUp() {
        creator = new User();
        creator.setUserId(UUID.randomUUID());
        creator.setName("Creator");
        creator.setEmail("creator@test.com");

        payer = new User();
        payer.setUserId(UUID.randomUUID());
        payer.setName("Payer");

        receiver = new User();
        receiver.setUserId(UUID.randomUUID());
        receiver.setName("Receiver");

        group = new Group();
        group.setGroupId(UUID.randomUUID());
        group.setGroupName("Group");
    }

    // =========================
    // CREATE SETTLEMENT
    // =========================

    @Test
    @DisplayName("Create settlement successfully and update balances")
    void shouldCreateSettlementSuccessfully() {

        SettlementCreateRequestDto req = new SettlementCreateRequestDto();
        req.setGroupId(group.getGroupId());
        req.setFromUserId(payer.getUserId());
        req.setToUserId(receiver.getUserId());
        req.setAmount(BigDecimal.TEN);

        when(userRepository.findByEmail(creator.getEmail()))
                .thenReturn(Optional.of(creator));

        when(groupRepository.findById(group.getGroupId()))
                .thenReturn(Optional.of(group));

        when(userRepository.findById(payer.getUserId()))
                .thenReturn(Optional.of(payer));

        when(userRepository.findById(receiver.getUserId()))
                .thenReturn(Optional.of(receiver));

        // IMPORTANT: match actual method signature usage
        when(membershipRepository.existsByGroupAndUser_UserId(eq(group), any(UUID.class)))
                .thenReturn(true);

        Settlement saved = new Settlement();
        saved.setFromUser(payer);
        saved.setToUser(receiver);
        saved.setGroup(group);
        saved.setAmount(BigDecimal.TEN);
        saved.setSettlementId(UUID.randomUUID());

        when(settlementRepository.save(any(Settlement.class)))
                .thenReturn(saved);

        when(mapper.toResponseDto(saved))
                .thenReturn(new SettlementResponseDto());

        SettlementResponseDto result =
                service.createSettlement(req, creator.getEmail());

        assertThat(result).isNotNull();

        verify(balanceService).updateBalancesForSettlement(saved);
        verify(notificationService).notifyUser(any(), any(), any(), any(), any(), any());
    }

    // =========================
    // INVALID AMOUNT
    // =========================

    @Test
    @DisplayName("Throw exception when settlement amount is invalid")
    void shouldFailWhenAmountInvalid() {

        SettlementCreateRequestDto req = new SettlementCreateRequestDto();
        req.setAmount(BigDecimal.ZERO);

        when(userRepository.findByEmail(creator.getEmail()))
                .thenReturn(Optional.of(creator));

        when(groupRepository.findById(group.getGroupId()))
                .thenReturn(Optional.of(group));

        when(membershipRepository.existsByGroupAndUser_UserId(eq(group), any(UUID.class)))
                .thenReturn(true);

        assertThatThrownBy(() ->
                service.createSettlement(req, creator.getEmail()))
                .isInstanceOf(RuntimeException.class);
    }

    // =========================
    // DELETE SETTLEMENT
    // =========================

    @Test
    @DisplayName("Reverse balance and delete settlement successfully")
    void shouldDeleteSettlementSuccessfully() {

        Settlement settlement = new Settlement();
        settlement.setGroup(group);
        settlement.setFromUser(payer);     // IMPORTANT FIX
        settlement.setToUser(receiver);    // IMPORTANT FIX
        settlement.setAmount(BigDecimal.TEN);

        when(userRepository.findByEmail(creator.getEmail()))
                .thenReturn(Optional.of(creator));

        when(settlementRepository.findById(any()))
                .thenReturn(Optional.of(settlement));

        when(membershipRepository.existsByGroupAndUser_UserId(eq(group), any(UUID.class)))
                .thenReturn(true);

        when(activityRepository.findBySettlement(settlement))
                .thenReturn(List.of());

        service.deleteSettlement(UUID.randomUUID(), creator.getEmail());

        verify(balanceService).reverseSettlement(settlement);
        verify(settlementRepository).delete(settlement);
    }
}