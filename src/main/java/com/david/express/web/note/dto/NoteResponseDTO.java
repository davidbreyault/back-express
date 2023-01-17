package com.david.express.web.note.dto;

import lombok.Data;

import java.util.List;

@Data
public class NoteResponseDTO {

    private List<NoteDTO> notes;
    private int totalNotes;
    private Long ts;

    public NoteResponseDTO(List<NoteDTO> notes, int totalNotes) {
        this.notes = notes;
        this.totalNotes = totalNotes;
        this.ts = System.currentTimeMillis();
    }
}
