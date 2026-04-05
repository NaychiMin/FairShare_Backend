package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.Expense;
import com.example.fairsharebackend.entity.Settlement;

public interface BadgeEngine {
    void handleExpenseCreated(Expense event);
    void handleSettlementCreated(Settlement event);

    void evaluate(Settlement settlement);
    void evaluate(Expense expense);
}
