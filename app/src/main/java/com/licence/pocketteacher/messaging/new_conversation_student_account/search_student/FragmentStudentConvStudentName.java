package com.licence.pocketteacher.messaging.new_conversation_student_account.search_student;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.aiding_classes.Student;
import com.licence.pocketteacher.adapters.StudentsStartConversationRecyclerAdapter;
import com.licence.pocketteacher.messaging.new_conversation_student_account.FragmentStudentConvLandingPage;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.student.MainPageS;


import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class FragmentStudentConvStudentName extends Fragment {

    private View view;
    private EditText searchET;
    private Button cancelBttn;
    private TextView universityTV, infoTV;
    private RecyclerView studentsRV;
    private StudentsStartConversationRecyclerAdapter studentsStartConversationRecyclerAdapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_followers_name_page, container, false);

        setHasOptionsMenu(true);
        initiateComponents();
        setListeners();

        return view;

    }

    private void initiateComponents() {

        // Edit Text
        searchET = view.findViewById(R.id.searchET);

        // Button
        cancelBttn = view.findViewById(R.id.cancelBttn);

        // Text Views
        universityTV = view.findViewById(R.id.universityTV);
        infoTV = view.findViewById(R.id.infoTV);

        // Recycler View
        studentsRV = view.findViewById(R.id.studentsRV);

    }

    private void setListeners() {

        // Button
        cancelBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Hide keyboard
                try {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(!HelpingFunctions.isConnected(view.getContext())){
                    Toast.makeText(view.getContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create new fragment and transaction
                Fragment newFragment = new FragmentStudentConvLandingPage();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.fragmentContainer, newFragment);
                transaction.addToBackStack("landing_page");

                transaction.commit();
            }
        });


        // Recycler View
        ArrayList<Student> emptyStudents = new ArrayList<>();
        studentsStartConversationRecyclerAdapter = new StudentsStartConversationRecyclerAdapter(emptyStudents, view.getContext(), MainPageS.student.getUsername(), 0);
        studentsRV.setAdapter(studentsStartConversationRecyclerAdapter);
        studentsRV.setLayoutManager(new LinearLayoutManager(view.getContext()));


        // Edit Text
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    infoTV.setVisibility(View.INVISIBLE);
                    filter(s.toString());
                } else {
                    infoTV.setVisibility(View.VISIBLE);
                    infoTV.setText(R.string.message_search_3);
                    filter("No text");
                }
            }
        });

        // Text Views
        universityTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new FragmentStudentConvStudentUniversity();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.fragmentContainer, newFragment);
                transaction.addToBackStack("search_university");

                transaction.commit();
            }
        });
    }


    private void filter(String text) {
        ArrayList<Student> filteredStudents = new ArrayList<>();

        if (!text.equals("No text")) {
            for (Student student : FragmentStudentConvLandingPage.allStudents) {
                if (student.getUsername().toLowerCase().contains(text.toLowerCase()) || student.getFirstName().toLowerCase().contains(text.toLowerCase()) || student.getLastName().toLowerCase().contains(text.toLowerCase())) {
                    filteredStudents.add(student);
                }
            }

            if(filteredStudents.size() == 0){
                infoTV.setVisibility(View.VISIBLE);
                infoTV.setText(R.string.message_search_2);
            }
        }

        studentsStartConversationRecyclerAdapter.filterList(filteredStudents);
    }
}
