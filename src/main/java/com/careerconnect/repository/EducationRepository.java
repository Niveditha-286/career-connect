package com.careerconnect.repository;

import com.careerconnect.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Long> {

    List<Education> findByUserId(Long userId);
}