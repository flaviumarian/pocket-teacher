package com.licence.pocketteacher.messaging.new_conversation_student_account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.aiding_classes.Student;
import com.licence.pocketteacher.aiding_classes.Teacher;
import com.licence.pocketteacher.adapters.TeachersStartConversationRecyclerAdapter;
import com.licence.pocketteacher.messaging.new_conversation_student_account.search_student.FragmentStudentConvStudentName;
import com.licence.pocketteacher.messaging.new_conversation_student_account.search_teacher.FragmentStudentConvTeacherName;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.student.MainPageS;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentStudentConvLandingPage extends Fragment {

    private View view;
    private ImageView backIV;
    private RecyclerView teachersRV;
    private Button studentBttn, teacherBttn;
    private TeachersStartConversationRecyclerAdapter teachersStartConversationRecyclerAdapter;

    private boolean noFollowers = false;

    public static ArrayList<Teacher> displayedTeachers;
    public static ArrayList<Student> allStudents;
    public static ArrayList<Teacher> allTeachers;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_conversation_student_landing_page, container, false);

        setHasOptionsMenu(true);
        initiateComponents();

        return view;
    }

    private void initiateComponents(){

        // Toolbar
        Toolbar profileToolbar = view.findViewById(R.id.searchToolbar);
        profileToolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(profileToolbar);

        new Thread(new Runnable() {
            @Override
            public void run() {

                // Image View
                backIV = view.findViewById(R.id.backIV);

                // Buttons
                studentBttn = view.findViewById(R.id.studentBttn);
                teacherBttn = view.findViewById(R.id.teacherBttn);

                // Recycle View
                teachersRV = view.findViewById(R.id.teachersRV);

                // Array List
                displayedTeachers = HelpingFunctions.getAllFollowingTeachers(MainPageS.student.getUsername());
                if(displayedTeachers.size() == 0){

                    // get premium teachers
                    displayedTeachers = HelpingFunctions.getAllPremiumTeachers(MainPageS.student.getUsername());

                    noFollowers = true;
                }


                final TextView infoTV = view.findViewById(R.id.infoTV);

                try{
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // Array List
                            if(noFollowers){
                                infoTV.setText(R.string.message_search_1);
                            }else{
                                infoTV.setText(R.string.message_user_profile_4);
                            }

                            setListeners();
                        }
                    });

                }catch(Exception e){
                    e.printStackTrace();
                }


                // Get all students and all teachers
                allStudents = HelpingFunctions.getAllStudentsExceptFor(MainPageS.student.getUsername());
                allTeachers = HelpingFunctions.getAllTeachers(MainPageS.student.getUsername());




            }
        }).start();

    }

    private void setListeners(){

        // Image View
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        // Buttons
        studentBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!HelpingFunctions.isConnected(view.getContext())){
                    Toast.makeText(view.getContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create new fragment and transaction
                Fragment newFragment = new FragmentStudentConvStudentName();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.fragmentContainer, newFragment);
                transaction.addToBackStack("search_name");

                transaction.commit();
            }
        });

        teacherBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!HelpingFunctions.isConnected(view.getContext())){
                    Toast.makeText(view.getContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create new fragment and transaction
                Fragment newFragment = new FragmentStudentConvTeacherName();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.fragmentContainer, newFragment);
                transaction.addToBackStack("search_name");

                transaction.commit();
            }
        });


        // Recycler View
        teachersStartConversationRecyclerAdapter = new TeachersStartConversationRecyclerAdapter(displayedTeachers, view.getContext(), MainPageS.student.getUsername(), 0);
        teachersRV.setAdapter(teachersStartConversationRecyclerAdapter);
        teachersRV.setLayoutManager(new LinearLayoutManager(view.getContext()));


        view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }
}
