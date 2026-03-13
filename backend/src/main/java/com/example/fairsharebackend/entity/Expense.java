package com.example.fairsharebackend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "tb_expense")
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID expenseId;

    @ManyToOne
    @JoinColumn(name = "groupId", nullable = false)
    private Group group;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User paidBy;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String description;

    @Column
    private String notes; // For expense owner to add notes

    @Column(nullable = false)
    private String splitStrategy; // only "EQUAL" for Sprint 2

    @Column(nullable = false)
    private Boolean isSettled = false;

    @Column(nullable = false)
    private LocalDateTime expenseDate;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false) 
    private User createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExpenseSplit> expenseSplits;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Expense() {
    }

    public UUID getExpenseId() {
        return this.expenseId;
    }

    public void setExpenseId(UUID expenseId) {
        this.expenseId = expenseId;
    }

    public Group getGroup() {
        return this.group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public User getPaidBy() {
        return this.paidBy;
    }

    public void setPaidBy(User paidBy) {
        this.paidBy = paidBy;
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

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getSplitStrategy() {
        return this.splitStrategy;
    }

    public void setSplitStrategy(String splitStrategy) {
        this.splitStrategy = splitStrategy;
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

    public LocalDateTime getExpenseDate() {
        return this.expenseDate;
    }

    public void setExpenseDate(LocalDateTime expenseDate) {
        this.expenseDate = expenseDate;
    }

    public User getCreatedBy() {
    return this.createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
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

    public List<ExpenseSplit> getExpenseSplits() {
        return this.expenseSplits;
    }

    public void setExpenseSplits(List<ExpenseSplit> expenseSplits) {
        this.expenseSplits = expenseSplits;
    }

}