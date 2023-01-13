package com.david.express.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "app_comment")
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String message;

    @Column(name = "created_at")
    @NotBlank
    private String createdAt;

    @ManyToOne
    @JoinColumn(name = "app_note_id")
    private Note note;

    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private User user;
}
