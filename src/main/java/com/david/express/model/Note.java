package com.david.express.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_note")
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String note;

    @NotBlank
    private int likes;

    @NotBlank
    private int dislikes;

    @Column(name = "created_at")
    @NotBlank
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private User user;
}
