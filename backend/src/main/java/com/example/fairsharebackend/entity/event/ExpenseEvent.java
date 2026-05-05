package com.example.fairsharebackend.entity.event;

import com.example.fairsharebackend.entity.Expense;

public class ExpenseEvent {
    private Expense expense;

    public ExpenseEvent() {
    }

    public ExpenseEvent(Expense expense) {
        this.expense = expense;
    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }
}
