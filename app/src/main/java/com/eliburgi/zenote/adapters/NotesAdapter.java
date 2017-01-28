package com.eliburgi.zenote.adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eliburgi.zenote.R;
import com.eliburgi.zenote.models.Note;
import com.eliburgi.zenote.customviews.SmoothCheckBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elias on 14.01.2017.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
    private List<Note> mNotes;
    private NoteItemListener mNoteItemListener;

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTvNoteContent;
        public SmoothCheckBox mScbNoteCompleted;

        public ViewHolder(View rowItem) {
            super(rowItem);
            mTvNoteContent = (TextView) rowItem.findViewById(R.id.tv_note_content);
            mScbNoteCompleted = (SmoothCheckBox) rowItem.findViewById(R.id.scb_note_completed);
        }
    }

    public NotesAdapter(@NonNull List<Note> notes, @Nullable NoteItemListener noteItemListener) {
        mNotes = notes;
        mNoteItemListener = noteItemListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create the new view
        View v = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_note, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // Get the element from the dataset at this position
        final Note note = mNotes.get(position);

        // Replace the contents of the view with that element
        holder.mTvNoteContent.setText(note.getContent());

        // Important: Prevents unwanted behaviour (Previous listener would be called!)
        holder.mScbNoteCompleted.setOnCheckedChangeListener(null);

        holder.mScbNoteCompleted.setChecked(note.isCompleted());
        holder.mScbNoteCompleted.setCheckedColor(note.getColor());
        holder.mScbNoteCompleted.setStrokeColor(note.getColor());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mScbNoteCompleted.setChecked(!holder.mScbNoteCompleted.isChecked(), true);
            }
        });

        holder.mScbNoteCompleted.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                mNoteItemListener.onCompleteNoteItemClick(note, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public void replaceData(@NonNull List<Note> notes) {
        setList(notes);
        notifyDataSetChanged();
    }

    public void add(@NonNull Note note) {
        mNotes.add(note);
        notifyDataSetChanged();
    }

    public void removeAll() {
        mNotes.clear();
        notifyDataSetChanged();
    }

    public List<Note> getCompletedNotes() {
        List<Note> completedNotes = new ArrayList<>();
        for(Note n : mNotes) {
            if(n.isCompleted()) completedNotes.add(n);
        }
        return completedNotes;
    }

    public void removeCompletedNotes() {
        mNotes.removeAll(getCompletedNotes());
        notifyDataSetChanged();
    }

    public boolean hasCompletedNote() {
        for(Note n : mNotes) {
            if(n.isCompleted()) return true;
        }
        return false;
    }

    private void setList(@NonNull List<Note> notes) {
        mNotes = notes;
    }

    public interface NoteItemListener {
        void onCompleteNoteItemClick(Note note, boolean completed);
    }
}
