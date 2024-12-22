package com.david.express.web;

import com.david.express.model.dto.PaginatedResponseDto;
import com.david.express.service.CommentService;
import com.david.express.service.NoteService;
import com.david.express.model.dto.CommentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private NoteService noteService;

    @GetMapping("/comments")
    public ResponseEntity<PaginatedResponseDto<CommentDto>> getAllComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "500") int size,
            @RequestParam(defaultValue = "id, desc") String[] sort
    ) {
        PaginatedResponseDto<CommentDto> response = commentService.getAllComments(page, size, sort);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable Long id) {
        CommentDto commentDto = commentService.getCommentById(id);
        return ResponseEntity.ok(commentDto);
    }

    @GetMapping("/notes/{id}/comments")
    public ResponseEntity<?> getNoteComments(@PathVariable Long id) {
        List<CommentDto> noteCommentsDto = noteService.findNoteComments(id);
        return ResponseEntity.ok(noteCommentsDto);
    }

    @PostMapping("/notes/{id}/comments")
    @PreAuthorize("hasRole('WRITER')")
    public ResponseEntity<CommentDto> postComment(@PathVariable Long id, @Valid @RequestBody CommentDto commentDto) {
        CommentDto savedCommentDto = commentService.saveComment(id, commentDto);
        return new ResponseEntity<>(savedCommentDto, HttpStatus.CREATED);
    }

    @PutMapping("/comments/{id}")
    @PreAuthorize("hasRole('WRITER')")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long id, @Valid @RequestBody CommentDto commentDto) {
        CommentDto updatedCommentDto = commentService.updateComment(id, commentDto);
        return ResponseEntity.ok(updatedCommentDto);
    }

    @DeleteMapping("/comments/{id}")
    @PreAuthorize("hasRole('WRITER')")
    public ResponseEntity<HttpStatus> deleteComment(@PathVariable Long id) {
        commentService.deleteCommentById(id);
        return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
    }
}
