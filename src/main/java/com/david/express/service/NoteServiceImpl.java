package com.david.express.service;

import com.david.express.common.Utils;
import com.david.express.exception.ResourceNotFoundException;
import com.david.express.model.Comment;
import com.david.express.model.Note;
import com.david.express.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Override
    public List<Note> findAll() {
        return noteRepository.findAll();
    }

    @Override
    public Page<Note> findAll(Pageable pageable) {
        return noteRepository.findAll(pageable);
    }

    @Override
    public Optional<Note> findNoteById(Long id) throws ResourceNotFoundException {
        Optional<Note> note = noteRepository.findById(id);
        if (!note.isPresent()) {
            throw new ResourceNotFoundException("Note not found with id : " + id);
        }
        return note;
    }

    @Override
    public List<Comment> findNoteComments(Long id) throws ResourceNotFoundException {
        Optional<Note> note = findNoteById(id);
        if (!note.isPresent()) {
            throw new ResourceNotFoundException("Note not found with id : " + id);
        }
        return note.get().getComments();
    }

    @Override
    public Note save(Note note) {
        noteRepository.save(note);
        return note;
    }

    @Override
    public void deleteNoteById(Long id) throws ResourceNotFoundException {
        Optional<Note> note = findNoteById(id);
        if (!note.isPresent()) {
            throw new ResourceNotFoundException("Note not found with id : " + id);
        }
        noteRepository.deleteById(note.get().getId());
    }

    @Override
    public HashMap<String, Integer> getTrendingWords() {
        List<Note> notes = findAll();
        HashMap<String, Integer> trendingWords = new HashMap<String, Integer>();
        notes.forEach(note ->
                Arrays.stream(note.getNote().split("(\\s+)|([.,!?:;’'\"])")).forEach(word -> {
                    if (word.length() > 2) {
                        trendingWords.put(
                                word,
                                trendingWords.containsKey(word) ? (trendingWords.get(word) + 1) : 1);
                    }
                })
        );
        return Utils.sortHashMapByValue(trendingWords);
    }
}
