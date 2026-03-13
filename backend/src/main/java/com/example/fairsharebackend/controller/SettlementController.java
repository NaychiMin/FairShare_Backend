package com.example.fairsharebackend.controller;

import com.example.fairsharebackend.entity.dto.request.SettlementCreateRequestDto;
import com.example.fairsharebackend.entity.dto.response.SettlementResponseDto;
import com.example.fairsharebackend.service.SettlementService;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settlements")
public class SettlementController {

    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    // Create settlment for user
    @PostMapping
    public ResponseEntity<SettlementResponseDto> createSettlement(
            @Valid @RequestBody SettlementCreateRequestDto request,
            @RequestParam String requesterEmail) {
        
        SettlementResponseDto response = settlementService.createSettlement(request, requesterEmail);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Retrieve all settlements of user
    @GetMapping("/group/{groupId}/user")
    public ResponseEntity<List<SettlementResponseDto>> getUserSettlements(
            @PathVariable UUID groupId,
            @RequestParam String requesterEmail) {
        
        List<SettlementResponseDto> settlements = 
            settlementService.getUserSettlementsInGroup(groupId, requesterEmail);
        return ResponseEntity.ok(settlements);
    }

    // Retrieve all settlement in group
    @GetMapping("/group/{groupId}/all")
    public ResponseEntity<List<SettlementResponseDto>> getAllGroupSettlements(
            @PathVariable UUID groupId,
            @RequestParam String requesterEmail) {
        
        List<SettlementResponseDto> settlements = 
            settlementService.getAllGroupSettlements(groupId, requesterEmail);
        return ResponseEntity.ok(settlements);
    }
}