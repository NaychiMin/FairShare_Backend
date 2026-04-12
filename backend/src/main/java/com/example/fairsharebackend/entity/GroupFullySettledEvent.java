package com.example.fairsharebackend.entity;

public class GroupFullySettledEvent {
    private Group group;

    public GroupFullySettledEvent() {
    }

    public GroupFullySettledEvent(Group group) {
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
