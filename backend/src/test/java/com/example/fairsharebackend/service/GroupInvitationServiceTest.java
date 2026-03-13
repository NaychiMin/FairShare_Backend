package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.*;
import com.example.fairsharebackend.entity.dto.request.CreateInvitationRequestDto;
import com.example.fairsharebackend.entity.dto.response.CreateInvitationResponseDto;
import com.example.fairsharebackend.entity.dto.response.GroupInvitationDetailResponseDto;
import com.example.fairsharebackend.entity.dto.response.GroupInvitationListItemResponseDto;
import com.example.fairsharebackend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupInvitationServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupMembershipRepository groupMembershipRepository;

    @Mock
    private GroupInvitationRepository groupInvitationRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private GroupInvitationServiceImpl groupInvitationService;

    private Group group;
    private User requester;
    private User invitedUser;
    private Role memberRole;
    private UUID groupId;
    private String requesterEmail;

    @BeforeEach
    void setUp() {
        groupId = UUID.randomUUID();
        requesterEmail = "admin@example.com";

        group = new Group();
        group.setGroupId(groupId);
        group.setGroupName("Trip Group");
        group.setCategory("Travel");

        requester = new User();
        requester.setUserId(UUID.randomUUID());
        requester.setEmail(requesterEmail);
        requester.setName("Admin");

        invitedUser = new User();
        invitedUser.setUserId(UUID.randomUUID());
        invitedUser.setEmail("friend@example.com");
        invitedUser.setName("Friend");

        memberRole = new Role();
        memberRole.setRoleId(UUID.randomUUID());
        memberRole.setName("GROUP_MEMBER");
    }

    @Test
    @DisplayName("Create invitation successfully")
    void shouldCreateInvitationSuccessfully() {
        CreateInvitationRequestDto dto = new CreateInvitationRequestDto();
        dto.setInvitedEmail("friend@example.com");

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.of(requester));
        when(groupMembershipRepository.existsByGroup_GroupIdAndUser_EmailAndRole_NameAndMembershipStatus(
                groupId, requesterEmail, "GROUP_ADMIN", "Active"
        )).thenReturn(true);
        when(groupMembershipRepository.existsByGroup_GroupIdAndUser_EmailAndMembershipStatus(
                groupId, dto.getInvitedEmail(), "Active"
        )).thenReturn(false);

        CreateInvitationResponseDto response =
                groupInvitationService.createInvitation(groupId, requesterEmail, dto);

        assertThat(response).isNotNull();
        assertThat(response.getInvitedEmail()).isEqualTo("friend@example.com");
        assertThat(response.getStatus()).isEqualTo("PENDING");
        assertThat(response.getToken()).isNotBlank();
        assertThat(response.getInviteLink()).contains(response.getToken());

        verify(groupInvitationRepository).save(any(GroupInvitation.class));
    }

    @Test
    @DisplayName("Fail to create invitation when requester is not admin")
    void shouldFailCreateInvitationWhenNotAdmin() {
        CreateInvitationRequestDto dto = new CreateInvitationRequestDto();
        dto.setInvitedEmail("friend@example.com");

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.of(requester));
        when(groupMembershipRepository.existsByGroup_GroupIdAndUser_EmailAndRole_NameAndMembershipStatus(
                groupId, requesterEmail, "GROUP_ADMIN", "Active"
        )).thenReturn(false);

        assertThatThrownBy(() -> groupInvitationService.createInvitation(groupId, requesterEmail, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Not authorized");

        verify(groupInvitationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Fail to create invitation when invited user is already a member")
    void shouldFailCreateInvitationWhenAlreadyMember() {
        CreateInvitationRequestDto dto = new CreateInvitationRequestDto();
        dto.setInvitedEmail("friend@example.com");

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.of(requester));
        when(groupMembershipRepository.existsByGroup_GroupIdAndUser_EmailAndRole_NameAndMembershipStatus(
                groupId, requesterEmail, "GROUP_ADMIN", "Active"
        )).thenReturn(true);
        when(groupMembershipRepository.existsByGroup_GroupIdAndUser_EmailAndMembershipStatus(
                groupId, dto.getInvitedEmail(), "Active"
        )).thenReturn(true);

        assertThatThrownBy(() -> groupInvitationService.createInvitation(groupId, requesterEmail, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already in this group");

        verify(groupInvitationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Accept invitation successfully")
    void shouldAcceptInvitationSuccessfully() {
        String token = "abc123token";

        GroupInvitation invitation = new GroupInvitation();
        invitation.setToken(token);
        invitation.setGroup(group);
        invitation.setInvitedEmail("friend@example.com");
        invitation.setStatus("PENDING");
        invitation.setExpiresAt(LocalDateTime.now().plusDays(1));

        when(groupInvitationRepository.findByToken(token)).thenReturn(Optional.of(invitation));
        when(groupMembershipRepository.existsByGroup_GroupIdAndUser_EmailAndMembershipStatus(
                groupId, "friend@example.com", "Active"
        )).thenReturn(false);
        when(userRepository.findByEmail("friend@example.com")).thenReturn(Optional.of(invitedUser));
        when(roleRepository.getByName("GROUP_MEMBER")).thenReturn(memberRole);

        groupInvitationService.acceptInvitation(token, "friend@example.com");

        assertThat(invitation.getStatus()).isEqualTo("ACCEPTED");
        verify(groupMembershipRepository).save(any(GroupMembership.class));
        verify(groupInvitationRepository, times(1)).save(invitation);
    }

    @Test
    @DisplayName("Fail to accept invitation when expired")
    void shouldFailAcceptInvitationWhenExpired() {
        String token = "expiredtoken";

        GroupInvitation invitation = new GroupInvitation();
        invitation.setToken(token);
        invitation.setGroup(group);
        invitation.setStatus("PENDING");
        invitation.setExpiresAt(LocalDateTime.now().minusDays(1));

        when(groupInvitationRepository.findByToken(token)).thenReturn(Optional.of(invitation));

        assertThatThrownBy(() -> groupInvitationService.acceptInvitation(token, "friend@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("expired");

        verify(groupMembershipRepository, never()).save(any());
    }

    @Test
    @DisplayName("Fail to accept invitation when email does not match invited email")
    void shouldFailAcceptInvitationWhenEmailMismatch() {
        String token = "token123";

        GroupInvitation invitation = new GroupInvitation();
        invitation.setToken(token);
        invitation.setGroup(group);
        invitation.setInvitedEmail("someoneelse@example.com");
        invitation.setStatus("PENDING");
        invitation.setExpiresAt(LocalDateTime.now().plusDays(1));

        when(groupInvitationRepository.findByToken(token)).thenReturn(Optional.of(invitation));

        assertThatThrownBy(() -> groupInvitationService.acceptInvitation(token, "friend@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("different email");

        verify(groupMembershipRepository, never()).save(any());
    }

    @Test
    @DisplayName("Get invitation by token successfully")
    void shouldGetInvitationByToken() {
        String token = "token123";

        GroupInvitation invitation = new GroupInvitation();
        invitation.setToken(token);
        invitation.setGroup(group);
        invitation.setInvitedEmail("friend@example.com");
        invitation.setStatus("PENDING");
        invitation.setExpiresAt(LocalDateTime.now().plusDays(7));
        invitation.setCreatedAt(LocalDateTime.now());
        invitation.setCreatedBy(requester);

        when(groupInvitationRepository.findByToken(token)).thenReturn(Optional.of(invitation));

        GroupInvitationDetailResponseDto result = groupInvitationService.getInvitationByToken(token);

        assertThat(result.getToken()).isEqualTo(token);
        assertThat(result.getGroupName()).isEqualTo("Trip Group");
        assertThat(result.getInvitedEmail()).isEqualTo("friend@example.com");
        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getCreatedByEmail()).isEqualTo(requesterEmail);
    }

    @Test
    @DisplayName("Get pending invitations successfully")
    void shouldGetPendingInvitations() {
        GroupInvitation invitation = new GroupInvitation();
        invitation.setToken("token123");
        invitation.setGroup(group);
        invitation.setInvitedEmail("friend@example.com");
        invitation.setStatus("PENDING");
        invitation.setExpiresAt(LocalDateTime.now().plusDays(7));

        when(groupInvitationRepository.findAllByInvitedEmailAndStatusOrderByCreatedAtDesc(
                "friend@example.com", "PENDING"
        )).thenReturn(List.of(invitation));

        List<GroupInvitationListItemResponseDto> result =
                groupInvitationService.getPendingInvitations("friend@example.com");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getToken()).isEqualTo("token123");
        assertThat(result.get(0).getGroupName()).isEqualTo("Trip Group");
    }
}