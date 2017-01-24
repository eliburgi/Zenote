package com.eliburgi.zenote.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.eliburgi.zenote.models.Note;

import java.util.List;

/**
 * Created by Elias on 21.01.2017.
 */

public class NoteDbRepository implements NoteRepository {

    private NoteDbHelper mDbHelper;

    public NoteDbRepository(Context context) {
        mDbHelper = new NoteDbHelper(context);
    }

    @Override
    public Note createNote(String content, int color) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NoteContract.NoteEntry.COLUMN_NAME_CONTENT, content);
        values.put(NoteContract.NoteEntry.COLUMN_NAME_COMPLETED, false);
        values.put(NoteContract.NoteEntry.COLUMN_NAME_COLOR, color);

        long rowId = db.insert(NoteContract.NoteEntry.TABLE_NAME, null, values);

        Note note = new Note(rowId, content, color);
        return note;
    }

    @Override
    public Note readNote(int id) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();



        return null;
    }

    @Override
    public List<Note> readNotes() {
        return null;
    }

    @Override
    public void updateNote(Note note) {

    }

    @Override
    public void deleteNote(int id) {

    }

    @Override
    public void deleteNotes() {

    }

    @Override
    public void onDestroy() {
        mDbHelper.close();
    }
}
