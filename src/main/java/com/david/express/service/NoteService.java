package com.david.express.service;

import com.david.express.exception.ResourceAffiliationException;
import com.david.express.exception.ResourceNotFoundException;
import com.david.express.entity.Note;
import com.david.express.model.dto.CommentDto;
import com.david.express.model.dto.NoteDto;
import com.david.express.model.dto.PaginatedResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Date;
import java.util.List;
import java.util.Map;

public interface NoteService {
    Note findNoteById(Long id) throws ResourceNotFoundException;
    Page<Note> findAllNotes(Pageable pageable);
    PaginatedResponseDto<NoteDto> getNotes(String username, String keyword, Date dateStart, Date dateEnd, int page, int size, String[] sort);
    List<CommentDto> findNoteComments(Long id) throws ResourceNotFoundException;
    NoteDto getNoteById(Long id);
    NoteDto saveNote(NoteDto noteDto);
    NoteDto updateNote(Long id, NoteDto noteDto) throws ResourceNotFoundException, ResourceAffiliationException;
    NoteDto likeNote(Long id) throws ResourceNotFoundException;
    void deleteNoteById(Long id) throws ResourceNotFoundException, ResourceAffiliationException;
    boolean isNotePostedByAuthenticatedUser(Long id);
    boolean isNoteLikedByAuthenticatedUser(Note note);
}