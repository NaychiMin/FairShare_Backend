package com.example.fairsharebackend.controller;

import com.example.fairsharebackend.entity.dto.request.ExpenseCreateRequestDto;
import com.example.fairsharebackend.entity.dto.response.ExpenseResponseDto;
import com.example.fairsharebackend.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    // Create expenses
    @PostMapping
    public ResponseEntity<ExpenseResponseDto> createExpense(
            @Valid @RequestBody ExpenseCreateRequestDto request,
            @RequestParam String requesterEmail) { 
        
        ExpenseResponseDto response = expenseService.createExpense(request, requesterEmail);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get a singular expense
    @GetMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponseDto> getExpenseDetails(
            @PathVariable UUID expenseId,
            @RequestParam String requesterEmail) {
        
        ExpenseResponseDto response = expenseService.getExpenseDetails(expenseId, requesterEmail);
        return ResponseEntity.ok(response);
    }
    
    // Get all expenses for a group
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<ExpenseResponseDto>> getGroupExpenses(
            @PathVariable UUID groupId,
            @RequestParam String requesterEmail) {
        
        List<ExpenseResponseDto> responses = expenseService.getGroupExpenses(groupId, requesterEmail);
        return ResponseEntity.ok(responses);
    }
}