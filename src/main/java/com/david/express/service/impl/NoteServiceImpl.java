package com.david.express.service.impl;

import com.david.express.exception.ResourceAffiliationException;
import com.david.express.exception.ResourceNotFoundException;
import com.david.express.entity.Comment;
import com.david.express.entity.Note;
import com.david.express.repository.NoteRepository;
import com.david.express.service.NoteService;
import com.david.express.service.RoleService;
import com.david.express.service.UserService;
import com.david.express.web.note.dto.NoteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.*;

@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    @Override
    public Page<Note> findAllNotes(Pageable pageable) {
        return noteRepository.findAll(pageable);
    }

    @Override
    public Note findNoteById(Long id) throws ResourceNotFoundException {
        return noteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Note not found with id " + id));
    }

    @Override
    public Page<Note> getNotes(String username, String keyword, Date dateStart, Date dateEnd, int page, int size, String[] sort) {
        // ?sort=column1,direction1 => array of 2 elements : [“column1”, “direction1”]
        // ?sort=column1,direction1&sort=column2,direction2 => array of 2 elements : [“column1, direction1”, “column2, direction2”]
        List<Sort.Order> orders = new ArrayList<>();
        if (sort[0].contains(",")) {
            // Tri selon plusieurs champs (sortOrder = "field, direction")
            for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");
                orders.add(new Sort.Order(Sort.Direction.fromString(_sort[1]), _sort[0]));
            }
        } else {
            // Tri selon un seul champ (sortOrder = "field, direction")
            orders.add(new Sort.Order(Sort.Direction.fromString(sort[1]), sort[0]));
        }

        Page<Note> pageNotes = null;
        Pageable paging = PageRequest.of(page, size, Sort.by(orders));

        if (username != null || keyword != null || dateStart != null || dateEnd != null) {
            dateStart = Optional.ofNullable(dateStart).orElse(new Date(0));
            dateEnd = Optional.ofNullable(dateEnd).orElse(new Date(System.currentTimeMillis()));
            pageNotes = noteRepository.findByCriteria(username, keyword, dateStart, dateEnd, paging);
        }

        if (pageNotes == null) {
            pageNotes = findAllNotes(paging);
        }

        return pageNotes;
    }

    @Override
    public List<Comment> findNoteComments(Long id) throws ResourceNotFoundException {
        Note note = findNoteById(id);
        return note.getComments();
    }

    @Override
    public Note saveNote(NoteDTO noteDto) {
        Note note = Note
            .builder()
            .note(noteDto.getNote())
            .likes(0)
            .dislikes(0)
            .createdAt(new Date(System.currentTimeMillis()))
            .user(userService.getLoggedInUser())
            .build();
        return noteRepository.save(note);
    }

    @Override
    public Note updateNote(Long id, NoteDTO noteDto) throws ResourceNotFoundException, ResourceAffiliationException {
        Note noteToUpdate = findNoteById(id);
        // Si l'utilisateur connecté possède le role admin, possibilité de modifier n'importe quelle note
        if (roleService.isLoggedUserHasAdminRole()) {
            noteToUpdate.setNote(noteDto.getNote() != null
                ? noteDto.getNote()
                : noteToUpdate.getNote());
            noteToUpdate.setCreatedAt(noteDto.getCreatedAt() != null
                ? noteDto.getCreatedAt()
                : noteToUpdate.getCreatedAt());
            noteToUpdate.setLikes(Optional.of(noteDto.getLikes()).orElse(0) != 0
                ? noteDto.getLikes()
                : noteToUpdate.getLikes());
            noteToUpdate.setDislikes(Optional.of(noteDto.getDislikes()).orElse(0) != 0
                ? noteDto.getDislikes()
                : noteToUpdate.getDislikes());
            noteToUpdate.setUser(noteDto.getAuthor() != null
                ? userService.findUserByUsername(noteDto.getAuthor())
                : noteToUpdate.getUser());
        } else {
            // Si non, vérifier que la note à modifier appartient bien à l'utilisateur connecté
            if (!isLoggedUserIsNoteOwner(id)) {
                throw new ResourceAffiliationException("You are not allowed to update this resource !");
            }
            // ...qui ne peut seulement modifier le contenu de sa note.
            noteToUpdate.setNote(noteDto.getNote() != null ? noteDto.getNote() : noteToUpdate.getNote());
        }
        return noteRepository.save(noteToUpdate);
    }

    @Override
    public void deleteNoteById(Long id) throws ResourceNotFoundException, ResourceAffiliationException {
        Note note = findNoteById(id);
        // Si l'utilisateur possède le rôle administrateur
        if (roleService.isLoggedUserHasAdminRole()) {
            noteRepository.delete(note);
        }
        // Si l'utilisateur connecté n'est pas administrateur
        if (!roleService.isLoggedUserHasAdminRole()) {
            // Il n'a pas la possibilité de supprimer n'importe quelle note
            // Donc, vérifier que la note à supprimer appartient bien à l'utilisateur connecté
            if (!isLoggedUserIsNoteOwner(id)) {
                throw new ResourceAffiliationException("You are not allowed to delete this resource !");
            }
            noteRepository.delete(note);
        }
    }

    @Override
    public Note likeNote(Long id) throws ResourceNotFoundException {
        Note note = findNoteById(id);
        note.like();
        return noteRepository.save(note);
    }

    @Override
    public Note dislikeNote(Long id) throws ResourceNotFoundException {
        Note note = findNoteById(id);
        note.dislike();
        return noteRepository.save(note);
    }

    /**
     *
     * @param id L'id de la note
     * @return True ou False en fonction de si la note a été posté par l'utilisateur connecté
     */
    @Override
    // isLoggedUserPost ?
    // isLoggedUserPost ?
    // isLoggedUserPost ?
    public boolean isLoggedUserIsNoteOwner(Long id) {
        Note note = findNoteById(id);
        return note.getUser().getUsername().equals(userService.getLoggedInUser().getUsername());
    }
}
