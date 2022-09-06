package com.quadrant.blog.service;

import com.quadrant.blog.dto.BaseDataResponse;
import com.quadrant.blog.dto.category.CategoryDataRequest;
import com.quadrant.blog.dto.category.CategoryDataResponse;
import com.quadrant.blog.dto.PageDataResponse;
import com.quadrant.blog.entity.CategoryEntity;
import com.quadrant.blog.exception.ResourceNotFoundException;
import com.quadrant.blog.repository.CategoryRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final Log logger = LogFactory.getLog(getClass());

    public CategoryService(ModelMapper modelMapper, CategoryRepository categoryRepository) {
        this.modelMapper = modelMapper;
        this.categoryRepository = categoryRepository;
    }

    public BaseDataResponse<List<CategoryDataResponse>> getCategories() {

        BaseDataResponse<List<CategoryDataResponse>> response =  new BaseDataResponse<>();

        List<CategoryEntity> findCategoriesAll = categoryRepository.findAll();

        List<CategoryDataResponse> categories = findCategoriesAll.stream()
                .map(category -> modelMapper.map(category, CategoryDataResponse.class))
                .collect(Collectors.toList());

        response.setStatus("SUCCESS");
        response.setCode(HttpStatus.OK);
        response.setPayload(categories);

        return response;
    }

    public BaseDataResponse<PageDataResponse<CategoryDataResponse>> getCategoriesWithPaging(String search, Pageable pageable) {

        BaseDataResponse<PageDataResponse<CategoryDataResponse>> response =  new BaseDataResponse<>();

        Page<CategoryEntity> findCategoriesByNameContains = categoryRepository.findByNameContains(search, pageable);

        List<CategoryDataResponse> categories = findCategoriesByNameContains.stream()
                .map(category -> modelMapper.map(category, CategoryDataResponse.class))
                .collect(Collectors.toList());

        PageDataResponse<CategoryDataResponse> pageResponse = new PageDataResponse<CategoryDataResponse>();

        pageResponse.setData(categories);
        pageResponse.setPageable(findCategoriesByNameContains.getPageable());

        response.setStatus("SUCCESS");
        response.setCode(HttpStatus.OK);
        response.setPayload(pageResponse);

        return response;
    }

    public BaseDataResponse<CategoryDataResponse> getCategory(Long id) {

        BaseDataResponse<CategoryDataResponse> response =  new BaseDataResponse<>();

        Optional<CategoryEntity> findCategoryById = categoryRepository.findById(id);

        if (findCategoryById.isPresent()) {
            CategoryDataResponse category = modelMapper.map(findCategoryById, CategoryDataResponse.class);

            response.setStatus("SUCCESS");
            response.setCode(HttpStatus.OK);
            response.setPayload(category);
        } else {
            logger.info("Category not found");

            response.setStatus("ERROR");
            response.setCode(HttpStatus.NOT_FOUND);
            response.setPayload(null);
        }

        return response;
    }


    public CategoryEntity getCategoryEntity(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Category", "Id", id));
    }

    public BaseDataResponse<CategoryDataResponse> createCategory(CategoryEntity categoryEntity, BaseDataResponse<CategoryDataResponse> response) {

        CategoryEntity saveCategory = categoryRepository.save(categoryEntity);

        CategoryDataResponse categoryResponse = modelMapper.map(saveCategory, CategoryDataResponse.class);

        logger.info("Category created");

        response.setStatus("SUCCESS");
        response.setCode(HttpStatus.OK);
        response.setPayload(categoryResponse);

        return response;
    }

    public BaseDataResponse<CategoryDataResponse> updateCategory(Long id, CategoryDataRequest request, BaseDataResponse<CategoryDataResponse> response) {

        Optional<CategoryEntity> findByIdCategory = categoryRepository.findById(id);

        if (findByIdCategory.isPresent()) {
            findByIdCategory.get().setName(request.getName());
            findByIdCategory.get().setSlug(request.getSlug());

            CategoryEntity updateCategory =  categoryRepository.save(findByIdCategory.get());

            CategoryDataResponse categoryResponse = modelMapper.map(updateCategory, CategoryDataResponse.class);

            logger.info("Category updated");

            response.setStatus("SUCCESS");
            response.setCode(HttpStatus.OK);
            response.setPayload(categoryResponse);
        } else {
            logger.info("Category not found");

            response.setStatus("ERROR");
            response.setCode(HttpStatus.NOT_FOUND);
            response.setPayload(null);
        }

        return response;
    }

    public void deleteCategory(Long id) {
        categoryRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Category", "Id", id));
        categoryRepository.deleteById(id);
    }
}
