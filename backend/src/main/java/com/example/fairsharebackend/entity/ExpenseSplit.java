package com.example.fairsharebackend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_expense_split", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"expense_id", "user_id"}))
public class ExpenseSplit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID splitId;

    @ManyToOne
    @JoinColumn(name = "expenseId", nullable = false)
    private Expense expense;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal shareAmount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal settledAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean isSettled = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public ExpenseSplit() {
    }

    public UUID getSplitId() {
        return this.splitId;
    }

    public void setSplitId(UUID splitId) {
        this.splitId = splitId;
    }

    public Expense getExpense() {
        return this.expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getShareAmount() {
        return this.shareAmount;
    }

    public void setShareAmount(BigDecimal shareAmount) {
        this.shareAmount = shareAmount;
    }

    public BigDecimal getSettledAmount() {
        return this.settledAmount;
    }

    public void setSettledAmount(BigDecimal settledAmount) {
        this.settledAmount = settledAmount;
    }

    public Boolean isIsSettled() {
        return this.isSettled;
    }

    public Boolean getIsSettled() {
        return this.isSettled;
    }

    public void setIsSettled(Boolean isSettled) {
        this.isSettled = isSettled;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}