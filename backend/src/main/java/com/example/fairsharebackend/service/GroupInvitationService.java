package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.dto.request.CreateInvitationRequestDto;
import com.example.fairsharebackend.entity.dto.response.CreateInvitationResponseDto;

import java.util.UUID;

public interface GroupInvitationService {
    CreateInvitationResponseDto createInvitation(UUID groupId, String requesterEmail, CreateInvitationRequestDto dto);
    void acceptInvitation(String token, String requesterEmail);
}