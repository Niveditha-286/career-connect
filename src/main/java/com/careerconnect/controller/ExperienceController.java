package com.careerconnect.controller;

import com.careerconnect.dto.ExperienceRequest;
import com.careerconnect.dto.ExperienceResponse;
import com.careerconnect.service.ExperienceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/me/experience")
public class ExperienceController {

    private final ExperienceService experienceService;

    public ExperienceController(
            ExperienceService experienceService) {
        this.experienceService = experienceService;
    }

    @PostMapping
    public ResponseEntity<ExperienceResponse> addExperience(
            Authentication authentication,
            @Valid @RequestBody ExperienceRequest request) {

        String email = authentication.getName();

        ExperienceResponse response =
                experienceService.addExperience(
                        email,
                        request
                );

        return ResponseEntity
                .status(201)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ExperienceResponse>> getMyExperience(
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                experienceService.getMyExperience(email)
        );
    }

    @DeleteMapping("/{experienceId}")
    public ResponseEntity<Void> deleteExperience(
            Authentication authentication,
            @PathVariable Long experienceId) {

        String email = authentication.getName();

        experienceService.deleteExperience(
                email,
                experienceId
        );

        return ResponseEntity.noContent().build();
    }
}