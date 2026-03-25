package com.example.fairsharebackend.controller;

import com.example.fairsharebackend.entity.dto.response.UserBadgeResponseDto;
import com.example.fairsharebackend.service.UserBadgeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/user/badges")
public class UserBadgeController {

    private final UserBadgeService userBadgeService;

    public UserBadgeController(UserBadgeService userBadgeService) {
        this.userBadgeService = userBadgeService;
    }

    @GetMapping
    public ResponseEntity<List<UserBadgeResponseDto>> getMyBadges(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();

        List<UserBadgeResponseDto> badges =
                userBadgeService.getUserBadges(email);

        return ResponseEntity.ok(badges);
    }

    // @GetMapping
    // public ResponseEntity<List<UserBadgeResponseDto>> getMyBadges(){
    //     String email = "mary@gmail.com";

    //     List<UserBadgeResponseDto> badges =
    //             userBadgeService.getUserBadges(email);

    //     return ResponseEntity.ok(badges);
    // }
}