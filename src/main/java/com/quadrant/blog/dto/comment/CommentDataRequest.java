package com.quadrant.blog.dto.comment;


import javax.validation.constraints.NotEmpty;

public class CommentDataRequest {

    private Long id;
    private Long userId;
    private Long blogId;


    @NotEmpty(message = "Comment is required")
    private String comment;

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
}
