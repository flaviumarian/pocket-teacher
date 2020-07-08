package com.licence.pocketteacher.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.aiding_classes.Student;
import com.licence.pocketteacher.teacher.MainPageT;
import com.licence.pocketteacher.teacher.profile.followers.SeeStudent;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StudentsRecyclerAdapter extends RecyclerView.Adapter<StudentsRecyclerAdapter.ViewHolder> {

    private ArrayList<Student> students;
    private Student recentlyDeletedStudent;
    private int recentlyDeletedStudentPosition;
    private Context context;
    private View view;


    // for followers
    public StudentsRecyclerAdapter(ArrayList<Student> students, Context context){
        this.students = students;
        this.context = context;
    }

    // for follow requests
    public StudentsRecyclerAdapter(ArrayList<Student> students, Context context, View view){
        this.students = students;
        this.context = context;
        this.view = view;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_searched_teacher, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Student student = students.get(position);

        // Profile image
        if(student.getProfileImageBase64().equals("")){
            switch(student.getGender()){
                case "0":
                    holder.profileImageIV.setImageResource(R.drawable.profile_picture_male);
                    break;
                case "1":
                    holder.profileImageIV.setImageResource(R.drawable.profile_picture_female);
                    break;
                case "2":
                    holder.profileImageIV.setImageResource(R.drawable.profile_picture_neutral);
                    break;
            }
        }else{
            holder.profileImageIV.setImageBitmap(HelpingFunctions.convertBase64toImage(student.getProfileImageBase64()));
        }

        holder.usernameTV.setText(student.getUsername());
        if(student.getUniversity().equals("")){
            holder.universityTV.setText("");
        } else {
            holder.universityTV.setText(student.getUniversity());
        }
        if(student.getFirstName().equals("") || student.getLastName().equals("")){
            holder.nameTV.setText(R.string.message_unknown_name);
        }else{
            String name = student.getFirstName() + " " + student.getLastName();
            holder.nameTV.setText(name);
        }

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!HelpingFunctions.isConnected(context)){
                    Toast.makeText(context, "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(context, SeeStudent.class);
                intent.putExtra("username", holder.usernameTV.getText().toString());
                v.getContext().startActivity(intent);
                ((Activity)context).overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });

    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public void filterList(ArrayList<Student> filteredStudents){
        students = filteredStudents;
        notifyDataSetChanged();
    }

    public Context getContext(){ return context; }

    public void deleteItem(int position, int direction) {

        if(!HelpingFunctions.isConnected(context)){
            Toast.makeText(context, "An internet connection is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        recentlyDeletedStudent = students.get(position);
        recentlyDeletedStudentPosition = position;
        students.remove(position);
        notifyItemRemoved(position);
        showUndoSnackbar(direction);

        if(direction == 4){
            // delete
            HelpingFunctions.declineRequest(recentlyDeletedStudent.getUsername(), MainPageT.teacher.getUsername());

        }else{
            // follow
            String result = HelpingFunctions.approveRequest(recentlyDeletedStudent.getUsername(), MainPageT.teacher.getUsername());
            if(result.equals("Data inserted.")){
                HelpingFunctions.sendNotificationToStudents(recentlyDeletedStudent.getUsername(), MainPageT.teacher.getUsername(), "", "", "", "Has approved your follow request.");
//                HelpingFunctions.sendNotification(recentlyDeletedStudent.getUsername(), MainPageT.teacher.getUsername() + " has approved your follow request.");
            }
        }
        if(students.size() == 0){
            TextView infoTV = view.findViewById(R.id.infoTV);
            infoTV.setText(R.string.message_follow_requests_2);
        }
    }

    private void showUndoSnackbar(final int direction) {
        Snackbar snackbar = Snackbar.make(view, "Want to undo?", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(students.size() == 0){
                    TextView infoTV = view.findViewById(R.id.infoTV);
                    infoTV.setText(R.string.message_follow_requests_1);
                }
                students.add(recentlyDeletedStudentPosition, recentlyDeletedStudent);
                notifyItemInserted(recentlyDeletedStudentPosition);
                if(direction == 4){
                    // undo delete
                    HelpingFunctions.requestFollowTeacher(recentlyDeletedStudent.getUsername(), MainPageT.teacher.getUsername());
                } else{
                    // undo follow
                    HelpingFunctions.undoApprovalRequest(recentlyDeletedStudent.getUsername(), MainPageT.teacher.getUsername());
                }
            }
        });
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }

    public void setStudents(ArrayList<Student> students){
        this.students = students;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImageIV;
        TextView usernameTV;
        TextView nameTV;
        TextView universityTV;
        RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImageIV = itemView.findViewById(R.id.profileImageIV);
            usernameTV = itemView.findViewById(R.id.usernameTV);
            nameTV = itemView.findViewById(R.id.nameTV);
            universityTV = itemView.findViewById(R.id.universityTV);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }
}
