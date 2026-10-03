package com.careerconnect.service;

import com.careerconnect.dto.ApplicationRequest;
import com.careerconnect.dto.ApplicationResponse;
import com.careerconnect.entity.Application;
import com.careerconnect.entity.ApplicationStatus;
import com.careerconnect.entity.Job;
import com.careerconnect.entity.User;
import com.careerconnect.exception.ForbiddenException;
import com.careerconnect.exception.ResourceNotFoundException;
import com.careerconnect.repository.ApplicationRepository;
import com.careerconnect.repository.JobRepository;
import com.careerconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            JobRepository jobRepository,
            UserRepository userRepository) {

        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ApplicationResponse applyToJob(
            String email,
            Long jobId,
            ApplicationRequest request) {

        User applicant = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Job not found"));

        if (applicationRepository
                .findByJobAndApplicant(job, applicant)
                .isPresent()) {

            throw new RuntimeException(
                    "You have already applied to this job");
        }

        Application application = new Application();

        application.setJob(job);
        application.setApplicant(applicant);
        application.setResumeUrl(request.getResumeUrl());
        application.setStatus(ApplicationStatus.APPLIED);

        Application savedApplication =
                applicationRepository.save(application);

        return convertToResponse(savedApplication);
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getMyApplications(
            String email) {

        User applicant = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return applicationRepository
                .findByApplicant(applicant)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getJobApplications(
            String email,
            Long jobId) {

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Job not found"));

        if (!job.getRecruiter().getId().equals(recruiter.getId())) {
            throw new ForbiddenException(
                    "You cannot view applications for another recruiter's job");
        }

        return applicationRepository
                .findByJob(job)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional
    public ApplicationResponse updateStatus(
            String email,
            Long applicationId,
            ApplicationStatus status) {

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Application application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Application not found"));

        Job job = application.getJob();

        if (!job.getRecruiter().getId().equals(recruiter.getId())) {
            throw new ForbiddenException(
                    "You cannot update this application");
        }

        application.setStatus(status);

        Application updatedApplication =
                applicationRepository.save(application);

        return convertToResponse(updatedApplication);
    }

    private ApplicationResponse convertToResponse(
            Application application) {

        ApplicationResponse response =
                new ApplicationResponse();

        response.setId(application.getId());

        response.setJobId(
                application.getJob().getId());

        response.setJobTitle(
                application.getJob().getTitle());

        response.setApplicantId(
                application.getApplicant().getId());

        response.setApplicantName(
                application.getApplicant().getName());

        response.setResumeUrl(
                application.getResumeUrl());

        response.setStatus(
                application.getStatus());

        response.setAppliedAt(
                application.getAppliedAt());

        response.setUpdatedAt(
                application.getUpdatedAt());

        return response;
    }
}