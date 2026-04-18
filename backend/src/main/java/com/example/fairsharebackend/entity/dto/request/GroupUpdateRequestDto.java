package com.example.fairsharebackend.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class GroupUpdateRequestDto {
    @NotBlank(message="Group Name is required")
    private String groupName;

    @NotBlank(message="Group category is required")
    private String category;

    // getters/setters

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
}