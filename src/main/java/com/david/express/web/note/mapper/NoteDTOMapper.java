package com.david.express.web.note.mapper;

import com.david.express.model.Note;
import com.david.express.web.note.dto.NoteDTO;

import java.util.List;
import java.util.stream.Collectors;

public class NoteDTOMapper {

    public static NoteDTO toNoteDTO(Note note) {
        return new NoteDTO(
                note.getId(),
                note.getNote(),
                note.getCreatedAt(),
                note.getLikes(),
                note.getDislikes(),
                note.getUser().getUsername()
        );
    }
}
