package com.example.fairsharebackend.mapper;

import com.example.fairsharebackend.entity.*;
import com.example.fairsharebackend.entity.dto.response.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FeedEntryMapper {
    FeedEntryResponseDto toDto(FeedEntry feedEntry);
    ExpenseResponseDto toDto(Expense expenseAdded);
    SettlementResponseDto toDto(SettlementResponseDto settlementAdded);
    UserBadgeDto toDto(UserBadge userBadgeEarned);
    GroupSummaryResponseDto toDto(Group group);
}
