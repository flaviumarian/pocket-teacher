package com.licence.pocketteacher.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.aiding_classes.Teacher;
import com.licence.pocketteacher.student.search.SeeTeacher;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TeachersRecyclerAdapter extends RecyclerView.Adapter<TeachersRecyclerAdapter.ViewHolder> {
    private ArrayList<Teacher> teachers;
    private Context context;

    public TeachersRecyclerAdapter(ArrayList<Teacher> teachers, Context context){
        this.teachers = teachers;
        this.context = context;
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

        final Teacher teacher = teachers.get(position);

        // Profile image
        if(teacher.getProfileImageBase64().equals("")){
            switch(teacher.getGender()){
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
            holder.profileImageIV.setImageBitmap(HelpingFunctions.convertBase64toImage(teacher.getProfileImageBase64()));
        }

        holder.usernameTV.setText(teacher.getUsername());
        if(teacher.getUniversity().equals("")){
            holder.universityTV.setText("");
        } else {
            holder.universityTV.setText(teacher.getUniversity());
        }
        if(teacher.getFirstName().equals("") || teacher.getLastName().equals("")){
            holder.nameTV.setText(R.string.message_unknown_name);
        }else{
            String name = teacher.getFirstName() + " " + teacher.getLastName();
            holder.nameTV.setText(name);
        }

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!HelpingFunctions.isConnected(context)){
                    Toast.makeText(context, "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(context, SeeTeacher.class);
                intent.putExtra("username", holder.usernameTV.getText().toString());
                v.getContext().startActivity(intent);
                ((Activity)context).overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });


    }

    @Override
    public int getItemCount() {
        return teachers.size();
    }

    public void filterList(ArrayList<Teacher> filteredTeachers){
        teachers = filteredTeachers;
        notifyDataSetChanged();
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
