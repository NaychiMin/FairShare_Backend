package com.example.fairsharebackend.repository;

import com.example.fairsharebackend.entity.GroupActivity;
import com.example.fairsharebackend.entity.Settlement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupActivityRepository extends JpaRepository<GroupActivity, UUID> {
    List<GroupActivity> findBySettlement(Settlement settlement);
}