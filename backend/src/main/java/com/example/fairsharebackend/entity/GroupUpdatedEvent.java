package com.example.fairsharebackend.entity;

import com.example.fairsharebackend.constant.GroupField;

public class GroupUpdatedEvent {
    private Group group;
    private GroupField groupField;
    private String oldValue;
    private String newValue;

    public GroupUpdatedEvent() {
        // Required by JPA (Hibernate) for entity instantiation via reflection
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public GroupField getGroupField() {
        return groupField;
    }

    public void setGroupField(GroupField groupField) {
        this.groupField = groupField;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
}
