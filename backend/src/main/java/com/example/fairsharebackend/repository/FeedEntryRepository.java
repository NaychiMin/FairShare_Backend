package com.example.fairsharebackend.repository;

import com.example.fairsharebackend.entity.FeedEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FeedEntryRepository extends JpaRepository<FeedEntry, UUID> {
}
