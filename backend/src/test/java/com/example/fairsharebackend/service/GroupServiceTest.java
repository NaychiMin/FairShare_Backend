package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.dto.response.UserSummaryResponseDto;
import com.example.fairsharebackend.entity.*;
import com.example.fairsharebackend.exception.ResourceNotFoundException;
import com.example.fairsharebackend.repository.GroupMembershipRepository;
import com.example.fairsharebackend.repository.GroupRepository;
import com.example.fairsharebackend.repository.UserRepository;
import com.example.fairsharebackend.repository.RoleRepository;
import com.example.fairsharebackend.mapper.GroupMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;


import com.example.fairsharebackend.entity.dto.request.GroupUpdateRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupMapper groupMapper;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupMembershipRepository groupMembershipRepository;

    @InjectMocks
    private GroupServiceImpl groupService;

    private UUID groupId;
    private UUID userId;
    private String requesterEmail;
    private Group group;
    private User user;
    private Role memberRole;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        groupId = UUID.randomUUID();
        userId = UUID.randomUUID();
        requesterEmail = "test@example.com";

        user = new User();
        user.setUserId(userId);
        user.setEmail(requesterEmail);
        user.setName("Test User");

        group = new Group();
        group.setGroupId(groupId);
        group.setGroupName("Test Group");

        memberRole = new Role();
        memberRole.setRoleId(UUID.randomUUID());
        memberRole.setName("MEMBER");

        adminRole = new Role();
        adminRole.setRoleId(UUID.randomUUID());
        adminRole.setName("GROUP_ADMIN");
    }

    @Test
    @DisplayName("Get group by ID successfully")
    void shouldGetGroupById() {
        // ARRANGE
        when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.of(user));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupMembershipRepository.existsByGroupAndUser_UserId(group, user.getUserId()))
                .thenReturn(true);

        // ACT
        Group result = groupService.getGroupById(groupId, requesterEmail);

        // ASSERT
        assertThat(result).isEqualTo(group);
    }

    @Test
    @DisplayName("Get group by ID fails when user not in group")
    void shouldFailGetGroupByIdWhenUserNotInGroup() {
        // ARRANGE
        when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.of(user));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupMembershipRepository.existsByGroupAndUser_UserId(group, user.getUserId()))
                .thenReturn(false);

        // ACT & ASSERT
        assertThatThrownBy(() -> groupService.getGroupById(groupId, requesterEmail))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not a member");
    }

    @Test
    @DisplayName("Get group by ID fails when group not found")
    void shouldFailGetGroupByIdWhenGroupNotFound() {
        // ARRANGE
        when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.of(user));
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> groupService.getGroupById(groupId, requesterEmail))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Group not found");
    }

    @Test
    @DisplayName("Get group members successfully")
    void shouldGetGroupMembers() {
        // ARRANGE
        User member1 = new User();
        member1.setUserId(UUID.randomUUID());
        member1.setName("Member 1");
        member1.setEmail("member1@example.com");

        User member2 = new User();
        member2.setUserId(UUID.randomUUID());
        member2.setName("Member 2");
        member2.setEmail("member2@example.com");

        GroupMembership membership1 = new GroupMembership();
        membership1.setUser(member1);
        membership1.setRole(memberRole);

        GroupMembership membership2 = new GroupMembership();
        membership2.setUser(member2);
        membership2.setRole(adminRole);

        List<GroupMembership> memberships = List.of(membership1, membership2);

        when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.of(user));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupMembershipRepository.existsByGroupAndUser_UserId(group, user.getUserId()))
                .thenReturn(true);
        when(groupMembershipRepository.findByGroup(group)).thenReturn(memberships);

        // ACT
        List<UserSummaryResponseDto> results = groupService.getGroupMembers(groupId, requesterEmail);

        // ASSERT
        assertThat(results).hasSize(2);
        
        UserSummaryResponseDto firstMember = results.get(0);
        assertThat(firstMember.getUserId()).isEqualTo(member1.getUserId());
        assertThat(firstMember.getName()).isEqualTo("Member 1");
        assertThat(firstMember.getRole()).isEqualTo("MEMBER");

        UserSummaryResponseDto secondMember = results.get(1);
        assertThat(secondMember.getUserId()).isEqualTo(member2.getUserId());
        assertThat(secondMember.getName()).isEqualTo("Member 2");
        assertThat(secondMember.getRole()).isEqualTo("GROUP_ADMIN");
    }

    @Test
    @DisplayName("Get group members fails when user not in group")
    void shouldFailGetGroupMembersWhenUserNotInGroup() {
        // ARRANGE
        when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.of(user));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupMembershipRepository.existsByGroupAndUser_UserId(group, user.getUserId()))
                .thenReturn(false);

        // ACT & ASSERT
        assertThatThrownBy(() -> groupService.getGroupMembers(groupId, requesterEmail))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not a member");
    }

    @Test
    @DisplayName("Get group members handles null roles gracefully")
    void shouldHandleNullRoles() {
        // ARRANGE
        User member = new User();
        member.setUserId(UUID.randomUUID());
        member.setName("Member");
        member.setEmail("member@example.com");

        GroupMembership membership = new GroupMembership();
        membership.setUser(member);
        membership.setRole(null);  // No role assigned

        when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.of(user));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupMembershipRepository.existsByGroupAndUser_UserId(group, user.getUserId()))
                .thenReturn(true);
        when(groupMembershipRepository.findByGroup(group)).thenReturn(List.of(membership));

        // ACT
        List<UserSummaryResponseDto> results = groupService.getGroupMembers(groupId, requesterEmail);

        // ASSERT
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getRole()).isEqualTo("MEMBER"); 
    }


    @Test
    @DisplayName("Archive group successfully when requester is admin")
    void shouldArchiveGroupSuccessfully() {
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupMembershipRepository.existsByGroup_GroupIdAndUser_EmailAndRole_NameAndMembershipStatus(
                groupId, requesterEmail, "GROUP_ADMIN", "Active"
        )).thenReturn(true);

        groupService.archiveGroup(groupId, requesterEmail);

        assertThat(group.getStatus()).isEqualTo("Archived");
        verify(groupRepository).save(group);
    }

    @Test
    @DisplayName("Fail to archive group when requester is not admin")
    void shouldFailArchiveGroupWhenNotAdmin() {
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupMembershipRepository.existsByGroup_GroupIdAndUser_EmailAndRole_NameAndMembershipStatus(
                groupId, requesterEmail, "GROUP_ADMIN", "Active"
        )).thenReturn(false);

        assertThatThrownBy(() -> groupService.archiveGroup(groupId, requesterEmail))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Not authorized");

        verify(groupRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update group successfully when requester is admin and name is unique")
    void shouldUpdateGroupSuccessfully() {
        GroupUpdateRequestDto dto = new GroupUpdateRequestDto();
        dto.setGroupName("Updated Group");
        dto.setCategory("Travel");

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.of(user));
        when(groupMembershipRepository.existsByGroupAndUserAndRole_NameAndMembershipStatus(
                group, user, "GROUP_ADMIN", "Active"
        )).thenReturn(true);
        when(groupRepository.existsByGroupName("Updated Group")).thenReturn(false);
        when(groupRepository.save(group)).thenReturn(group);

        Group result = groupService.updateGroup(groupId, dto, requesterEmail);

        assertThat(result.getGroupName()).isEqualTo("Updated Group");
        assertThat(result.getCategory()).isEqualTo("Travel");
        verify(groupRepository).save(group);
    }

    @Test
    @DisplayName("Fail to update group when new group name already exists")
    void shouldFailUpdateGroupWhenNameExists() {
        GroupUpdateRequestDto dto = new GroupUpdateRequestDto();
        dto.setGroupName("Existing Group");
        dto.setCategory("Travel");

        group.setGroupName("Old Group");

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.of(user));
        when(groupMembershipRepository.existsByGroupAndUserAndRole_NameAndMembershipStatus(
                group, user, "GROUP_ADMIN", "Active"
        )).thenReturn(true);
        when(groupRepository.existsByGroupName("Existing Group")).thenReturn(true);

        assertThatThrownBy(() -> groupService.updateGroup(groupId, dto, requesterEmail))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Group name already exists");

        verify(groupRepository, never()).save(any());
    }

    @Test
    @DisplayName("Assign admin successfully")
    void shouldAssignAdminSuccessfully() {
        UUID targetUserId = UUID.randomUUID();

        GroupMembership targetMembership = new GroupMembership();
        targetMembership.setUser(new User());
        targetMembership.setRole(memberRole);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupMembershipRepository.existsByGroup_GroupIdAndUser_EmailAndRole_NameAndMembershipStatus(
                groupId, requesterEmail, "GROUP_ADMIN", "Active"
        )).thenReturn(true);
        when(groupMembershipRepository.findByGroup_GroupIdAndUser_UserIdAndMembershipStatus(
                groupId, targetUserId, "Active"
        )).thenReturn(Optional.of(targetMembership));
        when(roleRepository.getByName("GROUP_ADMIN")).thenReturn(adminRole);

        groupService.assignAdmin(groupId, targetUserId, requesterEmail);

        assertThat(targetMembership.getRole()).isEqualTo(adminRole);
        verify(groupMembershipRepository).save(targetMembership);
    }


    @Test
    @DisplayName("Fail to revoke admin when target is the last admin")
    void shouldFailRevokeLastAdmin() {
        UUID targetUserId = UUID.randomUUID();

        GroupMembership targetMembership = new GroupMembership();
        targetMembership.setRole(adminRole);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupMembershipRepository.existsByGroup_GroupIdAndUser_EmailAndRole_NameAndMembershipStatus(
                groupId, requesterEmail, "GROUP_ADMIN", "Active"
        )).thenReturn(true);
        when(groupMembershipRepository.findByGroup_GroupIdAndUser_UserIdAndMembershipStatus(
                groupId, targetUserId, "Active"
        )).thenReturn(Optional.of(targetMembership));
        when(groupMembershipRepository.countByGroup_GroupIdAndRole_NameAndMembershipStatus(
                groupId, "GROUP_ADMIN", "Active"
        )).thenReturn(1L);

        assertThatThrownBy(() -> groupService.revokeAdmin(groupId, targetUserId, requesterEmail))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("last group admin");

        verify(groupMembershipRepository, never()).save(any());
    }
}