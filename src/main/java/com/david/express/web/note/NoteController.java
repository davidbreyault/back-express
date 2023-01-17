package com.david.express.web.note;

import com.david.express.model.Note;
import com.david.express.service.NoteService;
import com.david.express.service.UserService;
import com.david.express.web.note.dto.NoteDTO;
import com.david.express.web.note.dto.NoteResponseDTO;
import com.david.express.web.note.mapper.NoteDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;
    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<NoteResponseDTO> getAllNotes() {
        List<NoteDTO> notes = noteService.findAll()
                .stream()
                .map(NoteDTOMapper::toNoteDTO)
                .collect(Collectors.toList());
        NoteResponseDTO noteResponse = new NoteResponseDTO(
                notes,
                notes.size()
        );
        return ResponseEntity.ok(noteResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteDTO> getNoteById(@PathVariable Long id) {
        Optional<Note> note = noteService.findNoteById(id);
        NoteDTO noteDto = NoteDTOMapper.toNoteDTO(noteService.findNoteById(id).get());
        return ResponseEntity.ok(noteDto);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('WRITER')")
    public ResponseEntity<NoteDTO> postNote(@Valid @RequestBody NoteDTO noteDto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        Note note = Note
                .builder()
                .note(noteDto.getNote())
                .likes(0)
                .dislikes(0)
                .createdAt(LocalDateTime.now())
                .user(userService.findUserByUsername(username))
                .build();

        noteService.save(note);
        noteDto.setId(note.getId());
        noteDto.setLikes(note.getLikes());
        noteDto.setDislikes(note.getDislikes());
        noteDto.setCreatedAt(note.getCreatedAt());
        noteDto.setAuthor(note.getUser().getUsername());
        return new ResponseEntity<>(noteDto, HttpStatus.CREATED);
    }

    public void updateNote(Long id) {
        // Si l'utilisateur connecté
            // est admin, possibilité de modifier n'importe quelle note
            // n'est pas admin, vérifier que la note lui appartient avant de modifier
    }

    public void deleteNote(Long id) {
        // Si l'utilisateur connecté
            // est admin, possibilité de supprimer n'importe quelle note
            // n'est pas admin, vérifier que la note lui appartient avant de supprimer
    }
}
