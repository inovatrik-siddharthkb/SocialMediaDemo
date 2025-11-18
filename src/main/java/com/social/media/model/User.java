package com.social.media.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @Column(unique = true)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
    
    @ManyToMany
    @JoinTable(
            name = "user_followers",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    private Set<User> followers = new HashSet<>();

    @ManyToMany(mappedBy = "followers")
    private Set<User> following = new HashSet<>();

    public Set<User> getFollowers() { return followers; }
    public Set<User> getFollowing() { return following; }

    public User() {}

    public User(String name, String email, String username, String password, Role role) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String n) { this.name = n; }

    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }

    public String getUsername() { return username; }
    public void setUsername(String u) { this.username = u; }

    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }

    public Role getRole() { return role; }
    public void setRole(Role r) { this.role = r; }

}
