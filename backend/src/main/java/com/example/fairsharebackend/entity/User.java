package com.example.fairsharebackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tb_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String status = "A";

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    private UserCredential userCredential;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_static_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "static_role_id")
    )
    @JsonIgnore
    private Set<StaticRole> staticRoles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<UserBadge> userBadges;

    public User() {
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<StaticRole> getStaticRoles() {
        return staticRoles;
    }

    public void setStaticRoles(Set<StaticRole> staticRoles) {
        this.staticRoles = staticRoles;
    }

    public UserCredential getUserCredential() {
        return userCredential;
    }

    public void setUserCredential(UserCredential userCredential) {
        this.userCredential = userCredential;
    }

    public Set<UserBadge> getUserBadges() {
        return userBadges;
    }

    public void setUserBadges(Set<UserBadge> userBadges) {
        this.userBadges = userBadges;
    }
}
