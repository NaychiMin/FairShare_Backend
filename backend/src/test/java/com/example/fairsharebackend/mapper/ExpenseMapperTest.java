package com.example.fairsharebackend.mapper;

import com.example.fairsharebackend.entity.Expense;
import com.example.fairsharebackend.entity.ExpenseSplit;
import com.example.fairsharebackend.entity.Group;
import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.entity.dto.response.ExpenseResponseDto;
import com.example.fairsharebackend.entity.dto.response.ExpenseSplitResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ExpenseMapperTest {

    private final ExpenseMapper expenseMapper = Mappers.getMapper(ExpenseMapper.class);

    private Group group;
    private User paidBy;
    private User createdBy;
    private User splitUser1;
    private User splitUser2;
    private Expense expense;

    @BeforeEach
    void setUp() {
        group = new Group();
        group.setGroupId(UUID.randomUUID());
        group.setGroupName("Holiday Trip");

        paidBy = new User();
        paidBy.setUserId(UUID.randomUUID());
        paidBy.setName("Alice");
        paidBy.setEmail("alice@example.com");

        createdBy = new User();
        createdBy.setUserId(UUID.randomUUID());
        createdBy.setName("Bob");
        createdBy.setEmail("bob@example.com");

        splitUser1 = new User();
        splitUser1.setUserId(UUID.randomUUID());
        splitUser1.setName("Charlie");
        splitUser1.setEmail("charlie@example.com");

        splitUser2 = new User();
        splitUser2.setUserId(UUID.randomUUID());
        splitUser2.setName("Daisy");
        splitUser2.setEmail("daisy@example.com");

        ExpenseSplit split1 = new ExpenseSplit();
        split1.setUser(splitUser1);
        split1.setShareAmount(new BigDecimal("30.00"));
        split1.setSettledAmount(new BigDecimal("0.00"));
        split1.setIsSettled(false);

        ExpenseSplit split2 = new ExpenseSplit();
        split2.setUser(splitUser2);
        split2.setShareAmount(new BigDecimal("70.00"));
        split2.setSettledAmount(new BigDecimal("70.00"));
        split2.setIsSettled(true);

        expense = new Expense();
        expense.setExpenseId(UUID.randomUUID());
        expense.setGroup(group);
        expense.setPaidBy(paidBy);
        expense.setCreatedBy(createdBy);
        expense.setAmount(new BigDecimal("100.00"));
        expense.setDescription("Dinner");
        expense.setNotes("Team dinner");
        expense.setSplitStrategy("EQUAL");
        expense.setIsSettled(false);
        expense.setExpenseDate(LocalDateTime.of(2026, 3, 14, 12, 0));
        expense.setCreatedAt(LocalDateTime.of(2026, 3, 14, 12, 5));
        expense.setUpdatedAt(LocalDateTime.of(2026, 3, 14, 12, 10));
        expense.setExpenseSplits(List.of(split1, split2));
    }

    @Test
    @DisplayName("Map Expense to ExpenseResponseDto correctly")
    void shouldMapExpenseToExpenseResponseDto() {
        ExpenseResponseDto result = expenseMapper.toExpenseResponseDto(expense);

        assertThat(result).isNotNull();
        assertThat(result.getGroupId()).isEqualTo(group.getGroupId());
        assertThat(result.getGroupName()).isEqualTo("Holiday Trip");

        assertThat(result.getPaidByUserId()).isEqualTo(paidBy.getUserId());
        assertThat(result.getPaidByName()).isEqualTo("Alice");
        assertThat(result.getPaidByEmail()).isEqualTo("alice@example.com");

        assertThat(result.getCreatedByUserId()).isEqualTo(createdBy.getUserId());
        assertThat(result.getCreatedByName()).isEqualTo("Bob");
        assertThat(result.getCreatedByEmail()).isEqualTo("bob@example.com");

        assertThat(result.getAmount()).isEqualByComparingTo("100.00");
        assertThat(result.getDescription()).isEqualTo("Dinner");
        assertThat(result.getNotes()).isEqualTo("Team dinner");
        assertThat(result.getSplitStrategy()).isEqualTo("EQUAL");
        assertThat(result.getIsSettled()).isFalse();
        assertThat(result.getExpenseDate()).isEqualTo(LocalDateTime.of(2026, 3, 14, 12, 0));

        assertThat(result.getSplits()).hasSize(2);

        ExpenseSplitResponseDto firstSplit = result.getSplits().get(0);
        assertThat(firstSplit.getUserId()).isEqualTo(splitUser1.getUserId());
        assertThat(firstSplit.getUserName()).isEqualTo("Charlie");
        assertThat(firstSplit.getUserEmail()).isEqualTo("charlie@example.com");
        assertThat(firstSplit.getShareAmount()).isEqualByComparingTo("30.00");
        assertThat(firstSplit.getSettledAmount()).isEqualByComparingTo("0.00");
        assertThat(firstSplit.getIsSettled()).isFalse();
    }

    @Test
    @DisplayName("Map ExpenseSplit to ExpenseSplitResponseDto correctly")
    void shouldMapExpenseSplitToExpenseSplitResponseDto() {
        ExpenseSplit split = new ExpenseSplit();
        split.setUser(splitUser1);
        split.setShareAmount(new BigDecimal("45.50"));
        split.setSettledAmount(new BigDecimal("10.00"));
        split.setIsSettled(false);

        ExpenseSplitResponseDto result = expenseMapper.toSplitResponse(split);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(splitUser1.getUserId());
        assertThat(result.getUserName()).isEqualTo("Charlie");
        assertThat(result.getUserEmail()).isEqualTo("charlie@example.com");
        assertThat(result.getShareAmount()).isEqualByComparingTo("45.50");
        assertThat(result.getSettledAmount()).isEqualByComparingTo("10.00");
        assertThat(result.getIsSettled()).isFalse();
    }

    @Test
    @DisplayName("Map Expense with empty splits list")
    void shouldMapExpenseWithEmptySplits() {
        expense.setExpenseSplits(List.of());

        ExpenseResponseDto result = expenseMapper.toExpenseResponseDto(expense);

        assertThat(result).isNotNull();
        assertThat(result.getSplits()).isEmpty();
    }
}