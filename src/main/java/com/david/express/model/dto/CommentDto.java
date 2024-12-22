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
public class CommentDto {

    private Long id;

    @NotBlank
    @Size(min = 3, max = 255)
    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    private Long userId;

    private String username;

    private Long noteId;
}
