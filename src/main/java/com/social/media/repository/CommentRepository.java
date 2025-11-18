package com.social.media.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.social.media.model.*;

public interface CommentRepository extends JpaRepository<Comment, Long>{
	List<Comment> findByPost(Post post);
}
