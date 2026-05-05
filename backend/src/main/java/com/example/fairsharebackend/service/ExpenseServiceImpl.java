package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.dto.request.ExpenseCreateRequestDto;
import com.example.fairsharebackend.entity.dto.response.ExpenseResponseDto;
import com.example.fairsharebackend.entity.*;
import com.example.fairsharebackend.entity.event.ExpenseEvent;
import com.example.fairsharebackend.exception.ResourceNotFoundException;
import com.example.fairsharebackend.factory.SplitStrategyFactory;
import com.example.fairsharebackend.mapper.ExpenseMapper;
import com.example.fairsharebackend.repository.*;
import com.example.fairsharebackend.strategy.SplitStrategy;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMembershipRepository groupMembershipRepository;
    private final GroupActivityRepository groupActivityRepository;
    private final SplitStrategyFactory strategyFactory;
    private final ExpenseMapper expenseMapper;
    private final BalanceService balanceService;
    private final BadgeEngine badgeEngine;

    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;

    public static final String GROUP_NOT_FOUND = "Group not found";

    public ExpenseServiceImpl(
            ExpenseRepository expenseRepository,
            ExpenseSplitRepository expenseSplitRepository,
            GroupRepository groupRepository,
            UserRepository userRepository,
            GroupMembershipRepository groupMembershipRepository,
            GroupActivityRepository groupActivityRepository,
            SplitStrategyFactory strategyFactory,
            ExpenseMapper expenseMapper,
            BalanceService balanceService, BadgeEngine badgeEngine1, NotificationService notificationService1, ApplicationEventPublisher eventPublisher1) {

        this.expenseRepository = expenseRepository;
        this.expenseSplitRepository = expenseSplitRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.groupMembershipRepository = groupMembershipRepository;
        this.groupActivityRepository = groupActivityRepository;
        this.strategyFactory = strategyFactory;
        this.expenseMapper = expenseMapper;
        this.balanceService = balanceService;
        this.badgeEngine = badgeEngine1;
        this.notificationService = notificationService1;
        this.eventPublisher = eventPublisher1;
    }

    @Override
    @Transactional
    public ExpenseResponseDto createExpense(ExpenseCreateRequestDto request, String requesterEmail) {

        User creator = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Creator not found with email: " + requesterEmail));

        System.out.println("===== CREATOR DEBUG =====");
        System.out.println("Creator ID: " + creator.getUserId());
        System.out.println("Creator email: " + creator.getEmail());
        System.out.println("Creator name: " + creator.getName());

        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException(GROUP_NOT_FOUND));

        boolean isCreatorMember = groupMembershipRepository.existsByGroupAndUser_UserId(group, creator.getUserId());
        if (!isCreatorMember) {
            throw new RuntimeException("Creator is not a member of this group");
        }

        User paidBy = userRepository.findById(request.getPaidByUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Selected payer not found"));

        boolean isPayerMember = groupMembershipRepository.existsByGroupAndUser_UserId(group, paidBy.getUserId());
        if (!isPayerMember) {
            throw new RuntimeException("Selected payer is not a member of this group");
        }

        List<User> participants = userRepository.findAllById(request.getParticipantUserIds());
        if (participants.size() != request.getParticipantUserIds().size()) {
            throw new RuntimeException("Some participants not found");
        }

        for (User participant : participants) {
            if (!groupMembershipRepository.existsByGroupAndUser_UserId(group, participant.getUserId())) {
                throw new RuntimeException("User " + participant.getUserId() + " is not a member of this group");
            }
        }

        Expense expense = new Expense();
        expense.setGroup(group);
        expense.setPaidBy(paidBy);
        expense.setCreatedBy(creator);
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setNotes(request.getNotes());
        expense.setSplitStrategy(request.getSplitStrategy());
        expense.setIsSettled(false);
        expense.setExpenseDate(request.getExpenseDate());
        expense.setCreatedAt(LocalDateTime.now());
        expense.setUpdatedAt(LocalDateTime.now());

        Expense savedExpense = expenseRepository.save(expense);

        SplitStrategy strategy = strategyFactory.getStrategy(request.getSplitStrategy());
        List<ExpenseSplit> splits = strategy.calculateSplits(savedExpense, participants);

        for (ExpenseSplit split : splits) {
            if (split.getUser().getUserId().equals(paidBy.getUserId())) {
                split.setSettledAmount(split.getShareAmount());
                split.setIsSettled(true);
            }
        }

        expenseSplitRepository.saveAll(splits);
        savedExpense.setExpenseSplits(splits);
        balanceService.updateBalancesForNewExpense(savedExpense, false);

        updateExpenseSettlementStatus(savedExpense);

        logActivity(group, creator, savedExpense);

        eventPublisher.publishEvent(new ExpenseEvent(savedExpense));

        notifyDebtors(savedExpense, splits, paidBy, group);

        return expenseMapper.toExpenseResponseDto(savedExpense);
    }

    @Override
    @Transactional
    public ExpenseResponseDto updateExpense(ExpenseCreateRequestDto request, String requesterEmail) {

        User creator = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Creator not found with email: " + requesterEmail));

        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException(GROUP_NOT_FOUND));

        // Validate Creator Membership
        if (!groupMembershipRepository.existsByGroupAndUser_UserId(group, creator.getUserId())) {
            throw new RuntimeException("Creator is not a member of this group");
        }

        User paidBy = userRepository.findById(request.getPaidByUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Selected payer not found"));

        // Validate Payer Membership
        if (!groupMembershipRepository.existsByGroupAndUser_UserId(group, paidBy.getUserId())) {
            throw new RuntimeException("Selected payer is not a member of this group");
        }

        List<User> participants = userRepository.findAllById(request.getParticipantUserIds());
        if (participants.size() != request.getParticipantUserIds().size()) {
            throw new RuntimeException("Some participants not found");
        }

        // Validate Participant Memberships
        for (User participant : participants) {
            if (!groupMembershipRepository.existsByGroupAndUser_UserId(group, participant.getUserId())) {
                throw new RuntimeException("User " + participant.getUserId() + " is not a member of this group");
            }
        }

        // Load and Update the Expense Entity
        Expense expense = expenseRepository.findById(request.getExpenseId())
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        expense.setGroup(group);
        expense.setPaidBy(paidBy);
        expense.setCreatedBy(creator);
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setNotes(request.getNotes());
        expense.setSplitStrategy(request.getSplitStrategy());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setUpdatedAt(LocalDateTime.now());

        // Strategy Calculation
        SplitStrategy strategy = strategyFactory.getStrategy(request.getSplitStrategy());
        List<ExpenseSplit> newCalculatedSplits = strategy.calculateSplits(expense, participants);

        // Fetch current splits from DB
        List<ExpenseSplit> origSplits = expenseSplitRepository.findByExpense(expense);
        Map<UUID, ExpenseSplit> origSplitMap = origSplits.stream()
                .collect(Collectors.toMap(s -> s.getUser().getUserId(), s -> s));

        // 1. Identify splits to remove (users no longer in the list)
        Set<UUID> newUserIds = participants.stream().map(User::getUserId).collect(Collectors.toSet());
        List<ExpenseSplit> toRemove = origSplits.stream()
                .filter(s -> !newUserIds.contains(s.getUser().getUserId()))
                .collect(Collectors.toList());

        expenseSplitRepository.deleteAll(toRemove);
        origSplits.removeAll(toRemove);

        // 2. Sync Existing and Add New
        for (ExpenseSplit calculatedSplit : newCalculatedSplits) {
            ExpenseSplit existing = origSplitMap.get(calculatedSplit.getUser().getUserId());
            if (existing != null) {
                existing.setShareAmount(calculatedSplit.getShareAmount());
                syncSettlement(existing, paidBy);
            } else {
                // New participant: link to expense and sync
                calculatedSplit.setExpense(expense);
                syncSettlement(calculatedSplit, paidBy);
                origSplits.add(calculatedSplit);
            }
        }

        // 3. Save all changes
        expenseSplitRepository.saveAll(origSplits);
        Expense savedExpense = expenseRepository.save(expense);

        // 4. Update Balances & Expense Status
        // Note: ensure balanceService handles the "delta" or resets properly
        balanceService.updateBalancesForNewExpense(savedExpense, true);
        updateExpenseSettlementStatus(savedExpense);

        logActivity(group, creator, savedExpense);

        return expenseMapper.toExpenseResponseDto(savedExpense);
    }

    private void syncSettlement(ExpenseSplit split, User paidBy) {
        if (split.getUser().getUserId().equals(paidBy.getUserId())) {
            split.setSettledAmount(split.getShareAmount());
            split.setIsSettled(true);
        } else {
            // Crucial: reset for others if they haven't paid back yet
            split.setSettledAmount(BigDecimal.valueOf(0.0));
            split.setIsSettled(false);
        }
    }


    @Override
    public ExpenseResponseDto getExpenseDetails(UUID expenseId, String requesterEmail) {

        User user = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + requesterEmail));

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        boolean isMember = groupMembershipRepository.existsByGroupAndUser_UserId(
                expense.getGroup(), user.getUserId());
        if (!isMember) {
            throw new RuntimeException("User does not have access to this expense");
        }

        return expenseMapper.toExpenseResponseDto(expense);
    }

    @Override
    public String deleteExpense(UUID expenseId, String requesterEmail) {
        Expense exp = expenseRepository.findById(expenseId)
            .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + expenseId));
        exp.setDeleteInd(true);
        expenseRepository.save(exp);
        return "done";
    }

    @Override
    public List<ExpenseResponseDto> getGroupExpenses(UUID groupId, String requesterEmail) {

        User user = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + requesterEmail));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException(GROUP_NOT_FOUND));

        boolean isMember = groupMembershipRepository.existsByGroupAndUser_UserId(group, user.getUserId());
        if (!isMember) {
            throw new RuntimeException("User is not a member of this group");
        }

        List<Expense> expenses = expenseRepository.findActiveExpensesByGroupId(groupId);
        
        return expenses.stream()
                .map(expenseMapper::toExpenseResponseDto)
                .collect(Collectors.toList());
    }

    private void logActivity(Group group, User creator, Expense expense) {
        GroupActivity activity = new GroupActivity();
        activity.setGroup(group);
        activity.setUser(creator);
        activity.setActivityType("EXPENSE_ADDED");
        activity.setExpense(expense);
        activity.setAmount(expense.getAmount());

        String creatorName = creator.getName();
        String payerName = expense.getPaidBy().getName();
        String description = expense.getDescription();

        activity.setDescription(String.format("%s added expense: %s paid $%.2f - %s",
                creatorName,
                payerName,
                expense.getAmount(),
                description));

        activity.setActivityTime(LocalDateTime.now());

        groupActivityRepository.save(activity);
    }

    private void updateExpenseSettlementStatus(Expense expense) {
        List<ExpenseSplit> splits = expenseSplitRepository.findByExpense(expense);

        boolean allSettled = splits.stream()
                .allMatch(ExpenseSplit::getIsSettled);

        if (expense.getIsSettled() != allSettled) {
            expense.setIsSettled(allSettled);
            expense.setUpdatedAt(LocalDateTime.now());
            expenseRepository.save(expense);
        }
    }

    private void notifyDebtors(Expense expense, List<ExpenseSplit> splits, User paidBy, Group group) {
        for (ExpenseSplit split : splits) {
            User debtor = split.getUser();

            if (debtor == null || debtor.getUserId().equals(paidBy.getUserId())) {
                continue;
            }

            BigDecimal shareAmount = split.getShareAmount();
            if (shareAmount == null || shareAmount.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            notificationService.notifyUser(
                    debtor,
                    paidBy,
                    group,
                    "AMOUNT_OWED",
                    "You owe " + paidBy.getName() + " $" + shareAmount + " in " + group.getGroupName(),
                    expense.getExpenseId()
            );
        }
    }
}