package com.example.fairsharebackend.entity.dto.response;

public class GroupActionStatusResponse {
    private boolean canArchive;
    private boolean canDelete;
    private String warningMessage;

    public GroupActionStatusResponse() {}

    public GroupActionStatusResponse(boolean canArchive, boolean canDelete, String warningMessage) {
        this.canArchive = canArchive;
        this.canDelete = canDelete;
        this.warningMessage = warningMessage;
    }

    public boolean isCanArchive() {
        return canArchive;
    }

    public void setCanArchive(boolean canArchive) {
        this.canArchive = canArchive;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public String getWarningMessage() {
        return warningMessage;
    }

    public void setWarningMessage(String warningMessage) {
        this.warningMessage = warningMessage;
    }
}