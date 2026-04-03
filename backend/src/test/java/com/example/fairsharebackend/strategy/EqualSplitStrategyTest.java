package com.example.fairsharebackend.strategy;

import com.example.fairsharebackend.strategy.impl.EqualSplitStrategy;
import com.example.fairsharebackend.entity.Expense;
import com.example.fairsharebackend.entity.ExpenseSplit;
import com.example.fairsharebackend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EqualSplitStrategyTest {

    private EqualSplitStrategy splitStrategy;
    
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        splitStrategy = new EqualSplitStrategy();
        
        user1 = new User();
        user1.setUserId(UUID.randomUUID());
        user1.setName("Alice");
        
        user2 = new User();
        user2.setUserId(UUID.randomUUID());
        user2.setName("Bob");
        
        user3 = new User();
        user3.setUserId(UUID.randomUUID());
        user3.setName("Charlie");
    }

    @Test
    @DisplayName("Split equally among 3 people with proper rounding")
    void shouldSplitEquallyForThreePeople() {
        // ARRANGE
        Expense expense = new Expense();
        expense.setAmount(new BigDecimal("100.00"));
        expense.setPaidBy(user1);  // Payer doesn't affect calculation
        List<User> participants = List.of(user1, user2, user3);

        // ACT
        List<ExpenseSplit> splits = splitStrategy.calculateSplits(expense, participants);

        // ASSERT - checking share amounts
        assertThat(splits).hasSize(3);
        
        // Check total sum equals original amount
        BigDecimal total = splits.stream()
                .map(ExpenseSplit::getShareAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(total).isEqualTo(new BigDecimal("100.00"));
        
        // Check individual amounts (rounding: 33.34 + 33.33 + 33.33 = 100.00)
        assertThat(splits.get(0).getShareAmount()).isEqualTo(new BigDecimal("33.33"));
        assertThat(splits.get(1).getShareAmount()).isEqualTo(new BigDecimal("33.33"));
        assertThat(splits.get(2).getShareAmount()).isEqualTo(new BigDecimal("33.34"));
    }

    @Test
    @DisplayName("Split equally among 2 people (even amount)")
    void shouldSplitEquallyForTwoPeople() {
        // ARRANGE
        Expense expense = new Expense();
        expense.setAmount(new BigDecimal("100.00"));
        expense.setPaidBy(user1);
        List<User> participants = List.of(user1, user2);

        // ACT
        List<ExpenseSplit> splits = splitStrategy.calculateSplits(expense, participants);

        // ASSERT
        assertThat(splits).hasSize(2);
        assertThat(splits.get(0).getShareAmount()).isEqualTo(new BigDecimal("50.00"));
        assertThat(splits.get(1).getShareAmount()).isEqualTo(new BigDecimal("50.00"));
    }

    @Test
    @DisplayName("Split with single person")
    void shouldSplitWithSinglePerson() {
        // ARRANGE
        Expense expense = new Expense();
        expense.setAmount(new BigDecimal("100.00"));
        expense.setPaidBy(user1);
        List<User> participants = List.of(user1);  // Only the payer

        // ACT
        List<ExpenseSplit> splits = splitStrategy.calculateSplits(expense, participants);

        // ASSERT
        assertThat(splits).hasSize(1);
        ExpenseSplit split = splits.get(0);
        assertThat(split.getShareAmount()).isEqualTo(new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("Handle large amounts with decimals")
    void shouldHandleLargeDecimalAmounts() {
        // ARRANGE
        Expense expense = new Expense();
        expense.setAmount(new BigDecimal("1234.56"));
        expense.setPaidBy(user1);
        List<User> participants = List.of(user1, user2, user3);

        // ACT
        List<ExpenseSplit> splits = splitStrategy.calculateSplits(expense, participants);

        // ASSERT
        BigDecimal expectedPerPerson = new BigDecimal("411.52"); // 1234.56 / 3 = 411.52 exactly
        
        BigDecimal total = splits.stream()
                .map(ExpenseSplit::getShareAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        assertThat(total).isEqualTo(new BigDecimal("1234.56"));
        assertThat(splits.get(0).getShareAmount()).isEqualTo(expectedPerPerson);
    }

    @Test
    @DisplayName("Handle rounding with 4 people")
    void shouldHandleRoundingWithFourPeople() {
        // ARRANGE
        User user4 = new User();
        user4.setUserId(UUID.randomUUID());
        user4.setName("David");
        
        Expense expense = new Expense();
        expense.setAmount(new BigDecimal("100.00"));
        expense.setPaidBy(user1);
        List<User> participants = List.of(user1, user2, user3, user4);

        // ACT
        List<ExpenseSplit> splits = splitStrategy.calculateSplits(expense, participants);

        // ASSERT
        // 100/4 = 25.00 exactly, no rounding needed
        assertThat(splits).allMatch(split -> 
            split.getShareAmount().equals(new BigDecimal("25.00")));
    }

    @Test
    @DisplayName("Strategy name should be EQUAL")
    void shouldReturnCorrectStrategyName() {
        assertThat(splitStrategy.getStrategyName()).isEqualTo("EQUAL");
    }
}