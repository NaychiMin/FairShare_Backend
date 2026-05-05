package com.example.fairsharebackend.factory;

import com.example.fairsharebackend.entity.Group;
import com.example.fairsharebackend.entity.GroupMembership;
import com.example.fairsharebackend.entity.Role;
import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.entity.dto.request.GroupCreateRequestDto;
import com.example.fairsharebackend.mapper.GroupMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class GroupFactory {

    private static final String STATUS_ACTIVE = "Active";

    private final GroupMapper groupMapper;

    public GroupFactory(GroupMapper groupMapper) {
        this.groupMapper = groupMapper;
    }

    public Group createGroup(GroupCreateRequestDto dto) {
        Group group = groupMapper.toEntity(dto);
        group.setStatus(STATUS_ACTIVE);
        return group;
    }

    public GroupMembership createAdminMembership(Group group, User user, Role adminRole) {
        GroupMembership membership = new GroupMembership();
        membership.setGroup(group);
        membership.setUser(user);
        membership.setJoinedAt(LocalDateTime.now());
        membership.setMembershipStatus(STATUS_ACTIVE);
        membership.setRole(adminRole);
        return membership;
    }
}