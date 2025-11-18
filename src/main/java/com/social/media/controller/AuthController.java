package com.social.media.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;

import com.social.media.model.*;
import com.social.media.repository.UserRepository;
import com.social.media.config.JwtUtils;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtils jwt;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {

        if (repo.findByUsername(req.getUsername()).isPresent())
            return ResponseEntity.badRequest().body("Username exists");

        User u = new User(
                req.getName(),
                req.getEmail(),
                req.getUsername(),
                encoder.encode(req.getPassword()),
                req.getRole() == null ? Role.COMMON : req.getRole()
        );

        repo.save(u);
        return ResponseEntity.ok("Registered");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getUsername(), req.getPassword()
                )
        );

        String token = jwt.generateToken(req.getUsername());

        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing or invalid token");
        }

        String token = authHeader.substring(7);
        String username = jwt.extractUsername(token);

        User user = repo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(user);
    }

    public static class RegisterRequest {
        private String name, email, username, password;
        private Role role;

        public String getName() { return name; }
        public void setName(String n) { name = n; }

        public String getEmail() { return email; }
        public void setEmail(String e) { email = e; }

        public String getUsername() { return username; }
        public void setUsername(String u) { username = u; }

        public String getPassword() { return password; }
        public void setPassword(String p) { password = p; }

        public Role getRole() { return role; }
        public void setRole(Role r) { role = r; }
    }

    public static class LoginRequest {
        private String username, password;
        public String getUsername() { return username; }
        public void setUsername(String u) { username = u; }

        public String getPassword() { return password; }
        public void setPassword(String p) { password = p; }
    }

}
