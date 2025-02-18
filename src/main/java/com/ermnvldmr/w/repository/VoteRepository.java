package com.ermnvldmr.w.repository;

import com.ermnvldmr.w.domain.Discussion;
import com.ermnvldmr.w.domain.Post;
import com.ermnvldmr.w.domain.User;
import com.ermnvldmr.w.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByUserAndPost(User user, Post post);
    Optional<Vote> findByUserAndDiscussion(User user, Discussion discussion);
}
