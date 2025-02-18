package com.ermnvldmr.w.controller;

import com.ermnvldmr.w.domain.Discussion;
import com.ermnvldmr.w.domain.Post;
import com.ermnvldmr.w.domain.User;
import com.ermnvldmr.w.domain.Vote;
import com.ermnvldmr.w.service.DiscussionService;
import com.ermnvldmr.w.service.PostService;
import com.ermnvldmr.w.service.UserService;
import com.ermnvldmr.w.service.VoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api")
@SuppressWarnings("unused")
public class VoteController {
    private final DiscussionService discussionService;
    private final PostService postService;
    private final UserService userService;
    private final VoteService voteService;

    public VoteController(DiscussionService discussionService,
                          PostService postService,
                          UserService userService,
                          VoteService voteService) {
        this.discussionService = discussionService;
        this.postService = postService;
        this.userService = userService;
        this.voteService = voteService;
    }

    @Operation(summary = "Vote on a post",
            description = "Allows a user to upvote or downvote a post.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vote recorded successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @PostMapping("/votes/post/{id}")
    public ResponseEntity<Object> votePost(
            @Parameter(description = "ID of the post to vote on", required = true)
            @PathVariable long id,
            @Parameter(description = "JWT of the user", required = true)
            @RequestParam String jwt,
            @Parameter(description = "True for upvote, false for downvote", required = true)
            @RequestParam boolean upvote) {

        User user = userService.findByJwt(jwt);
        if (user == null) {
            return ResponseEntity.status(401).build();  // 401 Unauthorized
        }

        Post post = postService.findById(id);
        if (post == null) {
            return ResponseEntity.notFound().build();  // 404 Not Found
        }

        Vote vote = voteService.votePost(post, user, upvote);
        return ResponseEntity.ok(vote);  // Return 200 OK with the vote details
    }

    @Operation(summary = "Vote on a discussion",
            description = "Allows a user to upvote or downvote a discussion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vote recorded successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Discussion not found")
    })
    @PostMapping("/votes/discussion/{id}")
    public ResponseEntity<Object> voteDiscussion(
            @Parameter(description = "ID of the discussion to vote on", required = true)
            @PathVariable long id,
            @Parameter(description = "JWT of the user", required = true)
            @RequestParam String jwt,
            @Parameter(description = "True for upvote, false for downvote", required = true)
            @RequestParam boolean upvote) {

        User user = userService.findByJwt(jwt);
        if (user == null) {
            return ResponseEntity.status(401).build();  // 401 Unauthorized
        }

        Discussion discussion = discussionService.findById(id);
        if (discussion == null) {
            return ResponseEntity.notFound().build();  // 404 Not Found
        }

        Vote vote = voteService.voteDiscussion(discussion, user, upvote);
        return ResponseEntity.ok(vote);  // Return 200 OK with the vote details
    }

    @Operation(summary = "Get user's vote on a post",
            description = "Fetches the vote status for a specific post.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vote details retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @GetMapping("/votes/post/{id}")
    public ResponseEntity<Object> getPostVote(
            @Parameter(description = "ID of the post to check the vote", required = true)
            @PathVariable long id,
            @Parameter(description = "JWT of the user", required = true)
            @RequestParam String jwt) {

        User user = userService.findByJwt(jwt);
        if (user == null) {
            return ResponseEntity.status(401).build();  // 401 Unauthorized
        }

        Post post = postService.findById(id);
        if (post == null) {
            return ResponseEntity.badRequest().build();  // 404 Not Found
        }

        // Fetch the vote status for the post
        Vote vote = voteService.findByPostAndUser(post, user);
        return ResponseEntity.ok(vote);  // Return 200 OK with the vote details or null if no vote
    }

    @Operation(summary = "Get user's vote on a discussion",
            description = "Fetches the vote status for a specific discussion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vote details retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Discussion not found")
    })
    @GetMapping("/votes/discussion/{id}")
    public ResponseEntity<Object> getDiscussionVote(
            @Parameter(description = "ID of the discussion to check the vote", required = true)
            @PathVariable long id,
            @Parameter(description = "JWT of the user", required = true)
            @RequestParam String jwt) {

        User user = userService.findByJwt(jwt);
        if (user == null) {
            return ResponseEntity.status(401).build();  // 401 Unauthorized
        }

        Discussion discussion = discussionService.findById(id);
        if (discussion == null) {
            return ResponseEntity.badRequest().build();  // 401 Bad request
        }

        // Fetch the vote status for the discussion
        Vote vote = voteService.findByDiscussionAndUser(discussion, user);
        return ResponseEntity.ok(vote);  // Return 200 OK with the vote details or null if no vote
    }
}
