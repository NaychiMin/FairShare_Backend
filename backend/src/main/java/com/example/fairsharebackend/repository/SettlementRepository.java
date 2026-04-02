package com.example.fairsharebackend.repository;

import com.example.fairsharebackend.entity.Group;
import com.example.fairsharebackend.entity.Settlement;
import com.example.fairsharebackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, UUID> {
    
    List<Settlement> findByGroupOrderBySettlementDateDesc(Group group);
    
    @Query("SELECT s FROM Settlement s WHERE s.group = :group AND (s.fromUser = :user OR s.toUser = :user) ORDER BY s.settlementDate DESC")
    List<Settlement> findByGroupAndUser(@Param("group") Group group, @Param("user") User user);
    
    List<Settlement> findByGroupAndFromUserAndToUserOrderBySettlementDateDesc(
        Group group, User fromUser, User toUser);
}