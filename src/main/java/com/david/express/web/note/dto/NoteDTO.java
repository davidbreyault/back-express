package com.david.express.web.note.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class NoteDTO {

    private Long id;

    @NotBlank
    @Size(min = 10, max = 255, message = "Note must be 10-255 characters long.")
    private String note;

    private String author;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private int likes;
    private int dislikes;
    private int comments;
}
