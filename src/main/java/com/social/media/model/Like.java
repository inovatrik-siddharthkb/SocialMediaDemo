package com.social.media.model;

import javax.persistence.*;

@Entity
@Table(name = "likes",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "post_id"}))
public class Like {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Post post;

    public Like() {}

    public Like(User user, Post post) {
        this.user = user;
        this.post = post;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

}
