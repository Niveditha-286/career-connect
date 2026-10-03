package com.careerconnect.service;

import com.careerconnect.dto.EducationRequest;
import com.careerconnect.dto.EducationResponse;
import com.careerconnect.entity.Education;
import com.careerconnect.entity.User;
import com.careerconnect.repository.EducationRepository;
import com.careerconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EducationService {

    private final EducationRepository educationRepository;
    private final UserRepository userRepository;

    public EducationService(
            EducationRepository educationRepository,
            UserRepository userRepository) {

        this.educationRepository = educationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public EducationResponse addEducation(
            String email,
            EducationRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        Education education = new Education();

        education.setInstitution(request.getInstitution());
        education.setDegree(request.getDegree());
        education.setFieldOfStudy(request.getFieldOfStudy());
        education.setStartYear(request.getStartYear());
        education.setEndYear(request.getEndYear());
        education.setGrade(request.getGrade());
        education.setUser(user);

        Education savedEducation =
                educationRepository.save(education);

        return convertToResponse(savedEducation);
    }

    @Transactional(readOnly = true)
    public List<EducationResponse> getMyEducation(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        return educationRepository
                .findByUserId(user.getId())
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional
    public void deleteEducation(
            String email,
            Long educationId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        Education education = educationRepository
                .findById(educationId)
                .orElseThrow();

        if (!education.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You cannot delete another user's education"
            );
        }

        educationRepository.delete(education);
    }

    private EducationResponse convertToResponse(
            Education education) {

        EducationResponse response =
                new EducationResponse();

        response.setId(education.getId());
        response.setInstitution(
                education.getInstitution()
        );
        response.setDegree(
                education.getDegree()
        );
        response.setFieldOfStudy(
                education.getFieldOfStudy()
        );
        response.setStartYear(
                education.getStartYear()
        );
        response.setEndYear(
                education.getEndYear()
        );
        response.setGrade(
                education.getGrade()
        );

        return response;
    }
}