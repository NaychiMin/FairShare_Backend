package com.example.fairsharebackend.entity;

import com.example.fairsharebackend.constant.BadgeRuleType;
import com.example.fairsharebackend.constant.BadgeScope;
import com.example.fairsharebackend.constant.BadgeType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tb_badge")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID badgeId;

    // Display fields
    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    // Rule fields
    @Column
    private BadgeScope badgeScope;

    @Column(nullable = false)
    private BadgeRuleType badgeRuleType;

    @Column
    private String ruleConfig;

    public Badge() {
    }

    public UUID getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(UUID badgeId) {
        this.badgeId = badgeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BadgeScope getBadgeScope() {
        return badgeScope;
    }

    public void setBadgeScope(BadgeScope badgeScope) {
        this.badgeScope = badgeScope;
    }

    public BadgeRuleType getBadgeRuleType() {
        return badgeRuleType;
    }

    public void setBadgeRuleType(BadgeRuleType badgeRuleType) {
        this.badgeRuleType = badgeRuleType;
    }

    public String getRuleConfig() {
        return ruleConfig;
    }

    public void setRuleConfig(String ruleConfig) {
        this.ruleConfig = ruleConfig;
    }
}
