package com.example.fairsharebackend.entity.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class GroupBalanceResponseDto {
    private BigDecimal netBalance; 
    private List<UserBalanceDto> owesYou; 
    private List<UserBalanceDto> youOwe; 
    
    public GroupBalanceResponseDto() {
    }
    
    public BigDecimal getNetBalance() {
        return netBalance;
    }
    
    public void setNetBalance(BigDecimal netBalance) {
        this.netBalance = netBalance;
    }
    
    public List<UserBalanceDto> getOwesYou() {
        return owesYou;
    }
    
    public void setOwesYou(List<UserBalanceDto> owesYou) {
        this.owesYou = owesYou;
    }
    
    public List<UserBalanceDto> getYouOwe() {
        return youOwe;
    }
    
    public void setYouOwe(List<UserBalanceDto> youOwe) {
        this.youOwe = youOwe;
    }
}