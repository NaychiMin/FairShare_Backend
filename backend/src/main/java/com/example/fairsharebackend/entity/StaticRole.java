package com.example.fairsharebackend.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "static_role")
public class StaticRole {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID staticRoleId;

    @Column(unique = true)
    private String name;

    public StaticRole() {
    }

    public UUID getStaticRoleId() {
        return staticRoleId;
    }

    public void setStaticRoleId(UUID staticRoleId) {
        this.staticRoleId = staticRoleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}