package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.*;
import com.example.fairsharebackend.entity.dto.response.GroupBalanceResponseDto;
import com.example.fairsharebackend.repository.PairwiseBalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock
    private PairwiseBalanceRepository balanceRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private BalanceService balanceService;

    private User debtor;
    private User creditor;
    private Group group;

    @BeforeEach
    void setup() {
        debtor = new User();
        debtor.setUserId(UUID.randomUUID());
        debtor.setName("Debtor");

        creditor = new User();
        creditor.setUserId(UUID.randomUUID());
        creditor.setName("Creditor");

        group = new Group();
        group.setGroupId(UUID.randomUUID());
    }

    // =========================
    // updatePairwiseBalance
    // =========================

    @Test
    @DisplayName("Should create new balance when none exists")
    void shouldCreateNewBalance() {
        when(balanceRepository.findByGroupAndDebtorAndCreditor(group, debtor, creditor))
                .thenReturn(Optional.empty());
        when(balanceRepository.findByGroupAndDebtorAndCreditor(group, creditor, debtor))
                .thenReturn(Optional.empty());

        balanceService.updatePairwiseBalance(group, debtor, creditor, new BigDecimal("50"), false);

        verify(balanceRepository).save(any(PairwiseBalance.class));
    }

    @Test
    @DisplayName("Should update existing balance when positive")
    void shouldUpdateExistingBalance() {
        PairwiseBalance existing = new PairwiseBalance();
        existing.setAmount(new BigDecimal("30"));

        when(balanceRepository.findByGroupAndDebtorAndCreditor(group, debtor, creditor))
                .thenReturn(Optional.of(existing));

        balanceService.updatePairwiseBalance(group, debtor, creditor, new BigDecimal("20"), false);

        assertThat(existing.getAmount()).isEqualByComparingTo("50");
        verify(balanceRepository).save(existing);
    }

    @Test
    @DisplayName("Should delete balance when becomes zero")
    void shouldDeleteWhenZero() {
        PairwiseBalance existing = new PairwiseBalance();
        existing.setAmount(new BigDecimal("50"));

        when(balanceRepository.findByGroupAndDebtorAndCreditor(group, debtor, creditor))
                .thenReturn(Optional.of(existing));

        balanceService.updatePairwiseBalance(group, debtor, creditor, new BigDecimal("-50"), false);

        verify(balanceRepository).delete(existing);
        verify(eventPublisher).publishEvent(any(GroupFullySettledEvent.class));
    }

    @Test
    @DisplayName("Should reverse direction when negative")
    void shouldReverseDirection() {
        PairwiseBalance existing = new PairwiseBalance();
        existing.setAmount(new BigDecimal("30"));

        when(balanceRepository.findByGroupAndDebtorAndCreditor(group, debtor, creditor))
                .thenReturn(Optional.of(existing));

        balanceService.updatePairwiseBalance(group, debtor, creditor, new BigDecimal("-50"), false);

        verify(balanceRepository).delete(existing);
        verify(balanceRepository).save(any()); // new reverse balance
    }

    @Test
    @DisplayName("Should net off reverse balance")
    void shouldHandleReverseBalance() {
        PairwiseBalance reverse = new PairwiseBalance();
        reverse.setAmount(new BigDecimal("100"));

        when(balanceRepository.findByGroupAndDebtorAndCreditor(group, debtor, creditor))
                .thenReturn(Optional.empty());
        when(balanceRepository.findByGroupAndDebtorAndCreditor(group, creditor, debtor))
                .thenReturn(Optional.of(reverse));

        balanceService.updatePairwiseBalance(group, debtor, creditor, new BigDecimal("40"), false);

        assertThat(reverse.getAmount()).isEqualByComparingTo("60");
        verify(balanceRepository).save(reverse);
    }

    @Test
    @DisplayName("Should throw when settling non-existent debt with negative amount")
    void shouldThrowWhenInvalidNegative() {
        when(balanceRepository.findByGroupAndDebtorAndCreditor(group, debtor, creditor))
                .thenReturn(Optional.empty());
        when(balanceRepository.findByGroupAndDebtorAndCreditor(group, creditor, debtor))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                balanceService.updatePairwiseBalance(group, debtor, creditor, new BigDecimal("-10"), false)
        ).isInstanceOf(RuntimeException.class);
    }

    // =========================
    // updateBalancesForNewExpense
    // =========================

    @Test
    @DisplayName("Should update balances for expense splits")
    void shouldUpdateBalancesForExpense() {
        Expense expense = new Expense();
        expense.setGroup(group);
        expense.setPaidBy(creditor);

        ExpenseSplit split = new ExpenseSplit();
        split.setUser(debtor);
        split.setShareAmount(new BigDecimal("25"));

        expense.setExpenseSplits(List.of(split));

        balanceService.updateBalancesForNewExpense(expense);

        verify(balanceRepository, times(2))
        .findByGroupAndDebtorAndCreditor(any(), any(), any());
    }

    // =========================
    // getNetBalance
    // =========================

    @Test
    @DisplayName("Should calculate net balance correctly")
    void shouldCalculateNetBalance() {
        when(balanceRepository.sumAmountByGroupAndCreditor(group, creditor))
                .thenReturn(new BigDecimal("100"));
        when(balanceRepository.sumAmountByGroupAndDebtor(group, creditor))
                .thenReturn(new BigDecimal("40"));

        BigDecimal result = balanceService.getNetBalanceForUserInGroup(group, creditor);

        assertThat(result).isEqualByComparingTo("60");
    }

    // =========================
    // getUserBalanceDetails
    // =========================

    @Test
    @DisplayName("Should return balance details")
    void shouldReturnBalanceDetails() {
        PairwiseBalance owesYou = new PairwiseBalance();
        owesYou.setDebtor(debtor);
        owesYou.setAmount(new BigDecimal("50"));

        PairwiseBalance youOwe = new PairwiseBalance();
        youOwe.setCreditor(creditor);
        youOwe.setAmount(new BigDecimal("30"));

        when(balanceRepository.findByGroupAndCreditor(group, creditor))
                .thenReturn(List.of(owesYou));
        when(balanceRepository.findByGroupAndDebtor(group, creditor))
                .thenReturn(List.of(youOwe));

        GroupBalanceResponseDto result =
                balanceService.getUserBalanceDetails(group, creditor);

        assertThat(result.getNetBalance()).isEqualByComparingTo("20");
        assertThat(result.getOwesYou()).hasSize(1);
        assertThat(result.getYouOwe()).hasSize(1);
    }

    // =========================
    // settlement methods
    // =========================

    @Test
    @DisplayName("Should update settlement")
    void shouldUpdateSettlement() {

        Settlement settlement = new Settlement();
        settlement.setFromUser(debtor);
        settlement.setToUser(creditor);
        settlement.setGroup(group);
        settlement.setAmount(new BigDecimal("50"));

        PairwiseBalance existing = new PairwiseBalance();
        existing.setAmount(new BigDecimal("20"));

        when(balanceRepository.findByGroupAndDebtorAndCreditor(group, debtor, creditor))
                .thenReturn(Optional.of(existing));

        when(balanceRepository.findByGroupAndDebtorAndCreditor(group, creditor, debtor))
                .thenReturn(Optional.empty());

        balanceService.updateBalancesForSettlement(settlement);

        verify(balanceRepository, atLeastOnce())
                .findByGroupAndDebtorAndCreditor(any(), any(), any());
    }

    @Test
    @DisplayName("Should reverse settlement")
    void shouldReverseSettlement() {
        Settlement settlement = new Settlement();
        settlement.setFromUser(debtor);
        settlement.setToUser(creditor);
        settlement.setGroup(group);
        settlement.setAmount(new BigDecimal("50"));

        balanceService.reverseSettlement(settlement);

        verify(balanceRepository, times(2))
        .findByGroupAndDebtorAndCreditor(any(), any(), any());
    }

    @Test
    @DisplayName("Should update settlement edit")
    void shouldUpdateSettlementEdit() {

        Settlement settlement = new Settlement();
        settlement.setFromUser(debtor);
        settlement.setToUser(creditor);
        settlement.setGroup(group);

        PairwiseBalance existing = new PairwiseBalance();
        existing.setAmount(new BigDecimal("50"));

        when(balanceRepository.findByGroupAndDebtorAndCreditor(any(), any(), any()))
                .thenReturn(Optional.of(existing));

        balanceService.updateBalancesForSettlementEdit(
                settlement,
                new BigDecimal("30"),
                new BigDecimal("50")
        );

        verify(balanceRepository, atLeastOnce())
                .findByGroupAndDebtorAndCreditor(any(), any(), any());
    }

    // =========================
    // misc
    // =========================

    @Test
    void shouldCheckOutstandingBalances() {
        when(balanceRepository.existsOutstandingBalancesByGroup(group))
                .thenReturn(true);

        boolean result = balanceService.hasOutstandingBalances(group);

        assertThat(result).isTrue();
    }

    @Test
    void shouldDeleteGroupBalances() {
        balanceService.deleteGroupBalances(group);

        verify(balanceRepository).deleteByGroup(group);
    }
}