package com.david.express.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class NoteResponseDto {

    private List<NoteDto> notes;
    private int totalNotes;
    private Long ts;

    public NoteResponseDto(List<NoteDto> notes) {
        this.notes = notes;
        this.totalNotes = notes.size();
        this.ts = System.currentTimeMillis();
    }
}
