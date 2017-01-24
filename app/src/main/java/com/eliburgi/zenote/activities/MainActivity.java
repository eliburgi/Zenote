package com.eliburgi.zenote.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.eliburgi.zenote.R;
import com.eliburgi.zenote.adapters.NotesAdapter;
import com.eliburgi.zenote.customviews.ColorSelectionWheelView;
import com.eliburgi.zenote.data.NoteManager;
import com.eliburgi.zenote.models.Note;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesAdapter.NoteItemListener {

    private NotesAdapter mNotesAdapter;

    // VIEWS
    private View mAddNoteDialogCustomView;
    private MaterialDialog mAddNoteDialog;

    /********************************************
    /*--------------- ACTIVITY OVERRIDES --------
    /********************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show AddNote-Dialog
                showAddNoteDialog();
            }
        });

        // Inflate the AddNoteDialog View for reusing and finalize the layout dynamically
        initAddNoteDialog();

        // Establish a connection to the database
        // Important: Must be called!
        NoteManager.newInstance(getApplicationContext()).connectToDb();

        // Load all notes from Database
        List<Note> notes = NoteManager.newInstance(getApplicationContext()).getAllNotes();

        RecyclerView rvNotes = (RecyclerView) findViewById(R.id.rv_main_notes);
        rvNotes.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvNotes.setLayoutManager(layoutManager);

        mNotesAdapter = new NotesAdapter(notes, this);
        rvNotes.setAdapter(mNotesAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.menu_remove_all_notes:
                deleteAllNotes();
                return true;
            default:
                return false;
        }
    }


    /********************************************
    /*--------------- CALLBACKS -----------------
    /********************************************/

    @Override
    public void onCompleteNoteItemClick(Note note, boolean completed) {
        completeNote(note, completed);
    }


    /********************************************
    /*--------------- PUBLIC METHODS -------------
    /********************************************/



    /********************************************
    /*--------------- PRIVATE METHODS ------------
    /********************************************/



    /**************** PRESENTER METHODS ****************/
    /**
     * Creates a new Note and adds it to the RecyclerView Adapter cache and inserts it into the database
     * @return true if the note was correctly created and added to the underlying data repository, otherwise false
     */
    private void saveNote(@NonNull String content, int color) {
        Note note = new Note();
        note.setContent(content);
        note.setColor(color);

        // Add the note to database
        long id = NoteManager.newInstance(getApplicationContext()).createNote(note);
        note.setId(id);

        // Add the note to the adapter cache
        mNotesAdapter.add(note);
    }

    // Completes/Uncompletes the given note and updates its database entry
    private void completeNote(@NonNull Note note, boolean completed) {
        // Update note completed flag
        note.setCompleted(completed);
        // Update Database
        NoteManager.newInstance(getApplicationContext()).updateNote(note);
    }

    private void deleteAllNotes() {
        // Delete all entries in the database
        NoteManager.newInstance(getApplicationContext()).deleteAllNotes();
        // Delete all entries from the adapter cache
        mNotesAdapter.removeAll();
    }


    /**************** VIEW METHODS *********************/

    // ********* ADD-NOTE DIALOG *************/
    // Shows the AddNote Dialog
    private void showAddNoteDialog() {
        mAddNoteDialog.show();
    }

    // Creates the AddNoteDialog and its customView
    private void initAddNoteDialog() {
        mAddNoteDialogCustomView = LayoutInflater.from(this).inflate(R.layout.dialog_add_note, null, false);
        ColorSelectionWheelView csw = (ColorSelectionWheelView) mAddNoteDialogCustomView.findViewById(R.id.dialog_add_note_color_selection);
        csw.create(
                getResources().getIntArray(R.array.priorityColors),
                (int) getResources().getDimension(R.dimen.dialog_add_note_priority_item_size),
                (int) getResources().getDimension((R.dimen.dialog_add_note_priority_list_margin))
        );

        mAddNoteDialog = new MaterialDialog.Builder(this)
                .title(R.string.dialog_add_note_title)
                .customView(mAddNoteDialogCustomView, false)
                .positiveText(R.string.dialog_add_note_positive)
                .negativeText(R.string.dialog_add_note_negative)
                .showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        // Reset all fields before showing the dialog
                        resetAddNoteDialog();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        View customView = dialog.getCustomView();
                        EditText etNoteTitle = (EditText) customView.findViewById(R.id.et_dialog_add_note_title);
                        ColorSelectionWheelView csw = (ColorSelectionWheelView) mAddNoteDialogCustomView.findViewById(R.id.dialog_add_note_color_selection);
                        // Create and save a new Note
                        saveNote(etNoteTitle.getText().toString(), csw.getSelectedColor());
                        // Note saved successfully
                        Toast.makeText(MainActivity.this, "Successfully created the new Note!", Toast.LENGTH_SHORT).show();
                    }
                }).build();

        final EditText etAddNoteTitle = (EditText) mAddNoteDialogCustomView.findViewById(R.id.et_dialog_add_note_title);

        // Listen for text changes for the title edittext and disable positive button when the edittext is empty
        etAddNoteTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String editTextContent = etAddNoteTitle.getText().toString().trim();
                if(!editTextContent.equals("")) {
                    mAddNoteDialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                } else {
                    mAddNoteDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    // Resets all views inside the AddNoteDialog, because it is being reused and not created every time
    private void resetAddNoteDialog() {
        final EditText etAddNoteTitle = (EditText) mAddNoteDialogCustomView.findViewById(R.id.et_dialog_add_note_title);
        etAddNoteTitle.setText("");
        etAddNoteTitle.requestFocus();

        // Show keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(etAddNoteTitle, InputMethodManager.SHOW_IMPLICIT);

        // Reset colors
        mAddNoteDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
    }
}
