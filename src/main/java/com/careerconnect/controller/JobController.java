package com.careerconnect.controller;

import com.careerconnect.dto.JobRequest;
import com.careerconnect.dto.JobResponse;
import com.careerconnect.entity.EmploymentType;
import com.careerconnect.service.JobService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public ResponseEntity<JobResponse> createJob(
            Authentication authentication,
            @Valid @RequestBody JobRequest request) {

        String email = authentication.getName();

        JobResponse response =
                jobService.createJob(email, request);

        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobResponse> getJob(
            @PathVariable Long jobId) {

        return ResponseEntity.ok(
                jobService.getJob(jobId)
        );
    }

    @GetMapping
    public ResponseEntity<Page<JobResponse>> getAllJobs(
            @PageableDefault(size = 10)
            Pageable pageable) {

        return ResponseEntity.ok(
                jobService.getAllJobs(pageable)
        );
    }

    // =========================
    // JOB SEARCH
    // =========================

    @GetMapping("/search")
    public ResponseEntity<List<JobResponse>> searchJobs(

            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            String location,

            @RequestParam(required = false)
            EmploymentType employmentType) {

        return ResponseEntity.ok(
                jobService.searchJobs(
                        keyword,
                        location,
                        employmentType
                )
        );
    }

    @GetMapping("/my")
    public ResponseEntity<List<JobResponse>> getMyJobs(
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                jobService.getMyJobs(email)
        );
    }

    @PutMapping("/{jobId}")
    public ResponseEntity<JobResponse> updateJob(
            Authentication authentication,
            @PathVariable Long jobId,
            @Valid @RequestBody JobRequest request) {

        String email = authentication.getName();

        JobResponse response =
                jobService.updateJob(email, jobId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> deleteJob(
            Authentication authentication,
            @PathVariable Long jobId) {

        String email = authentication.getName();

        jobService.deleteJob(email, jobId);

        return ResponseEntity.noContent().build();
    }
}