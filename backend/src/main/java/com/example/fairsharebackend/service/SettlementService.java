package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.dto.request.SettlementCreateRequestDto;
import com.example.fairsharebackend.entity.dto.response.SettlementResponseDto;
import com.example.fairsharebackend.entity.*;
import com.example.fairsharebackend.exception.ResourceNotFoundException;
import com.example.fairsharebackend.mapper.SettlementMapper;
import com.example.fairsharebackend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupMembershipRepository groupMembershipRepository;
    private final BalanceService balanceService;
    private final SettlementMapper settlementMapper;
    private final GroupActivityRepository groupActivityRepository;

    public SettlementService(
            SettlementRepository settlementRepository,
            UserRepository userRepository,
            GroupRepository groupRepository,
            GroupMembershipRepository groupMembershipRepository,
            BalanceService balanceService,
            SettlementMapper settlementMapper,
            GroupActivityRepository groupActivityRepository) {
        this.settlementRepository = settlementRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.groupMembershipRepository = groupMembershipRepository;
        this.balanceService = balanceService;
        this.settlementMapper = settlementMapper;
        this.groupActivityRepository = groupActivityRepository;
    }

    // Create settlement in group
    @Transactional
    public SettlementResponseDto createSettlement(SettlementCreateRequestDto request, String requesterEmail) {
        
        User creator = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        
        if (!groupMembershipRepository.existsByGroupAndUser_UserId(group, creator.getUserId())) {
            throw new RuntimeException("User is not a member of this group");
        }
        
        User payer = userRepository.findById(request.getFromUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Payer not found"));
        
        User receiver = userRepository.findById(request.getToUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));
        
        if (!groupMembershipRepository.existsByGroupAndUser_UserId(group, payer.getUserId())) {
            throw new RuntimeException("Payer is not a member of this group");
        }
        if (!groupMembershipRepository.existsByGroupAndUser_UserId(group, receiver.getUserId())) {
            throw new RuntimeException("Receiver is not a member of this group");
        }
        
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Settlement amount must be positive");
        }
        
        Settlement settlement = new Settlement();
        settlement.setGroup(group);
        settlement.setFromUser(payer);
        settlement.setToUser(receiver);
        settlement.setCreatedBy(creator);
        settlement.setAmount(request.getAmount());
        settlement.setSettlementDate(request.getSettlementDate());
        settlement.setPaymentMethod(request.getPaymentMethod());
        settlement.setNotes(request.getNotes());
        
        Settlement savedSettlement = settlementRepository.save(settlement);
        
        balanceService.updateBalancesForSettlement(savedSettlement);
        
        logSettlementActivity(group, creator, savedSettlement);
        
        return settlementMapper.toResponseDto(savedSettlement);
    }
    
    // Log settlement in activity log
    private void logSettlementActivity(Group group, User creator, Settlement settlement) {
        GroupActivity activity = new GroupActivity();
        activity.setGroup(group);
        activity.setUser(creator);
        activity.setActivityType("SETTLEMENT_MADE");
        activity.setSettlement(settlement);
        activity.setAmount(settlement.getAmount());
        activity.setDescription(String.format("%s recorded payment of $%.2f from %s to %s",
                creator.getName(),
                settlement.getAmount(),
                settlement.getFromUser().getName(),
                settlement.getToUser().getName()));
        activity.setActivityTime(LocalDateTime.now());
        
        groupActivityRepository.save(activity);
    }

    // Get all settlements in group for user
    public List<SettlementResponseDto> getUserSettlementsInGroup(UUID groupId, String requesterEmail) {
    
        User user = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        
        if (!groupMembershipRepository.existsByGroupAndUser_UserId(group, user.getUserId())) {
            throw new RuntimeException("User is not a member of this group");
        }
        
        List<Settlement> settlements = settlementRepository.findByGroupAndUser(group, user);
        
        return settlements.stream()
                .map(settlementMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    // Get all settlements in group
    public List<SettlementResponseDto> getAllGroupSettlements(UUID groupId, String requesterEmail) {
        
        User user = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        
        if (!groupMembershipRepository.existsByGroupAndUser_UserId(group, user.getUserId())) {
            throw new RuntimeException("User is not a member of this group");
        }
        
        List<Settlement> settlements = settlementRepository.findByGroupOrderBySettlementDateDesc(group);
        
        return settlements.stream()
                .map(settlementMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    // Get a single settlement by ID
    public SettlementResponseDto getSettlementById(UUID settlementId, String requesterEmail) {
        
        User user = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new ResourceNotFoundException("Settlement not found"));
        
        if (!groupMembershipRepository.existsByGroupAndUser_UserId(settlement.getGroup(), user.getUserId())) {
            throw new RuntimeException("User does not have access to this settlement");
        }
        
        return settlementMapper.toResponseDto(settlement);
    }
}