package com.example.fairsharebackend.repository;

import com.example.fairsharebackend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByRecipient_EmailOrderByCreatedAtDesc(String email);

    long countByRecipient_EmailAndIsReadFalse(String email);

    List<Notification> findByRecipient_EmailAndIsReadFalseOrderByCreatedAtDesc(String email);

    @Query("""
        select n.group.groupId as groupId, count(n) as unreadCount
        from Notification n
        where n.recipient.email = :email
          and n.isRead = false
          and n.group is not null
        group by n.group.groupId
    """)
    List<UnreadGroupNotificationProjection> countUnreadByGroup(@Param("email") String email);
}