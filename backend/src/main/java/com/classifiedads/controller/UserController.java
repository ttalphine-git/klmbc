package com.classifiedads.controller;

import com.classifiedads.model.dto.UserProfileDto;
import com.classifiedads.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDto> getCurrentUser() {
        UserProfileDto profile = userService.getCurrentUserProfile();
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable Long userId) {
        UserProfileDto profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDto> updateCurrentUser(@RequestBody UserProfileDto dto) {
        String email = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
        com.classifiedads.model.entity.User user = userService.getUserByEmail(email);
        UserProfileDto updated = userService.updateUserProfile(user.getId(), dto);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/me/avatar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDto> uploadAvatar(@RequestParam MultipartFile file) {
        try {
            String email = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication().getName();
            com.classifiedads.model.entity.User user = userService.getUserByEmail(email);
            UserProfileDto updated = userService.uploadProfileImage(user.getId(), file);
            return ResponseEntity.ok(updated);
        } catch (IOException e) {
            log.error("Avatar upload failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
