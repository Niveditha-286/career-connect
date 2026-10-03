package com.careerconnect.service;

import com.careerconnect.entity.Post;
import com.careerconnect.entity.PostLike;
import com.careerconnect.entity.User;
import com.careerconnect.repository.PostLikeRepository;
import com.careerconnect.repository.PostRepository;
import com.careerconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public LikeService(
            PostLikeRepository postLikeRepository,
            PostRepository postRepository,
            UserRepository userRepository) {

        this.postLikeRepository = postLikeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void likePost(String email, Long postId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        Post post = postRepository.findById(postId)
                .orElseThrow();

        if (postLikeRepository.findByPostAndUser(post, user).isPresent()) {
            throw new RuntimeException("You have already liked this post");
        }

        PostLike postLike = new PostLike();
        postLike.setPost(post);
        postLike.setUser(user);

        postLikeRepository.save(postLike);
    }

    @Transactional
    public void unlikePost(String email, Long postId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        Post post = postRepository.findById(postId)
                .orElseThrow();

        PostLike postLike = postLikeRepository
                .findByPostAndUser(post, user)
                .orElseThrow();

        postLikeRepository.delete(postLike);
    }

    @Transactional(readOnly = true)
    public long getLikeCount(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow();

        return postLikeRepository.countByPost(post);
    }
}