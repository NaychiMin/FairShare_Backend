package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.*;
import com.example.fairsharebackend.entity.dto.request.GroupCreateRequestDto;
import com.example.fairsharebackend.entity.dto.request.GroupUpdateRequestDto;
import com.example.fairsharebackend.entity.dto.response.GroupSummaryResponseDto; // new
import com.example.fairsharebackend.entity.dto.response.UserSummaryResponseDto;
import com.example.fairsharebackend.exception.ResourceNotFoundException;
import com.example.fairsharebackend.entity.dto.request.UserRegisterRequestDto;
import com.example.fairsharebackend.entity.dto.response.GroupMemberActionStatusResponse;
import java.math.BigDecimal;
import com.example.fairsharebackend.mapper.GroupMapper;
import com.example.fairsharebackend.mapper.UserMapper;
import com.example.fairsharebackend.repository.*;
import com.example.fairsharebackend.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class GroupServiceImpl implements GroupService {
    private static final Logger log = LoggerFactory.getLogger(GroupServiceImpl.class);

    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;
    private final UserRepository userRepository;
    private final GroupMembershipRepository groupMembershipRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;
    private final BalanceService balanceService;


    private static final String STATUS_ACTIVE = "Active";
    private static final String STATUS_ARCHIVED = "Archived";
    private static final String ROLE_GROUP_ADMIN = "GROUP_ADMIN";;
    private static final String ROLE_GROUP_MEMBER = "GROUP_MEMBER";


    public GroupServiceImpl(
            GroupRepository groupRepository,
            GroupMapper groupMapper,
            UserRepository userRepository,
            GroupMembershipRepository groupMembershipRepository,
            RoleRepository roleRepository,
            JwtUtil jwtUtil,
            BalanceService balanceService
    ) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
        this.userRepository = userRepository;
        this.groupMembershipRepository = groupMembershipRepository;
        this.roleRepository = roleRepository;
        this.jwtUtil = jwtUtil;
        this.balanceService = balanceService;
    }

    @Override
    @Transactional
    public Group createGroup(GroupCreateRequestDto dto) {
        log.error("Create new group with name :: {}", dto.getGroupName());
        try {
            Group group = this.groupMapper.toEntity(dto);
            //User user = userRepository.getByName(dto.getAdmin());

            User user = userRepository.findByEmail(dto.getAdmin())
                    .orElseThrow(() -> new RuntimeException("Admin user not found"));

            GroupMembership groupMembership = new GroupMembership();
            groupMembership.setGroup(group);
            groupMembership.setUser(user);
            groupMembership.setJoinedAt(LocalDateTime.now());
            groupMembership.setMembershipStatus("Active");
            groupMembership.setRole(roleRepository.getByName("GROUP_ADMIN"));
            groupRepository.save(group);
            groupMembershipRepository.save(groupMembership);
            return group;

        } catch (BadCredentialsException e) {
            log.error("Exception :: {}", e.getMessage());

            throw e;
        } catch (Exception e) {
            log.error("Exception :: {}", e.getMessage());

            throw new RuntimeException("Unable to register at this time. Please try again later.");
        }
    }

    @Override
    public List<GroupSummaryResponseDto> getAllGroups(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<GroupMembership> memberships =
                groupMembershipRepository.findAllByUserOrderByJoinedAtDesc(user);

        List<GroupSummaryResponseDto> result = new ArrayList<>();
        for (GroupMembership m : memberships) {
            Group g = m.getGroup();
            if (!STATUS_ACTIVE.equals(g.getStatus())) continue;

            boolean isAdmin = m.getRole() != null
                    && ROLE_GROUP_ADMIN.equals(m.getRole().getName())
                    && STATUS_ACTIVE.equals(m.getMembershipStatus());

            result.add(new GroupSummaryResponseDto(
                    g.getGroupId(), g.getGroupName(), g.getCategory(), isAdmin, g.getStatus()
            ));
        }
        return result;
    }


    @Override
    public List<GroupSummaryResponseDto> getArchivedGroups(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<GroupMembership> memberships =
                groupMembershipRepository.findAllByUserOrderByJoinedAtDesc(user);

        List<GroupSummaryResponseDto> result = new ArrayList<>();
        for (GroupMembership m : memberships) {
            Group g = m.getGroup();
            if (!STATUS_ARCHIVED.equals(g.getStatus())) continue;

            boolean isAdmin = m.getRole() != null
                    && ROLE_GROUP_ADMIN.equals(m.getRole().getName())
                    && STATUS_ACTIVE.equals(m.getMembershipStatus());

            result.add(new GroupSummaryResponseDto(
                    g.getGroupId(), g.getGroupName(), g.getCategory(), isAdmin, g.getStatus()
            ));
        }
        return result;
    }

    @Override
    public void archiveGroup(UUID groupId, String requesterEmail) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        boolean isAdmin = groupMembershipRepository
                .existsByGroup_GroupIdAndUser_EmailAndRole_NameAndMembershipStatus(
                        groupId, requesterEmail, ROLE_GROUP_ADMIN, STATUS_ACTIVE
                );

        if (!isAdmin) {
            throw new RuntimeException("Not authorized to archive this group");
        }

        group.setStatus(STATUS_ARCHIVED);
        groupRepository.save(group);
    }

