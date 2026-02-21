package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.*;
import com.example.fairsharebackend.entity.dto.request.GroupCreateRequestDto;
import com.example.fairsharebackend.entity.dto.request.GroupUpdateRequestDto;
import com.example.fairsharebackend.entity.dto.response.GroupSummaryResponseDto; // new
import com.example.fairsharebackend.entity.dto.request.UserRegisterRequestDto;
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


    private static final String STATUS_ACTIVE = "Active";
    private static final String STATUS_ARCHIVED = "Archived";
    private static final String ROLE_GROUP_ADMIN = "GROUP_ADMIN";


    public GroupServiceImpl(
            GroupRepository groupRepository,
            GroupMapper groupMapper,
            UserRepository userRepository,
            GroupMembershipRepository groupMembershipRepository,
            RoleRepository roleRepository,
            JwtUtil jwtUtil
    ) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
        this.userRepository = userRepository;
        this.groupMembershipRepository = groupMembershipRepository;
        this.roleRepository = roleRepository;
        this.jwtUtil = jwtUtil;
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

//    public List<Group> getAllGroups(String email) {
//        User user = userRepository.findByEmail(email).get();
//        List<GroupMembership> groupMemberships = groupMembershipRepository.findAllByUserOrderByJoinedAtDesc(user);
//        List groups = new ArrayList();
//        for (GroupMembership membership : groupMemberships) {
//            groups.add(membership.getGroup());
//        }
//        return groups;
//    }


//    public List<GroupSummaryResponseDto> getAllGroups(String email) {
//        User user = userRepository.findByEmail(email).orElseThrow();
//
//        List<GroupMembership> groupMemberships =
//                groupMembershipRepository.findAllByUserOrderByJoinedAtDesc(user);
//
//        List<GroupSummaryResponseDto> result = new ArrayList<>();
//
//        for (GroupMembership membership : groupMemberships) {
//            Group g = membership.getGroup();
//            boolean isAdmin = membership.getRole() != null
//                    && "GROUP_ADMIN".equals(membership.getRole().getName())
//                    && "Active".equals(membership.getMembershipStatus());
//
//            result.add(new GroupSummaryResponseDto(
//                    g.getGroupId(),
//                    g.getGroupName(),
//                    g.getCategory(),
//                    isAdmin
//            ));
//        }
//        return result;
//    }

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
}
