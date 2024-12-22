package com.david.express.service.impl;

import com.david.express.common.Utils;
import com.david.express.entity.Like;
import com.david.express.entity.User;
import com.david.express.exception.ResourceAffiliationException;
import com.david.express.exception.ResourceNotFoundException;
import com.david.express.entity.Note;
import com.david.express.model.dto.PaginatedResponseDto;
import com.david.express.repository.LikeRepository;
import com.david.express.repository.NoteRepository;
import com.david.express.security.service.SecurityService;
import com.david.express.service.NoteService;
import com.david.express.service.UserService;
import com.david.express.model.dto.CommentDto;
import com.david.express.model.mapper.CommentMapper;
import com.david.express.model.dto.NoteDto;
import com.david.express.model.mapper.NoteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private UserService userService;
    @Autowired
    private NoteMapper noteMapper;
    @Autowired
    private CommentMapper commentMapper;


    @Override
    public Note findNoteById(Long id) throws ResourceNotFoundException {
        return noteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No note found with id " + id));
    }

    @Override
    public Page<Note> findAllNotes(Pageable pageable) {
        return noteRepository.findAll(pageable);
    }

    @Override
    public PaginatedResponseDto<NoteDto> getNotes(String username, String keyword, Date dateStart, Date dateEnd, int page, int size, String[] sort) {

        Page<Note> pageNotes;

        // Créer le Pageable avec le tri
        Pageable paging = Utils.createPaging(page, size, sort);

        // Recherche des notes selon les critères de filtrage ou toutes les notes
        if (username != null || keyword != null || dateStart != null || dateEnd != null) {
            // Définir les valeurs par défaut pour la période de recherche si nécessaire
            dateStart = Optional.ofNullable(dateStart).orElse(new Date(0));
            dateEnd = Optional.ofNullable(dateEnd).orElse(new Date(System.currentTimeMillis()));
            pageNotes = noteRepository.findByCriteria(username, keyword, dateStart, dateEnd, paging);
        } else {
            pageNotes = findAllNotes(paging);
        }

        // Transformation des notes en NoteDto et ajout de la propriété "likedByLoggedUser"
        List<NoteDto> notesDto = Optional.ofNullable(pageNotes)
            .map(Page::getContent)
            .orElseGet(Collections::emptyList)
            .stream()
            .map(note -> {
                NoteDto noteDto = noteMapper.toNoteDto(note);
                noteDto.setLikedByAuthenticatedUser(isNoteLikedByAuthenticatedUser(note));
                return noteDto;
            })
            .collect(Collectors.toList());

        // Créer la réponse avec les informations de pagination
        PaginatedResponseDto<NoteDto> response = new PaginatedResponseDto<NoteDto>();
        response.setKey("notes");
        response.setData(notesDto);
        response.setCurrentPage(Optional.ofNullable(pageNotes).map(Page::getNumber).orElse(0));
        response.setTotalItems(Optional.ofNullable(pageNotes).map(Page::getTotalElements).orElse(0L));
        response.setTotalPages(Optional.ofNullable(pageNotes).map(Page::getTotalPages).orElse(0));

        return response;
    }

    @Override
    public List<CommentDto> findNoteComments(Long id) throws ResourceNotFoundException {
        Note note = findNoteById(id);
        return note.getComments()
            .stream()
            .map(commentMapper::toCommentDto)
            .collect(Collectors.toList());
    }

    @Override
    public NoteDto getNoteById(Long id) {
        Note note = findNoteById(id);
        NoteDto noteDto = noteMapper.toNoteDto(note);
        noteDto.setLikedByAuthenticatedUser(isNoteLikedByAuthenticatedUser(note));
        return noteDto;
    }

    @Override
    public NoteDto saveNote(NoteDto noteDto) {
        Note note = Note
            .builder()
            .note(noteDto.getNote())
            .createdAt(new Date(System.currentTimeMillis()))
            .likes(new HashSet<>())
            .user(securityService.getAuthenticatedUser())
            .build();

        // Enregistrement de la note
        Note savedNote = noteRepository.save(note);

        // Opération de mapping
        NoteDto savedNoteDto = noteMapper.toNoteDto(savedNote);
        savedNoteDto.setLikedByAuthenticatedUser(false);
        return savedNoteDto;
    }

    @Override
    public NoteDto updateNote(Long id, NoteDto noteDto) throws ResourceNotFoundException, ResourceAffiliationException {
        Note noteToUpdate = findNoteById(id);
        // Si l'utilisateur connecté possède le role admin, possibilité de modifier n'importe quelle note
        if (securityService.isLoggedUserHasAdminRole()) {
            noteToUpdate = noteMapper.toNoteEntity(noteToUpdate, noteDto);
        } else {
            // Si non, vérifier que la note à modifier appartient bien à l'utilisateur connecté
            if (!isNotePostedByAuthenticatedUser(id)) {
                throw new ResourceAffiliationException("You are not allowed to update this resource !");
            }
            // ...qui ne peut seulement modifier le contenu de sa note.
            noteToUpdate.setNote(noteDto.getNote() != null ? noteDto.getNote() : noteToUpdate.getNote());
        }

        // Mise à jour de la note
        Note updatedNote = noteRepository.save(noteToUpdate);

        // Opération de mapping
        NoteDto updatedNoteDto = noteMapper.toNoteDto(updatedNote);
        updatedNoteDto.setLikedByAuthenticatedUser(isNoteLikedByAuthenticatedUser(updatedNote));
        return updatedNoteDto;
    }

    @Override
    public void deleteNoteById(Long id) throws ResourceNotFoundException, ResourceAffiliationException {
        Note note = findNoteById(id);
        // Si l'utilisateur possède le rôle administrateur
        if (securityService.isLoggedUserHasAdminRole()) {
            noteRepository.delete(note);
        }
        // Si l'utilisateur connecté n'est pas administrateur
        if (!securityService.isLoggedUserHasAdminRole()) {
            // Vérifier que la note à supprimer appartient bien à l'utilisateur connecté
            if (!isNotePostedByAuthenticatedUser(id)) {
                throw new ResourceAffiliationException("You are not allowed to delete this resource !");
            }
            noteRepository.delete(note);
        }
    }

    @Override
    public NoteDto likeNote(Long id) throws ResourceNotFoundException {
        Note note = findNoteById(id);
        User user = securityService.getAuthenticatedUser();

        // Vérifier si la note est déjà liker par l'utilisateur
        if (isNoteLikedByAuthenticatedUser(note)) {
            note.removeLike(likeRepository.getLikeByNoteAndUser(note, user));
        } else {
            note.addLike(new Like(null, user, note));
        }

        // Mise à jour de la note
        noteRepository.save(note);

        // Opération de mapping
        NoteDto noteDto = noteMapper.toNoteDto(note);
        noteDto.setLikedByAuthenticatedUser(isNoteLikedByAuthenticatedUser(note));
        return noteDto;
    }

    @Override
    public boolean isNotePostedByAuthenticatedUser(Long id) {
        Note note = findNoteById(id);
        // Vérifie que la note appartient bien à l'utilisateur connecté
        return note.getUser().getUsername().equals(securityService.getAuthenticatedUsername());
    }

    @Override
    public boolean isNoteLikedByAuthenticatedUser(Note note) {
        // Vérifier si l'utilisateur est authentifié
        if (securityService.isUserAuthenticated()) {
            // Récupérer l'utilisateur connecté et vérifier s'il aime la note
            return likeRepository.existsByNoteAndUser(note, securityService.getAuthenticatedUser());
        }
        return false;
    }
}
