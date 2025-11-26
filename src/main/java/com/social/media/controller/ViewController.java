package com.social.media.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.social.media.model.Comment;
import com.social.media.model.Post;
import com.social.media.model.User;
import com.social.media.repository.CommentRepository;
import com.social.media.repository.LikeRepository;
import com.social.media.repository.PostRepository;
import com.social.media.repository.UserRepository;

@Controller
public class ViewController {
	
	@Autowired private PostRepository postRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private LikeRepository likeRepo;
    @Autowired private CommentRepository commentRepo; 
    
    @GetMapping({"/", "/feed"})
    public String feed(Model model) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        if (username == null || "anonymousUser".equals(username)) {
             return "redirect:/login";
        }

        Optional<User> currentUserOpt = userRepo.findByUsername(username);
        if (!currentUserOpt.isPresent()) {
            return "redirect:/login";
        }
        User currentUser = currentUserOpt.get();
        model.addAttribute("currentUser", currentUser);
        
        Set<User> following = new HashSet<>(currentUser.getFollowing());
        
        following.add(currentUser);
        
        List<Post> posts = postRepo.findByAuthorInOrderByCreatedAtDesc(following);

        List<PostDto> enhancedPosts = posts.stream().map(post -> {
            PostDto dto = new PostDto(post);
            
            dto.setLikeCount(likeRepo.countByPost(post));
            
            dto.setIsLiked(likeRepo.findByUserAndPost(currentUser, post).isPresent());
            
            List<Comment> comments = commentRepo.findByPost(post);
            dto.setCommentCount(comments.size());
            
            return dto;
        }).collect(Collectors.toList());

        model.addAttribute("posts", enhancedPosts);

        return "feed";
    }
    
    @GetMapping("/search")
    public String searchUsers(@RequestParam("query") String query, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || "anonymousUser".equals(auth.getName())) return "redirect:/login";

        User currentUser = userRepo.findByUsername(auth.getName()).orElse(null);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("query", query);

        if (query != null && !query.trim().isEmpty()) {
            List<User> results = userRepo.findByNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(query, query);
            results.removeIf(u -> u.getId().equals(currentUser.getId()));
            model.addAttribute("users", results);
        } else {
            model.addAttribute("users", new ArrayList<User>());
        }

        return "search";
    }
    
    @GetMapping("/profile")
    public String myProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || "anonymousUser".equals(auth.getName())) return "redirect:/login";
        return userProfile(auth.getName(), model);
    }

    @GetMapping("/user/{username}")
    public String userProfile(@PathVariable String username, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;
        if (auth != null && !"anonymousUser".equals(auth.getName())) {
            currentUser = userRepo.findByUsername(auth.getName()).orElse(null);
        }
        
        if (currentUser == null) return "redirect:/login";
        model.addAttribute("currentUser", currentUser);

        Optional<User> profileOwnerOpt = userRepo.findByUsername(username);
        if (!profileOwnerOpt.isPresent()) {
            return "redirect:/feed";
        }
        User profileOwner = profileOwnerOpt.get();
        model.addAttribute("profileOwner", profileOwner);

        boolean isFollowing = currentUser.getFollowing().contains(profileOwner);
        model.addAttribute("isFollowing", isFollowing);

        List<Post> posts = postRepo.findByAuthorOrderByCreatedAtDesc(profileOwner);
        
        User finalCurrentUser = currentUser;
        List<PostDto> enhancedPosts = posts.stream().map(post -> {
            PostDto dto = new PostDto(post);
            dto.setLikeCount(likeRepo.countByPost(post));
            dto.setIsLiked(likeRepo.findByUserAndPost(finalCurrentUser, post).isPresent());
            dto.setCommentCount(commentRepo.findByPost(post).size());
            return dto;
        }).collect(Collectors.toList());

        model.addAttribute("posts", enhancedPosts);

        return "profile";
    }

    @GetMapping("/post/{id}")
    public String postView(@PathVariable Long id, Model model) {
        Optional<Post> postOpt = postRepo.findById(id);
        
        if (!postOpt.isPresent()) {
            return "redirect:/feed";
        }

        Post post = postOpt.get();
        PostDto dto = new PostDto(post);
        
        List<Comment> comments = commentRepo.findByPost(post);
        dto.setComments(comments);
        dto.setCommentCount(comments.size());
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !"anonymousUser".equals(auth.getName())) {
             User currentUser = userRepo.findByUsername(auth.getName()).orElse(null);
             if (currentUser != null) {
                 dto.setLikeCount(likeRepo.countByPost(post));
                 dto.setIsLiked(likeRepo.findByUserAndPost(currentUser, post).isPresent());
                 model.addAttribute("currentUser", currentUser);
             }
        }

        model.addAttribute("post", dto);
        return "post";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    public static class PostDto extends Post {
        private long likeCount;
        private int commentCount;
        private boolean isLiked;
        private List<Comment> comments;

        public PostDto(Post post) {
            this.setId(post.getId());
            this.setAuthor(post.getAuthor());
            this.setText(post.getText());
            this.setMediaList(post.getMediaList());
            this.setCreatedAt(post.getCreatedAt());
        }

        public long getLikeCount() { return likeCount; }
        public void setLikeCount(long likeCount) { this.likeCount = likeCount; }

        public int getCommentCount() { return commentCount; }
        public void setCommentCount(int commentCount) { this.commentCount = commentCount; }

        public boolean getIsLiked() { return isLiked; }
        public void setIsLiked(boolean isLiked) { this.isLiked = isLiked; }
        
        public List<Comment> getComments() { return comments; }
        public void setComments(List<Comment> comments) { this.comments = comments; }
    }
}
