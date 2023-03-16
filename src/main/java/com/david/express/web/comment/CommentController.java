package com.david.express.web.comment;

import com.david.express.common.CheckRoles;
import com.david.express.exception.UserNotResourceOwnerException;
import com.david.express.model.Comment;
import com.david.express.model.Note;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<Map<String, Object>> getAllComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "500") int size
    ) {
        Pageable paging = PageRequest.of(page, size);
        Page<Comment> pageComments = commentService.findAll(paging);
        List<CommentDTO> comments = pageComments.getContent()
                .stream()
                .map(CommentDTOMapper::toCommentDTO)
                .collect(Collectors.toList());
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("comments", comments);
        response.put("currentPage", pageComments.getNumber());
        response.put("totalItems", pageComments.getTotalElements());
        response.put("totalPages", pageComments.getTotalPages());
        response.put("ts", System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.OK);
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
            // Admin : peut modifier le message, la date de création, l'auteur de n'importe quel utilisateur
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
            // Opération de suppression à effectuer en tout dernier pour éviter les exceptions
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
