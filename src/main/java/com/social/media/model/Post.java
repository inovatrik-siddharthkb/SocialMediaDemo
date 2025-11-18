package com.social.media.model;

import javax.persistence.*;

import java.util.*;

@Entity
public class Post {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User author;

    private String text;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Media> mediaList = new ArrayList<>();

    private Date createdAt = new Date();

    public Post() {}

    public Post(User author, String text) {
        this.author = author;
        this.text = text;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<Media> getMediaList() {
		return mediaList;
	}

	public void setMediaList(List<Media> mediaList) {
		this.mediaList = mediaList;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

}
