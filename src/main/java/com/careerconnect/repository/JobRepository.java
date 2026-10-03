package com.careerconnect.repository;

import com.careerconnect.entity.EmploymentType;
import com.careerconnect.entity.Job;
import com.careerconnect.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByRecruiter(User recruiter);

    List<Job> findAllByOrderByCreatedAtDesc();

    Page<Job> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<Job> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String title,
            String description
    );

    List<Job> findByLocationContainingIgnoreCase(String location);

    List<Job> findByEmploymentType(EmploymentType employmentType);
}