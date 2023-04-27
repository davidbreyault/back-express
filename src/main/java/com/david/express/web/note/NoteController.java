package com.david.express.web.note;

import com.david.express.entity.Note;
import com.david.express.service.NoteService;
import com.david.express.web.note.dto.NoteDTO;
import com.david.express.web.note.dto.NoteResponseDTO;
import com.david.express.web.note.mapper.NoteDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllNotes(
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "500") int size,
            @RequestParam(defaultValue = "id, desc") String[] sort
    ) {
        // ?sort=column1,direction1 => array of 2 elements : [“column1”, “direction1”]
        // ?sort=column1,direction1&sort=column2,direction2 => array of 2 elements : [“column1, direction1”, “column2, direction2”]
        List<Sort.Order> orders = new ArrayList<>();
        if (sort[0].contains(",")) {
            // Tri selon plusieurs champs (sortOrder = "field, direction")
            for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");
                orders.add(new Sort.Order(Sort.Direction.fromString(_sort[1]), _sort[0]));
            }
        } else {
            // Tri selon un seul champ (sortOrder = "field, direction")
            orders.add(new Sort.Order(Sort.Direction.fromString(sort[1]), sort[0]));
        }

        Pageable paging = PageRequest.of(page, size, Sort.by(orders));
        Page<Note> pageNotes;
        if (username == null) {
            pageNotes = noteService.findAllNotes(paging);
        } else {
            pageNotes = noteService.findNotesByUsername(username, paging);
        }
        List<NoteDTO> notesDto = pageNotes.getContent()
                .stream()
                .map(NoteDTOMapper::toNoteDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("notes", notesDto);
        response.put("currentPage", pageNotes.getNumber());
        response.put("totalItems", pageNotes.getTotalElements());
        response.put("totalPages", pageNotes.getTotalPages());
        response.put("ts", System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteDTO> getNoteById(@PathVariable Long id) {
        Note note = noteService.findNoteById(id);
        return ResponseEntity.ok(NoteDTOMapper.toNoteDTO(note));
    }

    @GetMapping("/best")
    public ResponseEntity<NoteResponseDTO> getBestNotes(@RequestParam int top, Pageable pageable) {
        List<NoteDTO> bestNotes = noteService.findAllNotes(pageable)
                .toList()
                .stream()
                .map(NoteDTOMapper::toNoteDTO)
                .sorted((NoteDTO n1, NoteDTO n2) -> n2.getLikes() - n1.getLikes())
                .limit(top)
                .collect(Collectors.toList());
        NoteResponseDTO bestNotesResponse = new NoteResponseDTO(
                bestNotes,
                bestNotes.size()
        );
        return ResponseEntity.ok(bestNotesResponse);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('WRITER')")
    public ResponseEntity<NoteDTO> postNote(@Valid @RequestBody NoteDTO noteDto) {
        Note note = noteService.saveNote(noteDto);
        return new ResponseEntity<>(NoteDTOMapper.toNoteDTO(note), HttpStatus.CREATED);
    }

    @PutMapping("/like/{id}")
    @PreAuthorize("hasRole('WRITER')")
    public ResponseEntity<NoteDTO> likeNote(@PathVariable Long id) {
        Note note = noteService.likeNote(id);
        return ResponseEntity.ok(NoteDTOMapper.toNoteDTO(note));
    }

    @PutMapping("dislike/{id}")
    @PreAuthorize("hasRole('WRITER')")
    public ResponseEntity<NoteDTO> dislikeNote(@PathVariable Long id) {
        Note note = noteService.dislikeNote(id);
        return ResponseEntity.ok(NoteDTOMapper.toNoteDTO(note));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('WRITER')")
    public ResponseEntity<NoteDTO> updateNote(@PathVariable Long id, @Valid @RequestBody NoteDTO noteDto) {
        Note updatedNote = noteService.updateNote(id, noteDto);
        return ResponseEntity.ok(NoteDTOMapper.toNoteDTO(updatedNote));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('WRITER')")
    public ResponseEntity<HttpStatus> deleteNote(@PathVariable Long id) {
        noteService.deleteNoteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
