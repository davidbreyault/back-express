package com.david.express.service;

import com.david.express.exception.ResourceNotFoundException;
import com.david.express.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    public Page<Comment> findAll(Pageable pageable);
    public Optional<Comment> findCommentById(Long id) throws ResourceNotFoundException;
    public Comment save(Comment comment);
    public void deleteCommentById(Long id) throws ResourceNotFoundException;
}
