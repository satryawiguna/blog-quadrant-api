package com.quadrant.blog.controller;

import com.quadrant.blog.dto.BaseDataResponse;
import com.quadrant.blog.dto.category.CategoryDataRequest;
import com.quadrant.blog.dto.category.CategoryDataResponse;
import com.quadrant.blog.dto.comment.CommentDataRequest;
import com.quadrant.blog.dto.comment.CommentDataResponse;
import com.quadrant.blog.dto.comment.CreateCommentDataResponse;
import com.quadrant.blog.entity.CategoryEntity;
import com.quadrant.blog.entity.CommentEntity;
import com.quadrant.blog.service.CommentService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CommentController {
    private final CommentService commentService;

    private final ModelMapper modelMapper;

    private final Log logger = LogFactory.getLog(getClass());

    public CommentController(ModelMapper modelMapper,
                             CommentService commentService) {
        this.modelMapper = modelMapper;
        this.commentService = commentService;
    }

    @PostMapping("/blog/{id}/comment")
    public ResponseEntity<BaseDataResponse<CommentDataResponse>> store(@PathVariable("id") Long id, @Valid @RequestBody CommentDataRequest request, Errors errors) {
        BaseDataResponse<CommentDataResponse> response =  new BaseDataResponse<>();

        if (errors.hasErrors()) {
            for (ObjectError error : errors.getAllErrors()) {
                response.getMessages().add(error.getDefaultMessage());
            }

            response.setStatus("ERROR");
            response.setCode(HttpStatus.BAD_REQUEST);
            response.setPayload(null);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        request.setBlogId(id);

        CommentEntity comment = modelMapper.map(request, CommentEntity.class);

        BaseDataResponse<CommentDataResponse> createCommentResponse = commentService.createComment(comment, response);

        return ResponseEntity.status(createCommentResponse.getCode()).body(createCommentResponse);
    }

    @GetMapping("/blog/{id}/comments")
    public ResponseEntity<BaseDataResponse<List<CommentDataResponse>>> allByBlogId(@PathVariable("id") Long id) {

        BaseDataResponse<List<CommentDataResponse>> getCommentsResponse = commentService.getCommentsByBlogId(id);

        return ResponseEntity.status(getCommentsResponse.getCode()).body(getCommentsResponse);
    }
}
