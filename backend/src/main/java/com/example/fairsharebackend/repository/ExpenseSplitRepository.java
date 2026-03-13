// ExpenseSplitRepository.java
package com.example.fairsharebackend.repository;

import com.example.fairsharebackend.entity.Expense;
import com.example.fairsharebackend.entity.ExpenseSplit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, UUID> {
    List<ExpenseSplit> findByExpense(Expense expense);
}