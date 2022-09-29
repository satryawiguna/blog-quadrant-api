package com.quadrant.blog.service;

import com.quadrant.blog.dto.BaseDataResponse;
import com.quadrant.blog.dto.comment.CommentDataResponse;
import com.quadrant.blog.dto.comment.CreateCommentDataResponse;
import com.quadrant.blog.entity.CommentEntity;
import com.quadrant.blog.entity.UserEntity;
import com.quadrant.blog.repository.CommentRepository;
import com.quadrant.blog.repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final Log logger = LogFactory.getLog(getClass());

    public CommentService(ModelMapper modelMapper,
                          CommentRepository commentRepository,
                          UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    public BaseDataResponse<List<CommentDataResponse>> getCommentsByBlogId(Long id) {

        BaseDataResponse<List<CommentDataResponse>> response =  new BaseDataResponse<>();

        List<CommentEntity> findCommentsAllByBlogId = commentRepository.findByBlogId(id);

        List<CommentDataResponse> comments = findCommentsAllByBlogId.stream()
                .map(comment -> modelMapper.map(comment, CommentDataResponse.class))
                .collect(Collectors.toList());

        response.setStatus("SUCCESS");
        response.setCode(HttpStatus.OK);
        response.setPayload(comments);

        return response;
    }

    public BaseDataResponse<CommentDataResponse> createComment(CommentEntity commentEntity, BaseDataResponse<CommentDataResponse> response) {

        CommentEntity saveComment = commentRepository.save(commentEntity);

        Optional<UserEntity> user = userRepository.findById(saveComment.getUser().getId());

        saveComment.setUser(user.get());

        CommentDataResponse commentResponse = modelMapper.map(saveComment, CommentDataResponse.class);

        logger.info("Comment created");

        response.setStatus("SUCCESS");
        response.setCode(HttpStatus.OK);
        response.setPayload(commentResponse);

        return response;
    }
}
