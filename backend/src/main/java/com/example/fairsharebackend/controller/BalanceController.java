package com.example.fairsharebackend.controller;

import com.example.fairsharebackend.entity.dto.response.GroupBalanceResponseDto;
import com.example.fairsharebackend.entity.Group;
import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.exception.ResourceNotFoundException;
import com.example.fairsharebackend.repository.GroupRepository;
import com.example.fairsharebackend.repository.UserRepository;
import com.example.fairsharebackend.service.BalanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/balances")
public class BalanceController {

    private final BalanceService balanceService;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public BalanceController(
            BalanceService balanceService,
            UserRepository userRepository,
            GroupRepository groupRepository) {
        this.balanceService = balanceService;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    // Get balance details for a user in a group
    @GetMapping("/group/{groupId}/user")
    public ResponseEntity<GroupBalanceResponseDto> getUserBalanceInGroup(
            @PathVariable UUID groupId,
            @RequestParam String requesterEmail) {
        
        // Find user by email
        User user = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Find group
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        
        // Get balance details
        GroupBalanceResponseDto balance = 
            balanceService.getUserBalanceDetails(group, user);
        
        return ResponseEntity.ok(balance);
    }
    
    // Get net balance only
    @GetMapping("/group/{groupId}/net")
    public ResponseEntity<BigDecimal> getUserNetBalance(
            @PathVariable UUID groupId,
            @RequestParam String requesterEmail) {
        
        User user = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        
        BigDecimal netBalance = 
            balanceService.getNetBalanceForUserInGroup(group, user);
        
        return ResponseEntity.ok(netBalance);
    }
}