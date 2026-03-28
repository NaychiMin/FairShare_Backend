package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.dto.request.ExpenseCreateRequestDto;
import com.example.fairsharebackend.entity.dto.response.ExpenseResponseDto;
import com.example.fairsharebackend.entity.*;
import com.example.fairsharebackend.exception.ResourceNotFoundException;
import com.example.fairsharebackend.factory.SplitStrategyFactory;
import com.example.fairsharebackend.mapper.ExpenseMapper;
import com.example.fairsharebackend.repository.*;
import com.example.fairsharebackend.strategy.SplitStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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

    public ExpenseServiceImpl(
            ExpenseRepository expenseRepository,
            ExpenseSplitRepository expenseSplitRepository,
            GroupRepository groupRepository,
            UserRepository userRepository,
            GroupMembershipRepository groupMembershipRepository,
            GroupActivityRepository groupActivityRepository,
            SplitStrategyFactory strategyFactory,
            ExpenseMapper expenseMapper) {
        this.expenseRepository = expenseRepository;
        this.expenseSplitRepository = expenseSplitRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.groupMembershipRepository = groupMembershipRepository;
        this.groupActivityRepository = groupActivityRepository;
        this.strategyFactory = strategyFactory;
        this.expenseMapper = expenseMapper;
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
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

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
        expense.setPaidBy(paidBy);           // Selected payer (from request)
        expense.setCreatedBy(creator);        // Creator (from email)
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

        updateExpenseSettlementStatus(savedExpense);

        logActivity(group, creator, savedExpense);

        return expenseMapper.toExpenseResponseDto(savedExpense);
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
        Expense exp = expenseRepository.findById(expenseId).get();
        exp.setDeleteInd(true);
        expenseRepository.save(exp);
        return "done";
    }
    
    @Override
    public List<ExpenseResponseDto> getGroupExpenses(UUID groupId, String requesterEmail) {
    
        User user = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + requesterEmail));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        
        boolean isMember = groupMembershipRepository.existsByGroupAndUser_UserId(group, user.getUserId());
        if (!isMember) {
            throw new RuntimeException("User is not a member of this group");
        }

        List<Expense> expenses = expenseRepository.findByGroup_GroupIdOrderByExpenseDateDesc(groupId);
        
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
        
        // Safe name handling for debug
        // String creatorName = creator != null && creator.getName() != null ? creator.getName() : "Someone";
        // String payerName = expense.getPaidBy() != null && expense.getPaidBy().getName() != null 
        //         ? expense.getPaidBy().getName() : "someone";
        // String description = expense.getDescription() != null ? expense.getDescription() : "";

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
}