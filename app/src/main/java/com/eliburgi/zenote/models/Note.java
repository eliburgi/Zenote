package com.eliburgi.zenote.models;

import android.database.Cursor;

import com.eliburgi.zenote.data.NoteContract;

/**
 * Created by Elias on 14.01.2017.
 */

/**
 * Class representing a note the user created.
 */
public class Note {
    private long id;
    private String content;
    private boolean completed = false;
    private int color;

    public Note() {

    }

    public Note(long id, String content, int color) {
        this.id = id;
        this.content = content;
        this.color = color;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    /**
     * Converts a cursor to a note object.
     * @param cursor The cursor containing the note data
     * @return A new note containing the data of the cursor.
     */
    public static Note fromCursor(Cursor cursor) {
        Note note = new Note();

        note.setId(cursor.getLong(cursor.getColumnIndex(NoteContract.NoteEntry._ID)));
        note.setContent(cursor.getString(cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NAME_CONTENT)));
        note.setCompleted(cursor.getInt(cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NAME_COMPLETED)) > 0);
        note.setColor(cursor.getInt(cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NAME_COLOR)));

        return note;
    }
}
