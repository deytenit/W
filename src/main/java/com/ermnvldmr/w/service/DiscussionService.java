package com.ermnvldmr.w.service;

import com.ermnvldmr.w.domain.Post;
import org.springframework.stereotype.Service;
import com.ermnvldmr.w.domain.Discussion;
import com.ermnvldmr.w.repository.DiscussionRepository;

import java.util.List;

@Service
public class DiscussionService {
    private final DiscussionRepository discussionRepository;

    public DiscussionService(DiscussionRepository discussionRepository) {
        this.discussionRepository = discussionRepository;
    }

    public Discussion findById(long id) {
        return discussionRepository.findById(id).orElse(null);
    }

    public List<Discussion> findAllByPostId(long postId) { return discussionRepository.findAllByPostId(postId); }

    public Discussion writeDiscussion(Discussion discussion) {
        return discussionRepository.save(discussion);
    }

    public void deleteDiscussionById(long id) {
        discussionRepository.deleteById(id);
    }

    public List<Discussion> findAllByUserId(Long userId) {
        return discussionRepository.findAllByUserId(userId);
    }
}
