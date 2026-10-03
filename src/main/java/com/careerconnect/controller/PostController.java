package com.careerconnect.controller;

import com.careerconnect.dto.PostRequest;
import com.careerconnect.dto.PostResponse;
import com.careerconnect.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            Authentication authentication,
            @Valid @RequestBody PostRequest request) {

        String email = authentication.getName();

        PostResponse response =
                postService.createPost(email, request);

        return ResponseEntity
                .status(201)
                .body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<PostResponse>> getMyPosts(
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                postService.getMyPosts(email)
        );
    }

    @GetMapping("/feed")
    public ResponseEntity<List<PostResponse>> getFeed() {

        return ResponseEntity.ok(
                postService.getFeed()
        );
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            Authentication authentication,
            @PathVariable Long postId) {

        String email = authentication.getName();

        postService.deletePost(email, postId);

        return ResponseEntity.noContent().build();
    }
}