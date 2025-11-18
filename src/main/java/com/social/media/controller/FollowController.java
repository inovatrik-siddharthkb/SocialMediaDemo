package com.social.media.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.social.media.model.User;
import com.social.media.repository.UserRepository;

@RestController
@RequestMapping("/api/follow")
public class FollowController {
	@Autowired
    private UserRepository userRepo;

    @PostMapping("/{username}")
    public ResponseEntity<?> follow(@PathVariable String username, Authentication auth) {

        User me = userRepo.findByUsername(auth.getName()).get();
        User other = userRepo.findByUsername(username).orElse(null);

        if (other == null)
            return ResponseEntity.notFound().build();

        if (me.getUsername().equals(username))
            return ResponseEntity.badRequest().body("Cannot follow yourself");

        me.getFollowing().add(other);
        other.getFollowers().add(me);

        userRepo.save(me);
        userRepo.save(other);

        return ResponseEntity.ok("Followed");
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> unfollow(@PathVariable String username, Authentication auth) {

        User me = userRepo.findByUsername(auth.getName()).get();
        User other = userRepo.findByUsername(username).orElse(null);

        if (other == null)
            return ResponseEntity.notFound().build();

        me.getFollowing().remove(other);
        other.getFollowers().remove(me);

        userRepo.save(me);
        userRepo.save(other);

        return ResponseEntity.ok("Unfollowed");
    }

    @GetMapping("/{username}/followers")
    public ResponseEntity<?> followers(@PathVariable String username) {
    	Optional<User> u = userRepo.findByUsername(username);
        if (!u.isPresent()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(u.get().getFollowers());
    }

    @GetMapping("/{username}/following")
    public ResponseEntity<?> following(@PathVariable String username) {
    	Optional<User> u = userRepo.findByUsername(username);
        if (!u.isPresent()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(u.get().getFollowing());
    }

}
