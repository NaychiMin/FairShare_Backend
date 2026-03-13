package com.example.fairsharebackend.controller;

import com.example.fairsharebackend.entity.*;
import com.example.fairsharebackend.entity.dto.request.GroupCreateRequestDto;
import com.example.fairsharebackend.entity.dto.request.GroupUpdateRequestDto;
import com.example.fairsharebackend.entity.dto.response.GroupSummaryResponseDto;
import com.example.fairsharebackend.entity.dto.request.UserRegisterRequestDto;
import com.example.fairsharebackend.entity.dto.request.UserUpdateRequestDto;
import com.example.fairsharebackend.entity.dto.response.UserRegisterResponseDto;
import com.example.fairsharebackend.entity.dto.response.UserSummaryResponseDto;
import com.example.fairsharebackend.service.GroupService;
import com.example.fairsharebackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/group")
public class GroupController {
    private final GroupService groupService;
    public GroupController(
            GroupService groupService
    ) {
        this.groupService = groupService;
    }

    @PostMapping("/create-new-group")
    public ResponseEntity<String> createGroup(
            @Valid @RequestBody GroupCreateRequestDto requestDto
    ) {
        Group group = this.groupService.createGroup(requestDto);
        return new ResponseEntity<>(group.getGroupName(), HttpStatus.CREATED);
    }

    @GetMapping("/all/{email}")
    public ResponseEntity<List<GroupSummaryResponseDto>> getAll(@PathVariable String email) {
        return ResponseEntity.ok(groupService.getAllGroups(email));
    }


    @GetMapping("/archived/{email}")
    public ResponseEntity<List<GroupSummaryResponseDto>> getArchived(@PathVariable String email) {
        return ResponseEntity.ok(groupService.getArchivedGroups(email));
    }

    @PutMapping("/archive/{groupId}")
    public ResponseEntity<String> archiveGroup(
            @PathVariable UUID groupId,
            @RequestParam String requesterEmail
    ) {
        groupService.archiveGroup(groupId, requesterEmail);
        return ResponseEntity.ok("Group archived");
    }

    @PutMapping("/unarchive/{groupId}")
    public ResponseEntity<String> unarchiveGroup(
            @PathVariable UUID groupId,
            @RequestParam String requesterEmail
    ) {
        groupService.unarchiveGroup(groupId, requesterEmail);
        return ResponseEntity.ok("Group unarchived");
    }

    // Edit group info (name + category)
    @PutMapping("/{groupId}")
    public ResponseEntity<String> updateGroup(
            @PathVariable UUID groupId,
            @Valid @RequestBody GroupUpdateRequestDto dto,
            @RequestParam String requesterEmail
    ) {
        Group updated = groupService.updateGroup(groupId, dto, requesterEmail);
        return ResponseEntity.ok(updated.getGroupName());
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<String> deleteGroup(
            @PathVariable UUID groupId,
            @RequestParam String requesterEmail
    ) {
        groupService.deleteGroup(groupId, requesterEmail);
        return ResponseEntity.ok("Group deleted");
    }

    // Method used in Group Details Page
    @GetMapping("/{groupId}")
    public ResponseEntity<Group> getGroupById(
            @PathVariable UUID groupId,
            @RequestParam String requesterEmail  
    ) {
        Group group = groupService.getGroupById(groupId, requesterEmail);
        return ResponseEntity.ok(group);
    }

    // Method used in Group Details Page
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<UserSummaryResponseDto>> getGroupMembers(
            @PathVariable UUID groupId,
            @RequestParam String requesterEmail
    ) {
        List<UserSummaryResponseDto> members = groupService.getGroupMembers(groupId, requesterEmail);
        return ResponseEntity.ok(members);
    }

    @PutMapping("/{groupId}/members/{userId}/assign-admin")
    public ResponseEntity<String> assignAdmin(
            @PathVariable UUID groupId,
            @PathVariable UUID userId,
            @RequestParam String requesterEmail
    ) {
        groupService.assignAdmin(groupId, userId, requesterEmail);
        return ResponseEntity.ok("Admin privileges assigned");
    }

    @PutMapping("/{groupId}/members/{userId}/revoke-admin")
    public ResponseEntity<String> revokeAdmin(
            @PathVariable UUID groupId,
            @PathVariable UUID userId,
            @RequestParam String requesterEmail
    ) {
        groupService.revokeAdmin(groupId, userId, requesterEmail);
        return ResponseEntity.ok("Admin privileges revoked");
    }
}