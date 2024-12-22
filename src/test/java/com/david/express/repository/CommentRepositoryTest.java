package com.david.express.repository;

import com.david.express.entity.Comment;
import com.david.express.entity.Note;
import com.david.express.entity.RoleEnum;
import com.david.express.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CommentRepositoryTest {

    private Comment comment1;
    private Comment comment2;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NoteRepository noteRepository;

    @BeforeEach
    public void init() {
        User user = userRepository.save(
                User.builder()
                        .id(1L)
                        .username("John")
                        .email("johndoe@gmail.com")
                        .password("password")
                        .roles(new HashSet<>(RoleEnum.ROLE_READER.ordinal(), RoleEnum.ROLE_WRITER.ordinal()))
                        .build()
        );
        Note note = noteRepository.save(
                Note.builder()
                        .id(1L)
                        .user(user)
                        .note("Bonjour")
                        .likes(new HashSet<>())
                        .createdAt(generateDateFromString("2023-10-02"))
                        .build()
        );

        comment1 = Comment.builder()
                .id(1L)
                .createdAt(generateDateFromString("2023-10-02"))
                .message("Voici mon premier commentaire.")
                .note(note)
                .user(user)
                .build();
        comment2 = Comment.builder()
                .id(2L)
                .createdAt(generateDateFromString("2023-10-02"))
                .message("Voici un deuxième commentaire.")
                .note(note)
                .user(user)
                .build();
    }

    private Date generateDateFromString(String date) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Test
    public void itShouldFindAllComments() {
        // Given
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        // When
        List<Comment> comments = commentRepository.findAll();
        // Then
        assertEquals(2, comments.size());
        assertThat(comments).hasSize(2);
    }

    @Test
    public void itShouldSaveComment() {
        // When
        Comment savedComment1 = commentRepository.save(comment1);
        // Then
        assertThat(savedComment1).isNotNull();
        assertThat(savedComment1.getId()).isGreaterThan(0);
        assertEquals(comment1.getMessage(), savedComment1.getMessage());
    }

    @Test
    public void itShouldFindCommentById() {
        // Given
        Comment savedComment1 = commentRepository.save(comment1);
        Comment savedComment2 = commentRepository.save(comment2);
        // When
        Optional<Comment> optionalComment = commentRepository.findById(savedComment2.getId());
        // Then
        assertThat(optionalComment).isPresent();
        assertEquals(optionalComment.get().getMessage(), comment2.getMessage());
    }

    @Test
    public void itShouldDeleteCommentById() {
        // Given
        Comment savedComment1 = commentRepository.save(comment1);
        Comment savedComment2 = commentRepository.save(comment2);
        // When
        commentRepository.deleteById(savedComment1.getId());
        Optional<Comment> optionalComment = commentRepository.findById(comment1.getId());
        // Then
        assertThat(optionalComment).isEmpty();
        assertThat(commentRepository.findAll()).hasSize(1).contains(savedComment2);
    }
}
