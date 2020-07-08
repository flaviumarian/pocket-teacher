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
import com.licence.pocketteacher.aiding_classes.Student;
import com.licence.pocketteacher.messaging.MessagingPage;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StudentsStartConversationRecyclerAdapter extends RecyclerView.Adapter<StudentsStartConversationRecyclerAdapter.ViewHolder>{

    private ArrayList<Student> students;
    private Context context;
    private String usernameSender;
    private int senderType;


    public StudentsStartConversationRecyclerAdapter(ArrayList<Student> students, Context context, String usernameSender, int senderType){
        this.students = students;
        this.context = context;
        this.usernameSender = usernameSender;
        this.senderType = senderType;
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

                Intent intent = new Intent(context, MessagingPage.class);
                intent.putExtra("username_sender", usernameSender);
                intent.putExtra("type", senderType);
                intent.putExtra("username_receiver", student.getUsername());
                intent.putExtra("blocked", 0);
                intent.putExtra("image", student.getProfileImageBase64());
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
