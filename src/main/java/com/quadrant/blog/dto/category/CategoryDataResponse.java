package com.quadrant.blog.dto.category;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CategoryDataResponse {

    private Long id;

    private String name;

    private String slug;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
