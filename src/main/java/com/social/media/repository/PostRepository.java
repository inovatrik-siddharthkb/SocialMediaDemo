package com.social.media.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.social.media.model.Post;
import com.social.media.model.User;

public interface PostRepository extends JpaRepository<Post, Long>{
	
	List<Post> findAllByOrderByCreatedAtDesc();
	
	List<Post> findByAuthorInOrderByCreatedAtDesc(Collection<User> authors);
	
	List<Post> findByAuthorOrderByCreatedAtDesc(User author);
}
