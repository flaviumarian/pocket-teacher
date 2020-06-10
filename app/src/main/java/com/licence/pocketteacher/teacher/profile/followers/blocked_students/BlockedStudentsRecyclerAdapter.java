package com.licence.pocketteacher.teacher.profile.followers.blocked_students;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.aiding_classes.Student;
import com.licence.pocketteacher.teacher.MainPageT;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

public class BlockedStudentsRecyclerAdapter extends RecyclerView.Adapter<BlockedStudentsRecyclerAdapter.ViewHolder>{

    private ArrayList<Student> students;
    private Context context;
    private FragmentManager fragmentManager;
    private Dialog unblockPopup, unblockDonePopup;


    public BlockedStudentsRecyclerAdapter(ArrayList<Student> students, Context context, FragmentManager fragmentManager){
        this.students = students;
        this.context = context;
        this.fragmentManager = fragmentManager;
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
                    holder.profileImageIV.setImageResource(0);
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

                unblockPopup = new Dialog(context);
                unblockPopup.setContentView(R.layout.popup_unblock_student);

                ImageView closePopupIV = unblockPopup.findViewById(R.id.closePopupIV);
                closePopupIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        unblockPopup.dismiss();
                    }
                });

                unblockPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                unblockPopup.show();

                Button yesBttn = unblockPopup.findViewById(R.id.yesBttn);
                yesBttn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Block user

                        String result = HelpingFunctions.unblockUser(MainPageT.teacher.getUsername(), holder.usernameTV.getText().toString());
                        unblockPopup.dismiss();
                        if(result.equals("User unblocked.")){
                            unblockDonePopup = new Dialog(context);
                            unblockDonePopup.setContentView(R.layout.popup_unblock_student_done);

                            ImageView closePopupIV = unblockDonePopup.findViewById(R.id.closePopupIV);
                            closePopupIV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    unblockDonePopup.dismiss();
                                }
                            });
                            unblockDonePopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            unblockDonePopup.show();

                            unblockDonePopup.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    FragmentTransaction transaction = fragmentManager.beginTransaction();

                                    transaction.replace(R.id.fragmentContainer, new FragmentBlockedLandingPage());
                                    transaction.addToBackStack("landing_page");
                                    transaction.commit();

                                }
                            });
                        }
                    }
                });
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
