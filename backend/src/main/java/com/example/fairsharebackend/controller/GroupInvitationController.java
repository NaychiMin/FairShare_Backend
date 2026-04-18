//package com.example.fairsharebackend.controller;
//
//import com.example.fairsharebackend.entity.dto.request.CreateInvitationRequestDto;
//import com.example.fairsharebackend.entity.dto.response.CreateInvitationResponseDto;
//import com.example.fairsharebackend.service.GroupInvitationService;
//import jakarta.validation.Valid;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/group")
//public class GroupInvitationController {
//
//    private final GroupInvitationService groupInvitationService;
//
//    public GroupInvitationController(GroupInvitationService groupInvitationService) {
//        this.groupInvitationService = groupInvitationService;
//    }
//
//    @PostMapping("/{groupId}/invite")
//    public ResponseEntity<CreateInvitationResponseDto> createInvitation(
//            @PathVariable UUID groupId,
//            @RequestParam String requesterEmail,
//            @Valid @RequestBody CreateInvitationRequestDto dto
//    ) {
//        return ResponseEntity.ok(
//                groupInvitationService.createInvitation(groupId, requesterEmail, dto)
//        );
//    }
//
//    @PostMapping("/invite/accept")
//    public ResponseEntity<String> acceptInvitation(
//            @RequestParam String token,
//            @RequestParam String requesterEmail
//    ) {
//        groupInvitationService.acceptInvitation(token, requesterEmail);
//        return ResponseEntity.ok("Invitation accepted");
//    }
//}
package com.example.fairsharebackend.controller;

import com.example.fairsharebackend.entity.dto.request.CreateInvitationRequestDto;
import com.example.fairsharebackend.entity.dto.response.CreateInvitationResponseDto;
import com.example.fairsharebackend.entity.dto.response.GroupInvitationDetailResponseDto;
import com.example.fairsharebackend.entity.dto.response.GroupInvitationListItemResponseDto;
import com.example.fairsharebackend.service.GroupInvitationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("/invite/by-token")
    public ResponseEntity<GroupInvitationDetailResponseDto> getInvitationByToken(
            @RequestParam String token
    ) {
        return ResponseEntity.ok(groupInvitationService.getInvitationByToken(token));
    }

    @GetMapping("/invitations/pending/{email}")
    public ResponseEntity<List<GroupInvitationListItemResponseDto>> getPendingInvitations(
            @PathVariable String email
    ) {
        return ResponseEntity.ok(groupInvitationService.getPendingInvitations(email));
    }

    @GetMapping("/invitations/sent/{email}")
    public ResponseEntity<List<GroupInvitationListItemResponseDto>> getSentInvitations(
            @PathVariable String email
    ) {
        return ResponseEntity.ok(groupInvitationService.getSentInvitations(email));
    }
}