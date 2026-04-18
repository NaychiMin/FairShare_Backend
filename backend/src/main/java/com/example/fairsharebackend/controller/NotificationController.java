package com.example.fairsharebackend.controller;

import com.example.fairsharebackend.entity.dto.response.NotificationResponseDto;
import com.example.fairsharebackend.entity.dto.response.UnreadCountResponseDto;
import com.example.fairsharebackend.entity.dto.response.UnreadGroupNotificationDto;
import com.example.fairsharebackend.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/me")
    public ResponseEntity<List<NotificationResponseDto>> getMyNotifications(
            @RequestParam String requesterEmail
    ) {
        return ResponseEntity.ok(notificationService.getMyNotifications(requesterEmail));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<UnreadCountResponseDto> getUnreadCount(
            @RequestParam String requesterEmail
    ) {
        return ResponseEntity.ok(new UnreadCountResponseDto(
                notificationService.getUnreadCount(requesterEmail)
        ));
    }

    @GetMapping("/unread-by-group")
    public ResponseEntity<List<UnreadGroupNotificationDto>> getUnreadByGroup(
            @RequestParam String requesterEmail
    ) {
        return ResponseEntity.ok(notificationService.getUnreadCountsByGroup(requesterEmail));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable UUID notificationId,
            @RequestParam String requesterEmail
    ) {
        notificationService.markAsRead(notificationId, requesterEmail);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(
            @RequestParam String requesterEmail
    ) {
        notificationService.markAllAsRead(requesterEmail);
        return ResponseEntity.ok().build();
    }
}
