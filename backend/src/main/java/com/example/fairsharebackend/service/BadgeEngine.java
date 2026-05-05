package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.Expense;
import com.example.fairsharebackend.entity.Settlement;
import com.example.fairsharebackend.entity.event.ExpenseEvent;
import com.example.fairsharebackend.entity.event.SettlementEvent;

public interface BadgeEngine {
    void handleExpenseCreated(ExpenseEvent event);
    void handleSettlementCreated(SettlementEvent event);

    void evaluate(Settlement settlement);
    void evaluate(Expense expense);
}
