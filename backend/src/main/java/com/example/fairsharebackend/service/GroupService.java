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
import com.example.fairsharebackend.entity.dto.response.UserSummaryResponseDto;
import com.example.fairsharebackend.entity.dto.response.GroupMemberActionStatusResponse;
import com.example.fairsharebackend.entity.dto.response.GroupActionStatusResponse;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface GroupService {

    Group createGroup(GroupCreateRequestDto group);
    List<GroupSummaryResponseDto> getAllGroups(String email); // active groups
    void leaveGroup(UUID groupId, String requesterEmail);
    List<GroupSummaryResponseDto> getArchivedGroups(String email);      // archived
    void archiveGroup(UUID groupId, String requesterEmail);
    void unarchiveGroup(UUID groupId, String requesterEmail);

    Group updateGroup(UUID groupId, GroupUpdateRequestDto dto, String requesterEmail);
    void deleteGroup(UUID groupId, String requesterEmail);

    List<UserSummaryResponseDto> getGroupMembers(UUID groupId, String requesterEmail);
    Group getGroupById(UUID groupId, String requesterEmail);

    void assignAdmin(UUID groupId, UUID userId, String requesterEmail);
    void revokeAdmin(UUID groupId, UUID userId, String requesterEmail);

    List<GroupMemberActionStatusResponse> getGroupMemberActionStatuses(UUID groupId, String requesterEmail);

    void removeGroupMember(UUID groupId, UUID userId, String requesterEmail);



    GroupActionStatusResponse getGroupActionStatus(UUID groupId, String requesterEmail);


}
