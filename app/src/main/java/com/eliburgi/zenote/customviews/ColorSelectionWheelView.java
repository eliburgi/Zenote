package com.eliburgi.zenote.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.eliburgi.zenote.R;

import java.util.Arrays;

/**
 * Created by Elias on 22.01.2017.
 */

public class ColorSelectionWheelView extends HorizontalScrollView implements SelectableColorView.OnSelectedChangeListener {
    private View mRootView;
    private LinearLayout mLinearLayout;
    private int mCurrentlySelectedIndex = 0;
    private int mColors[];

    // Flag indicating if the view has to recreated
    private boolean dirty = false;

    public ColorSelectionWheelView(Context context) {
        super(context);
        init(context);
    }

    public ColorSelectionWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ColorSelectionWheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ColorSelectionWheelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @Override
    public void onSelectedChanged(SelectableColorView selectableColorView, boolean isSelected) {
        if(isSelected) {
            int index = mLinearLayout.indexOfChild(selectableColorView);
            select(index);
        }
    }

    public void create(@NonNull int[] colors, int itemSize, int margin) {
        // Recreate the view if new colors
        mColors = Arrays.copyOf(colors, colors.length);
        createColorWheel(itemSize, margin);
        getSelectedView().setChecked(true, false);
    }

    public void selectItem(int index) {
        if(index < 0 || index >= mLinearLayout.getChildCount()) {
            throw new IndexOutOfBoundsException("Selected index is out of bounds!");
        }
        select(index);
    }

    public int getSelectedIndex() {
        return mCurrentlySelectedIndex;
    }

    public SelectableColorView getSelectedView() {
        return (SelectableColorView) mLinearLayout.getChildAt(mCurrentlySelectedIndex);
    }

    public int getSelectedColor() {
        return mColors[mCurrentlySelectedIndex];
    }

    private void select(int index) {
        if(index != mCurrentlySelectedIndex) {
            // Uncheck currently selected view
            getSelectedView().setChecked(false, true);
            getSelectedView().setClickable(true);
            // Set new index
            mCurrentlySelectedIndex = index;
            getSelectedView().setClickable(false);
        }
    }

    private void init(Context context) {
        mRootView = inflate(context, R.layout.color_selection_wheel, this);
        mLinearLayout = (LinearLayout) mRootView.findViewById(R.id.csw_ll);
    }

    private void createColorWheel(int iSize, int margin) {
        // remove all views from the linear layout
        mLinearLayout.removeAllViews();

        // Iterate over all colors
        for(int i = 0; i < mColors.length; i++) {
            // Create a new SelectableColorView
            SelectableColorView scv = new SelectableColorView(getContext());

            // Set the views size in pixels (parent layout is HorizontalScrollView)
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(iSize, iSize);

            // Don´t add a right margin to the last view in the list
            if(i == mColors.length - 1) {
                margin = 0;
            }
            // Set margins of the view
            layoutParams.setMargins(0, 0, margin, margin);
            layoutParams.setMarginEnd(margin);
            scv.setLayoutParams(layoutParams);

            // Set color attributes of the view
            int color = mColors[i];
            scv.setCheckedColor(color);
            scv.setStrokeColor(color);
            scv.setOnSelectedChangeListener(this);
            // Add the view to the horizontal scroll view
            mLinearLayout.addView(scv);
        }
    }
}
