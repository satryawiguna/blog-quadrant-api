package com.quadrant.blog.dto.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class LoginDataRequest {

    @NotEmpty(message = "Email is required")
    @Email
    private String email;

    @NotEmpty(message = "Password is required")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
