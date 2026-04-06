package com.example.fairsharebackend.repository;

import com.example.fairsharebackend.constant.FeedEntryType;
import com.example.fairsharebackend.entity.FeedEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FeedEntryRepository extends JpaRepository<FeedEntry, UUID> {
    @Query("""
        SELECT f 
        FROM FeedEntry f
        LEFT JOIN GroupMembership gm ON gm.group = f.group
        WHERE (:userId IS NULL OR gm.user.id = :userId)
          AND (:groupId IS NULL OR f.group.id = :groupId)
          AND (:types IS NULL OR f.feedEntryType IN :types)
        """)
    Page<FeedEntry> findFeedEntryFilteredSorted(
            @Param("userId") UUID userId,
            @Param("groupId") UUID groupId,
            @Param("types") List<FeedEntryType> types,
            Pageable pageable
    );
}
