package com.quadrant.blog;

import com.quadrant.blog.dto.BaseDataResponse;
import com.quadrant.blog.dto.PageDataResponse;
import com.quadrant.blog.dto.auth.RegisterDataResponse;
import com.quadrant.blog.dto.blog.BlogDataRequest;
import com.quadrant.blog.dto.blog.BlogDataResponse;
import com.quadrant.blog.dto.category.CategoryDataRequest;
import com.quadrant.blog.dto.category.CategoryDataResponse;
import com.quadrant.blog.entity.BlogEntity;
import com.quadrant.blog.entity.CategoryEntity;
import com.quadrant.blog.entity.RoleEntity;
import com.quadrant.blog.entity.UserEntity;
import com.quadrant.blog.exception.ResourceNotFoundException;
import com.quadrant.blog.service.AuthService;
import com.quadrant.blog.service.BlogService;
import com.quadrant.blog.service.CategoryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BlogApplicationTest {
    @Autowired
    private AuthService authService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BlogService blogService;

    @Autowired
    private Environment environment;

    @Order(1)
    @Test
    void testLoginIsSuccess() {
        BaseDataResponse<Map<String, Object>> loginResponse = authService.login("admin@quadrant-blog.com", "12345", new BaseDataResponse<Map<String, Object>>());

        assertEquals("SUCCESS", loginResponse.getStatus());
    }

    @Order(2)
    @Test
    void testLoginIsFailed() {
        BaseDataResponse<Map<String, Object>> loginResponse = authService.login("tono@gmail.com", "12345", new BaseDataResponse<Map<String, Object>>());

        assertEquals("ERROR", loginResponse.getStatus());
    }

    @Order(3)
    @Test
    void testRegisterIsSuccess() {
        BaseDataResponse<RegisterDataResponse> response =  new BaseDataResponse<>();

        RoleEntity roleEntity = authService.getRoleAsUser();

        UserEntity userEntity = UserEntity.builder()
                .fullName("Full Name")
                .nickName("Nick name")
                .email("email@gmail.com")
                .password(new BCryptPasswordEncoder().encode("12345"))
                .role(roleEntity)
                .build();

        authService.register(userEntity, response);

        Assertions.assertNotEquals(userEntity.getId(), 0);
    }

    @Order(4)
    @Test()
    void testRegisterIsFailed() {
        BaseDataResponse<RegisterDataResponse> response =  new BaseDataResponse<>();

        UserEntity userEntity = UserEntity.builder()
                .fullName("Full Name")
                .nickName("Nick name")
                .build();

        DataIntegrityViolationException thrown = assertThrows(
                DataIntegrityViolationException.class, () -> authService.register(userEntity, response),
                "could not execute statement");

        assertTrue(thrown.getMessage().contains("could not execute statement"));
    }



    @Order(5)
    @Test()
    void testGetCategoriesIsSuccess() {
        BaseDataResponse<List<CategoryDataResponse>> getCategoriesResponse = categoryService.getCategories();

        assertEquals("SUCCESS", getCategoriesResponse.getStatus());
    }

    @Order(6)
    @Test()
    void testGetCategoriesWithPagingIsSuccess() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        BaseDataResponse<PageDataResponse<CategoryDataResponse>> getCategoriesResponse = categoryService.getCategoriesWithPaging("", pageable);

        assertEquals("SUCCESS", getCategoriesResponse.getStatus());
    }

    @Order(7)
    @Test()
    @WithMockUser(username = "admin@quadrant-blog.com", authorities = { "ADMIN" })
    void testGetCategoryIsSuccess() {
        BaseDataResponse<CategoryDataResponse> response =  new BaseDataResponse<>();

        CategoryEntity category = CategoryEntity.builder()
                .name("Category 7")
                .slug("category-7")
                .build();

        categoryService.createCategory(category, response);

        BaseDataResponse<CategoryDataResponse> getCategoryResponse = categoryService.getCategory((long) 1);

        assertEquals("SUCCESS", getCategoryResponse.getStatus());
    }

    @Order(8)
    @Test()
    @WithMockUser(username = "admin@quadrant-blog.com", authorities = { "ADMIN" })
    void testGetCategoryIsFailed() {
        BaseDataResponse<CategoryDataResponse> getCategoryResponse = categoryService.getCategory((long) 8);

        assertEquals("ERROR", getCategoryResponse.getStatus());
    }

    @Order(9)
    @Test()
    @WithMockUser(username = "admin@quadrant-blog.com", authorities = { "ADMIN" })
    void testCreateCategoryIsSuccess() {
        BaseDataResponse<CategoryDataResponse> response =  new BaseDataResponse<>();

        CategoryEntity category = CategoryEntity.builder()
                .name("Category 9")
                .slug("category-9")
                .build();

        BaseDataResponse<CategoryDataResponse> createCategoryResponse = categoryService.createCategory(category, response);

        assertEquals("SUCCESS", createCategoryResponse.getStatus());
    }

    @Order(10)
    @Test()
    @WithMockUser(username = "admin@quadrant-blog.com", authorities = { "ADMIN" })
    void testCreateCategoryIsFailed() {
        BaseDataResponse<CategoryDataResponse> response =  new BaseDataResponse<>();

        CategoryEntity category = CategoryEntity.builder()
                .build();

        DataIntegrityViolationException thrown = assertThrows(
                DataIntegrityViolationException.class, () -> categoryService.createCategory(category, response),
                "could not execute statement");

        assertTrue(thrown.getMessage().contains("could not execute statement"));
    }

    @Order(11)
    @Test()
    @WithMockUser(username = "admin@quadrant-blog.com", authorities = { "ADMIN" })
    void testUpdateCategoryIsSuccess() {
        BaseDataResponse<CategoryDataResponse> response =  new BaseDataResponse<>();

        CategoryEntity category = CategoryEntity.builder()
                .name("Category 1")
                .slug("category-1")
                .build();

        BaseDataResponse<CategoryDataResponse> createCategoryResponse = categoryService.createCategory(category, response);

        CategoryDataRequest request = new CategoryDataRequest();
        request.setName("Category 2");
        request.setSlug("category-2");

        BaseDataResponse<CategoryDataResponse> updateCategoryResponse = categoryService.updateCategory(createCategoryResponse.getPayload().getId(), request, response);

        assertEquals("SUCCESS", updateCategoryResponse.getStatus());
    }

    @Order(12)
    @Test()
    @WithMockUser(username = "admin@quadrant-blog.com", authorities = { "ADMIN" })
    void testUpdateCategoryIsFailed() {
        BaseDataResponse<CategoryDataResponse> response =  new BaseDataResponse<>();

        CategoryEntity category = CategoryEntity.builder()
                .name("Category 12")
                .slug("category-12")
                .build();

        BaseDataResponse<CategoryDataResponse> createCategoryResponse = categoryService.createCategory(category, response);

        CategoryDataRequest request = new CategoryDataRequest();
        request.setName("");

        DataIntegrityViolationException thrown = assertThrows(
                DataIntegrityViolationException.class, () -> categoryService.updateCategory(createCategoryResponse.getPayload().getId(), request, response),
                "could not execute statement");

        assertTrue(thrown.getMessage().contains("could not execute statement"));
    }

    @Order(13)
    @Test()
    @WithMockUser(username = "admin@quadrant-blog.com", authorities = { "ADMIN" })
    void testDeleteCategoryIsSuccess() {
        CategoryEntity category = CategoryEntity.builder()
                .name("Category 1")
                .slug("category-1")
                .build();

        BaseDataResponse<CategoryDataResponse> response =  new BaseDataResponse<>();

        BaseDataResponse<CategoryDataResponse> createCategoryResponse = categoryService.createCategory(category, response);

        assertDoesNotThrow(() -> categoryService.deleteCategory(createCategoryResponse.getPayload().getId()));
    }

    @Order(14)
    @Test()
    @WithMockUser(username = "admin@quadrant-blog.com", authorities = { "ADMIN" })
    void testDeleteCategoryIsFailed() {
        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory((long) 14));
    }



    @Order(15)
    @Test()
    void testGetBlogsIsSuccess() {
        BaseDataResponse<List<BlogDataResponse>> getBlogsResponse = blogService.getBlogs();

        assertEquals("SUCCESS", getBlogsResponse.getStatus());
    }

    @Order(16)
    @Test()
    void testGetBlogsWithPagingIsSuccess() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        BaseDataResponse<PageDataResponse<BlogDataResponse>> getBlogsResponse = blogService.getBlogsWithPaging("", pageable);

        assertEquals("SUCCESS", getBlogsResponse.getStatus());
    }

    @Order(17)
    @Test()
    @WithMockUser(username = "admin@quadrant-blog.com", authorities = { "ADMIN" })
    void testGetBlogIsSuccess() {
        BaseDataResponse<BlogDataResponse> response =  new BaseDataResponse<>();

        RoleEntity roleEntity = authService.getRoleAsUser();

        UserEntity user = UserEntity.builder()
                .fullName("Full Name")
                .nickName("Nick name")
                .email("email@gmail.com")
                .password(new BCryptPasswordEncoder().encode("12345"))
                .role(roleEntity)
                .build();

        CategoryEntity category = CategoryEntity.builder()
                .name("Category 17")
                .slug("category-17")
                .build();

        BlogEntity blog = BlogEntity.builder()
                .title("Blog 17")
                .slug("blog-17")
                .category(category)
                .user(user)
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "test.jpg",
                "test.jpg",
                "application/json",
                String.format("{\"image\": \"%s\"}", environment.getProperty("app.file.upload.mapping.test") + File.separator + "test.jpg").getBytes()
        );

        BaseDataResponse<BlogDataResponse> createBlogResponse = blogService.createBlog(blog, file, response);

        BaseDataResponse<BlogDataResponse> getBlogResponse = blogService.getBlog(createBlogResponse.getPayload().getId());

        assertEquals("SUCCESS", getBlogResponse.getStatus());
    }

    @Order(18)
    @Test()
    @WithMockUser(username = "admin@quadrant-blog.com", authorities = { "ADMIN" })
    void testGetBlogIsFailed() {
        BaseDataResponse<BlogDataResponse> response =  new BaseDataResponse<>();

        RoleEntity roleEntity = authService.getRoleAsUser();

        UserEntity user = UserEntity.builder()
                .fullName("Full Name")
                .nickName("Nick name")
                .email("email@gmail.com")
                .password(new BCryptPasswordEncoder().encode("12345"))
                .role(roleEntity)
                .build();

        CategoryEntity category = CategoryEntity.builder()
                .name("Category 18")
                .slug("category-18")
                .build();

        BlogEntity blog = BlogEntity.builder()
                .title("Blog 18")
                .slug("blog-18")
                .category(category)
                .user(user)
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "test.jpg",
                "test.jpg",
                "application/json",
                String.format("{\"image\": \"%s\"}", environment.getProperty("app.file.upload.mapping.test") + File.separator + "test.jpg").getBytes()
        );

        blogService.createBlog(blog, file, response);

        BaseDataResponse<BlogDataResponse> getBlogResponse = blogService.getBlog((long) 18);

        assertEquals("ERROR", getBlogResponse.getStatus());
    }

    @Order(19)
    @Test()
    @WithMockUser(username = "admin@quadrant-blog.com", authorities = { "ADMIN" })
    void testCreateBlogIsSuccess() {
        BaseDataResponse<BlogDataResponse> response =  new BaseDataResponse<>();

        RoleEntity roleEntity = authService.getRoleAsUser();

        UserEntity user = UserEntity.builder()
                .fullName("Full Name")
                .nickName("Nick name")
                .email("email@gmail.com")
                .password(new BCryptPasswordEncoder().encode("12345"))
                .role(roleEntity)
                .build();

        CategoryEntity category = CategoryEntity.builder()
                .name("Category 19")
                .slug("category-19")
                .build();

        BlogEntity blog = BlogEntity.builder()
                .title("Blog 19")
                .slug("blog-19")
                .category(category)
                .user(user)
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "test.jpg",
                "test.jpg",
                "application/json",
                String.format("{\"image\": \"%s\"}", environment.getProperty("app.file.upload.mapping.test") + File.separator + "test.jpg").getBytes()
        );

        BaseDataResponse<BlogDataResponse> createBlogResponse = blogService.createBlog(blog, file, response);

        assertEquals("SUCCESS", createBlogResponse.getStatus());
    }

    @Order(20)
    @Test()
    @WithMockUser(username = "admin@quadrant-blog.com", authorities = { "ADMIN" })
    void testCreateBlogIsFailed() {
        BaseDataResponse<BlogDataResponse> response =  new BaseDataResponse<>();

        BlogEntity blog = BlogEntity.builder()
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "test.jpg",
                "test.jpg",
                "application/json",
                String.format("{\"image\": \"%s\"}", environment.getProperty("app.file.upload.mapping.test") + File.separator + "test.jpg").getBytes()
        );

        DataIntegrityViolationException thrown = assertThrows(
                DataIntegrityViolationException.class, () -> blogService.createBlog(blog, file, response),
                "could not execute statement");

        assertTrue(thrown.getMessage().contains("could not execute statement"));
    }

    @Order(21)
    @Test()
    @WithMockUser(username = "admin@quadrant-blog.com", authorities = { "ADMIN" })
    void testUpdateBlogIsSuccess() {
        BaseDataResponse<BlogDataResponse> response =  new BaseDataResponse<>();

        RoleEntity roleEntity = authService.getRoleAsUser();

        UserEntity user = UserEntity.builder()
                .fullName("Full Name")
                .nickName("Nick name")
                .email("email@gmail.com")
                .password(new BCryptPasswordEncoder().encode("12345"))
                .role(roleEntity)
                .build();

        CategoryEntity category = CategoryEntity.builder()
                .name("Category 21")
                .slug("category-21")
                .build();

        BlogEntity blog = BlogEntity.builder()
                .title("Blog 21")
                .slug("blog-21")
                .category(category)
                .user(user)
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "test.jpg",
                "test.jpg",
                "application/json",
                String.format("{\"image\": \"%s\"}", environment.getProperty("app.file.upload.mapping.test") + File.separator + "test.jpg").getBytes()
        );

        BaseDataResponse<BlogDataResponse> createBlogResponse = blogService.createBlog(blog, file, response);

        BlogDataRequest request = BlogDataRequest.builder()
                .title("Category 22")
                .slug("category-22")
                .build();

        BaseDataResponse<BlogDataResponse> updateBlogResponse = blogService.updateBlog(createBlogResponse.getPayload().getId(), request, response);

        assertEquals("SUCCESS", updateBlogResponse.getStatus());
    }

    @Order(22)
    @Test()
    @WithMockUser(username = "admin@quadrant-blog.com", authorities = { "ADMIN" })
    void testUpdateBlogIsFailed() {
        BaseDataResponse<BlogDataResponse> response =  new BaseDataResponse<>();

        RoleEntity roleEntity = authService.getRoleAsUser();

        UserEntity user = UserEntity.builder()
                .fullName("Full Name")
                .nickName("Nick name")
                .email("email@gmail.com")
                .password(new BCryptPasswordEncoder().encode("12345"))
                .role(roleEntity)
                .build();

        CategoryEntity category = CategoryEntity.builder()
                .name("Category 22")
                .slug("category-22")
                .build();

        BlogEntity blog = BlogEntity.builder()
                .title("Blog 22")
                .slug("blog-22")
                .category(category)
                .user(user)
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "test.jpg",
                "test.jpg",
                "application/json",
                String.format("{\"image\": \"%s\"}", environment.getProperty("app.file.upload.mapping.test") + File.separator + "test.jpg").getBytes()
        );

        BaseDataResponse<BlogDataResponse> createBlogResponse = blogService.createBlog(blog, file, response);

        BlogDataRequest request = BlogDataRequest.builder()
                .build();

        DataIntegrityViolationException thrown = assertThrows(
                DataIntegrityViolationException.class, () -> blogService.updateBlog(createBlogResponse.getPayload().getId(), request, response),
                "could not execute statement");

        assertTrue(thrown.getMessage().contains("could not execute statement"));
    }

    @Order(23)
    @Test()
    @WithMockUser(username = "admin@quadrant-blog.com", authorities = { "ADMIN" })
    void testDeleteBlogIsSuccess() {
        BaseDataResponse<BlogDataResponse> response =  new BaseDataResponse<>();

        RoleEntity roleEntity = authService.getRoleAsUser();

        UserEntity user = UserEntity.builder()
                .fullName("Full Name")
                .nickName("Nick name")
                .email("email@gmail.com")
                .password(new BCryptPasswordEncoder().encode("12345"))
                .role(roleEntity)
                .build();

        CategoryEntity category = CategoryEntity.builder()
                .name("Category 23")
                .slug("category-23")
                .build();

        BlogEntity blog = BlogEntity.builder()
                .title("Blog 23")
                .slug("blog-23")
                .category(category)
                .user(user)
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "test.jpg",
                "test.jpg",
                "application/json",
                String.format("{\"image\": \"%s\"}", environment.getProperty("app.file.upload.mapping.test") + File.separator + "test.jpg").getBytes()
        );

        BaseDataResponse<BlogDataResponse> createBlogResponse = blogService.createBlog(blog, file, response);

        assertDoesNotThrow(() -> categoryService.deleteCategory(createBlogResponse.getPayload().getId()));
    }

    @Order(24)
    @Test()
    @WithMockUser(username = "admin@quadrant-blog.com", authorities = { "ADMIN" })
    void testDeleteBlogIsFailed() {
        assertThrows(ResourceNotFoundException.class, () -> blogService.deleteBlog((long) 24));
    }

}
