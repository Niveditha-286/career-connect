package com.careerconnect.repository;

import com.careerconnect.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExperienceRepository
        extends JpaRepository<Experience, Long> {

    List<Experience> findByUserId(Long userId);
}