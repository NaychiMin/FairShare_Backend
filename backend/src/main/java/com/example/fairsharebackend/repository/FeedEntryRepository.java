package com.example.fairsharebackend.repository;

import com.example.fairsharebackend.entity.FeedEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface FeedEntryRepository extends JpaRepository<FeedEntry, UUID> {
    @Query("""
        SELECT f 
        FROM FeedEntry f
        LEFT JOIN GroupMembership gm ON gm.group = f.group
        WHERE (:userId IS NULL OR gm.user.id = :userId)
          AND (:groupId IS NULL OR f.group.id = :groupId)
        """)
    Page<FeedEntry> findFeedEntryFilteredSorted(
            @Param("userId") UUID userId,
            @Param("groupId") UUID groupId,
            Pageable pageable
    );
}
