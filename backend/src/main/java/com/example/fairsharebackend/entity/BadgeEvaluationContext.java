package com.example.fairsharebackend.entity;

import com.example.fairsharebackend.constant.EventType;

import java.util.Map;

public class BadgeEvaluationContext {
    private Group group;
    private EventType eventType;
    private Map<String, Object> metadata;

    public BadgeEvaluationContext() {
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
