package com.david.express.web.comment;

import com.david.express.common.CheckRoles;
import com.david.express.exception.UserNotResourceOwnerException;
import com.david.express.model.Comment;
import com.david.express.service.CommentService;
import com.david.express.service.NoteService;
import com.david.express.service.UserService;
import com.david.express.validation.dto.SuccessResponseDTO;
import com.david.express.web.comment.dto.CommentDTO;
import com.david.express.web.comment.dto.CommentResponseDTO;
import com.david.express.web.comment.mapper.CommentDTOMapper;
import com.david.express.web.comment.dto.NoteCommentDTO;
import com.david.express.web.comment.dto.NoteCommentResponseDTO;
import com.david.express.web.comment.mapper.NoteCommentDTOMapper;
import org.apache.coyote.Response;
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
@RequestMapping("/api/v1")
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private NoteService noteService;

    @GetMapping("/comments")
    public ResponseEntity<CommentResponseDTO> getAllComments() {
        List<CommentDTO> comments = commentService.findAll()
                .stream()
                .map(CommentDTOMapper::toCommentDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new CommentResponseDTO(comments));
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Long id) {
        Optional<Comment> comment = commentService.findCommentById(id);
        CommentDTO commentDTO = CommentDTOMapper.toCommentDTO(comment.get());
        return ResponseEntity.ok(commentDTO);
    }

    @GetMapping("/notes/{id}/comments")
    public ResponseEntity<?> getNoteComments(@PathVariable Long id) {
        List<NoteCommentDTO> comments = noteService.findNoteComments(id)
                .stream()
                .map(NoteCommentDTOMapper::toNoteCommentDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new NoteCommentResponseDTO(comments));
    }

    @PostMapping("/notes/{id}/comments")
    @PreAuthorize("hasRole('WRITER')")
    public ResponseEntity<CommentDTO> postComment(@Valid @RequestBody CommentDTO commentDto, @PathVariable Long id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        Comment comment = Comment.builder()
                .message(commentDto.getMessage())
                .createdAt(LocalDateTime.now())
                .user(userService.findUserByUsername(username))
                .note(noteService.findNoteById(id).get())
                .build();
        commentService.save(comment);
        commentDto.setId(comment.getId());
        commentDto.setMessage(comment.getMessage());
        commentDto.setCreatedAt(comment.getCreatedAt());
        commentDto.setUserId(comment.getUser().getId());
        commentDto.setUsername(comment.getUser().getUsername());
        commentDto.setNoteId(comment.getNote().getId());
        return new ResponseEntity<>(commentDto, HttpStatus.CREATED);
    }

    @PutMapping("/comments/{id}")
    @PreAuthorize("hasRole('WRITER')")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long id, @Valid @RequestBody CommentDTO commentDto) {
        Comment commentToUpdate = commentService.findCommentById(id).get();
        if (CheckRoles.isLoggedUserHasAdminRole()) {
            // Admin : peut modifier le message, la date de cr??ation, l'auteur de n'importe quel utilisateur
            commentToUpdate.setMessage(commentDto.getMessage() != null
                    ? commentDto.getMessage() : commentToUpdate.getMessage());
            commentToUpdate.setCreatedAt(commentDto.getCreatedAt() != null
                    ? commentDto.getCreatedAt() : commentToUpdate.getCreatedAt());
            commentToUpdate.setUser(commentDto.getUsername() != null
                    ? userService.findUserByUsername(commentDto.getUsername()) : commentToUpdate.getUser());
        } else {
            if (!isLoggedUserIsCommentOwner(id)) {
                throw new UserNotResourceOwnerException("You are not allowed to update this resource !");
            }
            // Non admin : ne peut seulement modifier le message
            commentToUpdate.setMessage(commentDto.getMessage() != null
                    ? commentDto.getMessage() : commentToUpdate.getMessage());
        }
        commentService.save(commentToUpdate);
        commentDto.setId(commentToUpdate.getId());
        commentDto.setMessage(commentToUpdate.getMessage());
        commentDto.setCreatedAt(commentToUpdate.getCreatedAt());
        commentDto.setUserId(commentToUpdate.getUser().getId());
        commentDto.setUsername(commentToUpdate.getUser().getUsername());
        commentDto.setNoteId(commentToUpdate.getNote().getId());
        return ResponseEntity.ok(commentDto);
    }

    @DeleteMapping("/comments/{id}")
    @PreAuthorize("hasRole('WRITER')")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        if (CheckRoles.isLoggedUserHasAdminRole()) {
            String message = isLoggedUserIsCommentOwner(id)
                    ? "Your comment has been deleted successfully !"
                    : commentService.findCommentById(id).get().getUser().getUsername()
                        + "'s comment has been deleted successfully !";
            // Op??ration de suppression ?? effectuer en tout dernier pour ??viter les exceptions
            commentService.deleteCommentById(id);
            return ResponseEntity.ok(new SuccessResponseDTO(message));
        } else {
            if (!isLoggedUserIsCommentOwner(id)) {
                throw new UserNotResourceOwnerException("You are not allowed to delete this resource !");
            }
            commentService.deleteCommentById(id);
            return ResponseEntity.ok(new SuccessResponseDTO("Your comment has been deleted successfully !"));
        }
    }

    private boolean isLoggedUserIsCommentOwner(Long id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Comment comment = commentService.findCommentById(id).get();
        return comment.getUser().getUsername().equals(userDetails.getUsername());
    }
}
