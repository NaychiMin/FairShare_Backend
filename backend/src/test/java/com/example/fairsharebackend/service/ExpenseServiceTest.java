package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.dto.request.ExpenseCreateRequestDto;
import com.example.fairsharebackend.entity.dto.response.ExpenseResponseDto;
import com.example.fairsharebackend.entity.*;
import com.example.fairsharebackend.exception.ResourceNotFoundException;
import com.example.fairsharebackend.factory.SplitStrategyFactory;
import com.example.fairsharebackend.mapper.ExpenseMapper;
import com.example.fairsharebackend.repository.*;
import com.example.fairsharebackend.strategy.SplitStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExpenseSplitRepository expenseSplitRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupMembershipRepository groupMembershipRepository;

    @Mock
    private GroupActivityRepository groupActivityRepository;

    @Mock
    private SplitStrategyFactory strategyFactory;

    @Mock
    private SplitStrategy splitStrategy;

    @Mock
    private ExpenseMapper expenseMapper;

    @Mock
    private BalanceService balanceService;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    @Captor
    private ArgumentCaptor<Expense> expenseCaptor;

    @Captor
    private ArgumentCaptor<List<ExpenseSplit>> splitsCaptor;

    private User creator;
    private User payer;
    private User participant1;
    private User participant2;
    private Group group;
    private Expense expense;
    private ExpenseCreateRequestDto createRequest;
    private ExpenseResponseDto expectedResponse;

    @BeforeEach
    void setUp() {
        creator = new User();
        creator.setUserId(UUID.randomUUID());
        creator.setEmail("creator@example.com");
        creator.setName("Creator");

        payer = new User();
        payer.setUserId(UUID.randomUUID());
        payer.setEmail("payer@example.com");
        payer.setName("Payer");

        participant1 = new User();
        participant1.setUserId(UUID.randomUUID());
        participant1.setEmail("participant1@example.com");
        participant1.setName("Participant 1");

        participant2 = new User();
        participant2.setUserId(UUID.randomUUID());
        participant2.setEmail("participant2@example.com");
        participant2.setName("Participant 2");

        group = new Group();
        group.setGroupId(UUID.randomUUID());
        group.setGroupName("Test Group");

        expense = new Expense();
        expense.setExpenseId(UUID.randomUUID());
        expense.setGroup(group);
        expense.setPaidBy(payer);
        expense.setCreatedBy(creator);
        expense.setAmount(new BigDecimal("100.00"));
        expense.setDescription("Test Expense");

        createRequest = new ExpenseCreateRequestDto();
        createRequest.setGroupId(group.getGroupId());
        createRequest.setPaidByUserId(payer.getUserId());
        createRequest.setAmount(new BigDecimal("100.00"));
        createRequest.setDescription("Test Expense");
        createRequest.setNotes("Test Notes");
        createRequest.setExpenseDate(LocalDateTime.now());
        createRequest.setSplitStrategy("EQUAL");
        createRequest.setParticipantUserIds(List.of(
            creator.getUserId(), payer.getUserId(), participant1.getUserId()
        ));

        expectedResponse = new ExpenseResponseDto();
    }

    // ========== CREATE EXPENSE TESTS ==========

    @Test
    @DisplayName("Successfully create expense with valid data")
    void shouldCreateExpenseSuccessfully() {
        // ARRANGE
        String requesterEmail = creator.getEmail();
        
        when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.of(creator));
        when(groupRepository.findById(createRequest.getGroupId())).thenReturn(Optional.of(group));
        when(groupMembershipRepository.existsByGroupAndUser_UserId(group, creator.getUserId()))
                .thenReturn(true);
        when(userRepository.findById(payer.getUserId())).thenReturn(Optional.of(payer));
        when(groupMembershipRepository.existsByGroupAndUser_UserId(group, payer.getUserId()))
                .thenReturn(true);
        
        List<User> participants = List.of(creator, payer, participant1);
        when(userRepository.findAllById(createRequest.getParticipantUserIds()))
                .thenReturn(participants);
        
        for (User participant : participants) {
            when(groupMembershipRepository.existsByGroupAndUser_UserId(group, participant.getUserId()))
                    .thenReturn(true);
        }
        
        when(strategyFactory.getStrategy("EQUAL")).thenReturn(splitStrategy);
        
        List<ExpenseSplit> mockSplits = new ArrayList<>();
        ExpenseSplit split1 = new ExpenseSplit();
        split1.setUser(creator);
        split1.setShareAmount(new BigDecimal("33.34"));
        ExpenseSplit split2 = new ExpenseSplit();
        split2.setUser(payer);
        split2.setShareAmount(new BigDecimal("33.33"));
        ExpenseSplit split3 = new ExpenseSplit();
        split3.setUser(participant1);
        split3.setShareAmount(new BigDecimal("33.33"));
        mockSplits.addAll(List.of(split1, split2, split3));
        
        when(splitStrategy.calculateSplits(any(Expense.class), eq(participants)))
                .thenReturn(mockSplits);
        
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
        when(expenseMapper.toExpenseResponseDto(any(Expense.class))).thenReturn(expectedResponse);

        // ACT
        ExpenseResponseDto result = expenseService.createExpense(createRequest, requesterEmail);

        // ASSERT
        assertThat(result).isEqualTo(expectedResponse);
        
        verify(expenseRepository, times(2)).save(expenseCaptor.capture());
        Expense savedExpense = expenseCaptor.getValue();
        assertThat(savedExpense.getGroup()).isEqualTo(group);
        assertThat(savedExpense.getPaidBy()).isEqualTo(payer);
        assertThat(savedExpense.getCreatedBy()).isEqualTo(creator);
        assertThat(savedExpense.getAmount()).isEqualTo(new BigDecimal("100.00"));
        
        verify(expenseSplitRepository, times(1)).saveAll(any());
        verify(groupActivityRepository, times(1)).save(any(GroupActivity.class));
    }

    @Test
    @DisplayName("Fail when user not in group")
    void shouldFailWhenCreatorNotInGroup() {
        // ARRANGE
        String requesterEmail = creator.getEmail();
        
        when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.of(creator));
        when(groupRepository.findById(createRequest.getGroupId())).thenReturn(Optional.of(group));
        when(groupMembershipRepository.existsByGroupAndUser_UserId(group, creator.getUserId()))
                .thenReturn(false);  // Not a member!

        // ACT & ASSERT
        assertThatThrownBy(() -> expenseService.createExpense(createRequest, requesterEmail))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not a member");
        
        verify(expenseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Fail when group doesn't exist")
    void shouldFailWhenGroupNotFound() {
        // ARRANGE
        String requesterEmail = creator.getEmail();
        
        when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.of(creator));
        when(groupRepository.findById(createRequest.getGroupId())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> expenseService.createExpense(createRequest, requesterEmail))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Group not found");
        
        verify(expenseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Fail when payer not in group")
    void shouldFailWhenPayerNotInGroup() {
        // ARRANGE
        String requesterEmail = creator.getEmail();
        
        when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.of(creator));
        when(groupRepository.findById(createRequest.getGroupId())).thenReturn(Optional.of(group));
        when(groupMembershipRepository.existsByGroupAndUser_UserId(group, creator.getUserId()))
                .thenReturn(true);
        when(userRepository.findById(payer.getUserId())).thenReturn(Optional.of(payer));
        when(groupMembershipRepository.existsByGroupAndUser_UserId(group, payer.getUserId()))
                .thenReturn(false);  // Payer not in group!

        // ACT & ASSERT
        assertThatThrownBy(() -> expenseService.createExpense(createRequest, requesterEmail))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not a member");
        
        verify(expenseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Fail when participant not in group")
    void shouldFailWhenParticipantNotInGroup() {
        // ARRANGE
        String requesterEmail = creator.getEmail();
        
        when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.of(creator));
        when(groupRepository.findById(createRequest.getGroupId())).thenReturn(Optional.of(group));
        when(groupMembershipRepository.existsByGroupAndUser_UserId(group, creator.getUserId()))
                .thenReturn(true);
        when(userRepository.findById(payer.getUserId())).thenReturn(Optional.of(payer));
        when(groupMembershipRepository.existsByGroupAndUser_UserId(group, payer.getUserId()))
                .thenReturn(true);
        
        List<User> participants = List.of(creator, payer, participant1);
        when(userRepository.findAllById(createRequest.getParticipantUserIds()))
                .thenReturn(participants);
        
        // Make participant1 not in group
        when(groupMembershipRepository.existsByGroupAndUser_UserId(group, creator.getUserId()))
                .thenReturn(true);
        when(groupMembershipRepository.existsByGroupAndUser_UserId(group, payer.getUserId()))
                .thenReturn(true);
        when(groupMembershipRepository.existsByGroupAndUser_UserId(group, participant1.getUserId()))
                .thenReturn(false);  // Participant not in group!

        // ACT & ASSERT
        assertThatThrownBy(() -> expenseService.createExpense(createRequest, requesterEmail))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not a member");
        
        verify(expenseRepository, never()).save(any());
    }

    // ========== GET EXPENSE DETAILS TESTS ==========

    @Test
    @DisplayName("Return expense for valid ID")
    void shouldReturnExpenseForValidId() {
        // ARRANGE
        UUID expenseId = expense.getExpenseId();
        String requesterEmail = creator.getEmail();
        
        when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.of(creator));
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        when(groupMembershipRepository.existsByGroupAndUser_UserId(group, creator.getUserId()))
                .thenReturn(true);
        when(expenseMapper.toExpenseResponseDto(expense)).thenReturn(expectedResponse);

        // ACT
        ExpenseResponseDto result = expenseService.getExpenseDetails(expenseId, requesterEmail);

        // ASSERT
        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Fail when user not in group")
    void shouldFailWhenUserNotInGroupForExpenseDetails() {
        // ARRANGE
        UUID expenseId = expense.getExpenseId();
        String requesterEmail = creator.getEmail();
        
        when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.of(creator));
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        when(groupMembershipRepository.existsByGroupAndUser_UserId(group, creator.getUserId()))
                .thenReturn(false);  // User not in group!

        // ACT & ASSERT
        assertThatThrownBy(() -> expenseService.getExpenseDetails(expenseId, requesterEmail))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("does not have access");
    }

    @Test
    @DisplayName("Fail when expense doesn't exist")
    void shouldFailWhenExpenseNotFound() {
        // ARRANGE
        UUID expenseId = UUID.randomUUID();
        String requesterEmail = creator.getEmail();
        
        when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.of(creator));
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> expenseService.getExpenseDetails(expenseId, requesterEmail))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Expense not found");
    }

    // ========== GET GROUP EXPENSES TESTS ==========

    @Test
    @DisplayName("Return empty list when no expenses")
    void shouldReturnEmptyListWhenNoExpenses() {
        // ARRANGE
        UUID groupId = group.getGroupId();
        String requesterEmail = creator.getEmail();
        
        when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.of(creator));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupMembershipRepository.existsByGroupAndUser_UserId(group, creator.getUserId()))
                .thenReturn(true);
        when(expenseRepository.findByGroup_GroupIdOrderByExpenseDateDesc(groupId))
                .thenReturn(Collections.emptyList());

        // ACT
        List<ExpenseResponseDto> results = expenseService.getGroupExpenses(groupId, requesterEmail);

        // ASSERT
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("Return all expenses for group")
    void shouldReturnAllExpensesForGroup() {
        // ARRANGE
        UUID groupId = group.getGroupId();
        String requesterEmail = creator.getEmail();
        
        Expense expense2 = new Expense();
        expense2.setExpenseId(UUID.randomUUID());
        
        List<Expense> expenses = List.of(expense, expense2);
        List<ExpenseResponseDto> expectedDtos = List.of(new ExpenseResponseDto(), new ExpenseResponseDto());
        
        when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.of(creator));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupMembershipRepository.existsByGroupAndUser_UserId(group, creator.getUserId()))
                .thenReturn(true);
        when(expenseRepository.findByGroup_GroupIdOrderByExpenseDateDesc(groupId))
                .thenReturn(expenses);
        when(expenseMapper.toExpenseResponseDto(expense)).thenReturn(expectedDtos.get(0));
        when(expenseMapper.toExpenseResponseDto(expense2)).thenReturn(expectedDtos.get(1));

        // ACT
        List<ExpenseResponseDto> results = expenseService.getGroupExpenses(groupId, requesterEmail);

        // ASSERT
        assertThat(results).hasSize(2);
        assertThat(results).containsExactlyElementsOf(expectedDtos);
    }

    @Test
    @DisplayName("Fail when user not in group for group expenses")
    void shouldFailWhenUserNotInGroupForGroupExpenses() {
        // ARRANGE
        UUID groupId = group.getGroupId();
        String requesterEmail = creator.getEmail();
        
        when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.of(creator));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupMembershipRepository.existsByGroupAndUser_UserId(group, creator.getUserId()))
                .thenReturn(false);  // User not in group!

        // ACT & ASSERT
        assertThatThrownBy(() -> expenseService.getGroupExpenses(groupId, requesterEmail))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not a member");
    }
}