package com.david.express.web.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
public class CommentResponseDTO {

    private List<CommentDTO> comments;
    private int totalComments;
    private Long ts;

    public CommentResponseDTO(List<CommentDTO> comments) {
        this.comments = comments;
        this.totalComments = comments.size();
        this.ts = System.currentTimeMillis();
    }
}
