package com.david.express.service;

import com.david.express.exception.ResourceAffiliationException;
import com.david.express.exception.ResourceNotFoundException;
import com.david.express.entity.Comment;
import com.david.express.entity.Note;
import com.david.express.web.note.dto.NoteDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Date;
import java.util.List;

public interface NoteService {
    Page<Note> findAllNotes(Pageable pageable);
    Note findNoteById(Long id) throws ResourceNotFoundException;
    Page<Note> findByCriteria(String username, String keyword, Date dateStart, Date dateEnd, Pageable pageable);
    List<Comment> findNoteComments(Long id) throws ResourceNotFoundException;
    Note saveNote(NoteDTO noteDto);
    Note updateNote(Long id, NoteDTO noteDto) throws ResourceNotFoundException, ResourceAffiliationException;
    void deleteNoteById(Long id) throws ResourceNotFoundException, ResourceAffiliationException;
    Note likeNote(Long id) throws ResourceNotFoundException;
    Note dislikeNote(Long id) throws ResourceNotFoundException;
    boolean isLoggedUserIsNoteOwner(Long id);
}