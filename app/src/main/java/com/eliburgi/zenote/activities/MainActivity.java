package com.eliburgi.zenote.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.eliburgi.zenote.R;
import com.eliburgi.zenote.adapters.NotesAdapter;
import com.eliburgi.zenote.customviews.ColorSelectionWheelView;
import com.eliburgi.zenote.data.NoteManager;
import com.eliburgi.zenote.models.Note;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class MainActivity extends AppCompatActivity implements NotesAdapter.NoteItemListener {

    private NotesAdapter mNotesAdapter;
    private FloatingActionButton mFabDeleteNotes;

    private List<Note> mRemovedNotes = new ArrayList<>();

    /********************************************
    /*--------------- ACTIVITY OVERRIDES --------
    /********************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabAddNote = (FloatingActionButton) findViewById(R.id.fab_add_note);
        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show AddNote-Dialog
                showAddNoteDialog();
            }
        });

        mFabDeleteNotes = (FloatingActionButton) findViewById(R.id.fab_delete_notes);
        mFabDeleteNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete all completed notes
                deleteCompletedNotes();
            }
        });

        // Hide and disable the delete FAB by default
        mFabDeleteNotes.setVisibility(View.GONE);

        // Load all notes from Database
        List<Note> notes = NoteManager.newInstance(this).getAllNotes();

        RecyclerView rvNotes = (RecyclerView) findViewById(R.id.rv_main_notes);
        rvNotes.setHasFixedSize(true);
        rvNotes.setItemAnimator(new FadeInLeftAnimator());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvNotes.setLayoutManager(layoutManager);

        mNotesAdapter = new NotesAdapter(this, notes, this);
        rvNotes.setAdapter(mNotesAdapter);

        checkForCompletedNotes();
    }

    @Override
    protected void onDestroy() {
        // Close the database
        NoteManager.newInstance(this).destroy();

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
            case R.id.menu_undo:
                performUndo();
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
        updateNote(note, completed);
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

        // Save the note to the database
        long id = NoteManager.newInstance(this).createNote(note);
        note.setId(id);

        // Add the note to the adapter cache
        mNotesAdapter.add(note);

        // Note saved successfully
        Toast.makeText(MainActivity.this, "Successfully created the new Note!", Toast.LENGTH_SHORT).show();
    }

    // Completes/Uncompletes the given note and updates its database entry
    private void updateNote(@NonNull Note note, boolean completed) {
        // Update note completed flag
        note.setCompleted(completed);

        // Update database entry
        NoteManager.newInstance(this).updateNote(note);

        // Show/Hide Fab
        checkForCompletedNotes();

        // Refresh view of particular note by removing and reinserting it
        mNotesAdapter.remove(note);
        mNotesAdapter.add(note);
    }

    private void deleteCompletedNotes() {
        // Delete all entries in the database which are completed
        NoteManager.newInstance(this).deleteCompletedNotes();

        // Remember the notes that are going to be deleted
        mRemovedNotes = mNotesAdapter.getCompletedNotes();

        // Delete all completed entries from the adapter cache
        mNotesAdapter.removeCompletedNotes();

        // Show/Hide FAB accordingly
        checkForCompletedNotes();

        // Show Snackbar with Undo option
        if(isUndoAvailable()) {
            showUndoUi();
        }

    }

    private void checkForCompletedNotes() {
        // Check if there is at least one completed note in the list
        // If so, show delete_all_notes FAB with animation
        if(mNotesAdapter.hasCompletedNote()) {
            if(mFabDeleteNotes.getVisibility() != View.VISIBLE) {
                showDeleteNoteFab();
            }
        } else {
            if(mFabDeleteNotes.getVisibility() == View.VISIBLE) {
                hideDeleteNotesFab();
            }
        }
    }

    private boolean isUndoAvailable() {
        return !mRemovedNotes.isEmpty();
    }

    private void performUndo() {
        for(Note n : mRemovedNotes) {
            // Add removed notes to the database again
            long id = NoteManager.newInstance(this).createNote(n);
            n.setId(id);

            // Add removed notes to the adapter cache
            mNotesAdapter.add(n);
        }
        mRemovedNotes.clear();

        checkForCompletedNotes();
    }

    /**************** VIEW METHODS *********************/

    // ********* ADD-NOTE DIALOG *************/
    private void showAddNoteDialog() {
        final MaterialDialog addNoteDialog = new MaterialDialog.Builder(this)
                .title(R.string.dialog_add_note_title)
                .customView(R.layout.dialog_add_note, false)
                .positiveText(R.string.dialog_add_note_positive)
                .negativeText(R.string.dialog_add_note_negative)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        View customView = dialog.getCustomView();
                        EditText etNoteTitle = (EditText) customView.findViewById(R.id.et_dialog_add_note_title);
                        ColorSelectionWheelView csw = (ColorSelectionWheelView) customView.findViewById(R.id.dialog_add_note_color_selection);

                        saveNote(etNoteTitle.getText().toString(), csw.getSelectedColor());
                    }
                })
                .showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {

                    }
                })
                .build();

        View customView = addNoteDialog.getCustomView();

        // Initialize the color selection wheel view
        ColorSelectionWheelView csw = (ColorSelectionWheelView) customView.findViewById(R.id.dialog_add_note_color_selection);
        csw.create(
                getResources().getIntArray(R.array.priorityColors),
                (int) getResources().getDimension(R.dimen.dialog_add_note_priority_item_size),
                (int) getResources().getDimension((R.dimen.dialog_add_note_priority_list_margin))
        );

        final EditText etNoteTitle = (EditText) customView.findViewById(R.id.et_dialog_add_note_title);
        // Listen for text changes for the title edittext and disable positive button when the edittext is empty
        etNoteTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String editTextContent = etNoteTitle.getText().toString().trim();
                if(!editTextContent.equals("")) {
                    addNoteDialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                } else {
                    addNoteDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Disable positive action by default
        addNoteDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);

        // Show keyboard
        addNoteDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        etNoteTitle.requestFocus();

        // Show the dialog
        addNoteDialog.show();
    }

    private void showDeleteNoteFab() {
        mFabDeleteNotes.setVisibility(View.VISIBLE);

        Animation animationFabShow = AnimationUtils.loadAnimation(this, R.anim.expand_in);
        mFabDeleteNotes.startAnimation(animationFabShow);
    }

    private void hideDeleteNotesFab() {
        Animation animationFabHide = AnimationUtils.loadAnimation(this, R.anim.expand_out);
        animationFabHide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFabDeleteNotes.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //mFabDeleteNotes.setAnimation(animationFabHide);
        //animationFabHide.start();
        mFabDeleteNotes.startAnimation(animationFabHide);
    }

    private void showUndoUi() {
        Snackbar undoSnackbar = Snackbar.make(findViewById(R.id.coordinator_layout), R.string.snackbar_removed_note_message, Snackbar.LENGTH_LONG);
        undoSnackbar.setAction(R.string.snackbar_undo_action, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performUndo();
            }
        });
        undoSnackbar.show();
    }
}
