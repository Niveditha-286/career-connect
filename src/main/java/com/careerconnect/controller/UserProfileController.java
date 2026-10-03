package com.careerconnect.controller;

import com.careerconnect.dto.UpdateProfileRequest;
import com.careerconnect.dto.UserResponse;
import com.careerconnect.entity.User;
import com.careerconnect.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {

    private final UserRepository userRepository;

    public UserProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        return ResponseEntity.ok(convertToResponse(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            Authentication authentication,
            @RequestBody UpdateProfileRequest request) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        user.setName(request.getName());
        user.setHeadline(request.getHeadline());
        user.setBio(request.getBio());
        user.setLocation(request.getLocation());
        user.setProfileImageUrl(request.getProfileImageUrl());

        User updatedUser = userRepository.save(user);

        return ResponseEntity.ok(convertToResponse(updatedUser));
    }

    private UserResponse convertToResponse(User user) {

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setHeadline(user.getHeadline());
        response.setBio(user.getBio());
        response.setLocation(user.getLocation());
        response.setProfileImageUrl(user.getProfileImageUrl());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        return response;
    }
}