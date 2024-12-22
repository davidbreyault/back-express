package com.david.express.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class TalkerDto {

    private String username;
    private int totalNotes;
    private int totalComments;
}
