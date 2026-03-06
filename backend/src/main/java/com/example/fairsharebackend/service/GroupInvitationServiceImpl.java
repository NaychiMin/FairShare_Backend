package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.*;
import com.example.fairsharebackend.entity.dto.request.CreateInvitationRequestDto;
import com.example.fairsharebackend.entity.dto.response.CreateInvitationResponseDto;
import com.example.fairsharebackend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class GroupInvitationServiceImpl implements GroupInvitationService {

    private static final String ROLE_GROUP_ADMIN = "GROUP_ADMIN";
    private static final String ROLE_GROUP_MEMBER = "GROUP_MEMBER";
    private static final String STATUS_ACTIVE = "Active";
    private static final String INVITE_PENDING = "PENDING";
    private static final String INVITE_ACCEPTED = "ACCEPTED";

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMembershipRepository groupMembershipRepository;
    private final GroupInvitationRepository groupInvitationRepository;
    private final RoleRepository roleRepository;

    public GroupInvitationServiceImpl(
            GroupRepository groupRepository,
            UserRepository userRepository,
            GroupMembershipRepository groupMembershipRepository,
            GroupInvitationRepository groupInvitationRepository,
            RoleRepository roleRepository
    ) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.groupMembershipRepository = groupMembershipRepository;
        this.groupInvitationRepository = groupInvitationRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public CreateInvitationResponseDto createInvitation(UUID groupId, String requesterEmail, CreateInvitationRequestDto dto) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new RuntimeException("Requester not found"));

        boolean isAdmin = groupMembershipRepository
                .existsByGroup_GroupIdAndUser_EmailAndRole_NameAndMembershipStatus(
                        groupId, requesterEmail, ROLE_GROUP_ADMIN, STATUS_ACTIVE
                );

        if (!isAdmin) {
            throw new RuntimeException("Not authorized to invite users to this group");
        }

        if (dto.getInvitedEmail() != null && !dto.getInvitedEmail().isBlank()) {
            boolean alreadyMember = groupMembershipRepository
                    .existsByGroup_GroupIdAndUser_EmailAndMembershipStatus(
                            groupId, dto.getInvitedEmail(), STATUS_ACTIVE
                    );

            if (alreadyMember) {
                throw new RuntimeException("User is already in this group");
            }
        }

        GroupInvitation invitation = new GroupInvitation();
        invitation.setGroup(group);
        invitation.setCreatedBy(requester);
        invitation.setInvitedEmail(dto.getInvitedEmail());
        invitation.setToken(UUID.randomUUID().toString().replace("-", ""));
        invitation.setStatus(INVITE_PENDING);
        invitation.setCreatedAt(LocalDateTime.now());
        invitation.setExpiresAt(LocalDateTime.now().plusDays(7));

        groupInvitationRepository.save(invitation);

        String inviteLink = "http://localhost:5173/groups/invite/accept?token=" + invitation.getToken();

        return new CreateInvitationResponseDto(
                invitation.getToken(),
                inviteLink,
                invitation.getInvitedEmail(),
                invitation.getStatus()
        );
    }

    @Override
    public void acceptInvitation(String token, String requesterEmail) {
        GroupInvitation invitation = groupInvitationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));

        if (!INVITE_PENDING.equals(invitation.getStatus())) {
            throw new RuntimeException("Invitation is no longer valid");
        }

        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invitation has expired");
        }

        if (invitation.getInvitedEmail() != null
                && !invitation.getInvitedEmail().equalsIgnoreCase(requesterEmail)) {
            throw new RuntimeException("This invitation was issued to a different email");
        }

        boolean alreadyMember = groupMembershipRepository
                .existsByGroup_GroupIdAndUser_EmailAndMembershipStatus(
                        invitation.getGroup().getGroupId(),
                        requesterEmail,
                        STATUS_ACTIVE
                );

        if (alreadyMember) {
            throw new RuntimeException("User is already in this group");
        }

        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role memberRole = roleRepository.getByName(ROLE_GROUP_MEMBER);
        if (memberRole == null) {
            throw new RuntimeException("Missing role: GROUP_MEMBER");
        }

        GroupMembership membership = new GroupMembership();
        membership.setGroup(invitation.getGroup());
        membership.setUser(requester);
        membership.setRole(memberRole);
        membership.setMembershipStatus(STATUS_ACTIVE);
        membership.setJoinedAt(LocalDateTime.now());

        groupMembershipRepository.save(membership);

        invitation.setStatus(INVITE_ACCEPTED);
        groupInvitationRepository.save(invitation);
    }
}