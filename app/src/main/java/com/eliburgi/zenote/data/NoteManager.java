package com.eliburgi.zenote.data;

/**
 * Created by Elias on 22.01.2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.eliburgi.zenote.models.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for performing CRUD operations on the Notes database
 */
public class NoteManager {
    private static NoteManager sNoteManager;

    private Context mContext;
    private NoteDbHelper mDbHelper;

    private NoteManager(Context context) {
        mContext = context.getApplicationContext();
        mDbHelper = new NoteDbHelper(mContext);
    }

    public static NoteManager newInstance(Context context) {
        if(sNoteManager == null) {
            sNoteManager = new NoteManager(context.getApplicationContext());
        }
        return sNoteManager;
    }

    // CRUD
    public long createNote(@NonNull final Note note) {
        // Open database for writing
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Put the new data
        ContentValues values = new ContentValues();
        values.put(NoteContract.NoteEntry.COLUMN_NAME_CONTENT, note.getContent());
        values.put(NoteContract.NoteEntry.COLUMN_NAME_COMPLETED, note.isCompleted() ? 1 : 0);
        values.put(NoteContract.NoteEntry.COLUMN_NAME_COLOR, note.getColor());

        // Insert the new entry into the db
        long rowId = db.insert(NoteContract.NoteEntry.TABLE_NAME, null, values);
        return rowId;
    }

    // CRUD
    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        // Open database for reading
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // Query the database for all note entries
        Cursor cursor = db.query(
                NoteContract.NoteEntry.TABLE_NAME,                  // The table to query
                NoteDbHelper.COLUMNS_PROJECTION,                    // The columns to return
                null,                                               // Where clause null -> return everything
                null,
                null,
                null,
                null
        );

        if(cursor != null) {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                notes.add(Note.fromCursor(cursor));
                cursor.moveToNext();
            }
            // Important: Close cursor after use
            cursor.close();
        }
        return notes;
    }

    /**
     * Searches the note with the given ID in the database and returns it.
     * @param id The ID of the note to search and return
     * @return The note entry with the given ID. If no entry with @param id is found, null is returned.
     */
    @Nullable
    public Note getNote(long id) {
        Note note;
        // Open database for reading
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // Query the database for a note with the given id
        Cursor cursor = db.query(
                NoteContract.NoteEntry.TABLE_NAME,              // The table to query
                NoteDbHelper.COLUMNS_PROJECTION,                // The columns to return
                NoteContract.NoteEntry._ID + " = " + id,        // The columns for the where clause
                null,
                null,
                null,
                null
        );

        if(cursor != null) {
            cursor.moveToFirst();
            note = Note.fromCursor(cursor);
            return note;
        }
        return null;
    }

    // CRUD
    public void updateNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(NoteContract.NoteEntry.COLUMN_NAME_CONTENT, note.getContent());
        values.put(NoteContract.NoteEntry.COLUMN_NAME_COMPLETED, note.isCompleted() ? 1 : 0);
        values.put(NoteContract.NoteEntry.COLUMN_NAME_COLOR, note.getColor());

        // Open database for writing
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Update the note with the new values
        db.update(
                NoteContract.NoteEntry.TABLE_NAME,
                values,
                NoteContract.NoteEntry._ID + " = " + note.getId(),
                null
        );
    }

    // CRUD
    public void deleteNote(Note note) {
        // Open database for writing
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(
                NoteContract.NoteEntry.TABLE_NAME,
                NoteContract.NoteEntry._ID + " = " + note.getId(),
                null
        );
    }

    // CRUD
    public void deleteCompletedNotes() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(
                NoteContract.NoteEntry.TABLE_NAME,
                NoteContract.NoteEntry.COLUMN_NAME_COMPLETED + " = 1",
                null
        );
    }

    public void destroy() {
        mDbHelper.close();
    }

}
