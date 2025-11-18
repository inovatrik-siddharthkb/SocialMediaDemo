package com.social.media.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.social.media.model.*;

public interface LikeRepository extends JpaRepository<Like, Long>{
	long countByPost(Post post);

    Optional<Like> findByUserAndPost(User user, Post post);
}
