package com.david.express.service;

import com.david.express.common.Utils;
import com.david.express.model.Note;
import com.david.express.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Service
public class TrendingServiceImpl implements TrendingService {
    @Autowired
    private NoteRepository noteRepository;
    @Override
    public HashMap<String, Integer> getTrendingWords() {
        List<Note> notes = noteRepository.findAll();
        HashMap<String, Integer> trendingWords = new HashMap<String, Integer>();
        notes.forEach(note ->
                Arrays.stream(note.getNote().split("(\\s+)|([.,!?:;’'\"])")).forEach(word -> {
                    if (word.length() > 2) {
                        trendingWords.put(
                                word,
                                trendingWords.containsKey(word) ? (trendingWords.get(word) + 1) : 1);
                    }
                })
        );
        return Utils.sortHashMapByValue(trendingWords);
    }
}
