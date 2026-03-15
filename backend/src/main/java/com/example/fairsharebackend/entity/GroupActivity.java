package com.example.fairsharebackend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_group_activity")
public class GroupActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID activityId;

    @ManyToOne
    @JoinColumn(name = "groupId", nullable = false)
    private Group group;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user; // Who performed the action

    @Column(nullable = false)
    private String activityType; // "EXPENSE_ADDED"

    @ManyToOne
    @JoinColumn(name = "expenseId")
    private Expense expense; // For expense-related activities

    @Column(precision = 10, scale = 2)
    private BigDecimal amount; // For monetary activities

    @Column(length = 500)
    private String description;

    @Column(name = "activity_time", nullable = false)
    private LocalDateTime activityTime;


    public GroupActivity() {
    }


    public UUID getActivityId() {
        return this.activityId;
    }

    public void setActivityId(UUID activityId) {
        this.activityId = activityId;
    }

    public Group getGroup() {
        return this.group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getActivityType() {
        return this.activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public Expense getExpense() {
        return this.expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getActivityTime() {
        return this.activityTime;
    }

    public void setActivityTime(LocalDateTime activityTime) {
        this.activityTime = activityTime;
    }    
}