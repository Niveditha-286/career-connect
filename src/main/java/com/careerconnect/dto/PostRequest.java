package com.careerconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PostRequest {

    @NotBlank(message = "Post content is required")
    @Size(max = 3000, message = "Post content cannot exceed 3000 characters")
    private String content;

    public PostRequest() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}