package com.example.fairsharebackend.controller;

import com.example.fairsharebackend.entity.Group;
import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.entity.dto.request.GroupCreateRequestDto;
import com.example.fairsharebackend.entity.dto.request.UserRegisterRequestDto;
import com.example.fairsharebackend.entity.dto.request.UserUpdateRequestDto;
import com.example.fairsharebackend.entity.dto.response.UserRegisterResponseDto;
import com.example.fairsharebackend.service.GroupService;
import com.example.fairsharebackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(group.getGroupName());
    }
}