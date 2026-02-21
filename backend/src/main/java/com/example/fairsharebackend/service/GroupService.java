package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.Group;
import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.entity.dto.request.GroupCreateRequestDto;
import com.example.fairsharebackend.entity.dto.request.GroupUpdateRequestDto;
import com.example.fairsharebackend.entity.dto.response.GroupSummaryResponseDto;
import com.example.fairsharebackend.entity.dto.request.UserLoginRequestDto;
import com.example.fairsharebackend.entity.dto.request.UserRegisterRequestDto;
import com.example.fairsharebackend.entity.dto.request.UserUpdateRequestDto;
import com.example.fairsharebackend.entity.dto.response.UserLoginResponseDto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface GroupService {
    Group createGroup(GroupCreateRequestDto group);
    //List<Group> getAllGroups(String email);
    List<GroupSummaryResponseDto> getAllGroups(String email);
    Group updateGroup(UUID groupId, GroupUpdateRequestDto dto, String requesterEmail);
}
