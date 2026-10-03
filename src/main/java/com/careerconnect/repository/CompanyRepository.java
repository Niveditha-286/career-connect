package com.careerconnect.repository;

import com.careerconnect.entity.Company;
import com.careerconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByRecruiter(User recruiter);

}