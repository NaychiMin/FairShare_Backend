//package com.example.fairsharebackend.repository;
//
//import com.example.fairsharebackend.entity.Group;
//import com.example.fairsharebackend.entity.GroupMembership;
//import com.example.fairsharebackend.entity.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//import java.util.UUID;
//
//@Repository
//public interface GroupMembershipRepository extends JpaRepository<GroupMembership, UUID> {
//
//    boolean existsByGroupAndUserAndRole_NameAndMembershipStatus(
//            Group group, User user, String roleName, String membershipStatus
//    );
//    boolean existsByGroup_GroupIdAndUser_EmailAndRole_NameAndMembershipStatus(
//            UUID groupId,
//            String email,
//            String roleName,
//            String membershipStatus
//    );
//    void deleteByGroup_GroupId(UUID groupId);
//    boolean existsByGroupAndUser_UserId(Group group, UUID userId);
//    List<GroupMembership> findByGroup(Group group);
//<<<<<<< HEAD
//
//=======
//>>>>>>> origin/feature/US_042
//
//    boolean existsByGroup_GroupIdAndUser_EmailAndMembershipStatus(
//            java.util.UUID groupId,
//            String email,
//            String membershipStatus
//    );
//
//    Optional<GroupMembership> findByGroup_GroupIdAndUser_UserIdAndMembershipStatus(
//            UUID groupId,
//            UUID userId,
//            String membershipStatus
//    );
//
//    List<GroupMembership> findAllByUserOrderByJoinedAtDesc(User user);
//
//    List<GroupMembership> findAllByGroup_GroupIdOrderByJoinedAtAsc(UUID groupId);
//
//    long countByGroup_GroupIdAndRole_NameAndMembershipStatus(
//            UUID groupId,
//            String roleName,
//            String membershipStatus
//    );
//<<<<<<< HEAD
//=======
//
//>>>>>>> origin/feature/US_042
//    @Query("SELECT gm.user FROM GroupMembership gm " +
//       "WHERE gm.group = :group " +
//       "AND gm.user != :currentUser")
//        List<User> findOtherMembersInGroup(@Param("group") Group group, @Param("currentUser") User currentUser);
//<<<<<<< HEAD
//}
//=======
//}
//>>>>>>> origin/feature/US_042
package com.example.fairsharebackend.repository;

import com.example.fairsharebackend.entity.Group;
import com.example.fairsharebackend.entity.GroupMembership;
import com.example.fairsharebackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupMembershipRepository extends JpaRepository<GroupMembership, UUID> {

    boolean existsByGroupAndUserAndRole_NameAndMembershipStatus(
            Group group, User user, String roleName, String membershipStatus
    );

    boolean existsByGroup_GroupIdAndUser_EmailAndRole_NameAndMembershipStatus(
            UUID groupId,
            String email,
            String roleName,
            String membershipStatus
    );

    void deleteByGroup_GroupId(UUID groupId);

    boolean existsByGroupAndUser_UserId(Group group, UUID userId);

    List<GroupMembership> findByGroup(Group group);

    boolean existsByGroup_GroupIdAndUser_EmailAndMembershipStatus(
            UUID groupId,
            String email,
            String membershipStatus
    );

    Optional<GroupMembership> findByGroup_GroupIdAndUser_UserIdAndMembershipStatus(
            UUID groupId,
            UUID userId,
            String membershipStatus
    );

    List<GroupMembership> findAllByUserOrderByJoinedAtDesc(User user);

    List<GroupMembership> findAllByGroup_GroupIdOrderByJoinedAtAsc(UUID groupId);

    long countByGroup_GroupIdAndRole_NameAndMembershipStatus(
            UUID groupId,
            String roleName,
            String membershipStatus
    );

    @Query("SELECT gm.user FROM GroupMembership gm " +
            "WHERE gm.group = :group " +
            "AND gm.user != :currentUser")
    List<User> findOtherMembersInGroup(@Param("group") Group group, @Param("currentUser") User currentUser);
}