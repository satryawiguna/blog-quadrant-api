package com.quadrant.blog.dto.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quadrant.blog.entity.UserEntity;


public class CommentDataResponse {
    private Long id;

    @JsonProperty("blog_id")
    private Long blogId;

    @JsonProperty("user_id")
    private Long userId;

    private String comment;

    private UserEntity user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
