package com.careerconnect.service;

import com.careerconnect.dto.PostRequest;
import com.careerconnect.dto.PostResponse;
import com.careerconnect.entity.Post;
import com.careerconnect.entity.User;
import com.careerconnect.repository.PostRepository;
import com.careerconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(
            PostRepository postRepository,
            UserRepository userRepository) {

        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PostResponse createPost(
            String email,
            PostRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        Post post = new Post();

        post.setContent(request.getContent());
        post.setUser(user);

        Post savedPost = postRepository.save(post);

        return convertToResponse(savedPost);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getMyPosts(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        return postRepository
                .findByUser(user)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getFeed() {

        return postRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional
    public void deletePost(
            String email,
            Long postId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        Post post = postRepository
                .findById(postId)
                .orElseThrow();

        if (!post.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You cannot delete another user's post"
            );
        }

        postRepository.delete(post);
    }

    private PostResponse convertToResponse(Post post) {

        PostResponse response = new PostResponse();

        response.setId(post.getId());
        response.setContent(post.getContent());

        response.setUserId(
                post.getUser().getId()
        );

        response.setUserName(
                post.getUser().getName()
        );

        response.setCreatedAt(
                post.getCreatedAt()
        );

        response.setUpdatedAt(
                post.getUpdatedAt()
        );

        return response;
    }
}