package com.david.express.service.impl;

import com.david.express.exception.ResourceAffiliationException;
import com.david.express.exception.ResourceNotFoundException;
import com.david.express.entity.Comment;
import com.david.express.repository.CommentRepository;
import com.david.express.service.CommentService;
import com.david.express.service.NoteService;
import com.david.express.service.RoleService;
import com.david.express.service.UserService;
import com.david.express.web.comment.dto.CommentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private NoteService noteService;

    @Override
    public Page<Comment> getAllComments(Pageable pageable) {
        return commentRepository.findAll(pageable);
    }

    @Override
    public Comment getCommentById(Long id) throws ResourceNotFoundException {
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isPresent()) {
            return comment.get();
        }
        throw new ResourceNotFoundException("Comment not found with id : " + id);
    }

    @Override
    public Comment save(Long noteId, CommentDTO commentDto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        Comment comment = Comment.builder()
                .message(commentDto.getMessage())
                .createdAt(LocalDateTime.now())
                .user(userService.findUserByUsername(username))
                .note(noteService.findNoteById(noteId))
                .build();
        return commentRepository.save(comment);
    }

    @Override
    public Comment update(Long id, CommentDTO commentDto) throws ResourceNotFoundException, ResourceAffiliationException {
        Comment commentToUpdate = getCommentById(id);
        if (roleService.isLoggedUserHasAdminRole()) {
            // Admin : peut modifier le message, la date de création, l'auteur de n'importe quel utilisateur
            commentToUpdate.setMessage(commentDto.getMessage() != null
                    ? commentDto.getMessage() : commentToUpdate.getMessage());
            commentToUpdate.setCreatedAt(commentDto.getCreatedAt() != null
                    ? commentDto.getCreatedAt() : commentToUpdate.getCreatedAt());
            commentToUpdate.setUser(commentDto.getUsername() != null
                    ? userService.findUserByUsername(commentDto.getUsername()) : commentToUpdate.getUser());
        } else {
            if (!isLoggedUserIsCommentOwner(id)) {
                throw new ResourceAffiliationException("You are not allowed to update this resource !");
            }
            // Non admin : ne peut seulement modifier le message
            commentToUpdate.setMessage(commentDto.getMessage() != null
                    ? commentDto.getMessage() : commentToUpdate.getMessage());
        }
        return commentRepository.save(commentToUpdate);
    }

    @Override
    public void deleteCommentById(Long id) throws ResourceNotFoundException, ResourceAffiliationException {
        if (!roleService.isLoggedUserHasAdminRole()) {
            // Non admin : Vérifier que le commentaire appartient bien à l'utilisateur connecté
            if (!isLoggedUserIsCommentOwner(id)) {
                throw new ResourceAffiliationException("You are not allowed to delete this resource !");
            }
        }
        commentRepository.deleteById(id);
    }

    @Override
    public boolean isLoggedUserIsCommentOwner(Long id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Comment comment = getCommentById(id);
        return comment.getUser().getUsername().equals(userDetails.getUsername());
    }
}
