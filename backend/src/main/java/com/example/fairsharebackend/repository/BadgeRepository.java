package com.example.fairsharebackend.repository;

import com.example.fairsharebackend.constant.BadgeType;
import com.example.fairsharebackend.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, UUID> {
    List<Badge> findByBadgeType(BadgeType badgeType);
}