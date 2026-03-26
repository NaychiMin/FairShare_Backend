package com.example.fairsharebackend.controller;

import com.example.fairsharebackend.entity.dto.response.UserBadgeResponseDto;
import com.example.fairsharebackend.service.UserBadgeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@RestController
@RequestMapping("/user/badges")
public class UserBadgeController {

    private final UserBadgeService userBadgeService;

    public UserBadgeController(UserBadgeService userBadgeService) {
        this.userBadgeService = userBadgeService;
    }

    @GetMapping("/{email}")
    public ResponseEntity<List<UserBadgeResponseDto>> getMyBadges(
            @PathVariable String email
    ) {
        List<UserBadgeResponseDto> badges =
                userBadgeService.getUserBadges(email);

        return ResponseEntity.ok(badges);
    }
}