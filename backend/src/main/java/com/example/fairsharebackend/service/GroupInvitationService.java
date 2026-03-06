package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.dto.request.CreateInvitationRequestDto;
import com.example.fairsharebackend.entity.dto.response.CreateInvitationResponseDto;
import com.example.fairsharebackend.entity.dto.response.GroupInvitationDetailResponseDto;
import com.example.fairsharebackend.entity.dto.response.GroupInvitationListItemResponseDto;

import java.util.UUID;
import java.util.List;

public interface GroupInvitationService {
    CreateInvitationResponseDto createInvitation(UUID groupId, String requesterEmail, CreateInvitationRequestDto dto);
    void acceptInvitation(String token, String requesterEmail);

    GroupInvitationDetailResponseDto getInvitationByToken(String token);
    List<GroupInvitationListItemResponseDto> getPendingInvitations(String email);
    List<GroupInvitationListItemResponseDto> getSentInvitations(String requesterEmail);
}