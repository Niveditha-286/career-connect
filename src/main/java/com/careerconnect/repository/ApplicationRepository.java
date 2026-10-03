package com.careerconnect.repository;

import com.careerconnect.entity.Application;
import com.careerconnect.entity.User;
import com.careerconnect.entity.Job;
import com.careerconnect.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Optional<Application> findByJobAndApplicant(Job job, User applicant);

    List<Application> findByApplicant(User applicant);

    List<Application> findByJob(Job job);

    List<Application> findByJobAndStatus(Job job, ApplicationStatus status);
}