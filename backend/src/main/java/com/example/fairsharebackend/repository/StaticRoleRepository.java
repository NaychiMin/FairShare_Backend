package com.example.fairsharebackend.repository;

import com.example.fairsharebackend.entity.Role;
import com.example.fairsharebackend.entity.StaticRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StaticRoleRepository extends JpaRepository<StaticRole, UUID> {
    StaticRole getByName(String name);
}