package com.example.fairsharebackend.mapper;

import com.example.fairsharebackend.entity.dto.response.SettlementResponseDto;
import com.example.fairsharebackend.entity.Settlement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SettlementMapper {
    
    @Mapping(source = "group.groupId", target = "groupId")
    @Mapping(source = "group.groupName", target = "groupName")
    
    @Mapping(source = "fromUser.userId", target = "fromUserId")
    @Mapping(source = "fromUser.name", target = "fromUserName")
    @Mapping(source = "fromUser.email", target = "fromUserEmail")
    
    @Mapping(source = "toUser.userId", target = "toUserId")
    @Mapping(source = "toUser.name", target = "toUserName")
    @Mapping(source = "toUser.email", target = "toUserEmail")
    
    @Mapping(source = "createdBy.userId", target = "createdById")
    @Mapping(source = "createdBy.name", target = "createdByName")
    
    SettlementResponseDto toResponseDto(Settlement settlement);
}