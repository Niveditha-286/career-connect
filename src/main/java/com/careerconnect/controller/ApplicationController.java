package com.careerconnect.controller;

import com.careerconnect.dto.ApplicationRequest;
import com.careerconnect.dto.ApplicationResponse;
import com.careerconnect.entity.ApplicationStatus;
import com.careerconnect.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/jobs/{jobId}/apply")
    public ResponseEntity<ApplicationResponse> applyToJob(
            Authentication authentication,
            @PathVariable Long jobId,
            @Valid @RequestBody ApplicationRequest request) {

        String email = authentication.getName();

        ApplicationResponse response =
                applicationService.applyToJob(
                        email,
                        jobId,
                        request
                );

        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/applications/my")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                applicationService.getMyApplications(email)
        );
    }

    @GetMapping("/jobs/{jobId}/applications")
    public ResponseEntity<List<ApplicationResponse>> getJobApplications(
            Authentication authentication,
            @PathVariable Long jobId) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                applicationService.getJobApplications(
                        email,
                        jobId
                )
        );
    }

    @PutMapping("/applications/{applicationId}/status")
    public ResponseEntity<ApplicationResponse> updateStatus(
            Authentication authentication,
            @PathVariable Long applicationId,
            @RequestParam ApplicationStatus status) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                applicationService.updateStatus(
                        email,
                        applicationId,
                        status
                )
        );
    }
}