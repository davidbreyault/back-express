package com.david.express.service.impl;

import com.david.express.common.Utils;
import com.david.express.entity.User;
import com.david.express.exception.ResourceAffiliationException;
import com.david.express.exception.ResourceNotFoundException;
import com.david.express.entity.Comment;
import com.david.express.model.dto.PaginatedResponseDto;
import com.david.express.repository.CommentRepository;
import com.david.express.security.service.SecurityService;
import com.david.express.service.CommentService;
import com.david.express.service.NoteService;
import com.david.express.service.UserService;
import com.david.express.model.dto.CommentDto;
import com.david.express.model.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private NoteService noteService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private CommentMapper commentMapper;


    @Override
    public Comment findCommentById(Long id) throws ResourceNotFoundException {
        return commentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No comment found with id " + id));
    }

    @Override
    public Page<Comment> findAllComments(Pageable pageable) {
        return commentRepository.findAll(pageable);
    }

    @Override
    public PaginatedResponseDto<CommentDto> getAllComments(int page, int size, String[] sort) {
        // Créer le Pageable avec le tri
        Pageable paging = Utils.createPaging(page, size, sort);

        // Récupération des commentaires
        Page<Comment> pageComments = findAllComments(paging);

        // Mapping
        List<CommentDto> commentsDto = pageComments.getContent()
            .stream()
            .map(commentMapper::toCommentDto)
            .collect(Collectors.toList());

        // Créer la réponse avec les informations de pagination
        PaginatedResponseDto<CommentDto> response = new PaginatedResponseDto<>();
        response.setKey("comments");
        response.setData(commentsDto);
        response.setCurrentPage(Optional.of(pageComments).map(Page::getNumber).orElse(0));
        response.setTotalItems(Optional.of(pageComments).map(Page::getTotalElements).orElse(0L));
        response.setTotalPages(Optional.of(pageComments).map(Page::getTotalPages).orElse(0));

        return response;
    }

    @Override
    public CommentDto getCommentById(Long id) throws ResourceNotFoundException {
        Comment comment = findCommentById(id);
        return commentMapper.toCommentDto(comment);
    }

    @Override
    public CommentDto saveComment(Long noteId, CommentDto commentDto) {
        User authenticatedUser = securityService.getAuthenticatedUser();
        Comment comment = Comment.builder()
                .message(commentDto.getMessage())
                .createdAt(new Date(System.currentTimeMillis()))
                .user(authenticatedUser)
                .note(noteService.findNoteById(noteId))
                .build();

        // Enregistrement du commentaire
        commentRepository.save(comment);

        // Opération de mapping
        return commentMapper.toCommentDto(comment);
    }

    @Override
    public CommentDto updateComment(Long id, CommentDto commentDto) throws ResourceNotFoundException, ResourceAffiliationException {
        // Récupération du commentaire à modifier
        Comment commentToUpdate = findCommentById(id);

        // Admin : peut modifier le message, la date de création, l'auteur de n'importe quel utilisateur
        if (securityService.isLoggedUserHasAdminRole()) {
            commentToUpdate = commentMapper.toCommentEntity(commentToUpdate, commentDto);
        } else {
            if (!isAuthenticatedUserComment(id)) {
                throw new ResourceAffiliationException("You are not allowed to update this resource !");
            }
            // Non admin : peut seulement modifier le message du commentaire
            commentToUpdate.setMessage(commentDto.getMessage() != null ? commentDto.getMessage() : commentToUpdate.getMessage());
        }

        // Mise à jour du commentaire
        Comment savedComment = commentRepository.save(commentToUpdate);

        // Opération de mapping
        return commentMapper.toCommentDto(savedComment);
    }

    @Override
    public void deleteCommentById(Long id) throws ResourceNotFoundException, ResourceAffiliationException {
        if (!securityService.isLoggedUserHasAdminRole()) {
            // Non admin : Vérifier que le commentaire appartient bien à l'utilisateur connecté
            if (!isAuthenticatedUserComment(id)) {
                throw new ResourceAffiliationException("You are not allowed to delete this resource !");
            }
        }
        commentRepository.deleteById(id);
    }

    @Override
    public boolean isAuthenticatedUserComment(Long id) {
        Comment comment = findCommentById(id);
        // Vérifie que la note appartient bien à l'utilisateur connecté
        return comment.getUser().getUsername().equals(securityService.getAuthenticatedUsername());
    }
}
