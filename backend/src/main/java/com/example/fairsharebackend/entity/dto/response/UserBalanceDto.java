package com.example.fairsharebackend.entity.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public class UserBalanceDto {
    private UUID userId;
    private String name;
    private String email;
    private BigDecimal amount; 
    
    public UserBalanceDto() {
    }
    
    public UserBalanceDto(UUID userId, String name, String email, BigDecimal amount) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.amount = amount;
    }
    
    public UUID getUserId() {
        return userId;
    }
    
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}