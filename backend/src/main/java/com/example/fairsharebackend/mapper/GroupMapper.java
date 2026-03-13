package com.example.fairsharebackend.mapper;

import com.example.fairsharebackend.entity.Group;
import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.entity.dto.request.GroupCreateRequestDto;
import com.example.fairsharebackend.entity.dto.request.UserRegisterRequestDto;
import com.example.fairsharebackend.entity.dto.request.UserUpdateRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GroupMapper {

    Group toEntity(GroupCreateRequestDto request);

}
