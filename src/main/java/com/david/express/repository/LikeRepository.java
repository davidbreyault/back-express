package com.david.express.repository;

import com.david.express.entity.Like;
import com.david.express.entity.Note;
import com.david.express.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Like getLikeByNoteAndUser(Note note, User user);
    boolean existsByNoteAndUser(Note note, User user);
}
