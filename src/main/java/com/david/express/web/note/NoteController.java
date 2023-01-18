package com.david.express.web.note;

import com.david.express.common.CheckRoles;
import com.david.express.exception.UserNotResourceOwnerException;
import com.david.express.model.Note;
import com.david.express.service.NoteService;
import com.david.express.service.UserService;
import com.david.express.validation.dto.SuccessResponseDTO;
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

    @PutMapping("/like/{id}")
    @PreAuthorize("hasRole('WRITER')")
    public ResponseEntity<SuccessResponseDTO> likeNote(@PathVariable Long id) {
        Optional<Note> note = noteService.findNoteById(id);
        note.ifPresent(n -> {
            n.like();
            noteService.save(n);
        });
        return ResponseEntity.ok(new SuccessResponseDTO("Your like has been sent to this note"));
    }

    @PutMapping("dislike/{id}")
    @PreAuthorize("hasRole('WRITER')")
    public ResponseEntity<SuccessResponseDTO> dislikeNote(@PathVariable Long id) {
        Optional<Note> note = noteService.findNoteById(id);
        note.ifPresent(n -> {
            n.dislike();
            noteService.save(n);
        });
        return ResponseEntity.ok(new SuccessResponseDTO("Your dislike has been sent to this note"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('WRITER')")
    public ResponseEntity<NoteDTO> updateNote(@PathVariable Long id, @Valid @RequestBody NoteDTO noteDto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Note note = noteService.findNoteById(id).get();
        // Si l'utilisateur connecté possède le role admin
        if (CheckRoles.isLoggedUserHasAdminRole()) {
            // Possibilité de modifier n'importe quelle note
            note.setNote(noteDto.getNote());
            note.setLikes(noteDto.getLikes());
            note.setDislikes(noteDto.getDislikes());
            note.setUser(userService.findUserByUsername(noteDto.getAuthor()));
            noteService.save(note);
            noteDto.setId(note.getId());
            noteDto.setLikes(note.getLikes());
            noteDto.setDislikes(note.getDislikes());
            noteDto.setCreatedAt(note.getCreatedAt());
            noteDto.setAuthor(note.getUser().getUsername());
        } else {
            // Si non, vérifier que la note à supprimer appartient bien à l'utilisateur connecté
            if (note.getUser().getUsername().equals(userDetails.getUsername())) {
                // ...qui ne peut seulement modifier le contenu de sa note.
                note.setNote(noteDto.getNote());
                noteService.save(note);
                noteDto.setId(note.getId());
                noteDto.setLikes(note.getLikes());
                noteDto.setDislikes(note.getDislikes());
                noteDto.setCreatedAt(note.getCreatedAt());
                noteDto.setAuthor(note.getUser().getUsername());
            } else {
                throw new UserNotResourceOwnerException("You are not allowed to update this resource !");
            }
        }
        return ResponseEntity.ok(noteDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('WRITER')")
    public ResponseEntity<SuccessResponseDTO> deleteNote(@PathVariable Long id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Si l'utilisateur connecté possède le role admin
        if (CheckRoles.isLoggedUserHasAdminRole()) {
            // Possibilité de supprimer n'importe quelle note
            noteService.deleteNoteById(id);
            return ResponseEntity.ok(new SuccessResponseDTO("Note has been deleted successfully !"));
        } else {
            Optional<Note> note = noteService.findNoteById(id);
            note.ifPresent(n -> {
                // Si non, vérifier que la note à supprimer appartient bien à l'utilisateur connecté
                if (n.getUser().getUsername().equals(userDetails.getUsername())) {
                    noteService.deleteNoteById(id);
                } else {
                    throw new UserNotResourceOwnerException("You are not allowed to delete this resource !");
                }
            });
            return ResponseEntity.ok(new SuccessResponseDTO("Your note has been deleted successfully !"));
        }
    }
}
