package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.Group;
import com.example.fairsharebackend.entity.GroupMembership;
import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.entity.UserCredential;
import com.example.fairsharebackend.entity.dto.request.GroupCreateRequestDto;
import com.example.fairsharebackend.entity.dto.request.UserRegisterRequestDto;
import com.example.fairsharebackend.mapper.GroupMapper;
import com.example.fairsharebackend.mapper.UserMapper;
import com.example.fairsharebackend.repository.GroupMembershipRepository;
import com.example.fairsharebackend.repository.GroupRepository;
import com.example.fairsharebackend.repository.UserRepository;
import com.example.fairsharebackend.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class GroupServiceImpl implements GroupService {
    private static final Logger log = LoggerFactory.getLogger(GroupServiceImpl.class);

    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;
    private final UserRepository userRepository;
    private final GroupMembershipRepository groupMembershipRepository;
    private final JwtUtil jwtUtil;

    public GroupServiceImpl(
            GroupRepository groupRepository,
            GroupMapper groupMapper,
            UserRepository userRepository,
            GroupMembershipRepository groupMembershipRepository,
            JwtUtil jwtUtil
    ) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
        this.userRepository = userRepository;
        this.groupMembershipRepository = groupMembershipRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public Group createGroup(GroupCreateRequestDto dto) {
        log.error("Create new group with name :: {}", dto.getGroupName());
        try {
            Group group = this.groupMapper.toEntity(dto);
            User user = userRepository.getByName(dto.getAdmin());
            GroupMembership groupMembership = new GroupMembership();
            groupMembership.setGroup(group);
            groupMembership.setUser(user);
            groupMembership.setJoinedAt(LocalDateTime.now());
            groupMembership.setMembershipStatus("Active");
//            groupMembership.setRole(); set group admin, skip for now
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
}
