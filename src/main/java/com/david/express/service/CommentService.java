package com.david.express.service;

import com.david.express.exception.ResourceAffiliationException;
import com.david.express.exception.ResourceNotFoundException;
import com.david.express.entity.Comment;
import com.david.express.web.comment.dto.CommentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    Page<Comment> getAllComments(Pageable pageable);
    Comment getCommentById(Long id) throws ResourceNotFoundException;
    Comment save(Long noteId, CommentDTO commentDto);
    Comment update(Long id, CommentDTO commentDto) throws ResourceNotFoundException, ResourceAffiliationException;
    void deleteCommentById(Long id) throws ResourceNotFoundException, ResourceAffiliationException;
    boolean isLoggedUserIsCommentOwner(Long id);
}
