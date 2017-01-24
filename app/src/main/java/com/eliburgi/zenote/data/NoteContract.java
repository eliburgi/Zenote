package com.eliburgi.zenote.data;

import android.provider.BaseColumns;

/**
 * Created by Elias on 20.01.2017.
 */

public class NoteContract {

    // Private constructor to prevent code from instatiating the contract class
    private NoteContract() {
    }

    public class NoteEntry implements BaseColumns {
        public static final String TABLE_NAME = "notes";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_COMPLETED = "completed";
        public static final String COLUMN_NAME_COLOR = "color";
    }

}
