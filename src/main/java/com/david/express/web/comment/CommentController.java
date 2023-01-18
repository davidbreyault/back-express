package com.david.express.web.comment;

import com.david.express.model.Comment;
import com.david.express.service.CommentService;
import com.david.express.service.NoteService;
import com.david.express.service.UserService;
import com.david.express.web.comment.dto.CommentDTO;
import com.david.express.web.comment.dto.CommentResponseDTO;
import com.david.express.web.comment.mapper.CommentDTOMapper;
import com.david.express.web.comment.dto.NoteCommentDTO;
import com.david.express.web.comment.dto.NoteCommentResponseDTO;
import com.david.express.web.comment.mapper.NoteCommentDTOMapper;
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
}
