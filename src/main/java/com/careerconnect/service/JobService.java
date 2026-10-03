package com.careerconnect.service;

import com.careerconnect.dto.JobRequest;
import com.careerconnect.dto.JobResponse;
import com.careerconnect.entity.Company;
import com.careerconnect.entity.EmploymentType;
import com.careerconnect.entity.Job;
import com.careerconnect.entity.User;
import com.careerconnect.exception.ForbiddenException;
import com.careerconnect.exception.ResourceNotFoundException;
import com.careerconnect.repository.CompanyRepository;
import com.careerconnect.repository.JobRepository;
import com.careerconnect.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public JobService(
            JobRepository jobRepository,
            CompanyRepository companyRepository,
            UserRepository userRepository) {

        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public JobResponse createJob(String email, JobRequest request) {

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Company not found"));

        if (!company.getRecruiter().getId().equals(recruiter.getId())) {
            throw new ForbiddenException(
                    "You cannot create a job for another recruiter's company");
        }

        Job job = new Job();

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setSalary(request.getSalary());
        job.setEmploymentType(request.getEmploymentType());
        job.setCompany(company);
        job.setRecruiter(recruiter);

        Job savedJob = jobRepository.save(job);

        return convertToResponse(savedJob);
    }

    @Transactional(readOnly = true)
    public JobResponse getJob(Long jobId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Job not found"));

        return convertToResponse(job);
    }

    // =========================
    // GET ALL JOBS - PAGINATION
    // =========================

    @Transactional(readOnly = true)
    public Page<JobResponse> getAllJobs(Pageable pageable) {

        return jobRepository
                .findAllByOrderByCreatedAtDesc(pageable)
                .map(this::convertToResponse);
    }

    // =========================
    // JOB SEARCH
    // =========================

    @Transactional(readOnly = true)
    public List<JobResponse> searchJobs(
            String keyword,
            String location,
            EmploymentType employmentType) {

        String normalizedKeyword =
                keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);

        String normalizedLocation =
                location == null ? "" : location.trim().toLowerCase(Locale.ROOT);

        return jobRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .filter(job -> {

                    // Keyword filter
                    if (!normalizedKeyword.isEmpty()) {

                        boolean matchesTitle =
                                job.getTitle() != null &&
                                job.getTitle()
                                        .toLowerCase(Locale.ROOT)
                                        .contains(normalizedKeyword);

                        boolean matchesDescription =
                                job.getDescription() != null &&
                                job.getDescription()
                                        .toLowerCase(Locale.ROOT)
                                        .contains(normalizedKeyword);

                        if (!matchesTitle && !matchesDescription) {
                            return false;
                        }
                    }

                    // Location filter
                    if (!normalizedLocation.isEmpty()) {

                        if (job.getLocation() == null ||
                                !job.getLocation()
                                        .toLowerCase(Locale.ROOT)
                                        .contains(normalizedLocation)) {

                            return false;
                        }
                    }

                    // Employment type filter
                    if (employmentType != null &&
                            job.getEmploymentType() != employmentType) {

                        return false;
                    }

                    return true;
                })
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getMyJobs(String email) {

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return jobRepository.findByRecruiter(recruiter)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional
    public JobResponse updateJob(
            String email,
            Long jobId,
            JobRequest request) {

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Job not found"));

        if (!job.getRecruiter().getId().equals(recruiter.getId())) {
            throw new ForbiddenException(
                    "You cannot update another recruiter's job");
        }

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Company not found"));

        if (!company.getRecruiter().getId().equals(recruiter.getId())) {
            throw new ForbiddenException(
                    "You cannot use another recruiter's company");
        }

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setSalary(request.getSalary());
        job.setEmploymentType(request.getEmploymentType());
        job.setCompany(company);

        Job updatedJob = jobRepository.save(job);

        return convertToResponse(updatedJob);
    }

    @Transactional
    public void deleteJob(String email, Long jobId) {

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Job not found"));

        if (!job.getRecruiter().getId().equals(recruiter.getId())) {
            throw new ForbiddenException(
                    "You cannot delete another recruiter's job");
        }

        jobRepository.delete(job);
    }

    private JobResponse convertToResponse(Job job) {

        JobResponse response = new JobResponse();

        response.setId(job.getId());
        response.setTitle(job.getTitle());
        response.setDescription(job.getDescription());
        response.setLocation(job.getLocation());
        response.setSalary(job.getSalary());
        response.setEmploymentType(job.getEmploymentType());

        response.setCompanyId(job.getCompany().getId());
        response.setCompanyName(job.getCompany().getName());

        response.setRecruiterId(job.getRecruiter().getId());
        response.setRecruiterName(job.getRecruiter().getName());

        response.setCreatedAt(job.getCreatedAt());
        response.setUpdatedAt(job.getUpdatedAt());

        return response;
    }
}