package com.example.fairsharebackend.repository;

import com.example.fairsharebackend.entity.Group;
import com.example.fairsharebackend.entity.Role;
import com.example.fairsharebackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Role getByName(String name);
}