package com.careerconnect.controller;

import com.careerconnect.dto.EducationRequest;
import com.careerconnect.dto.EducationResponse;
import com.careerconnect.service.EducationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/me/education")
public class EducationController {

    private final EducationService educationService;

    public EducationController(EducationService educationService) {
        this.educationService = educationService;
    }

    @PostMapping
    public ResponseEntity<EducationResponse> addEducation(
            Authentication authentication,
            @Valid @RequestBody EducationRequest request) {

        String email = authentication.getName();

        EducationResponse response =
                educationService.addEducation(email, request);

        return ResponseEntity
                .status(201)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<EducationResponse>> getMyEducation(
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                educationService.getMyEducation(email)
        );
    }

    @DeleteMapping("/{educationId}")
    public ResponseEntity<Void> deleteEducation(
            Authentication authentication,
            @PathVariable Long educationId) {

        String email = authentication.getName();

        educationService.deleteEducation(
                email,
                educationId
        );

        return ResponseEntity.noContent().build();
    }
}