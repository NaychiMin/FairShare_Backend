package com.example.fairsharebackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tb_group")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID groupId;

    @Column(unique = true, nullable = false)
    private String groupName;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String status = "Active";

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<GroupMembership> memberships = new HashSet<>(); // Changed to Set


    public Group() {
    }

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Set<GroupMembership> getMemberships() {
        return memberships;
    }

    public void setMemberships(Set<GroupMembership> memberships) {
        this.memberships = memberships;
    }

}
