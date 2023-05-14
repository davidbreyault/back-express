package com.david.express.repository;

import com.david.express.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    @Query("SELECT n FROM Note n WHERE" +
            "(?1 is null OR n.user.username = ?1)\n" +
            "AND (?2 is null OR n.note LIKE %?2%)\n" +
            "AND (?3 is null OR n.createdAt BETWEEN ?3 AND ?4)")
    Page<Note> findByCriteria(String username, String keyword, Date dateStart, Date dateEnd, Pageable pageable);
}
