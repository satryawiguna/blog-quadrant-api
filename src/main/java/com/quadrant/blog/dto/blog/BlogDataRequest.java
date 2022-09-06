package com.quadrant.blog.dto.blog;

import com.quadrant.blog.entity.CategoryEntity;
import com.quadrant.blog.entity.UserEntity;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;

@Builder
public class BlogDataRequest {

    @NotEmpty(message = "Title is required")
    private String title;

    @NotEmpty(message = "Slug is required")
    private String slug;

    private String description;

    private MultipartFile image;

    private CategoryEntity category;

    private UserEntity user;

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

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
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
}
