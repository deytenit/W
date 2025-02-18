package com.ermnvldmr.w.domain;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(indexes = @Index(columnList = "creationTime"))
@SuppressWarnings("unused")
public class Discussion {
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @OneToOne
    @JoinColumn(name = "parent_discussion_id")
    private Discussion parentDiscussion;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 65000)
    @Lob
    private String text;

    @OneToMany(mappedBy = "discussion", cascade = CascadeType.ALL)
    private final List<Vote> votes = new ArrayList<>();

    @CreationTimestamp
    private Date creationTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPost(final Post post) {
        this.post = post;
    }

    public Long getParentDiscussionId() {
        return parentDiscussion != null ? parentDiscussion.getId() : null;
    }

    public void setParentDiscussion(Discussion parentDiscussion) {
        this.parentDiscussion = parentDiscussion;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getVoteCount() {
        return votes.stream().mapToLong((vote) -> vote.isUpvote() ? 1 : -1).sum();
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
}
