package com.example.fairsharebackend.repository;

import com.example.fairsharebackend.entity.Group;
import com.example.fairsharebackend.entity.GroupMembership;
import com.example.fairsharebackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface GroupMembershipRepository extends JpaRepository<GroupMembership, UUID> {
    List<GroupMembership> findAllByUserOrderByJoinedAtDesc(User user);
    boolean existsByGroupAndUserAndRole_NameAndMembershipStatus(
            Group group, User user, String roleName, String membershipStatus
    );
}