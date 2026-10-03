package com.careerconnect.repository;

import com.careerconnect.entity.Post;
import com.careerconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUser(User user);

    List<Post> findAllByOrderByCreatedAtDesc();
}