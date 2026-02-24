package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.*;
import com.example.fairsharebackend.entity.dto.request.GroupCreateRequestDto;
import com.example.fairsharebackend.mapper.GroupMapper;
import com.example.fairsharebackend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock private GroupRepository groupRepository;
    @Mock private GroupMapper groupMapper;
    @Mock private UserRepository userRepository;
    @Mock private GroupMembershipRepository groupMembershipRepository;
    @Mock private RoleRepository roleRepository;

    @InjectMocks
    private GroupServiceImpl groupService;

    private User testUser;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setName("JohnDoe");
        testUser.setEmail("john@example.com");

        adminRole = new Role();
        adminRole.setName("GROUP_ADMIN");
    }

    @Test
    void createGroup_Success() {
        // Arrange
        GroupCreateRequestDto dto = new GroupCreateRequestDto();
        dto.setGroupName("Test Group");
        dto.setAdmin("JohnDoe");

        Group groupEntity = new Group();
        groupEntity.setGroupName("Test Group");

        when(groupMapper.toEntity(any())).thenReturn(groupEntity);
        when(userRepository.getByName("JohnDoe")).thenReturn(testUser);
        when(roleRepository.getByName("GROUP_ADMIN")).thenReturn(adminRole);
        when(groupRepository.save(any(Group.class))).thenReturn(groupEntity);

        // Act
        Group result = groupService.createGroup(dto);

        // Assert
        assertNotNull(result);
        assertEquals("Test Group", result.getGroupName());

        // Verify interactions
        verify(groupRepository, times(1)).save(any(Group.class));
        verify(groupMembershipRepository, times(1)).save(any(GroupMembership.class));
    }

    @Test
    void getAllGroups_Success() {
        // Arrange
        String email = "john@example.com";
        Group group1 = new Group();
        group1.setGroupName("Group 1");

        GroupMembership membership = new GroupMembership();
        membership.setGroup(group1);
        membership.setUser(testUser);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(groupMembershipRepository.findAllByUserOrderByJoinedAtDesc(testUser))
                .thenReturn(List.of(membership));

        // Act
        List<Group> result = groupService.getAllGroups(email);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Group 1", result.get(0).getGroupName());
    }
}