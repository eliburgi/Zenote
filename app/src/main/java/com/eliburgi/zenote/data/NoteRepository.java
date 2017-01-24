package com.eliburgi.zenote.data;

import com.eliburgi.zenote.models.Note;

import java.util.List;

/**
 * Created by Elias on 21.01.2017.
 */

public interface NoteRepository {

    Note createNote(String content, int color);
    Note readNote(int id);
    List<Note> readNotes();
    void updateNote(Note note);
    void deleteNote(int id);
    void deleteNotes();

    void onDestroy();
}
