package com.licence.pocketteacher.miscellaneous;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.licence.pocketteacher.R;
import com.licence.pocketteacher.aiding_classes.Comment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BottomDeleteCommentDialog extends BottomSheetDialogFragment {

    private BottomDeleteCommentListener bottomDeleteCommentListener;
    private Comment comment;

    public BottomDeleteCommentDialog(Comment comment){
        this.comment = comment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_delete_comment, container, false);

        Button deleteCommentBttn = view.findViewById(R.id.deleteCommentBttn);
        Button cancelBttn = view.findViewById(R.id.cancelBttn);

        deleteCommentBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDeleteCommentListener.onButtonClicked("Delete comment.", comment);
                dismiss();
            }
        });

        cancelBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDeleteCommentListener.onButtonClicked("Cancel.", comment);
                dismiss();
            }
        });

        return view;
    }

    public interface BottomDeleteCommentListener {
        void onButtonClicked(String text, Comment comment);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try{
            bottomDeleteCommentListener = (BottomDeleteCommentListener)context;
        }catch(ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement BottomDeleteCommentLister.");
        }
    }
}