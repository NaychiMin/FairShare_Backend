package com.example.fairsharebackend.controller;

import com.example.fairsharebackend.entity.dto.request.CreateInvitationRequestDto;
import com.example.fairsharebackend.entity.dto.response.CreateInvitationResponseDto;
import com.example.fairsharebackend.service.GroupInvitationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/group")
public class GroupInvitationController {

    private final GroupInvitationService groupInvitationService;

    public GroupInvitationController(GroupInvitationService groupInvitationService) {
        this.groupInvitationService = groupInvitationService;
    }

    @PostMapping("/{groupId}/invite")
    public ResponseEntity<CreateInvitationResponseDto> createInvitation(
            @PathVariable UUID groupId,
            @RequestParam String requesterEmail,
            @Valid @RequestBody CreateInvitationRequestDto dto
    ) {
        return ResponseEntity.ok(
                groupInvitationService.createInvitation(groupId, requesterEmail, dto)
        );
    }

    @PostMapping("/invite/accept")
    public ResponseEntity<String> acceptInvitation(
            @RequestParam String token,
            @RequestParam String requesterEmail
    ) {
        groupInvitationService.acceptInvitation(token, requesterEmail);
        return ResponseEntity.ok("Invitation accepted");
    }
}