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
@CrossOrigin(origins = "*", maxAge = 3600)
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

    @GetMapping("/trending")
    public ResponseEntity<NoteResponseDTO> getTrendingNotes(@RequestParam int top) {
        System.out.println("/api/v1/notes/trend : get trend top notes number : " + top + ".");
        List<NoteDTO> trendingNotes = noteService.findAll()
                .stream()
                .map(NoteDTOMapper::toNoteDTO)
                .sorted((NoteDTO n1, NoteDTO n2) -> n2.getLikes() - n1.getLikes())
                .limit(top)
                .collect(Collectors.toList());
        NoteResponseDTO trendingNotesResponse = new NoteResponseDTO(
                trendingNotes,
                trendingNotes.size()
        );
        return ResponseEntity.ok(trendingNotesResponse);
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
        noteDto.setNote(note.getNote());
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
        Note noteToUpdate = noteService.findNoteById(id).get();
        // Si l'utilisateur connecté possède le role admin, possibilité de modifier n'importe quelle note
        if (CheckRoles.isLoggedUserHasAdminRole()) {
            noteToUpdate.setNote(noteDto.getNote() != null
                    ? noteDto.getNote() : noteToUpdate.getNote());
            noteToUpdate.setCreatedAt(noteDto.getCreatedAt() != null
                    ? noteDto.getCreatedAt() : noteToUpdate.getCreatedAt());
            noteToUpdate.setLikes(Optional.of(noteDto.getLikes()).orElse(0) != 0
                    ? noteDto.getLikes() : noteToUpdate.getLikes());
            noteToUpdate.setDislikes(Optional.of(noteDto.getDislikes()).orElse(0) != 0
                    ? noteDto.getDislikes() : noteToUpdate.getDislikes());
            noteToUpdate.setUser(noteDto.getAuthor() != null
                    ? userService.findUserByUsername(noteDto.getAuthor())
                    : userService.findUserByUsername(noteToUpdate.getUser().getUsername()));
        } else {
            // Si non, vérifier que la note à supprimer appartient bien à l'utilisateur connecté
            if (!isLoggedUserIsNoteOwner(id)) {
                throw new UserNotResourceOwnerException("You are not allowed to update this resource !");
            }
            // ...qui ne peut seulement modifier le contenu de sa note.
            noteToUpdate.setNote(noteDto.getNote() != null ? noteDto.getNote() : noteToUpdate.getNote());
        }
        noteService.save(noteToUpdate);
        noteDto.setId(noteToUpdate.getId());
        noteDto.setNote((noteToUpdate.getNote()));
        noteDto.setCreatedAt(noteToUpdate.getCreatedAt());
        noteDto.setLikes(noteToUpdate.getLikes());
        noteDto.setDislikes(noteToUpdate.getDislikes());
        noteDto.setAuthor(noteToUpdate.getUser().getUsername());
        return ResponseEntity.ok(noteDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('WRITER')")
    public ResponseEntity<SuccessResponseDTO> deleteNote(@PathVariable Long id) {
        // Si l'utilisateur connecté possède le role admin
        if (CheckRoles.isLoggedUserHasAdminRole()) {
            // Possibilité de supprimer n'importe quelle note
            String message = isLoggedUserIsNoteOwner(id)
                    ? "Your note has been deleted successfully !"
                    : noteService.findNoteById(id).get().getUser().getUsername()
                        + "'s note has been deleted successfully !";
            // Opération de suppression à effectuer en tout dernier pour éviter les exception
            noteService.deleteNoteById(id);
            return ResponseEntity.ok(new SuccessResponseDTO(message));
        } else {
            // Si non, vérifier que la note à supprimer appartient bien à l'utilisateur connecté
            if (!isLoggedUserIsNoteOwner(id)) {
                throw new UserNotResourceOwnerException("You are not allowed to delete this resource !");
            }
            noteService.deleteNoteById(id);
            return ResponseEntity.ok(new SuccessResponseDTO("Your note has been deleted successfully !"));
        }
    }

    private boolean isLoggedUserIsNoteOwner(Long id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Note note = noteService.findNoteById(id).get();
        return note.getUser().getUsername().equals(userDetails.getUsername());
    }
}
