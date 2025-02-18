package com.ermnvldmr.w.service;

import com.ermnvldmr.w.domain.Discussion;
import com.ermnvldmr.w.domain.Post;
import com.ermnvldmr.w.domain.User;
import com.ermnvldmr.w.domain.Vote;
import com.ermnvldmr.w.repository.VoteRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VoteService {
    private final VoteRepository voteRepository;

    public VoteService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    public Vote findByPostAndUser(Post post, User user) {
        return voteRepository.findByUserAndPost(user, post).orElse(null);
    }

    public Vote findByDiscussionAndUser(Discussion discussion, User user) {
        return voteRepository.findByUserAndDiscussion(user, discussion).orElse(null);
    }

    public Vote votePost(Post post, User user, boolean upvote) {
        Optional<Vote> existingVote = voteRepository.findByUserAndPost(user, post);
        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();
            if (vote.isUpvote() == upvote) {
                voteRepository.delete(vote); // Toggle off the vote if it’s the same type.
                return null;
            } else {
                vote.setUpvote(upvote); // Change vote type if it’s different.
                return voteRepository.save(vote);
            }
        } else {
            Vote newVote = new Vote();
            newVote.setUser(user);
            newVote.setUpvote(upvote);
            newVote.setPost(post);
            return voteRepository.save(newVote);
        }
    }

    public Vote voteDiscussion(Discussion discussion, User user, boolean upvote) {
        Optional<Vote> existingVote = voteRepository.findByUserAndDiscussion(user, discussion);
        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();
            if (vote.isUpvote() == upvote) {
                voteRepository.delete(vote); // Toggle off the vote if it’s the same type.
                return null;
            } else {
                vote.setUpvote(upvote); // Change vote type if it’s different.
                return voteRepository.save(vote);
            }
        } else {
            Vote newVote = new Vote();
            newVote.setUser(user);
            newVote.setUpvote(upvote);
            newVote.setDiscussion(discussion);
            return voteRepository.save(newVote);
        }
    }
}
