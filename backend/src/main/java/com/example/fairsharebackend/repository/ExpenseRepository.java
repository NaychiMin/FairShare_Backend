package com.example.fairsharebackend.repository;

import com.example.fairsharebackend.entity.Expense;
import com.example.fairsharebackend.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    List<Expense> findByGroup(Group group);
    @Query("SELECT e FROM Expense e WHERE e.group.groupId = :groupId " +
            "AND (e.deleteInd IS NULL OR e.deleteInd=false) ORDER BY e.expenseDate DESC")
    List<Expense> findActiveExpensesByGroupId(@Param("groupId") UUID groupId);
}