package com.social.media.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.social.media.dto.LoginRequest;
import com.social.media.dto.LoginResponse;
import com.social.media.dto.RegisterRequest;
import com.social.media.model.Role;
import com.social.media.model.User;
import com.social.media.repository.UserRepository;
import com.social.media.config.JwtUtils;

@Service
public class AuthService {
	@Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtils jwt;

    public String register(RegisterRequest req) {

        if (repo.existsByUsername(req.username)) {
            return "Username already exists!";
        }

        User u = new User();
        u.setName(req.name);
        u.setUsername(req.username);
        u.setPassword(encoder.encode(req.password));
        u.setRole(Role.valueOf(req.role.toUpperCase()));

        repo.save(u);
        return "User registered successfully!";
    }

    public LoginResponse login(LoginRequest req) {

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username, req.password)
        );

        User user = repo.findByUsername(req.username).get();

        String token = jwt.generateToken(user.getUsername());

        return new LoginResponse(token, user.getUsername(), user.getRole().name());
    }

}
