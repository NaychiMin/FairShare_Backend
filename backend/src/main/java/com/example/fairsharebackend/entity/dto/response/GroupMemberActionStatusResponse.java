package com.example.fairsharebackend.entity.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public class GroupMemberActionStatusResponse {

    private UUID userId;
    private BigDecimal netBalance;
    private boolean canLeave;
    private boolean canRemove;
    private String warningMessage;

    public GroupMemberActionStatusResponse() {
    }

    public GroupMemberActionStatusResponse(UUID userId, BigDecimal netBalance, boolean canLeave, boolean canRemove, String warningMessage) {
        this.userId = userId;
        this.netBalance = netBalance;
        this.canLeave = canLeave;
        this.canRemove = canRemove;
        this.warningMessage = warningMessage;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public BigDecimal getNetBalance() {
        return netBalance;
    }

    public void setNetBalance(BigDecimal netBalance) {
        this.netBalance = netBalance;
    }

    public boolean isCanLeave() {
        return canLeave;
    }

    public void setCanLeave(boolean canLeave) {
        this.canLeave = canLeave;
    }

    public boolean isCanRemove() {
        return canRemove;
    }

    public void setCanRemove(boolean canRemove) {
        this.canRemove = canRemove;
    }

    public String getWarningMessage() {
        return warningMessage;
    }

    public void setWarningMessage(String warningMessage) {
        this.warningMessage = warningMessage;
    }
}