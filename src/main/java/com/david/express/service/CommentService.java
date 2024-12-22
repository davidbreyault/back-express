package com.david.express.service;

import com.david.express.exception.ResourceAffiliationException;
import com.david.express.exception.ResourceNotFoundException;
import com.david.express.entity.Comment;
import com.david.express.model.dto.CommentDto;
import com.david.express.model.dto.PaginatedResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface CommentService {
    Comment findCommentById(Long id) throws ResourceNotFoundException;
    Page<Comment> findAllComments(Pageable pageable);
    PaginatedResponseDto<CommentDto> getAllComments(int page, int size, String[] sort);
    CommentDto getCommentById(Long id) throws ResourceNotFoundException;
    CommentDto saveComment(Long noteId, CommentDto commentDto);
    CommentDto updateComment(Long id, CommentDto commentDto) throws ResourceNotFoundException, ResourceAffiliationException;
    void deleteCommentById(Long id) throws ResourceNotFoundException, ResourceAffiliationException;
    boolean isCommentPostedByAuthenticatedUser(Long id);
}
