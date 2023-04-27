package com.david.express.web.note.mapper;

import com.david.express.entity.Note;
import com.david.express.web.note.dto.NoteDTO;

public class NoteDTOMapper {

    public static NoteDTO toNoteDTO(Note note) {
        return new NoteDTO(
                note.getId(),
                note.getNote(),
                note.getUser().getUsername(),
                note.getCreatedAt(),
                note.getLikes(),
                note.getDislikes(),
                note.getComments() != null ? note.getComments().size() : 0
        );
    }
}
