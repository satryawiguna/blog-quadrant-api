package com.quadrant.blog.controller;

import com.quadrant.blog.dto.BaseDataResponse;
import com.quadrant.blog.dto.category.CategoryDataRequest;
import com.quadrant.blog.dto.category.CategoryDataResponse;
import com.quadrant.blog.dto.SearchDataRequest;
import com.quadrant.blog.dto.PageDataResponse;
import com.quadrant.blog.entity.CategoryEntity;
import com.quadrant.blog.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CategoryController {

    private final CategoryService categoryService;
    private final ModelMapper modelMapper;

    public CategoryController(ModelMapper modelMapper,
                              CategoryService categoryService) {
        this.modelMapper = modelMapper;
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public ResponseEntity<BaseDataResponse<List<CategoryDataResponse>>> all() {

        BaseDataResponse<List<CategoryDataResponse>> getCategoriesResponse = categoryService.getCategories();

        return ResponseEntity.status(getCategoriesResponse.getCode()).body(getCategoriesResponse);
    }

    @GetMapping("/common/categories")
    public ResponseEntity<BaseDataResponse<List<CategoryDataResponse>>> commonAll() {

        BaseDataResponse<List<CategoryDataResponse>> getCategoriesResponse = categoryService.getCategories();

        return ResponseEntity.status(getCategoriesResponse.getCode()).body(getCategoriesResponse);
    }

    @GetMapping("/categories/page/{page}/{size}/{sort}")
    public ResponseEntity<BaseDataResponse<PageDataResponse<CategoryDataResponse>>> allWithPaging(@PathVariable("page") int page, @PathVariable("size") int size, @PathVariable("sort") String sort, @RequestBody SearchDataRequest request) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));

        if (sort.equalsIgnoreCase("desc"))
            pageable = PageRequest.of(page, size, Sort.by("id").descending());

        BaseDataResponse<PageDataResponse<CategoryDataResponse>> getCategoriesWithPagingResponse = categoryService.getCategoriesWithPaging(request.getSearch(), pageable);

        return ResponseEntity.status(getCategoriesWithPagingResponse.getCode()).body(getCategoriesWithPagingResponse);
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<BaseDataResponse<CategoryDataResponse>> get(@PathVariable("id") Long id) {
        BaseDataResponse<CategoryDataResponse> getCategoryResponse = categoryService.getCategory(id);

        return ResponseEntity.status(getCategoryResponse.getCode()).body(getCategoryResponse);
    }

    @PostMapping("/category")
    public ResponseEntity<BaseDataResponse<CategoryDataResponse>> store(@Valid @RequestBody CategoryDataRequest request, Errors errors) {

        BaseDataResponse<CategoryDataResponse> response =  new BaseDataResponse<>();

        if (errors.hasErrors()) {
            for (ObjectError error : errors.getAllErrors()) {
                response.getMessages().add(error.getDefaultMessage());
            }

            response.setStatus("ERROR");
            response.setCode(HttpStatus.BAD_REQUEST);
            response.setPayload(null);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        CategoryEntity category = modelMapper.map(request, CategoryEntity.class);

        BaseDataResponse<CategoryDataResponse> createCategoryResponse = categoryService.createCategory(category, response);

        return ResponseEntity.status(createCategoryResponse.getCode()).body(createCategoryResponse);
    }

    @PutMapping("/category/{id}")
    public ResponseEntity<BaseDataResponse<?>> update(@PathVariable("id") Long id, @Valid @RequestBody CategoryDataRequest request, Errors errors) {

        BaseDataResponse<CategoryDataResponse> response =  new BaseDataResponse<>();

        if (errors.hasErrors()) {
            for (ObjectError error : errors.getAllErrors()) {
                response.getMessages().add(error.getDefaultMessage());
            }

            response.setStatus("ERROR");
            response.setCode(HttpStatus.BAD_REQUEST);
            response.setPayload(null);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        BaseDataResponse<CategoryDataResponse> updateCategoryResponse = categoryService.updateCategory(id, request, response);

        return ResponseEntity.status(updateCategoryResponse.getCode()).body(updateCategoryResponse);

    }

    @DeleteMapping("/category/{id}")
    public ResponseEntity<BaseDataResponse<?>> delete(@PathVariable("id") Long id) {

        BaseDataResponse<?> response =  new BaseDataResponse<>();

        categoryService.deleteCategory(id);

        response.setStatus("SUCCESS");
        response.setCode(HttpStatus.OK);

        return ResponseEntity.status(response.getCode()).body(response);
    }
}
