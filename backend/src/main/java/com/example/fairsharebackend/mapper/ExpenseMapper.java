package com.example.fairsharebackend.mapper;

import com.example.fairsharebackend.entity.dto.response.ExpenseResponseDto;
import com.example.fairsharebackend.entity.dto.response.ExpenseSplitResponseDto;
import com.example.fairsharebackend.entity.Expense;
import com.example.fairsharebackend.entity.ExpenseSplit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    @Mapping(source = "group.groupId", target = "groupId")
    @Mapping(source = "group.groupName", target = "groupName")
    
    // Paid by mappings
    @Mapping(source = "paidBy.userId", target = "paidByUserId")
    @Mapping(source = "paidBy.name", target = "paidByName")
    @Mapping(source = "paidBy.email", target = "paidByEmail")
    
    // Created by mappings
    @Mapping(source = "createdBy.userId", target = "createdByUserId")
    @Mapping(source = "createdBy.name", target = "createdByName")
    @Mapping(source = "createdBy.email", target = "createdByEmail")
    
    @Mapping(source = "expenseSplits", target = "splits")
    ExpenseResponseDto toExpenseResponseDto(Expense expense);

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "user.email", target = "userEmail")
    ExpenseSplitResponseDto toSplitResponse(ExpenseSplit split);
}