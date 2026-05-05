package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.*;
import com.example.fairsharebackend.entity.event.ExpenseEvent;
import com.example.fairsharebackend.repository.*;
import com.example.fairsharebackend.constant.GroupField;
import com.example.fairsharebackend.entity.GroupFullySettledEvent;
import com.example.fairsharebackend.entity.GroupUpdatedEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedEntryEngineImplTest {

    @Mock
    private FeedEntryRepository repository;

    @InjectMocks
    private FeedEntryEngineImpl engine;

    private Expense expense;
    private Group group;

    @BeforeEach
    void setUp() {
        group = new Group();

        expense = new Expense();
        expense.setGroup(group);
        expense.setDescription("Dinner");
        expense.setAmount(BigDecimal.valueOf(100));
        expense.setPaidBy(new User());
    }

    @Test
    @DisplayName("Save feed entry when expense is created")
    void shouldSaveFeedEntry_whenExpenseCreated() {

        engine.handleExpenseCreated(new ExpenseEvent(expense));

        verify(repository).save(any(FeedEntry.class));
    }

    @Test
    @DisplayName("Save feed entry when group is fully settled")
    void shouldSaveEntry_whenGroupFullySettled() {

        GroupFullySettledEvent event = new GroupFullySettledEvent(group);

        engine.handleGroupBalance(event);

        verify(repository).save(any(FeedEntry.class));
    }

    @Test
    @DisplayName("Do not save when group update has no change")
    void shouldSkipSave_whenGroupFieldUnchanged() {

        GroupUpdatedEvent event = new GroupUpdatedEvent();
        event.setGroup(group);
        event.setGroupField(GroupField.GROUP_NAME);
        event.setOldValue("same");
        event.setNewValue("same");

        engine.handleGroupUpdated(event);

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Save when group field is updated")
    void shouldSave_whenGroupFieldChanged() {

        GroupUpdatedEvent event = new GroupUpdatedEvent();
        event.setGroup(group);
        event.setGroupField(GroupField.GROUP_NAME);
        event.setOldValue("old");
        event.setNewValue("new");

        engine.handleGroupUpdated(event);

        verify(repository).save(any(FeedEntry.class));
    }
}