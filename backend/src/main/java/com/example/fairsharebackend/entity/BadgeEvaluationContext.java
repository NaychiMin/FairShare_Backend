package com.example.fairsharebackend.entity;

import com.example.fairsharebackend.constant.BadgeType;

import java.util.Map;

public class BadgeEvaluationContext {
    private Group group;
    private BadgeType eventType;
    private Settlement settlement;
    private Expense expense;
    private Map<String, Object> metadata;

    public BadgeEvaluationContext() {
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public BadgeType getEventType() {
        return eventType;
    }

    public void setEventType(BadgeType eventType) {
        this.eventType = eventType;
    }

    public Settlement getSettlement() {
        return settlement;
    }

    public void setSettlement(Settlement settlement) {
        this.settlement = settlement;
    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
