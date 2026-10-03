package com.careerconnect.repository;

import com.careerconnect.entity.Post;
import com.careerconnect.entity.PostLike;
import com.careerconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByPostAndUser(Post post, User user);

    List<PostLike> findByPost(Post post);

    long countByPost(Post post);
}