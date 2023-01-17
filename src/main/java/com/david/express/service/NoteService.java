package com.david.express.service;

import com.david.express.exception.ResourceNotFoundException;
import com.david.express.model.Note;

import java.util.List;
import java.util.Optional;

public interface NoteService {
    public List<Note> findAll();
    public Optional<Note> findNoteById(Long id) throws ResourceNotFoundException;
    public Note save(Note note);
    public void deleteNoteById(Long id) throws ResourceNotFoundException;
}
