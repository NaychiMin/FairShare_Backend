//package com.example.fairsharebackend.controller;
//
//import com.example.fairsharebackend.entity.Group;
//import com.example.fairsharebackend.entity.dto.request.GroupUpdateRequestDto;
//import com.example.fairsharebackend.service.GroupService;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import java.util.UUID;
//import static org.mockito.ArgumentMatchers.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(GroupController.class)
//@AutoConfigureMockMvc(addFilters = false) // disables Spring Security filters for unit tests
//class GroupControllerTest {
//
//    @Autowired private MockMvc mockMvc;
//    @Autowired private ObjectMapper objectMapper;
//
//    @MockBean private GroupService groupService;
//
//    @Test
//    void updateGroup_returnsGroupName_whenSuccess() throws Exception {
//        UUID groupId = UUID.randomUUID();
//
//        GroupUpdateRequestDto dto = new GroupUpdateRequestDto();
//        dto.setGroupName("Japan Trip 2026");
//        dto.setCategory("Travel");
//
//        Group updated = new Group();
//        updated.setGroupId(groupId);
//        updated.setGroupName("Japan Trip 2026");
//        updated.setCategory("Travel");
//
//        Mockito.when(groupService.updateGroup(eq(groupId), any(GroupUpdateRequestDto.class), eq("admin@example.com")))
//                .thenReturn(updated);
//
//        mockMvc.perform(
//                        put("/api/groups/{groupId}", groupId)
//                                .param("requesterEmail", "admin@example.com")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(dto))
//                )
//                .andExpect(status().isOk())
//                //.andExpect(content().string("Japan Trip 2026"));
//                .andExpect(jsonPath("$.groupName").value("Japan Trip 2026"));
//    }
//
//    @Test
//    void updateGroup_returns400_whenInvalidPayload() throws Exception {
//        UUID groupId = UUID.randomUUID();
//
//        // invalid because @NotBlank fields are empty
//        GroupUpdateRequestDto dto = new GroupUpdateRequestDto();
//        dto.setGroupName("");
//        dto.setCategory("");
//
//        mockMvc.perform(
//                        put("/api/groups/{groupId}", groupId)
//                                .param("requesterEmail", "admin@example.com")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(dto))
//                )
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void updateGroup_returns500_whenServiceThrows() throws Exception {
//        UUID groupId = UUID.randomUUID();
//
//        GroupUpdateRequestDto dto = new GroupUpdateRequestDto();
//        dto.setGroupName("New Name");
//        dto.setCategory("Travel");
//
//        Mockito.when(groupService.updateGroup(eq(groupId), any(GroupUpdateRequestDto.class), anyString()))
//                .thenThrow(new RuntimeException("Not authorized to edit this group"));
//
//        mockMvc.perform(
//                        put("/api/groups/{groupId}", groupId)
//                                .param("requesterEmail", "notadmin@example.com")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(dto))
//                )
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.message").value("Not authorized to edit this group"));
//    }
//}