package com.david.express.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "app_like", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "note_id"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;
}
