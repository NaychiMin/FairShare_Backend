package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.*;
import com.example.fairsharebackend.entity.dto.response.GroupBalanceResponseDto;
import com.example.fairsharebackend.entity.dto.response.UserBalanceDto;
import com.example.fairsharebackend.repository.PairwiseBalanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BalanceService {

    private final PairwiseBalanceRepository balanceRepository;

    public BalanceService(PairwiseBalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    // Updates balance when a new expense is created
    @Transactional
    public void updateBalancesForNewExpense(Expense expense) {
        Group group = expense.getGroup();
        User creditor = expense.getPaidBy();

        for (ExpenseSplit split : expense.getExpenseSplits()) {
            User debtor = split.getUser();
            
            if (debtor.getUserId().equals(creditor.getUserId())) {
                continue;
            }
            
            BigDecimal amount = split.getShareAmount();
            
            updatePairwiseBalance(group, debtor, creditor, amount);
        }
    }

    // Updates or creates a pairwise balance between debtor and creditor (includes net logic)
    @Transactional
    public void updatePairwiseBalance(Group group, User debtor, User creditor, BigDecimal amount) {
        
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        
        // Try to find existing balance where debtor owes creditor
        Optional<PairwiseBalance> existingBalance = 
            balanceRepository.findByGroupAndDebtorAndCreditor(group, debtor, creditor);
        
        if (existingBalance.isPresent()) {
            // Debtor already owes creditor - modify it
            PairwiseBalance balance = existingBalance.get();
            BigDecimal newAmount = balance.getAmount().add(amount);
            
            if (newAmount.compareTo(BigDecimal.ZERO) == 0) {
                // Delete record if settled
                balanceRepository.delete(balance);
                System.out.println("[DEBUG] Deleted zero balance between " + debtor.getName() + " and " + creditor.getName());
            } else if (newAmount.compareTo(BigDecimal.ZERO) > 0) {
                // If positive, just update
                balance.setAmount(newAmount);
                balance.setLastUpdated(LocalDateTime.now());
                balance.setIsSettled(false);
                balanceRepository.save(balance);
                System.out.println("[DEBUG] Updated balance: " + debtor.getName() + " owes " + creditor.getName() + " $" + newAmount);
            } else {
                // If amount becomes negative means overpaid, so swap direction
                balanceRepository.delete(balance);
                // Create new balance in opposite direction with positive amount
                updatePairwiseBalance(group, creditor, debtor, newAmount.abs());
            }
            return;
        }
        
        // Check for reverse balance (creditor owes debtor)
        Optional<PairwiseBalance> reverseBalance = 
            balanceRepository.findByGroupAndDebtorAndCreditor(group, creditor, debtor);
        
        if (reverseBalance.isPresent()) {
            // There's a reverse balance, net them out
            PairwiseBalance reverse = reverseBalance.get();
            BigDecimal reverseAmount = reverse.getAmount();
            
            // So we need to subtract from reverse  because debtor to creditor
            BigDecimal newReverseAmount = reverseAmount.subtract(amount);
            
            if (newReverseAmount.compareTo(BigDecimal.ZERO) == 0) {
                // Delete record if settled
                balanceRepository.delete(reverse);
                System.out.println("[DEBUG] Cancelled balance between " + debtor.getName() + " and " + creditor.getName());
            } else if (newReverseAmount.compareTo(BigDecimal.ZERO) > 0) {
                // If positive, just update
                reverse.setAmount(newReverseAmount);
                reverse.setLastUpdated(LocalDateTime.now());
                reverse.setIsSettled(false);
                balanceRepository.save(reverse);
                System.out.println("[DEBUG] Reduced reverse balance: " + creditor.getName() + " owes " + debtor.getName() + " $" + newReverseAmount);
            } else {
                // Reverse went negative means overnet, so create new balance in original direction
                balanceRepository.delete(reverse);
                updatePairwiseBalance(group, debtor, creditor, newReverseAmount.abs());
            }
            return;
        }
        
        // No existing balance in either direction 
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            PairwiseBalance newBalance = new PairwiseBalance();
            newBalance.setGroup(group);
            newBalance.setDebtor(debtor);
            newBalance.setCreditor(creditor);
            newBalance.setAmount(amount);
            newBalance.setLastUpdated(LocalDateTime.now());
            newBalance.setIsSettled(false);
            balanceRepository.save(newBalance);
            System.out.println("[DEBUG] Created new balance: " + debtor.getName() + " owes " + creditor.getName() + " $" + amount);
        } else {
            // If amount is negative and no balances exist, this means trying to setlle non existent debt
            throw new RuntimeException("[DEBUG] Cannot settle non-existent debt");
        }
    }

    
    // Get net balance for user in a group
    public BigDecimal getNetBalanceForUserInGroup(Group group, User user) {
        BigDecimal owedToUser = balanceRepository.sumAmountByGroupAndCreditor(group, user);
        BigDecimal owedByUser = balanceRepository.sumAmountByGroupAndDebtor(group, user);
        
        return owedToUser.subtract(owedByUser);
    }


    // Get balance details for a user in a group
    public GroupBalanceResponseDto getUserBalanceDetails(Group group, User user) {
        GroupBalanceResponseDto response = new GroupBalanceResponseDto();
        
        // Get people who owe this user
        List<PairwiseBalance> owesYouBalances = 
            balanceRepository.findByGroupAndCreditor(group, user);
        
        List<UserBalanceDto> owesYou = owesYouBalances.stream()
            .map(balance -> new UserBalanceDto(
                balance.getDebtor().getUserId(),
                balance.getDebtor().getName(),
                balance.getDebtor().getEmail(),
                balance.getAmount()
            ))
            .collect(Collectors.toList());
        
        // Get people this user owes
        List<PairwiseBalance> youOweBalances = 
            balanceRepository.findByGroupAndDebtor(group, user);
        
        List<UserBalanceDto> youOwe = youOweBalances.stream()
            .map(balance -> new UserBalanceDto(
                balance.getCreditor().getUserId(),
                balance.getCreditor().getName(),
                balance.getCreditor().getEmail(),
                balance.getAmount().negate() 
            ))
            .collect(Collectors.toList());
        
        // Calculate net balance
        BigDecimal owedToUser = owesYouBalances.stream()
            .map(PairwiseBalance::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal owedByUser = youOweBalances.stream()
            .map(PairwiseBalance::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        response.setNetBalance(owedToUser.subtract(owedByUser));
        response.setOwesYou(owesYou);
        response.setYouOwe(youOwe);
        
        return response;
    }

    // Update balance when a settlement is made
    @Transactional
    public void updateBalancesForSettlement(Settlement settlement) {
        User payer = settlement.getFromUser();
        User receiver = settlement.getToUser();
        Group group = settlement.getGroup();
        BigDecimal amount = settlement.getAmount();
        
        updatePairwiseBalance(group, payer, receiver, amount.negate());
    }

    // Reverse settlement for edit/delete
    @Transactional
    public void reverseSettlement(Settlement settlement) {
        User payer = settlement.getFromUser();
        User receiver = settlement.getToUser();
        Group group = settlement.getGroup();
        BigDecimal amount = settlement.getAmount();
        
        updatePairwiseBalance(group, payer, receiver, amount);
    }


    public boolean hasOutstandingBalances(Group group) {
        return balanceRepository.existsOutstandingBalancesByGroup(group);
    }

    public void deleteGroupBalances(Group group) {
        balanceRepository.deleteByGroup(group);
    }
}