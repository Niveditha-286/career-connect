package com.careerconnect.controller;

import com.careerconnect.dto.CompanyRequest;
import com.careerconnect.dto.CompanyResponse;
import com.careerconnect.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    public ResponseEntity<CompanyResponse> createCompany(
            Authentication authentication,
            @Valid @RequestBody CompanyRequest request) {

        String email = authentication.getName();

        CompanyResponse response =
                companyService.createCompany(email, request);

        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyResponse> getCompany(
            @PathVariable Long companyId) {

        return ResponseEntity.ok(
                companyService.getCompany(companyId)
        );
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyResponse> updateCompany(
            Authentication authentication,
            @PathVariable Long companyId,
            @Valid @RequestBody CompanyRequest request) {

        String email = authentication.getName();

        CompanyResponse response =
                companyService.updateCompany(
                        email,
                        companyId,
                        request
                );

        return ResponseEntity.ok(response);
    }
}