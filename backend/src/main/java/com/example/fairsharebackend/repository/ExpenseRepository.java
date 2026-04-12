package com.example.fairsharebackend.repository;

import com.example.fairsharebackend.entity.Expense;
import com.example.fairsharebackend.entity.Group;
import com.example.fairsharebackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    List<Expense> findByGroup(Group group);
    List<Expense> findByGroup_GroupIdOrderByExpenseDateDesc(UUID groupId);
    long countByGroupAndCreatedBy(Group group, User createdBy);

    void deleteByGroup_GroupId(UUID groupId);
}