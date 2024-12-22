package com.david.express.model.mapper;

import com.david.express.entity.Comment;
import com.david.express.model.dto.CommentDto;
import com.david.express.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    @Autowired
    private UserService userService;


    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getMessage(),
                comment.getCreatedAt(),
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                comment.getNote().getId()
        );
    }

    public Comment toCommentEntity(Comment comment, CommentDto commentDto) {
        comment.setMessage(commentDto.getMessage() != null ? commentDto.getMessage() : comment.getMessage());
        comment.setCreatedAt(commentDto.getCreatedAt() != null ? commentDto.getCreatedAt() : comment.getCreatedAt());
        comment.setUser(commentDto.getUsername() != null ? userService.findUserByUsername(commentDto.getUsername()) : comment.getUser());
        return comment;
    }
}
