package com.example.fairsharebackend.repository;

import com.example.fairsharebackend.entity.Badge;
import com.example.fairsharebackend.entity.Group;
import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, UUID> {
    boolean existsByUserAndBadgeAndGroup(User user, Badge badge, Group group);
    boolean existsByUserAndBadgeAndGroupIsNull(User user, Badge badge);
}