package com.example.fairsharebackend.entity.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class FeedEntryFilterRequestDto {
    @NotNull
    private UUID userId;          // optional, filter by user’s groups
    private UUID groupId;         // optional, filter by specific group
    private Integer page = 0;     // page number
    private Integer size = 10;    // page size
    private String sortBy = "createdDate";      // default sort field
    private String direction = "DESC";       // ASC or DESC

    public FeedEntryFilterRequestDto() {
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
