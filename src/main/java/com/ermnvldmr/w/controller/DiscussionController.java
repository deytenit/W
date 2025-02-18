package com.ermnvldmr.w.controller;

import com.ermnvldmr.w.domain.Discussion;
import com.ermnvldmr.w.domain.Post;
import com.ermnvldmr.w.domain.User;
import com.ermnvldmr.w.service.DiscussionService;
import com.ermnvldmr.w.service.UserService;
import com.ermnvldmr.w.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ermnvldmr.w.utils.Utils.getValidationErrors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api")
@SuppressWarnings("unused")
public class DiscussionController {
    private final DiscussionService discussionService;
    private final PostService postService;
    private final UserService userService;

    public DiscussionController(DiscussionService discussionService,
                                PostService postService,
                                UserService userService) {
        this.discussionService = discussionService;
        this.postService = postService;
        this.userService = userService;
    }

    @Operation(summary = "Find discussions by post ID",
            description = "Retrieves a list of discussions associated with the given post ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved discussions"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @GetMapping(value = {"/discussions/post/{id}"})
    public ResponseEntity<List<Discussion>> findDiscussions(
            @Parameter(description = "ID of the post to find discussions for", required = true)
            @PathVariable long id) {
        Post post = postService.findById(id);
        if (post == null) {
            return ResponseEntity.notFound().build();  // 404 Not Found
        }

        List<Discussion> discussions = discussionService.findAllByPostId(post.getId());
        return ResponseEntity.ok(discussions);  // 200 OK
    }

    @Operation(summary = "Write a new discussion",
            description = "Creates a new discussion related to a post. Optionally, it can be a reply to an existing discussion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Discussion created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation errors or parent discussion not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @PostMapping(value = {"/discussions/post/{id}"})
    public ResponseEntity<Object> writeDiscussion(
            @Parameter(description = "ID of the post to write the discussion for", required = true)
            @PathVariable long id,

            @Parameter(description = "Discussion object to be created", required = true)
            @Valid @RequestBody Discussion discussion,

            @Parameter(description = "JWT token for user authentication", required = true)
            @RequestParam String jwt,

            @Parameter(description = "Optional parent discussion ID for nested discussions")
            @RequestParam(required = false) String parentDiscussionId,

            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(getValidationErrors(bindingResult));  // 400 Bad Request
        }

        Post post = postService.findById(id);
        if (post == null) {
            return ResponseEntity.notFound().build();  // 404 Not Found
        }

        User user = userService.findByJwt(jwt);
        if (user == null) {
            return ResponseEntity.status(401).body(null);  // 401 Unauthorized
        }

        if (parentDiscussionId != null) {
            Discussion parentDiscussion = discussionService.findById(Long.parseLong(parentDiscussionId));
            if (parentDiscussion == null) {
                return ResponseEntity.badRequest().build();  // 404 Not Found
            }
            discussion.setParentDiscussion(parentDiscussion);
        }

        discussion.setUser(user);
        discussion.setPost(post);

        Discussion savedDiscussion = discussionService.writeDiscussion(discussion);
        return ResponseEntity.status(201).body(savedDiscussion);  // 201 Created
    }

    @Operation(summary = "Delete a discussion",
            description = "Deletes the specified discussion. Only the author or an admin can perform this action.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Discussion deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Forbidden action"),
            @ApiResponse(responseCode = "404", description = "Discussion not found")
    })
    @DeleteMapping("discussions/{id}")
    public ResponseEntity<Void> deleteDiscussion(
            @Parameter(description = "ID of the discussion to delete", required = true)
            @PathVariable long id,
            @Parameter(description = "JWT token for authentication", required = true)
            @RequestParam String jwt) {
        User authenticatedUser = userService.findByJwt(jwt);
        if (authenticatedUser == null) {
            return ResponseEntity.status(401).build(); // 401 Unauthorized
        }

        Discussion discussionToDelete = discussionService.findById(id);
        if (discussionToDelete == null) {
            return ResponseEntity.status(404).build(); // 404 Not found
        }

        if (authenticatedUser.getId() != discussionToDelete.getUser().getId() && !authenticatedUser.isAdmin()) {
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        discussionService.deleteDiscussionById(id);
        return ResponseEntity.ok().build(); // 200 Ok
    }

    @Operation(summary = "Retrieve all discussions by user ID",
            description = "Fetches a list of discussions created by a specific user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user's discussions"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID provided"),
            @ApiResponse(responseCode = "404", description = "No discussions found for the specified user")
    })
    @GetMapping("/discussions/user/{userId}")
    public ResponseEntity<List<Discussion>> findDiscussionsByUserId(
            @Parameter(description = "ID of the user whose discussions are to be retrieved", required = true)
            @PathVariable Long userId) {
        if (userId == null || userId <= 0) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request
        }

        List<Discussion> discussions = discussionService.findAllByUserId(userId);
        if (discussions == null || discussions.isEmpty()) {
            return ResponseEntity.status(404).build(); // 404 Not Found
        }

        return ResponseEntity.ok(discussions); // 200 OK
    }
}
