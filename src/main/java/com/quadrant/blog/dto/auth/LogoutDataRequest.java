package com.quadrant.blog.dto.auth;

public class LogoutDataRequest {
    private Long userId;

    private String email;

    public Long getUserId() {
        return this.userId;
    }

    public String getEmail() {
        return email;
    }
}
