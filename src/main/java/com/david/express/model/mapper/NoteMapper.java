package com.david.express.model.mapper;

import com.david.express.entity.Note;
import com.david.express.model.dto.NoteDto;
import com.david.express.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NoteMapper {

    @Autowired
    private UserService userService;

    public NoteDto toNoteDto(Note note) {
        return new NoteDto(
                note.getId(),
                note.getNote(),
                note.getUser().getUsername(),
                note.getCreatedAt(),
                note.getLikes().size(),
                note.getComments() != null ? note.getComments().size() : 0
        );
    }

    public Note toNoteEntity(Note note, NoteDto noteDto) {
        note.setNote(noteDto.getNote() != null ? noteDto.getNote() : note.getNote());
        note.setCreatedAt(noteDto.getCreatedAt() != null ? noteDto.getCreatedAt() : note.getCreatedAt());
        note.setUser(noteDto.getAuthor() != null ? userService.findUserByUsername(noteDto.getAuthor()) : note.getUser());
        return note;
    }
}
