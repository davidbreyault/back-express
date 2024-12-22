package com.david.express.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class NoteDto {

    private Long id;

    @NotBlank
    @Size(min = 10, max = 255, message = "Note must be 10-255 characters long.")
    private String note;

    private String author;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    private int likes;

    private int comments;

    private boolean likedByAuthenticatedUser;


    public NoteDto(Long id, String note, String author, Date createdAt, int likes, int comments) {
        this.id = id;
        this.note = note;
        this.author = author;
        this.createdAt = createdAt;
        this.likes = likes;
        this.comments = comments;
    }
}
