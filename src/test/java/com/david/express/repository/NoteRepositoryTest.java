package com.david.express.repository;

import com.david.express.entity.Note;
import com.david.express.entity.RoleEnum;
import com.david.express.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional; 

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class NoteRepositoryTest {

    private Note note1;
    private Note note2;
    private Note note3;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NoteRepository noteRepository;

    @BeforeEach
    public void init() {
        List<User> users = initUsers();
        note1 = Note.builder()
                .id(1L)
                .user(users.get(0))
                .note("Bonjour")
                .likes(new HashSet<>())
                .createdAt(generateDateFromString("2023-10-02"))
                .build();
        note2 = Note.builder()
                .id(2L)
                .user(users.get(0))
                .note("Comment ça va ?")
                .likes(new HashSet<>())
                .createdAt(generateDateFromString("2023-10-03"))
                .build();
        note3 = Note.builder()
                .id(3L)
                .user(users.get(1))
                .note("Hello everyone !")
                .likes(new HashSet<>())
                .createdAt(generateDateFromString("2023-10-05"))
                .build();
    }

    private List<User> initUsers() {
        User user1 = User.builder()
                .id(1L)
                .username("John")
                .email("johndoe@gmail.com")
                .password("password")
                .roles(new HashSet<>(RoleEnum.ROLE_READER.ordinal(), RoleEnum.ROLE_WRITER.ordinal()))
                .build();
        User user2 = User.builder()
                .id(2L)
                .username("Jeannot")
                .email("jeanpetit@gmail.com")
                .password("password")
                .roles(new HashSet<>(RoleEnum.ROLE_READER.ordinal(), RoleEnum.ROLE_WRITER.ordinal()))
                .build();
        userRepository.save(user1);
        userRepository.save(user2);
        return userRepository.findAll();
    }

    private Date generateDateFromString(String stringDate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.parse(stringDate);
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Test
    public void itShouldFindAllNotes() {
        // Given
        noteRepository.save(note1);
        noteRepository.save(note2);
        noteRepository.save(note3);
        // When
        List<Note> notes = noteRepository.findAll();
        // Then
        assertEquals(3, notes.size());
        assertThat(notes).hasSize(3);
    }

    @Test
    public void itShouldFindNoteById() {
        // Given
        Note savedNote1 = noteRepository.save(note1);
        Note savedNote2 = noteRepository.save(note2);
        Note savedNote3 = noteRepository.save(note3);
        // When
        Optional<Note> optionalNote = noteRepository.findById(savedNote1.getId());
        // Then
        assertTrue(optionalNote.isPresent());
        assertEquals(savedNote1, optionalNote.get());
        assertNotEquals(savedNote2, optionalNote.get());
        assertNotEquals(savedNote3, optionalNote.get());
    }

    @Test
    public void itShouldFindNotesWithUsernameCriteria() {
        // Given
        noteRepository.save(note1);
        noteRepository.save(note2);
        noteRepository.save(note3);
        // When
        Page<Note> notes = noteRepository.findByCriteria("John", null, null, null, Pageable.unpaged());
        // Then
        assertThat(notes).hasSize(2);
        assertEquals(2, notes.getContent().size());
        assertEquals(2, notes.getTotalElements());
    }

    @Test
    public void itShouldFindNotesWithKeywordCriteria() {
        // Given
        noteRepository.save(note1);
        noteRepository.save(note2);
        noteRepository.save(note3);
        // When
        Page<Note> notes = noteRepository.findByCriteria(null, "Hello", null, null, Pageable.unpaged());
        // Then
        assertThat(notes).hasSize(1);
        assertEquals(1, notes.getContent().size());
        assertEquals(1, notes.getTotalElements());
        assertEquals(note3.getUser().getUsername(), notes.getContent().get(0).getUser().getUsername());
    }

    @Test
    public void itShouldFindNotesWithDatesCriteria() {
        // Given
        noteRepository.save(note1);
        noteRepository.save(note2);
        noteRepository.save(note3);
        // When
        Page<Note> notes = noteRepository.findByCriteria(
                null,
                null,
                java.sql.Date.valueOf("2023-09-25"),
                java.sql.Date.valueOf("2023-10-02"),
                Pageable.unpaged());
        // Then
        assertThat(notes).hasSize(1);
        assertEquals(1, notes.getContent().size());
        assertEquals(1, notes.getTotalElements());
        assertEquals(note1.getUser().getUsername(), notes.getContent().get(0).getUser().getUsername());
    }

    @Test
    public void itShouldDeleteNote() {
        // Given
        Note savedNote1 = noteRepository.save(note1);
        Note savedNote2 = noteRepository.save(note2);
        Note savedNote3 = noteRepository.save(note3);
        // When
        noteRepository.delete(savedNote2);
        Optional<Note> optionalNote2 = noteRepository.findById(note2.getId());
        List<Note> notes = noteRepository.findAll();
        // Then
        assertThat(optionalNote2).isEmpty();
        assertEquals(2, notes.size());
        assertThat(notes).hasSize(2).contains(savedNote1, savedNote3);
    }
}
