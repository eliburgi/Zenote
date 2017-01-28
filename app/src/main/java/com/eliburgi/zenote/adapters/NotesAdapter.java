package com.eliburgi.zenote.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eliburgi.zenote.R;
import com.eliburgi.zenote.customviews.SmoothCheckBox;
import com.eliburgi.zenote.models.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elias on 14.01.2017.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
    private Context mContext;
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

    public NotesAdapter(Context context, @NonNull List<Note> notes, @Nullable NoteItemListener noteItemListener) {
        mContext = context.getApplicationContext();
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

        if(holder.mScbNoteCompleted.isChecked()) {
            // Strikethrough
            holder.mTvNoteContent.setPaintFlags(holder.mTvNoteContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mTvNoteContent.setTextColor(ContextCompat.getColor(mContext, R.color.completed_note_title_color));
        } else {
            holder.mTvNoteContent.setPaintFlags(holder.mTvNoteContent.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mTvNoteContent.setTextColor(ContextCompat.getColor(mContext, R.color.normal_note_title_color));
        }

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
        //notifyDataSetChanged();
        notifyItemRangeInserted(0, notes.size());
    }

    public void add(@NonNull Note note) {
        mNotes.add(note);
        //notifyDataSetChanged();
        notifyItemInserted(mNotes.size() - 1);
    }

    public void addAll(@NonNull List<Note> notes) {
        for(Note n : notes) {
            mNotes.add(n);
        }
        notifyDataSetChanged();
    }

    public void remove(@NonNull Note note) {
        int index = mNotes.indexOf(note);
        mNotes.remove(note);
        notifyItemRemoved(index);
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
        List<Note> removedNotes = getCompletedNotes();
        int size = removedNotes.size();
        for(Note n : removedNotes) {
            int index = mNotes.indexOf(n);
            mNotes.remove(n);
            notifyItemRemoved(index);
        }
        //mNotes.removeAll(getCompletedNotes());
        //notifyDataSetChanged();
    }

    public boolean hasCompletedNote() {
        for(Note n : mNotes) {
            if(n.isCompleted()) return true;
        }
        return false;
    }

    public void sortNotes() {

    }

    public void sortUncompletedNotes() {

    }

    public void sortCompletedNotes() {

    }

    private void setList(@NonNull List<Note> notes) {
        mNotes = notes;
    }

    public interface NoteItemListener {
        void onCompleteNoteItemClick(Note note, boolean completed);
    }
}
