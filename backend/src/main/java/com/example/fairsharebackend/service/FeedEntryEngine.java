package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.*;

public interface FeedEntryEngine {
    void handleExpenseCreated(Expense event);
    void handleSettlementCreated(Settlement event);
    void handleGroupUpdated(GroupUpdatedEvent group);
    void handleBadgeCreated(UserBadge event);
}
