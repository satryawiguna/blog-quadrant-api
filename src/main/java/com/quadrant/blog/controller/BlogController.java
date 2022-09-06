package com.quadrant.blog.controller;

import com.quadrant.blog.dto.BaseDataResponse;
import com.quadrant.blog.dto.SearchDataRequest;
import com.quadrant.blog.dto.blog.BlogDataRequest;
import com.quadrant.blog.dto.blog.BlogDataResponse;
import com.quadrant.blog.dto.PageDataResponse;
import com.quadrant.blog.dto.category.CategoryDataResponse;
import com.quadrant.blog.entity.BlogEntity;
import com.quadrant.blog.entity.CategoryEntity;
import com.quadrant.blog.entity.UserEntity;
import com.quadrant.blog.service.AuthService;
import com.quadrant.blog.service.BlogService;
import com.quadrant.blog.service.CategoryService;
import com.quadrant.blog.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.bytebuddy.implementation.bytecode.Throw;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@Validated
public class BlogController {

    private final BlogService blogService;
    private final UserService userService;
    private final CategoryService categoryService;

    private final ModelMapper modelMapper;
    private final Log logger = LogFactory.getLog(getClass());

    public BlogController(ModelMapper modelMapper,
                          BlogService blogService,
                          UserService userService,
                          CategoryService categoryService) {
        this.modelMapper = modelMapper;
        this.blogService = blogService;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    @GetMapping("/blogs")
    public ResponseEntity<BaseDataResponse<List<BlogDataResponse>>> all() {
        BaseDataResponse<List<BlogDataResponse>> getBlogsResponse = blogService.getBlogs();

        return ResponseEntity.status(getBlogsResponse.getCode()).body(getBlogsResponse);
    }

    @GetMapping("/common/blogs")
    public ResponseEntity<BaseDataResponse<List<BlogDataResponse>>> commonAll() {
        BaseDataResponse<List<BlogDataResponse>> getBlogsResponse = blogService.getBlogs();

        return ResponseEntity.status(getBlogsResponse.getCode()).body(getBlogsResponse);
    }

    @GetMapping("/blogs/page/{page}/{size}/{sort}")
    public ResponseEntity<BaseDataResponse<PageDataResponse<BlogDataResponse>>> allWithPaging(@PathVariable("page") int page, @PathVariable("size") int size, @PathVariable("sort") String sort, @RequestBody SearchDataRequest request) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));

        if (sort.equalsIgnoreCase("desc"))
            pageable = PageRequest.of(page, size, Sort.by("id").descending());

        BaseDataResponse<PageDataResponse<BlogDataResponse>> getBlogsWithPagingResponse = blogService.getBlogsWithPaging(request.getSearch(), pageable);

        return ResponseEntity.status(getBlogsWithPagingResponse.getCode()).body(getBlogsWithPagingResponse);
    }

    @GetMapping("/blog/{id}")
    public ResponseEntity<BaseDataResponse<BlogDataResponse>> get(@PathVariable("id") Long id) {
        BaseDataResponse<BlogDataResponse> getBlogResponse = blogService.getBlog(id);

        return ResponseEntity.status(getBlogResponse.getCode()).body(getBlogResponse);
    }

    @PostMapping("/blog")
    public ResponseEntity<BaseDataResponse<BlogDataResponse>> store(@RequestParam(value = "title") @NotEmpty(message = "Title could not be empty") String title,
                                                                    @RequestParam(value = "slug") @NotEmpty(message = "Slug could not be empty") String slug,
                                                                    @RequestParam(value = "description") String description,
                                                                    @RequestParam(value = "file") MultipartFile file,
                                                                    @RequestParam(value = "category_id") Long categoryId,
                                                                    @RequestParam(value = "user_id") Long userId) {
        BaseDataResponse<BlogDataResponse> response =  new BaseDataResponse<>();

        // Get user
        UserEntity getUserResponse = userService.getUserEntity(userId);

        // Get category
        CategoryEntity getCategoryResponse = categoryService.getCategoryEntity(categoryId);

        BlogDataRequest request = BlogDataRequest.builder()
                .title(title)
                .slug(slug)
                .description(description)
                .file(file)
                .category(getCategoryResponse)
                .user(getUserResponse)
                .build();

        MultipartFile mf = request.getFile();
        BlogEntity blog = modelMapper.map(request, BlogEntity.class);

        BaseDataResponse<BlogDataResponse> createBlogResponse = blogService.createBlog(blog, mf, response);

        return ResponseEntity.status(createBlogResponse.getCode()).body(createBlogResponse);

    }

    @PutMapping("/blog/{id}")
    public ResponseEntity<BaseDataResponse<?>> update(@PathVariable("id") Long id,
                                                      @RequestParam(value = "title") @NotEmpty(message = "Title could not be empty") String title,
                                                      @RequestParam(value = "slug") @NotEmpty(message = "Slug could not be empty") String slug,
                                                      @RequestParam(value = "description") String description,
                                                      @RequestParam(value = "file") MultipartFile file,
                                                      @RequestParam(value = "category_id") Long categoryId,
                                                      @RequestParam(value = "user_id") Long userId) {
        BaseDataResponse<BlogDataResponse> response =  new BaseDataResponse<>();

        // Get user
        UserEntity getUserResponse = userService.getUserEntity(userId);

        // Get category
        CategoryEntity getCategoryResponse = categoryService.getCategoryEntity(categoryId);

        BlogDataRequest request = BlogDataRequest.builder()
                .title(title)
                .slug(slug)
                .description(description)
                .file(file)
                .category(getCategoryResponse)
                .user(getUserResponse)
                .build();

        BaseDataResponse<BlogDataResponse> updateBlogResponse = blogService.updateBlog(id, request, response);

        return ResponseEntity.status(updateBlogResponse.getCode()).body(updateBlogResponse);
    }

    @PutMapping("/blog/view/{id}")
    public ResponseEntity<BaseDataResponse<?>> updateView(@PathVariable("id") Long id) {
        BaseDataResponse<BlogDataResponse> response =  new BaseDataResponse<>();

        BaseDataResponse<BlogDataResponse> updateBlogResponse = blogService.updateBlogView(id, response);

        return ResponseEntity.status(updateBlogResponse.getCode()).body(updateBlogResponse);
    }

    @DeleteMapping("/blog/{id}")
    public ResponseEntity<BaseDataResponse<?>> delete(@PathVariable("id") Long id) {

        BaseDataResponse<?> response =  new BaseDataResponse<>();

        blogService.deleteBlog(id);

        response.setStatus("SUCCESS");
        response.setCode(HttpStatus.OK);

        return ResponseEntity.status(response.getCode()).body(response);
    }
}
