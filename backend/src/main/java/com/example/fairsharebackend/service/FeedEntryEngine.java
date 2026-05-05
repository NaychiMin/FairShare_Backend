package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.*;
import com.example.fairsharebackend.entity.event.ExpenseEvent;
import org.springframework.context.event.EventListener;

public interface FeedEntryEngine {
    void handleExpenseCreated(ExpenseEvent event);
    void handleGroupBalance(GroupFullySettledEvent event);
    void handleGroupUpdated(GroupUpdatedEvent group);
    void handleBadgeCreated(UserBadge event);
}
