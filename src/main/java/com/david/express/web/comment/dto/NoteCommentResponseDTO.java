package com.david.express.web.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class NoteCommentResponseDTO {

    private List<NoteCommentDTO> comments;
}
