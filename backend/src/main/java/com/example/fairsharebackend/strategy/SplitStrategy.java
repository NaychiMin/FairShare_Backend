package com.example.fairsharebackend.strategy;

import com.example.fairsharebackend.entity.Expense;
import com.example.fairsharebackend.entity.ExpenseSplit;
import com.example.fairsharebackend.entity.User;
import java.util.List;

public interface SplitStrategy {
    List<ExpenseSplit> calculateSplits(Expense expense, List<User> participants);
    String getStrategyName();
}