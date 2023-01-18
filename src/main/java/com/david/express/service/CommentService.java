package com.david.express.service;

import com.david.express.exception.ResourceNotFoundException;
import com.david.express.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    public List<Comment> findAll();
    public Optional<Comment> findCommentById(Long id) throws ResourceNotFoundException;
    public Comment save(Comment comment);
    public void deleteCommentById(Long id) throws ResourceNotFoundException;
}
