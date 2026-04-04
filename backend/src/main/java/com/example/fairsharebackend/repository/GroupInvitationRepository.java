package com.example.fairsharebackend.repository;

import com.example.fairsharebackend.entity.GroupInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.fairsharebackend.entity.User;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, UUID> {
    Optional<GroupInvitation> findByToken(String token);

    List<GroupInvitation> findAllByInvitedEmailAndStatusOrderByCreatedAtDesc(String invitedEmail, String status);

    List<GroupInvitation> findAllByCreatedByOrderByCreatedAtDesc(User createdBy);
}