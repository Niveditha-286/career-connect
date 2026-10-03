package com.careerconnect.controller;

import com.careerconnect.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> likePost(
            Authentication authentication,
            @PathVariable Long postId) {

        String email = authentication.getName();

        likeService.likePost(email, postId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> unlikePost(
            Authentication authentication,
            @PathVariable Long postId) {

        String email = authentication.getName();

        likeService.unlikePost(email, postId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}/likes")
    public ResponseEntity<Long> getLikeCount(
            @PathVariable Long postId) {

        return ResponseEntity.ok(
                likeService.getLikeCount(postId)
        );
    }
}