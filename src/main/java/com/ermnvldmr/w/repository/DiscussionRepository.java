package com.ermnvldmr.w.repository;

import com.ermnvldmr.w.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.ermnvldmr.w.domain.Discussion;

import java.util.List;

public interface DiscussionRepository extends JpaRepository<Discussion, Long> {
    @Query(value = "SELECT * FROM discussion WHERE post_id=?1", nativeQuery = true)
    List<Discussion> findAllByPostId(long postId);

    @Query(value = "SELECT * FROM discussion WHERE user_id=?1", nativeQuery = true)
    List<Discussion> findAllByUserId(Long userId);
}
