package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.Expense;
import com.example.fairsharebackend.entity.Settlement;

public interface BadgeEngine {
    void evaluate(Settlement settlement);
    void evaluate(Expense expense);
}
