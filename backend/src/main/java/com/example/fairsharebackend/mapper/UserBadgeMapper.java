package com.example.fairsharebackend.mapper;

import com.example.fairsharebackend.entity.Group;
import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.entity.UserBadge;
import com.example.fairsharebackend.entity.dto.response.GroupSummaryResponseDto;
import com.example.fairsharebackend.entity.dto.response.UserBadgeDto;
import com.example.fairsharebackend.entity.dto.response.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserBadgeMapper {
    UserBadgeDto toDto(UserBadge userBadge);
    UserDto toDto(User user);
    GroupSummaryResponseDto toDto(Group group);
}
