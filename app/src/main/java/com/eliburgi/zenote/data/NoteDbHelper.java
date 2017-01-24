package com.eliburgi.zenote.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Elias on 20.01.2017.
 */

public class NoteDbHelper extends SQLiteOpenHelper {

    // If the DB-Schema changes, the version must be increased
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "Zenote.db";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + NoteContract.NoteEntry.TABLE_NAME + " (" +
             NoteContract.NoteEntry._ID + " INTEGER PRIMARY KEY, " +
             NoteContract.NoteEntry.COLUMN_NAME_CONTENT + " TEXT NOT NULL, " +
             NoteContract.NoteEntry.COLUMN_NAME_COMPLETED + " INTEGER DEFAULT 0, " +
             NoteContract.NoteEntry.COLUMN_NAME_COLOR + " INTEGER)";

    private static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + NoteContract.NoteEntry.TABLE_NAME;

    public static final String[] COLUMNS_PROJECTION = {
            NoteContract.NoteEntry._ID,
            NoteContract.NoteEntry.COLUMN_NAME_CONTENT,
            NoteContract.NoteEntry.COLUMN_NAME_COMPLETED,
            NoteContract.NoteEntry.COLUMN_NAME_COLOR
    };

    public NoteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    // Called when the DB version changes
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLE);
        onCreate(db);
    }
}
