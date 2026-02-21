package com.example.fairsharebackend.controller;

import com.example.fairsharebackend.entity.Group;
import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.entity.dto.request.GroupCreateRequestDto;
import com.example.fairsharebackend.entity.dto.request.GroupUpdateRequestDto;
import com.example.fairsharebackend.entity.dto.response.GroupSummaryResponseDto;
import com.example.fairsharebackend.entity.dto.request.UserRegisterRequestDto;
import com.example.fairsharebackend.entity.dto.request.UserUpdateRequestDto;
import com.example.fairsharebackend.entity.dto.response.UserRegisterResponseDto;
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

//    @GetMapping("/all/{email}")
//    public ResponseEntity<List<Group>> getAllGroups(
//            @PathVariable String email
//    ) {
//        List<Group> groups = this.groupService.getAllGroups(email);
//        return new ResponseEntity<>(groups, HttpStatus.OK);
//    }

    @GetMapping("/all/{email}")
    public ResponseEntity<List<GroupSummaryResponseDto>> getAll(@PathVariable String email) {
        return ResponseEntity.ok(groupService.getAllGroups(email));
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
}