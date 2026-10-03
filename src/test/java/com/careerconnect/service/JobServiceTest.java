package com.careerconnect.service;

import com.careerconnect.dto.JobRequest;
import com.careerconnect.dto.JobResponse;
import com.careerconnect.entity.Company;
import com.careerconnect.entity.Job;
import com.careerconnect.entity.User;
import com.careerconnect.exception.ForbiddenException;
import com.careerconnect.exception.ResourceNotFoundException;
import com.careerconnect.repository.CompanyRepository;
import com.careerconnect.repository.JobRepository;
import com.careerconnect.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JobService jobService;


    // TEST 1
    @Test
    void testGetAllJobs() {

        Pageable pageable = Pageable.unpaged();

        when(jobRepository.findAllByOrderByCreatedAtDesc(pageable))
                .thenReturn(Page.empty());

        Page<JobResponse> result =
                jobService.getAllJobs(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(jobRepository, times(1))
                .findAllByOrderByCreatedAtDesc(pageable);
    }


    // TEST 2
    @Test
    void testCreateJobSuccessfully() {

        String email = "john@gmail.com";

        User recruiter = mock(User.class);
        Company company = mock(Company.class);
        JobRequest request = mock(JobRequest.class);
        Job savedJob = mock(Job.class);

        when(recruiter.getId()).thenReturn(1L);
        when(recruiter.getName()).thenReturn("John");

        when(company.getId()).thenReturn(1L);
        when(company.getName())
                .thenReturn("Career Connect Technologies");
        when(company.getRecruiter())
                .thenReturn(recruiter);

        when(request.getCompanyId()).thenReturn(1L);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(recruiter));

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        when(jobRepository.save(any(Job.class)))
                .thenReturn(savedJob);

        when(savedJob.getId()).thenReturn(1L);
        when(savedJob.getTitle())
                .thenReturn("Senior Java Backend Developer");
        when(savedJob.getCompany())
                .thenReturn(company);
        when(savedJob.getRecruiter())
                .thenReturn(recruiter);

        JobResponse result =
                jobService.createJob(email, request);

        assertNotNull(result);

        assertEquals(1L, result.getId());

        assertEquals(
                "Senior Java Backend Developer",
                result.getTitle()
        );

        assertEquals(
                1L,
                result.getCompanyId()
        );

        assertEquals(
                "Career Connect Technologies",
                result.getCompanyName()
        );

        assertEquals(
                1L,
                result.getRecruiterId()
        );

        assertEquals(
                "John",
                result.getRecruiterName()
        );

        verify(userRepository, times(1))
                .findByEmail(email);

        verify(companyRepository, times(1))
                .findById(1L);

        verify(jobRepository, times(1))
                .save(any(Job.class));
    }


    // TEST 3
    @Test
    void testCreateJobForbiddenForAnotherRecruitersCompany() {

        String email = "john@gmail.com";

        User recruiter = mock(User.class);
        User companyRecruiter = mock(User.class);
        Company company = mock(Company.class);
        JobRequest request = mock(JobRequest.class);

        when(recruiter.getId()).thenReturn(1L);
        when(companyRecruiter.getId()).thenReturn(2L);

        when(request.getCompanyId()).thenReturn(1L);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(recruiter));

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        when(company.getRecruiter())
                .thenReturn(companyRecruiter);

        assertThrows(
                ForbiddenException.class,
                () -> jobService.createJob(email, request)
        );

        verify(jobRepository, never())
                .save(any(Job.class));
    }


    // TEST 4
    @Test
    void testGetJobSuccessfully() {

        Long jobId = 1L;

        Job job = mock(Job.class);
        Company company = mock(Company.class);
        User recruiter = mock(User.class);

        when(jobRepository.findById(jobId))
                .thenReturn(Optional.of(job));

        when(job.getId()).thenReturn(1L);

        when(job.getTitle())
                .thenReturn("Java Backend Developer");

        when(job.getDescription())
                .thenReturn("Spring Boot backend development");

        when(job.getLocation())
                .thenReturn("Hyderabad, India");

        when(job.getCompany())
                .thenReturn(company);

        when(company.getId()).thenReturn(1L);

        when(company.getName())
                .thenReturn("TechNova Solutions");

        when(job.getRecruiter())
                .thenReturn(recruiter);

        when(recruiter.getId()).thenReturn(1L);

        when(recruiter.getName())
                .thenReturn("John");

        JobResponse result =
                jobService.getJob(jobId);

        assertNotNull(result);

        assertEquals(1L, result.getId());

        assertEquals(
                "Java Backend Developer",
                result.getTitle()
        );

        assertEquals(
                "Hyderabad, India",
                result.getLocation()
        );

        assertEquals(
                "TechNova Solutions",
                result.getCompanyName()
        );

        assertEquals(
                "John",
                result.getRecruiterName()
        );

        verify(jobRepository, times(1))
                .findById(jobId);
    }


    // TEST 5
    @Test
    void testGetJobNotFound() {

        Long jobId = 999L;

        when(jobRepository.findById(jobId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> jobService.getJob(jobId)
        );

        verify(jobRepository, times(1))
                .findById(jobId);
    }


    // TEST 6
    @Test
    void testCreateJobUserNotFound() {

        String email = "unknown@gmail.com";

        JobRequest request = mock(JobRequest.class);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> jobService.createJob(email, request)
        );

        verify(userRepository, times(1))
                .findByEmail(email);

        verify(companyRepository, never())
                .findById(anyLong());

        verify(jobRepository, never())
                .save(any(Job.class));
    }


    // TEST 7
    @Test
    void testCreateJobCompanyNotFound() {

        String email = "john@gmail.com";

        User recruiter = mock(User.class);
        JobRequest request = mock(JobRequest.class);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(recruiter));

        when(request.getCompanyId())
                .thenReturn(999L);

        when(companyRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> jobService.createJob(email, request)
        );

        verify(userRepository, times(1))
                .findByEmail(email);

        verify(companyRepository, times(1))
                .findById(999L);

        verify(jobRepository, never())
                .save(any(Job.class));
    }


    // TEST 8
    @Test
    void testUpdateJobSuccessfully() {

        String email = "john@gmail.com";
        Long jobId = 1L;

        User recruiter = mock(User.class);
        Company company = mock(Company.class);
        Job job = mock(Job.class);
        JobRequest request = mock(JobRequest.class);

        when(recruiter.getId()).thenReturn(1L);
        when(recruiter.getName()).thenReturn("John");

        when(job.getRecruiter())
                .thenReturn(recruiter);

        when(request.getCompanyId())
                .thenReturn(1L);

        when(company.getId())
                .thenReturn(1L);

        when(company.getName())
                .thenReturn("Career Connect Technologies");

        when(company.getRecruiter())
                .thenReturn(recruiter);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(recruiter));

        when(jobRepository.findById(jobId))
                .thenReturn(Optional.of(job));

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        when(jobRepository.save(job))
                .thenReturn(job);

        when(job.getId())
                .thenReturn(1L);

        when(job.getTitle())
                .thenReturn("Updated Java Developer");

        when(job.getCompany())
                .thenReturn(company);

        JobResponse result =
                jobService.updateJob(email, jobId, request);

        assertNotNull(result);

        assertEquals(
                1L,
                result.getId()
        );

        assertEquals(
                "Updated Java Developer",
                result.getTitle()
        );

        assertEquals(
                1L,
                result.getCompanyId()
        );

        verify(userRepository, times(1))
                .findByEmail(email);

        verify(jobRepository, times(1))
                .findById(jobId);

        verify(companyRepository, times(1))
                .findById(1L);

        verify(jobRepository, times(1))
                .save(job);
    }


    // TEST 9
    @Test
    void testUpdateJobForbiddenForAnotherRecruiter() {

        String email = "john@gmail.com";
        Long jobId = 1L;

        User recruiter = mock(User.class);
        User jobRecruiter = mock(User.class);
        Job job = mock(Job.class);
        JobRequest request = mock(JobRequest.class);

        when(recruiter.getId())
                .thenReturn(1L);

        when(jobRecruiter.getId())
                .thenReturn(2L);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(recruiter));

        when(jobRepository.findById(jobId))
                .thenReturn(Optional.of(job));

        when(job.getRecruiter())
                .thenReturn(jobRecruiter);

        assertThrows(
                ForbiddenException.class,
                () -> jobService.updateJob(email, jobId, request)
        );

        verify(jobRepository, never())
                .save(any(Job.class));

        verify(companyRepository, never())
                .findById(anyLong());
    }


    // TEST 10
    @Test
    void testDeleteJobForbiddenForAnotherRecruiter() {

        String email = "john@gmail.com";
        Long jobId = 1L;

        User recruiter = mock(User.class);
        User jobRecruiter = mock(User.class);
        Job job = mock(Job.class);

        when(recruiter.getId())
                .thenReturn(1L);

        when(jobRecruiter.getId())
                .thenReturn(2L);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(recruiter));

        when(jobRepository.findById(jobId))
                .thenReturn(Optional.of(job));

        when(job.getRecruiter())
                .thenReturn(jobRecruiter);

        assertThrows(
                ForbiddenException.class,
                () -> jobService.deleteJob(email, jobId)
        );

        verify(jobRepository, never())
                .delete(any(Job.class));
    }
}