//    @Override
//    public void leaveGroup(UUID groupId, String requesterEmail) {
//        Group group = groupRepository.findById(groupId)
//                .orElseThrow(() -> new RuntimeException("Group not found"));
//
//        List<GroupMembership> groupMem = groupMembershipRepository.findByGroup(group);
//
//        for (GroupMembership m : groupMem) {
//            User u = m.getUser();
//            if (u.getEmail().equals(requesterEmail)){
//                groupMembershipRepository.delete((m));
//                return;
//            }
//        }
//    }


    @Override
    public void leaveGroup(UUID groupId, String requesterEmail) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        GroupMembership membership = groupMembershipRepository
                .findByGroup_GroupIdAndUser_UserIdAndMembershipStatus(groupId, requester.getUserId(), STATUS_ACTIVE)
                .orElseThrow(() -> new RuntimeException("User is not an active member of this group"));

        BigDecimal netBalance = balanceService.getNetBalanceForUserInGroup(group, requester);

        if (netBalance.compareTo(BigDecimal.ZERO) != 0) {
            throw new RuntimeException(getLeaveWarningMessage(netBalance));
        }

        groupMembershipRepository.delete(membership);
    }

    @Override
    public void unarchiveGroup(UUID groupId, String requesterEmail) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        boolean isAdmin = groupMembershipRepository
                .existsByGroup_GroupIdAndUser_EmailAndRole_NameAndMembershipStatus(
                        groupId, requesterEmail, ROLE_GROUP_ADMIN, STATUS_ACTIVE
                );

        if (!isAdmin) {
            throw new RuntimeException("Not authorized to unarchive this group");
        }

        group.setStatus(STATUS_ACTIVE);
        groupRepository.save(group);
    }

    @Override
    public Group updateGroup(UUID groupId, GroupUpdateRequestDto dto, String requesterEmail) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        log.info("requester id={}, email={}", requester.getUserId(), requester.getEmail());
        log.info("group id={}", group.getGroupId());

        boolean isAdmin = groupMembershipRepository
                .existsByGroupAndUserAndRole_NameAndMembershipStatus(group, requester, "GROUP_ADMIN", "Active");

        if (!isAdmin) {
            throw new RuntimeException("Not authorized to edit this group");
        }

        // Uniqueness handling (only if name changed)
        if (!group.getGroupName().equals(dto.getGroupName())
                && groupRepository.existsByGroupName(dto.getGroupName())) {
            throw new RuntimeException("Group name already exists");
        }

        group.setGroupName(dto.getGroupName());
        group.setCategory(dto.getCategory());
        return groupRepository.save(group);
    }

    @Override
    public void deleteGroup(UUID groupId, String requesterEmail) {

        // Ensure group exists
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Admin check (email-based)
        boolean isAdmin = groupMembershipRepository
                .existsByGroup_GroupIdAndUser_EmailAndRole_NameAndMembershipStatus(
                        groupId,
                        requesterEmail,
                        "GROUP_ADMIN",
                        "Active"
                );

        if (!isAdmin) {
            throw new RuntimeException("Not authorized to delete this group");
        }

        // Remove memberships first (safe for FK constraints)
        groupMembershipRepository.deleteByGroup_GroupId(groupId);

        // Delete the group
        groupRepository.delete(group);
    }

    @Override
    public Group getGroupById(UUID groupId, String requesterEmail) {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        
        boolean isMember = groupMembershipRepository.existsByGroupAndUser_UserId(group, requester.getUserId());
        if (!isMember) {
            throw new RuntimeException("User is not a member of this group");
        }
        
        return group;
    }

    @Override
    public List<UserSummaryResponseDto> getGroupMembers(UUID groupId, String requesterEmail) {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        
        boolean isMember = groupMembershipRepository.existsByGroupAndUser_UserId(group, requester.getUserId());
        if (!isMember) {
            throw new RuntimeException("User is not a member of this group");
        }
        
        List<GroupMembership> memberships = groupMembershipRepository.findByGroup(group);
        
        return memberships.stream()
                .map(membership -> {
                    User user = membership.getUser();
                    UserSummaryResponseDto dto = new UserSummaryResponseDto();
                    dto.setUserId(user.getUserId());
                    dto.setName(user.getName());
                    dto.setEmail(user.getEmail());
                    dto.setRole(membership.getRole() != null ? membership.getRole().getName() : "MEMBER");
                    return dto;
                })
                .collect(Collectors.toList());
    }



    @Override
    public void assignAdmin(UUID groupId, UUID userId, String requesterEmail) {
        groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        boolean requesterIsAdmin = groupMembershipRepository
                .existsByGroup_GroupIdAndUser_EmailAndRole_NameAndMembershipStatus(
                        groupId, requesterEmail, ROLE_GROUP_ADMIN, STATUS_ACTIVE
                );

        if (!requesterIsAdmin) {
            throw new RuntimeException("Not authorized to assign admin privileges");
        }

        GroupMembership targetMembership = groupMembershipRepository
                .findByGroup_GroupIdAndUser_UserIdAndMembershipStatus(groupId, userId, STATUS_ACTIVE)
                .orElseThrow(() -> new RuntimeException("Member not found in this group"));

        if (targetMembership.getRole() != null
                && ROLE_GROUP_ADMIN.equals(targetMembership.getRole().getName())) {
            throw new RuntimeException("User is already a group admin");
        }

        Role adminRole = roleRepository.getByName(ROLE_GROUP_ADMIN);
        if (adminRole == null) {
            throw new RuntimeException("Missing role: GROUP_ADMIN");
        }

        targetMembership.setRole(adminRole);
        groupMembershipRepository.save(targetMembership);
    }

    @Override
    public void revokeAdmin(UUID groupId, UUID userId, String requesterEmail) {
        groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        boolean requesterIsAdmin = groupMembershipRepository
                .existsByGroup_GroupIdAndUser_EmailAndRole_NameAndMembershipStatus(
                        groupId, requesterEmail, ROLE_GROUP_ADMIN, STATUS_ACTIVE
                );

        if (!requesterIsAdmin) {
            throw new RuntimeException("Not authorized to revoke admin privileges");
        }

        GroupMembership targetMembership = groupMembershipRepository
                .findByGroup_GroupIdAndUser_UserIdAndMembershipStatus(groupId, userId, STATUS_ACTIVE)
                .orElseThrow(() -> new RuntimeException("Member not found in this group"));

        if (targetMembership.getRole() == null
                || !ROLE_GROUP_ADMIN.equals(targetMembership.getRole().getName())) {
            throw new RuntimeException("User is not a group admin");
        }

        long adminCount = groupMembershipRepository
                .countByGroup_GroupIdAndRole_NameAndMembershipStatus(
                        groupId, ROLE_GROUP_ADMIN, STATUS_ACTIVE
                );

        if (adminCount <= 1) {
            throw new RuntimeException("Cannot revoke the last group admin");
        }

        Role memberRole = roleRepository.getByName(ROLE_GROUP_MEMBER);
        if (memberRole == null) {
            throw new RuntimeException("Missing role: GROUP_MEMBER");
        }

        targetMembership.setRole(memberRole);
        groupMembershipRepository.save(targetMembership);
    }


    private String getLeaveWarningMessage(BigDecimal netBalance) {
        if (netBalance.compareTo(BigDecimal.ZERO) < 0) {
            return "You cannot leave this group because you still owe money.";
        }
        if (netBalance.compareTo(BigDecimal.ZERO) > 0) {
            return "You cannot leave this group because you are still owed money.";
        }
        return null;
    }

    private String getRemoveWarningMessage(BigDecimal netBalance) {
        if (netBalance.compareTo(BigDecimal.ZERO) < 0) {
            return "This member cannot be removed because they still owe money.";
        }
        if (netBalance.compareTo(BigDecimal.ZERO) > 0) {
            return "This member cannot be removed because they are still owed money.";
        }
        return null;
    }

    @Override
    public List<GroupMemberActionStatusResponse> getGroupMemberActionStatuses(UUID groupId, String requesterEmail) {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        boolean isMember = groupMembershipRepository.existsByGroupAndUser_UserId(group, requester.getUserId());
        if (!isMember) {
            throw new RuntimeException("User is not a member of this group");
        }

        boolean requesterIsAdmin = groupMembershipRepository
                .existsByGroup_GroupIdAndUser_EmailAndRole_NameAndMembershipStatus(
                        groupId, requesterEmail, ROLE_GROUP_ADMIN, STATUS_ACTIVE
                );

        List<GroupMembership> memberships = groupMembershipRepository.findByGroup(group);

        return memberships.stream().map(membership -> {
            User memberUser = membership.getUser();
            BigDecimal netBalance = balanceService.getNetBalanceForUserInGroup(group, memberUser);
            boolean zeroBalance = netBalance.compareTo(BigDecimal.ZERO) == 0;
            boolean isSelf = memberUser.getUserId().equals(requester.getUserId());

            boolean canLeave = isSelf && zeroBalance;
            boolean canRemove = requesterIsAdmin && !isSelf && zeroBalance;

            String warningMessage = null;
            if (isSelf && !zeroBalance) {
                warningMessage = getLeaveWarningMessage(netBalance);
            } else if (!isSelf && requesterIsAdmin && !zeroBalance) {
                warningMessage = getRemoveWarningMessage(netBalance);
            }

            return new GroupMemberActionStatusResponse(
                    memberUser.getUserId(),
                    netBalance,
                    canLeave,
                    canRemove,
                    warningMessage
            );
        }).toList();
    }


    @Override
    public void removeGroupMember(UUID groupId, UUID userId, String requesterEmail) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean requesterIsAdmin = groupMembershipRepository
                .existsByGroup_GroupIdAndUser_EmailAndRole_NameAndMembershipStatus(
                        groupId, requesterEmail, ROLE_GROUP_ADMIN, STATUS_ACTIVE
                );

        if (!requesterIsAdmin) {
            throw new RuntimeException("Only group admins can remove members");
        }

        if (requester.getUserId().equals(userId)) {
            throw new RuntimeException("Admins cannot remove themselves. Use leave group instead.");
        }

        GroupMembership targetMembership = groupMembershipRepository
                .findByGroup_GroupIdAndUser_UserIdAndMembershipStatus(groupId, userId, STATUS_ACTIVE)
                .orElseThrow(() -> new RuntimeException("Target member not found"));

        User targetUser = targetMembership.getUser();
        BigDecimal netBalance = balanceService.getNetBalanceForUserInGroup(group, targetUser);

        if (netBalance.compareTo(BigDecimal.ZERO) != 0) {
            throw new RuntimeException(getRemoveWarningMessage(netBalance));
        }

        groupMembershipRepository.delete(targetMembership);
    }


}
