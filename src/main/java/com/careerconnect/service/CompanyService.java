package com.careerconnect.service;

import com.careerconnect.dto.CompanyRequest;
import com.careerconnect.dto.CompanyResponse;
import com.careerconnect.entity.Company;
import com.careerconnect.entity.User;
import com.careerconnect.exception.ForbiddenException;
import com.careerconnect.exception.ResourceNotFoundException;
import com.careerconnect.repository.CompanyRepository;
import com.careerconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(
            CompanyRepository companyRepository,
            UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CompanyResponse createCompany(
            String email,
            CompanyRequest request) {

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (recruiter.getRole().name().equals("USER")) {
            throw new ForbiddenException(
                    "Only recruiters can create a company");
        }

        if (companyRepository.findByRecruiter(recruiter).isPresent()) {
            throw new RuntimeException(
                    "Recruiter already has a company");
        }

        Company company = new Company();

        company.setName(request.getName());
        company.setDescription(request.getDescription());
        company.setWebsite(request.getWebsite());
        company.setLocation(request.getLocation());
        company.setRecruiter(recruiter);

        Company savedCompany = companyRepository.save(company);

        return convertToResponse(savedCompany);
    }

    @Transactional(readOnly = true)
    public CompanyResponse getCompany(Long companyId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Company not found"));

        return convertToResponse(company);
    }

    @Transactional
    public CompanyResponse updateCompany(
            String email,
            Long companyId,
            CompanyRequest request) {

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Company not found"));

        if (!company.getRecruiter().getId().equals(recruiter.getId())) {
            throw new ForbiddenException(
                    "You cannot update another recruiter's company");
        }

        company.setName(request.getName());
        company.setDescription(request.getDescription());
        company.setWebsite(request.getWebsite());
        company.setLocation(request.getLocation());

        Company updatedCompany = companyRepository.save(company);

        return convertToResponse(updatedCompany);
    }

    private CompanyResponse convertToResponse(Company company) {

        CompanyResponse response = new CompanyResponse();

        response.setId(company.getId());
        response.setName(company.getName());
        response.setDescription(company.getDescription());
        response.setWebsite(company.getWebsite());
        response.setLocation(company.getLocation());

        response.setRecruiterId(
                company.getRecruiter().getId());

        response.setRecruiterName(
                company.getRecruiter().getName());

        response.setCreatedAt(company.getCreatedAt());
        response.setUpdatedAt(company.getUpdatedAt());

        return response;
    }
}