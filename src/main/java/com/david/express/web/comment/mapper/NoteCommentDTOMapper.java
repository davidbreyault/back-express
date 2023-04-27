package com.david.express.web.comment.mapper;

import com.david.express.entity.Comment;
import com.david.express.web.comment.dto.NoteCommentDTO;

public class NoteCommentDTOMapper {

    public static NoteCommentDTO toNoteCommentDTO(Comment comment) {
        return new NoteCommentDTO(
                comment.getId(),
                comment.getMessage(),
                comment.getCreatedAt(),
                comment.getUser().getId(),
                comment.getUser().getUsername()
        );
    }
}
