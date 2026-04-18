package com.example.fairsharebackend.entity.dto.request;

import com.example.fairsharebackend.validation.PasswordMatches;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class GroupCreateRequestDto {
    @NotBlank(message = "Group Name is required")
    private String groupName;

    @NotBlank(message = "Group category is required")
    private String category;

    @NotBlank(message = "Group Admin confirmation is required")
    private String admin;

    public GroupCreateRequestDto() {
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin (String admin) {
        this.admin = admin;
    }
}
