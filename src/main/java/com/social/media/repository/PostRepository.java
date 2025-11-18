package com.social.media.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.social.media.model.Post;

public interface PostRepository extends JpaRepository<Post, Long>{

}
