package com.social.media.controller;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.social.media.model.*;
import com.social.media.repository.*;
import com.social.media.service.FileStorageService;

@RestController
@RequestMapping("/api/posts")
public class PostController {
	@Autowired private PostRepository postRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private MediaRepository mediaRepo;
    @Autowired private LikeRepository likeRepo;
    @Autowired private CommentRepository commentRepo;
    @Autowired private FileStorageService fileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPost(
            @RequestPart(value = "text", required = false) String text,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            Authentication auth
    ) {
        User author = userRepo.findByUsername(auth.getName()).get();
        Post post = new Post(author, text);
        postRepo.save(post);

        if (files != null) {
            for (MultipartFile file : files) {
                String stored = fileService.store(file);

                com.social.media.model.MediaType type;

                String ct = file.getContentType() == null ? "" : file.getContentType();
                if (ct.startsWith("video")) type = com.social.media.model.MediaType.VIDEO;
                else if (ct.startsWith("audio")) type = com.social.media.model.MediaType.AUDIO;
                else type = com.social.media.model.MediaType.IMAGE;

                Media m = new Media(post, stored, "/files/" + stored, type);

                mediaRepo.save(m);
            }
        }

        return ResponseEntity.ok(post);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPost(@PathVariable Long id) {
        return postRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(postRepo.findAll());
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable Long postId, Authentication auth) {

        Post post = postRepo.findById(postId).orElse(null);
        if (post == null) return ResponseEntity.notFound().build();

        User user = userRepo.findByUsername(auth.getName()).get();

        if (likeRepo.findByUserAndPost(user, post).isPresent())
            return ResponseEntity.badRequest().body("Already liked");

        likeRepo.save(new Like(user, post));

        return ResponseEntity.ok("Liked");
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<?> unlikePost(@PathVariable Long postId, Authentication auth) {

        Post post = postRepo.findById(postId).orElse(null);
        if (post == null) return ResponseEntity.notFound().build();

        User user = userRepo.findByUsername(auth.getName()).get();

        Optional<Like> like = likeRepo.findByUserAndPost(user, post);
        if (!like.isPresent())
            return ResponseEntity.badRequest().body("Not liked");

        likeRepo.delete(like.get());

        return ResponseEntity.ok("Unliked");
    }

    @GetMapping("/{postId}/likes")
    public ResponseEntity<?> likeCount(@PathVariable Long postId) {
        return postRepo.findById(postId)
                .map(p -> ResponseEntity.ok(Collections.singletonMap("likes", likeRepo.countByPost(p))))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{postId}/comment")
    public ResponseEntity<?> addComment(
            @PathVariable Long postId,
            @RequestBody Map<String, String> req,
            Authentication auth
    ) {
        Post post = postRepo.findById(postId).orElse(null);
        if (post == null) return ResponseEntity.notFound().build();

        User user = userRepo.findByUsername(auth.getName()).get();

        Comment c = new Comment(post, user, req.get("text"));
        commentRepo.save(c);

        return ResponseEntity.ok(c);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, Authentication auth) {

        Comment c = commentRepo.findById(commentId).orElse(null);
        if (c == null) return ResponseEntity.notFound().build();

        if (!c.getAuthor().getUsername().equals(auth.getName()))
            return ResponseEntity.status(403).body("Not allowed");

        commentRepo.delete(c);

        return ResponseEntity.ok("Deleted");
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<?> listComments(@PathVariable Long postId) {
        Post post = postRepo.findById(postId).orElse(null);
        if (post == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(commentRepo.findByPost(post));
    }


}
