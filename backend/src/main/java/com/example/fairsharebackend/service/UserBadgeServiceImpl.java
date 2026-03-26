package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.dto.response.UserBadgeResponseDto;
import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.entity.UserBadge;
import com.example.fairsharebackend.repository.UserBadgeRepository;
import com.example.fairsharebackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserBadgeServiceImpl implements UserBadgeService {

    private final UserRepository userRepository;
    private final UserBadgeRepository userBadgeRepository;

    public UserBadgeServiceImpl(UserRepository userRepository,
                                UserBadgeRepository userBadgeRepository) {
        this.userRepository = userRepository;
        this.userBadgeRepository = userBadgeRepository;
    }

    @Override
    public List<UserBadgeResponseDto> getUserBadges(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserBadge> badges = userBadgeRepository.findByUserOrderByCreatedAtDesc(user);

        return badges.stream()
                     .map(this::mapToDto)
                     .toList();
    }

    // private UserBadgeResponseDto mapToDto(UserBadge ub) {
    //     UserBadgeResponseDto dto = new UserBadgeResponseDto();
    //     dto.setBadgeName(ub.getBadge().getName());
    //     dto.setDescription(ub.getBadge().getDescription());
    //     dto.setBadgeType(ub.getBadge().getBadgeType().name());
    //     dto.setBadgeScope(ub.getBadge().getBadgeScope().name());
    //     dto.setEarnedAt(ub.getCreatedAt());
    //     if (ub.getGroup() != null) {
    //         dto.setGroupName(ub.getGroup().getGroupName());
    //     }
    //     return dto;
    // }

    private UserBadgeResponseDto mapToDto(UserBadge ub) {
        UserBadgeResponseDto dto = new UserBadgeResponseDto();

        if (ub.getBadge() != null) {
            dto.setBadgeName(ub.getBadge().getName());
            dto.setDescription(ub.getBadge().getDescription());
            dto.setBadgeType(ub.getBadge().getBadgeType().name());
            dto.setBadgeScope(ub.getBadge().getBadgeScope().name());
        } else {
            dto.setBadgeName("Unknown Badge");
        }

        dto.setEarnedAt(ub.getCreatedAt());

        if (ub.getGroup() != null) {
            dto.setGroupName(ub.getGroup().getGroupName());
        }

        return dto;
    }
}