package com.careerconnect.service;

import com.careerconnect.dto.ExperienceRequest;
import com.careerconnect.dto.ExperienceResponse;
import com.careerconnect.entity.Experience;
import com.careerconnect.entity.User;
import com.careerconnect.repository.ExperienceRepository;
import com.careerconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final UserRepository userRepository;

    public ExperienceService(
            ExperienceRepository experienceRepository,
            UserRepository userRepository) {

        this.experienceRepository = experienceRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ExperienceResponse addExperience(
            String email,
            ExperienceRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        Experience experience = new Experience();

        experience.setCompany(request.getCompany());
        experience.setPosition(request.getPosition());
        experience.setEmploymentType(request.getEmploymentType());
        experience.setLocation(request.getLocation());
        experience.setStartYear(request.getStartYear());
        experience.setEndYear(request.getEndYear());
        experience.setCurrentlyWorking(
                request.isCurrentlyWorking()
        );
        experience.setDescription(request.getDescription());
        experience.setUser(user);

        Experience savedExperience =
                experienceRepository.save(experience);

        return convertToResponse(savedExperience);
    }

    @Transactional(readOnly = true)
    public List<ExperienceResponse> getMyExperience(
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        return experienceRepository
                .findByUserId(user.getId())
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional
    public void deleteExperience(
            String email,
            Long experienceId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        Experience experience = experienceRepository
                .findById(experienceId)
                .orElseThrow();

        if (!experience.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You cannot delete another user's experience"
            );
        }

        experienceRepository.delete(experience);
    }

    private ExperienceResponse convertToResponse(
            Experience experience) {

        ExperienceResponse response =
                new ExperienceResponse();

        response.setId(experience.getId());
        response.setCompany(experience.getCompany());
        response.setPosition(experience.getPosition());
        response.setEmploymentType(
                experience.getEmploymentType()
        );
        response.setLocation(experience.getLocation());
        response.setStartYear(experience.getStartYear());
        response.setEndYear(experience.getEndYear());
        response.setCurrentlyWorking(
                experience.isCurrentlyWorking()
        );
        response.setDescription(
                experience.getDescription()
        );

        return response;
    }
}