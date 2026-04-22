package com.example.fairsharebackend.entity.dto.response;

import com.example.fairsharebackend.constant.FeedEntryType;
import com.example.fairsharebackend.constant.GroupField;
import jakarta.persistence.Column;

import java.time.LocalDateTime;
import java.util.UUID;

public class FeedEntryResponseDto {
    private UUID feedEntryId;
    private FeedEntryType feedEntryType;
    private ExpenseResponseDto expenseAdded;
    private SettlementResponseDto settlementAdded;
    private UserBadgeDto userBadgeEarned;
    private GroupSummaryResponseDto group;
    private GroupField groupUpdatedField;
    private String groupUpdatedFieldOld;
    private String groupUpdatedFieldNew;
    private LocalDateTime createdDate;

    public FeedEntryResponseDto() {
        // Required by JPA (Hibernate) for entity instantiation via reflection
    }

    public UUID getFeedEntryId() {
        return feedEntryId;
    }

    public void setFeedEntryId(UUID feedEntryId) {
        this.feedEntryId = feedEntryId;
    }

    public FeedEntryType getFeedEntryType() {
        return feedEntryType;
    }

    public void setFeedEntryType(FeedEntryType feedEntryType) {
        this.feedEntryType = feedEntryType;
    }

    public ExpenseResponseDto getExpenseAdded() {
        return expenseAdded;
    }

    public void setExpenseAdded(ExpenseResponseDto expenseAdded) {
        this.expenseAdded = expenseAdded;
    }

    public SettlementResponseDto getSettlementAdded() {
        return settlementAdded;
    }

    public void setSettlementAdded(SettlementResponseDto settlementAdded) {
        this.settlementAdded = settlementAdded;
    }

    public UserBadgeDto getUserBadgeEarned() {
        return userBadgeEarned;
    }

    public void setUserBadgeEarned(UserBadgeDto userBadgeEarned) {
        this.userBadgeEarned = userBadgeEarned;
    }

    public GroupSummaryResponseDto getGroup() {
        return group;
    }

    public void setGroup(GroupSummaryResponseDto group) {
        this.group = group;
    }

    public GroupField getGroupUpdatedField() {
        return groupUpdatedField;
    }

    public void setGroupUpdatedField(GroupField groupUpdatedField) {
        this.groupUpdatedField = groupUpdatedField;
    }

    public String getGroupUpdatedFieldOld() {
        return groupUpdatedFieldOld;
    }

    public void setGroupUpdatedFieldOld(String groupUpdatedFieldOld) {
        this.groupUpdatedFieldOld = groupUpdatedFieldOld;
    }

    public String getGroupUpdatedFieldNew() {
        return groupUpdatedFieldNew;
    }

    public void setGroupUpdatedFieldNew(String groupUpdatedFieldNew) {
        this.groupUpdatedFieldNew = groupUpdatedFieldNew;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
