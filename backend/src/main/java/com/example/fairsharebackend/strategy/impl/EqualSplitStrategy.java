package com.example.fairsharebackend.strategy.impl;

import com.example.fairsharebackend.entity.Expense;
import com.example.fairsharebackend.entity.ExpenseSplit;
import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.strategy.SplitStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class EqualSplitStrategy implements SplitStrategy {

    @Override
    public String getStrategyName() {
        return "EQUAL";
    }

    @Override
    public List<ExpenseSplit> calculateSplits(Expense expense, List<User> participants) {
        BigDecimal totalAmount = expense.getAmount();
        int numParticipants = participants.size();
        
        // Calculate base share
        BigDecimal shareAmount = totalAmount.divide(BigDecimal.valueOf(numParticipants), 2, RoundingMode.HALF_UP);
        
        // Handle rounding
        BigDecimal totalCalculated = shareAmount.multiply(BigDecimal.valueOf(numParticipants));
        List<ExpenseSplit> splits = new ArrayList<>();
        
        for (int i = 0; i < participants.size(); i++) {
            User participant = participants.get(i);
            BigDecimal finalAmount = shareAmount;
            
            // Adjust last person's share for rounding
            if (i == participants.size() - 1 && totalCalculated.compareTo(totalAmount) != 0) {
                BigDecimal difference = totalAmount.subtract(totalCalculated);
                finalAmount = shareAmount.add(difference);
            }
            
            ExpenseSplit split = new ExpenseSplit();
            split.setExpense(expense);
            split.setUser(participant);
            split.setShareAmount(finalAmount);
            split.setSettledAmount(BigDecimal.ZERO); 
            split.setIsSettled(false);   
            split.setCreatedAt(LocalDateTime.now());
            split.setUpdatedAt(LocalDateTime.now());
            
            splits.add(split);
        }
        
        return splits;
    }
}