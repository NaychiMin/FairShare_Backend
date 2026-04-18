package com.example.fairsharebackend.repository;

import com.example.fairsharebackend.entity.Group;
import com.example.fairsharebackend.entity.PairwiseBalance;
import com.example.fairsharebackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PairwiseBalanceRepository extends JpaRepository<PairwiseBalance, UUID> {
    
    Optional<PairwiseBalance> findByGroupAndDebtorAndCreditor(
        Group group, User debtor, User creditor);
    
    List<PairwiseBalance> findByGroupAndDebtor(Group group, User debtor);
    
    List<PairwiseBalance> findByGroupAndCreditor(Group group, User creditor);
    
    @Query("SELECT COALESCE(SUM(pb.amount), 0) FROM PairwiseBalance pb " +
           "WHERE pb.group = :group AND pb.creditor = :user")
    BigDecimal sumAmountByGroupAndCreditor(@Param("group") Group group, @Param("user") User user);
    
    @Query("SELECT COALESCE(SUM(pb.amount), 0) FROM PairwiseBalance pb " +
           "WHERE pb.group = :group AND pb.debtor = :user")
    BigDecimal sumAmountByGroupAndDebtor(@Param("group") Group group, @Param("user") User user);
    
    void deleteByGroup(Group group);

    Optional<PairwiseBalance>
    findTopByGroup_GroupIdAndDebtor_UserIdOrderByLastUpdatedDesc(UUID groupId, UUID debtorId);

    @Query("""
        SELECT CASE WHEN COUNT(pb) > 0 THEN true ELSE false END
        FROM PairwiseBalance pb
        WHERE pb.group = :group
          AND pb.amount <> 0
    """)
        boolean existsOutstandingBalancesByGroup(@Param("group") Group group);
}