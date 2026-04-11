package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.*;
import org.springframework.context.event.EventListener;

public interface FeedEntryEngine {
    void handleExpenseCreated(Expense event);

    void handleGroupBalance(Group group);

    void handleSettlementCreated(Settlement event);
    void handleGroupUpdated(GroupUpdatedEvent group);
    void handleBadgeCreated(UserBadge event);
}
