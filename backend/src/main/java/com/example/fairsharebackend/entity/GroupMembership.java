package com.example.fairsharebackend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_group_membership")
public class GroupMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID groupMembershipId;

    @Column(nullable = false)
    private String membershipStatus;

    @Column(nullable = false)
    private LocalDateTime joinedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Removed unique = true
    private User user;

//    @ManyToOne
//    @JoinColumn(name = "role_id") // Removed unique = true
//    private Role role;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false) // Removed unique = true
    private Group group;

    public GroupMembership() {
    }

    public UUID getGroupMembershipId() {
        return groupMembershipId;
    }

    public void setGroupMembershipId(UUID groupMembershipId) {
        this.groupMembershipId = groupMembershipId;
    }

    public String getMembershipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(String membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

//    public Role getRole() {
//        return role;
//    }
//
//    public void setRole(Role role) {
//        this.role = role;
//    }
}
