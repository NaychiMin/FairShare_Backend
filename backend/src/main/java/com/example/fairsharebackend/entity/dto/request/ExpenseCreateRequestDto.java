package com.example.fairsharebackend.entity.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ExpenseCreateRequestDto {
    @NotNull(message = "Group ID is required")
    private UUID groupId;

    @NotNull(message = "Paid By is required")
    private UUID paidByUserId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description too long")
    private String description;

    private String notes;

    @NotNull(message = "Expense date is required")
    private LocalDateTime expenseDate;

    @NotNull(message = "Split strategy is required")
    private String splitStrategy; // "EQUAL" for sprint 2

    @NotNull(message = "Participants are required")
    @Size(min = 1, message = "At least one participant required")
    private List<UUID> participantUserIds; // Users who will split (includes payer)

    public ExpenseCreateRequestDto() {
    }

    public UUID getGroupId() {
        return this.groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }

    public UUID getPaidByUserId() {
        return this.paidByUserId;
    }

    public void setPaidByUserId(UUID paidByuserId) {
        this.paidByUserId = paidByuserId;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getExpenseDate() {
        return this.expenseDate;
    }

    public void setExpenseDate(LocalDateTime expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getSplitStrategy() {
        return this.splitStrategy;
    }

    public void setSplitStrategy(String splitStrategy) {
        this.splitStrategy = splitStrategy;
    }

    public List<UUID> getParticipantUserIds() {
        return this.participantUserIds;
    }

    public void setParticipantUserIds(List<UUID> participantUserIds) {
        this.participantUserIds = participantUserIds;
    }

}