package com.example.fairsharebackend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_pairwise_balance", 
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"group_id", "debtor_id", "creditor_id"}
       ))
public class PairwiseBalance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID balanceId;
    
    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;
    
    @ManyToOne
    @JoinColumn(name = "debtor_id", nullable = false)
    private User debtor;  // User who owes money
    
    @ManyToOne
    @JoinColumn(name = "creditor_id", nullable = false)
    private User creditor; // User who is owed money
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;  // Positive amount that debtor owes creditor
    
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;
    
    @Column(name = "is_settled", nullable = false)
    private Boolean isSettled = false;
    
    // Default constructor
    public PairwiseBalance() {
    }
    
    // Getters and Setters
    public UUID getBalanceId() {
        return balanceId;
    }
    
    public void setBalanceId(UUID balanceId) {
        this.balanceId = balanceId;
    }
    
    public Group getGroup() {
        return group;
    }
    
    public void setGroup(Group group) {
        this.group = group;
    }
    
    public User getDebtor() {
        return debtor;
    }
    
    public void setDebtor(User debtor) {
        this.debtor = debtor;
    }
    
    public User getCreditor() {
        return creditor;
    }
    
    public void setCreditor(User creditor) {
        this.creditor = creditor;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public Boolean getIsSettled() {
        return isSettled;
    }
    
    public void setIsSettled(Boolean isSettled) {
        this.isSettled = isSettled;
    }
}