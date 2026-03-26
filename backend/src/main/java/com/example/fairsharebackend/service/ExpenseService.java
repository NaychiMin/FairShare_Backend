package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.dto.request.ExpenseCreateRequestDto;
import com.example.fairsharebackend.entity.dto.response.ExpenseResponseDto;
import java.util.List;
import java.util.UUID;

public interface ExpenseService {
    ExpenseResponseDto createExpense(ExpenseCreateRequestDto request, String requesterEmail);
    ExpenseResponseDto getExpenseDetails(UUID expenseId, String requesterEmail);
    List<ExpenseResponseDto> getGroupExpenses(UUID groupId, String requesterEmail);
}