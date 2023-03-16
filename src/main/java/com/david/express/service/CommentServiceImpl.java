package com.david.express.service;

import com.david.express.exception.ResourceNotFoundException;
import com.david.express.model.Comment;
import com.david.express.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Page<Comment> findAll(Pageable pageable) {
        return commentRepository.findAll(pageable);
    }

    @Override
    public Optional<Comment> findCommentById(Long id) throws ResourceNotFoundException {
        Optional<Comment> comment = commentRepository.findById(id);
        if (!comment.isPresent()) {
            throw new ResourceNotFoundException("Comment not found with id : " + id);
        }
        return comment;
    }

    @Override
    public Comment save(Comment comment) {
        commentRepository.save(comment);
        return comment;
    }

    @Override
    public void deleteCommentById(Long id) throws ResourceNotFoundException {
        Optional<Comment> comment = findCommentById(id);
        comment.ifPresent(c -> commentRepository.deleteById(c.getId()));
    }
}
