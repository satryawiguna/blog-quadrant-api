package com.quadrant.blog.dto.blog;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quadrant.blog.entity.CategoryEntity;
import com.quadrant.blog.entity.UserEntity;

import java.time.Instant;
import java.util.Date;

public class BlogDataResponse {

    private Long id;

    private String title;

    private String slug;

    private String description;

    @JsonProperty("image_path")
    private String imagePath;

    @JsonProperty("image_absolute")
    private String imageAbsolute;

    @JsonProperty("image_file")
    private String imageFile;

    @JsonProperty("image_size")
    private Double imageSize;

    private int viewed;

    private CategoryEntity category;

    private UserEntity user;

    @JsonProperty("created_date")
    private Date createdDate;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageAbsolute() {
        return imageAbsolute;
    }

    public void setImageAbsolute(String imageAbsolute) {
        this.imageAbsolute = imageAbsolute;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    public Double getImageSize() {
        return imageSize;
    }

    public void setImageSize(Double imageSize) {
        this.imageSize = imageSize;
    }

    public int getViewed() {
        return viewed;
    }

    public void setViewed(int viewed) {
        this.viewed = viewed;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
