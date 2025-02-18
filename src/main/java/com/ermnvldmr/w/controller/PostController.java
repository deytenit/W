package com.ermnvldmr.w.controller;

import com.ermnvldmr.w.domain.Post;
import com.ermnvldmr.w.domain.User;
import com.ermnvldmr.w.form.WritePostForm;
import com.ermnvldmr.w.service.PostService;
import com.ermnvldmr.w.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.ermnvldmr.w.utils.Utils.getValidationErrors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api")
@SuppressWarnings("unused")
public class PostController {
    private final PostService postService;
    private final UserService userService;

    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @Operation(summary = "Retrieve all posts",
            description = "Fetches a list of all posts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved posts")
    })
    @GetMapping("posts")
    public ResponseEntity<List<Post>> findPosts() {
        List<Post> posts = postService.findAll();
        return ResponseEntity.ok(posts);  // 200 OK
    }

    @Operation(summary = "Create a new post",
            description = "Writes a new post. Requires user authentication via JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Post created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation errors"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "500", description = "Internal server error due to media upload failure")
    })
    @PostMapping("posts")
    public ResponseEntity<Object> writePost(
            @Parameter(description = "Post data to create a new post", required = true)
            @Valid @RequestBody WritePostForm postForm,

            @Parameter(description = "JWT token for user authentication", required = true)
            @RequestParam String jwt,

            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(getValidationErrors(bindingResult));  // 400 Bad Request
        }

        User user = userService.findByJwt(jwt);
        if (user == null) {
            return ResponseEntity.status(401).build();  // 401 Unauthorized
        }

        try {
            Post createdPost = postService.writePost(postForm, user);
            return ResponseEntity.status(201).body(createdPost);  // 201 Created
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Media upload failed.");  // 500 Internal Server Error
        }
    }

    @Operation(summary = "Find a post by ID",
            description = "Retrieves a specific post by its ID, incrementing its view count.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved post"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @GetMapping(value = {"/posts/{id}"})
    public ResponseEntity<Post> findPost(
            @Parameter(description = "ID of the post to retrieve", required = true)
            @PathVariable long id,
            @Parameter(description = "JWT token for view mapping")
            @RequestParam String jwt,
            HttpServletRequest request) {
        final String viewerIdentifier = getViewerIdentifier(jwt, request);
        Post post = postService.getAndIncrementViewCount(id, viewerIdentifier);
        if (post == null) {
            return ResponseEntity.notFound().build();  // 404 Not Found
        }
        return ResponseEntity.ok(post);  // 200 OK
    }

    @Operation(summary = "Delete a post",
            description = "Deletes the specified post. Only the author or an admin can perform this action.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Forbidden action"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @DeleteMapping("posts/{id}")
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "ID of the post to delete", required = true)
            @PathVariable long id,
            @Parameter(description = "JWT token for authentication", required = true)
            @RequestParam String jwt) {
        User authenticatedUser = userService.findByJwt(jwt);
        if (authenticatedUser == null) {
            return ResponseEntity.status(401).build(); // 401 Unauthorized
        }

        Post postToDelete = postService.findById(id);
        if (postToDelete == null) {
            return ResponseEntity.status(404).build(); // 404 Not found
        }

        if (authenticatedUser.getId() != postToDelete.getUser().getId() && !authenticatedUser.isAdmin()) {
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        postService.deletePostById(id);
        return ResponseEntity.ok().build(); // 200 OK
    }

    @Operation(summary = "Retrieve all posts by user ID",
            description = "Fetches a list of posts created by a specific user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user's posts"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID provided"),
            @ApiResponse(responseCode = "404", description = "No posts found for the specified user")
    })
    @GetMapping("/posts/user/{userId}")
    public ResponseEntity<List<Post>> findPostsByUserId(
            @Parameter(description = "ID of the user whose posts are to be retrieved", required = true)
            @PathVariable Long userId) {
        if (userId == null || userId <= 0) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request
        }

        List<Post> posts = postService.findAllByUserId(userId);
        if (posts == null || posts.isEmpty()) {
            return ResponseEntity.status(404).build(); // 404 Not Found
        }

        return ResponseEntity.ok(posts); // 200 OK
    }

    private String getViewerIdentifier(String jwt, HttpServletRequest request) {
        if (jwt != null && !jwt.isBlank()) {
            User user = userService.findByJwt(jwt);
            if (user != null) {
                return "user-" + user.getId();
            }
        }
        return "ip-" + request.getRemoteAddr();
    }
}
