package com.social.media.model;

import javax.persistence.*;

@Entity
@Table(name = "comments")
public class Comment {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Post post;

    @ManyToOne
    private User author;

    private String text;

    public Comment() {}

    public Comment(Post post, User author, String text) {
        this.post = post;
        this.author = author;
        this.text = text;
    }

    public Long getId() { return id; }
    public Post getPost() { return post; }
    public User getAuthor() { return author; }
    public String getText() { return text; }

    public void setText(String t) { text = t; }

}
