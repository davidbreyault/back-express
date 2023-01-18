package com.david.express.web.comment.mapper;

import com.david.express.model.Comment;
import com.david.express.web.comment.dto.CommentDTO;

public class CommentDTOMapper {

    public static CommentDTO toCommentDTO(Comment comment) {
        return new CommentDTO(
                comment.getId(),
                comment.getMessage(),
                comment.getCreatedAt(),
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                comment.getNote().getId()
        );
    }
}
