package com.quadrant.blog.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class RegisterDataRequest {

    @Size(max = 50, message = "Nick name could not exceed than 50")
    @NotEmpty(message = "Nick name is required")
    @JsonProperty("nick_name")
    private String nickName;

    @NotEmpty
    @JsonProperty("full_name")
    private String fullName;

    @NotEmpty(message = "Full name is required")
    @Email
    private String email;

    @Size(min = 5, message = "Password length should have over than 5")
    @NotEmpty(message = "Password is required")
    private String password;

    @JsonProperty("confirm_password")
    private String confirmPassword;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
