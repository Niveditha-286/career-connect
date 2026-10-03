package com.careerconnect.dto;

import jakarta.validation.constraints.Size;

public class ApplicationRequest {

    @Size(max = 500, message = "Resume URL cannot exceed 500 characters")
    private String resumeUrl;

    public ApplicationRequest() {
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }
}