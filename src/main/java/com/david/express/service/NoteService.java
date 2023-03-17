package com.david.express.service;

import com.david.express.exception.ResourceNotFoundException;
import com.david.express.model.Comment;
import com.david.express.model.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface NoteService {
    public List<Note> findAll();
    public Page<Note> findAll(Pageable pageable);
    public Optional<Note> findNoteById(Long id) throws ResourceNotFoundException;
    public List<Comment> findNoteComments(Long id) throws ResourceNotFoundException;
    public Note save(Note note);
    public void deleteNoteById(Long id) throws ResourceNotFoundException;
}