package com.careerconnect.service;

import com.careerconnect.dto.JobRequest;
import com.careerconnect.dto.JobResponse;
import com.careerconnect.entity.Company;
import com.careerconnect.entity.Job;
import com.careerconnect.entity.User;
import com.careerconnect.exception.ForbiddenException;
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

        // Arrange
        Pageable pageable = Pageable.unpaged();

        when(jobRepository.findAllByOrderByCreatedAtDesc(pageable))
                .thenReturn(Page.empty());

        // Act
        Page<JobResponse> result =
                jobService.getAllJobs(pageable);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(jobRepository, times(1))
                .findAllByOrderByCreatedAtDesc(pageable);
    }


    // TEST 2
    @Test
    void testCreateJobSuccessfully() {

        // Arrange
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

        // Act
        JobResponse result =
                jobService.createJob(email, request);

        // Assert
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

        // Arrange
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

        // Act + Assert
        assertThrows(
                ForbiddenException.class,
                () -> jobService.createJob(email, request)
        );

        // Job should NOT be saved
        verify(jobRepository, never())
                .save(any(Job.class));
    }
}