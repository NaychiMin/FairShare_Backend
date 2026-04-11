package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.*;
import com.example.fairsharebackend.repository.PairwiseBalanceRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock
    private PairwiseBalanceRepository balanceRepository;

    @InjectMocks
    private BalanceService balanceService;

    @Captor
    private ArgumentCaptor<PairwiseBalance> balanceCaptor;

    private Group group;
    private User alice;
    private User bob;
    private User charlie;
    private Expense expense;
    private ExpenseSplit splitBob;
    private ExpenseSplit splitCharlie;

    @BeforeEach
    void setUp() {
        group = new Group();
        group.setGroupId(UUID.randomUUID());
        group.setGroupName("Test Group");

        alice = new User();
        alice.setUserId(UUID.randomUUID());
        alice.setName("Alice");
        alice.setEmail("alice@example.com");

        bob = new User();
        bob.setUserId(UUID.randomUUID());
        bob.setName("Bob");
        bob.setEmail("bob@example.com");

        charlie = new User();
        charlie.setUserId(UUID.randomUUID());
        charlie.setName("Charlie");
        charlie.setEmail("charlie@example.com");

        expense = new Expense();
        expense.setExpenseId(UUID.randomUUID());
        expense.setGroup(group);
        expense.setPaidBy(alice);
        expense.setAmount(new BigDecimal("50.00"));

        splitBob = new ExpenseSplit();
        splitBob.setUser(bob);
        splitBob.setShareAmount(new BigDecimal("16.67"));
        splitBob.setExpense(expense);

        splitCharlie = new ExpenseSplit();
        splitCharlie.setUser(charlie);
        splitCharlie.setShareAmount(new BigDecimal("16.67"));
        splitCharlie.setExpense(expense);
    }

    @Test
    @DisplayName("Should create new balance when no existing balance")
    void shouldCreateNewBalance() {
        // ARRANGE
        when(balanceRepository.findByGroupAndDebtorAndCreditor(group, bob, alice))
            .thenReturn(Optional.empty());
        when(balanceRepository.findByGroupAndDebtorAndCreditor(group, alice, bob))
            .thenReturn(Optional.empty());

        // ACT
        balanceService.updatePairwiseBalance(group, bob, alice, new BigDecimal("16.67"));

        // ASSERT
        verify(balanceRepository).save(balanceCaptor.capture());
        PairwiseBalance saved = balanceCaptor.getValue();
        
        assertThat(saved.getGroup()).isEqualTo(group);
        assertThat(saved.getDebtor()).isEqualTo(bob);
        assertThat(saved.getCreditor()).isEqualTo(alice);
        assertThat(saved.getAmount()).isEqualTo(new BigDecimal("16.67"));
        assertThat(saved.getIsSettled()).isFalse();
    }

    @Test
    @DisplayName("Should update existing balance when debtor already owes creditor")
    void shouldUpdateExistingBalance() {
        // ARRANGE
        PairwiseBalance existingBalance = new PairwiseBalance();
        existingBalance.setGroup(group);
        existingBalance.setDebtor(bob);
        existingBalance.setCreditor(alice);
        existingBalance.setAmount(new BigDecimal("10.00"));
        existingBalance.setLastUpdated(LocalDateTime.now());

        when(balanceRepository.findByGroupAndDebtorAndCreditor(group, bob, alice))
            .thenReturn(Optional.of(existingBalance));

        // ACT
        balanceService.updatePairwiseBalance(group, bob, alice, new BigDecimal("16.67"));

        // ASSERT
        verify(balanceRepository).save(balanceCaptor.capture());
        PairwiseBalance updated = balanceCaptor.getValue();
        
        assertThat(updated.getAmount()).isEqualTo(new BigDecimal("26.67")); // 10 + 16.67
        assertThat(updated.getDebtor()).isEqualTo(bob);
        assertThat(updated.getCreditor()).isEqualTo(alice);
    }

    @Test
    @DisplayName("Should net out when reverse balance exists and new amount is larger")
    void shouldNetWhenReverseExistsAndNewIsLarger() {
        // ARRANGE
        // Alice owes Bob $10 (reverse balance)
        PairwiseBalance reverseBalance = new PairwiseBalance();
        reverseBalance.setGroup(group);
        reverseBalance.setDebtor(alice);
        reverseBalance.setCreditor(bob);
        reverseBalance.setAmount(new BigDecimal("10.00"));

        when(balanceRepository.findByGroupAndDebtorAndCreditor(group, bob, alice))
            .thenReturn(Optional.empty());
        when(balanceRepository.findByGroupAndDebtorAndCreditor(group, alice, bob))
            .thenReturn(Optional.of(reverseBalance));

        // ACT - Bob now owes Alice $16.67 (new amount is larger)
        balanceService.updatePairwiseBalance(group, bob, alice, new BigDecimal("16.67"));

        // ASSERT
        verify(balanceRepository).save(balanceCaptor.capture());
        PairwiseBalance updated = balanceCaptor.getValue();
        
        // Should now be Bob → Alice $6.67 (16.67 - 10)
        assertThat(updated.getDebtor()).isEqualTo(bob);
        assertThat(updated.getCreditor()).isEqualTo(alice);
        assertThat(updated.getAmount()).isEqualTo(new BigDecimal("6.67"));
        assertThat(updated.getIsSettled()).isFalse();
    }

    @Test
    @DisplayName("Should net out when reverse balance exists and new amount is smaller")
    void shouldNetWhenReverseExistsAndNewIsSmaller() {
        // ARRANGE
        // Alice owes Bob $20 (reverse balance)
        PairwiseBalance reverseBalance = new PairwiseBalance();
        reverseBalance.setGroup(group);
        reverseBalance.setDebtor(alice);
        reverseBalance.setCreditor(bob);
        reverseBalance.setAmount(new BigDecimal("20.00"));

        when(balanceRepository.findByGroupAndDebtorAndCreditor(group, bob, alice))
            .thenReturn(Optional.empty());
        when(balanceRepository.findByGroupAndDebtorAndCreditor(group, alice, bob))
            .thenReturn(Optional.of(reverseBalance));

        // ACT - Bob now owes Alice $15 (new amount is smaller)
        balanceService.updatePairwiseBalance(group, bob, alice, new BigDecimal("15.00"));

        // ASSERT
        verify(balanceRepository).save(balanceCaptor.capture());
        PairwiseBalance updated = balanceCaptor.getValue();
        
        // Should still be Alice → Bob $5 (20 - 15)
        assertThat(updated.getDebtor()).isEqualTo(alice);
        assertThat(updated.getCreditor()).isEqualTo(bob);
        assertThat(updated.getAmount()).isEqualTo(new BigDecimal("5.00"));
        assertThat(updated.getIsSettled()).isFalse();
    }

    @Test
    @DisplayName("Should delete balance when amounts exactly cancel")
    void shouldDeleteWhenAmountsCancel() {
        // ARRANGE
        // Alice owes Bob $16.67 (reverse balance)
        PairwiseBalance reverseBalance = new PairwiseBalance();
        reverseBalance.setGroup(group);
        reverseBalance.setDebtor(alice);
        reverseBalance.setCreditor(bob);
        reverseBalance.setAmount(new BigDecimal("16.67"));

        when(balanceRepository.findByGroupAndDebtorAndCreditor(group, bob, alice))
            .thenReturn(Optional.empty());
        when(balanceRepository.findByGroupAndDebtorAndCreditor(group, alice, bob))
            .thenReturn(Optional.of(reverseBalance));

        // ACT - Bob now owes Alice $16.67 (exactly equal)
        balanceService.updatePairwiseBalance(group, bob, alice, new BigDecimal("16.67"));

        // ASSERT
        verify(balanceRepository).delete(reverseBalance);
        verify(balanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update balances for entire expense")
    void shouldUpdateBalancesForNewExpense() {
        // ARRANGE
        expense.setExpenseSplits(List.of(splitBob, splitCharlie));
        
        when(balanceRepository.findByGroupAndDebtorAndCreditor(group, bob, alice))
            .thenReturn(Optional.empty());
        when(balanceRepository.findByGroupAndDebtorAndCreditor(group, alice, bob))
            .thenReturn(Optional.empty());
        when(balanceRepository.findByGroupAndDebtorAndCreditor(group, charlie, alice))
            .thenReturn(Optional.empty());
        when(balanceRepository.findByGroupAndDebtorAndCreditor(group, alice, charlie))
            .thenReturn(Optional.empty());

        // ACT
        balanceService.updateBalancesForNewExpense(expense);

        // ASSERT
        verify(balanceRepository, times(2)).save(balanceCaptor.capture());
        List<PairwiseBalance> savedBalances = balanceCaptor.getAllValues();
        
        assertThat(savedBalances).hasSize(2);
        
        // Check Bob's balance
        PairwiseBalance bobBalance = savedBalances.stream()
            .filter(b -> b.getDebtor().equals(bob))
            .findFirst().get();
        assertThat(bobBalance.getCreditor()).isEqualTo(alice);
        assertThat(bobBalance.getAmount()).isEqualTo(new BigDecimal("16.67"));
        
        // Check Charlie's balance
        PairwiseBalance charlieBalance = savedBalances.stream()
            .filter(b -> b.getDebtor().equals(charlie))
            .findFirst().get();
        assertThat(charlieBalance.getCreditor()).isEqualTo(alice);
        assertThat(charlieBalance.getAmount()).isEqualTo(new BigDecimal("16.67"));
    }

    @Test
    @DisplayName("Should calculate net balance correctly")
    void shouldCalculateNetBalance() {
        // ARRANGE
        when(balanceRepository.sumAmountByGroupAndCreditor(group, alice))
            .thenReturn(new BigDecimal("50.00")); // Alice is owed $50
        when(balanceRepository.sumAmountByGroupAndDebtor(group, alice))
            .thenReturn(new BigDecimal("20.00")); // Alice owes $20

        // ACT
        BigDecimal netBalance = balanceService.getNetBalanceForUserInGroup(group, alice);

        // ASSERT
        assertThat(netBalance).isEqualTo(new BigDecimal("30.00")); // $50 - $20 = $30
    }

    @Test
    @DisplayName("Should skip self-balance when debtor equals creditor")
    void shouldSkipSelfBalance() {
        // ARRANGE
        ExpenseSplit selfSplit = new ExpenseSplit();
        selfSplit.setUser(alice);  // Same as payer
        selfSplit.setShareAmount(new BigDecimal("16.67"));
        expense.setExpenseSplits(List.of(selfSplit));

        // ACT
        balanceService.updateBalancesForNewExpense(expense);

        // ASSERT
        verify(balanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle multiple splits with netting")
    void shouldHandleMultipleSplitsWithNetting() {
        // ARRANGE
        // Setup: Bob already owes Alice $10
        PairwiseBalance existingBalance = new PairwiseBalance();
        existingBalance.setGroup(group);
        existingBalance.setDebtor(bob);
        existingBalance.setCreditor(alice);
        existingBalance.setAmount(new BigDecimal("10.00"));

        when(balanceRepository.findByGroupAndDebtorAndCreditor(group, bob, alice))
            .thenReturn(Optional.of(existingBalance));
        when(balanceRepository.findByGroupAndDebtorAndCreditor(group, charlie, alice))
            .thenReturn(Optional.empty());
        when(balanceRepository.findByGroupAndDebtorAndCreditor(group, alice, charlie))
            .thenReturn(Optional.empty());

        expense.setExpenseSplits(List.of(splitBob, splitCharlie));

        // ACT
        balanceService.updateBalancesForNewExpense(expense);

        // ASSERT
        verify(balanceRepository, times(2)).save(balanceCaptor.capture());
        List<PairwiseBalance> savedBalances = balanceCaptor.getAllValues();
        
        // Bob's balance should be updated to 26.67 (10 + 16.67)
        PairwiseBalance bobBalance = savedBalances.stream()
            .filter(b -> b.getDebtor() != null && b.getDebtor().equals(bob))
            .findFirst().get();
        assertThat(bobBalance.getAmount()).isEqualTo(new BigDecimal("26.67"));
        
        // Charlie's balance should be new at 16.67
        PairwiseBalance charlieBalance = savedBalances.stream()
            .filter(b -> b.getDebtor().equals(charlie))
            .findFirst().get();
        assertThat(charlieBalance.getAmount()).isEqualTo(new BigDecimal("16.67"));
    }
}
