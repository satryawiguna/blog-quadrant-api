package com.quadrant.blog.dto;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public class BaseDataResponse<T> {

    private String status;
    private HttpStatus code;

    protected List<String> messages = new ArrayList<>();

    private T Payload;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public HttpStatus getCode() {
        return code;
    }

    public void setCode(HttpStatus code) {
        this.code = code;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public T getPayload() {
        return Payload;
    }

    public void setPayload(T payload) {
        Payload = payload;
    }
}
