package com.social.media.model;

import javax.persistence.*;

@Entity
public class Media {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
    private String url;

    @Enumerated(EnumType.STRING)
    private MediaType type;

    @ManyToOne
    private Post post;

    public Media() {}

    public Media(Post post, String filename, String url, MediaType type) {
        this.post = post;
        this.filename = filename;
        this.url = url;
        this.type = type;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public MediaType getType() {
		return type;
	}

	public void setType(MediaType type) {
		this.type = type;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}
    
}
