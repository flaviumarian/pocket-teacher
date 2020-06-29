package com.licence.pocketteacher.teacher.updates.follow_requests;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.adapters.StudentsRecyclerAdapter;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;



public class SwipeToApproveOrDenyRequest extends ItemTouchHelper.SimpleCallback {

    private StudentsRecyclerAdapter studentsRecyclerAdapter;

    public SwipeToApproveOrDenyRequest(StudentsRecyclerAdapter studentsRecyclerAdapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.studentsRecyclerAdapter = studentsRecyclerAdapter;

    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        int position = viewHolder.getAdapterPosition();
        studentsRecyclerAdapter.deleteItem(position, direction);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        final ColorDrawable backgroundRed = new ColorDrawable(Color.RED);
        final ColorDrawable backgroundGreen = new ColorDrawable(Color.GREEN);
        Drawable iconSwipeRight = ContextCompat.getDrawable(studentsRecyclerAdapter.getContext(), R.drawable.ic_add_white_36dp);
        Drawable iconSwipeLeft = ContextCompat.getDrawable(studentsRecyclerAdapter.getContext(), R.drawable.ic_delete_white_36dp);



        if(dX > 0){
            // Swipe Right
            backgroundGreen.setBounds(itemView.getLeft(), itemView.getTop(),   itemView.getLeft() + (int)dX, itemView.getBottom());
            backgroundGreen.draw(c);

            // Icon
            int iconMargin = (itemView.getHeight() - iconSwipeRight.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - iconMargin - iconSwipeRight.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop  + iconMargin + iconSwipeRight.getIntrinsicHeight();
            int iconLeft = (itemView.getLeft() + iconSwipeRight.getIntrinsicWidth())/2;
            int iconRight = itemView.getLeft() + iconMargin + iconSwipeRight.getIntrinsicWidth();
            iconSwipeRight.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            iconSwipeRight.draw(c);



        } else if(dX < 0){
            // Swipe Left

            // Background
            backgroundRed.setBounds(itemView.getLeft() + (int)dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            backgroundRed.draw(c);

            // Icon
            int iconMargin = (itemView.getHeight() - iconSwipeLeft.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - iconSwipeLeft.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + iconSwipeLeft.getIntrinsicHeight();
            int iconLeft = itemView.getRight() - iconMargin - iconSwipeLeft.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            iconSwipeLeft.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            iconSwipeLeft.draw(c);


        } else{
            // No swipe
        }
    }

}

