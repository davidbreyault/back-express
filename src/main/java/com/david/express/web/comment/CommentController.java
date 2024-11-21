package com.david.express.web.comment;

import com.david.express.entity.Comment;
import com.david.express.service.CommentService;
import com.david.express.service.NoteService;
import com.david.express.web.comment.dto.CommentDTO;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private NoteService noteService;

    @GetMapping("/comments")
    public ResponseEntity<Map<String, Object>> getAllComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "500") int size
    ) {
        Pageable paging = PageRequest.of(page, size);
        Page<Comment> pageComments = commentService.getAllComments(paging);
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
        Comment comment = commentService.getCommentById(id);
        CommentDTO commentDTO = CommentDTOMapper.toCommentDTO(comment);
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
    public ResponseEntity<CommentDTO> postComment(@PathVariable Long id, @Valid @RequestBody CommentDTO commentDto) {
        Comment comment = commentService.save(id, commentDto);
        return new ResponseEntity<>(CommentDTOMapper.toCommentDTO(comment), HttpStatus.CREATED);
    }

    @PutMapping("/comments/{id}")
    @PreAuthorize("hasRole('WRITER')")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long id, @Valid @RequestBody CommentDTO commentDto) {
        Comment updatedComment = commentService.update(id, commentDto);
        return ResponseEntity.ok(CommentDTOMapper.toCommentDTO(updatedComment));
    }

    @DeleteMapping("/comments/{id}")
    @PreAuthorize("hasRole('WRITER')")
    public ResponseEntity<HttpStatus> deleteComment(@PathVariable Long id) {
        commentService.deleteCommentById(id);
        return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
    }
}
