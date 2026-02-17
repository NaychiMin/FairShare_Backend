package com.example.fairsharebackend.mapper;

import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.entity.dto.request.UserRegisterRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRegisterRequestDto request);
}
