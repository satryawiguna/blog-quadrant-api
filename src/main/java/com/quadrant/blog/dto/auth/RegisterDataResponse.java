package com.quadrant.blog.dto.auth;


import com.fasterxml.jackson.annotation.JsonProperty;

public class RegisterDataResponse {

    @JsonProperty("nick_name")
    private String nickName;

    @JsonProperty("full_name")
    private String fullName;

    private String email;

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
}
