package com.david.express.web;

import com.david.express.model.dto.PaginatedResponseDto;
import com.david.express.service.NoteService;
import com.david.express.model.dto.NoteDto;
import com.david.express.model.dto.NoteResponseDto;
import com.david.express.model.mapper.NoteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @GetMapping("")
    public ResponseEntity<PaginatedResponseDto<NoteDto>> getNotes(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Date dateStart,
            @RequestParam(required = false) Date dateEnd,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "500") int size,
            @RequestParam(defaultValue = "id, desc") String[] sort
    ) {
        PaginatedResponseDto<NoteDto> response = noteService.getNotes(username, keyword, dateStart, dateEnd, page, size, sort);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteDto> getNoteById(@PathVariable Long id) {
        System.out.println("Search for a note by its id");
        NoteDto noteDto = noteService.getNoteById(id);
        System.out.println("Search for a note by its id successful");
        return ResponseEntity.ok(noteDto);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('WRITER')")
    public ResponseEntity<NoteDto> postNote(@Valid @RequestBody NoteDto noteDto) {
        NoteDto savedNoteDto = noteService.saveNote(noteDto);
        return new ResponseEntity<>(savedNoteDto, HttpStatus.CREATED);
    }

    @PutMapping("/like/{id}")
    @PreAuthorize("hasRole('WRITER')")
    public ResponseEntity<NoteDto> likeNote(@PathVariable Long id) {
        NoteDto noteDto = noteService.likeNote(id);
        return ResponseEntity.ok(noteDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('WRITER')")
    public ResponseEntity<NoteDto> updateNote(@PathVariable Long id, @Valid @RequestBody NoteDto noteDto) {
        NoteDto updatedNoteDto = noteService.updateNote(id, noteDto);
        return ResponseEntity.ok(updatedNoteDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('WRITER')")
    public ResponseEntity<HttpStatus> deleteNote(@PathVariable Long id) {
        noteService.deleteNoteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
