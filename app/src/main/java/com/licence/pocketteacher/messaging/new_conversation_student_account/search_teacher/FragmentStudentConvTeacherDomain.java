package com.licence.pocketteacher.messaging.new_conversation_student_account.search_teacher;

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
import com.licence.pocketteacher.aiding_classes.Teacher;
import com.licence.pocketteacher.adapters.TeachersStartConversationRecyclerAdapter;
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

public class FragmentStudentConvTeacherDomain extends Fragment {

    private View view;
    private EditText searchET;
    private Button cancelBttn;
    private TextView nameTV, subjectTV, universityTV, infoTV;
    private RecyclerView teachersRV;
    private TeachersStartConversationRecyclerAdapter teachersStartConversationRecyclerAdapter;




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_teachers_domain, container, false);

        initiateComponents();
        setListeners();

        return view;
    }


    private void initiateComponents(){

        // Edit Text
        searchET = view.findViewById(R.id.searchET);

        // Button
        cancelBttn = view.findViewById(R.id.cancelBttn);

        // Text Views
        nameTV = view.findViewById(R.id.nameTV);
        subjectTV = view.findViewById(R.id.subjectTV);
        universityTV = view.findViewById(R.id.universityTV);
        infoTV = view.findViewById(R.id.infoTV);

        // Recycler View
        teachersRV = view.findViewById(R.id.teachersRV);

    }

    private void setListeners(){

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
        ArrayList<Teacher> emptyTeachers = new ArrayList<>();
        teachersStartConversationRecyclerAdapter = new TeachersStartConversationRecyclerAdapter(emptyTeachers, view.getContext(), MainPageS.student.getUsername(), 0);
        teachersRV.setAdapter(teachersStartConversationRecyclerAdapter);
        teachersRV.setLayoutManager(new LinearLayoutManager(view.getContext()));


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
        nameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new FragmentStudentConvTeacherName();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.fragmentContainer, newFragment);
                transaction.addToBackStack("search_name");

                transaction.commit();
            }
        });
        subjectTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new FragmentStudentConvTeacherSubject();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.fragmentContainer, newFragment);
                transaction.addToBackStack("search_subject");

                transaction.commit();
            }
        });
        universityTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new FragmentStudentConvTeacherUniversity();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.fragmentContainer, newFragment);
                transaction.addToBackStack("search_university");

                transaction.commit();
            }
        });

    }


    private void filter(String text) {
        ArrayList<Teacher> filteredTeachers = new ArrayList<>();

        if (!text.equals("No text")) {
            for (Teacher teacher : FragmentStudentConvLandingPage.allTeachers) {
                for(String domain: teacher.getDomains()){
                    if(domain.toLowerCase().contains(text.toLowerCase())){
                        filteredTeachers.add(teacher);
                    }
                }
            }

            if(filteredTeachers.size() == 0){

                infoTV.setVisibility(View.VISIBLE);
                infoTV.setText(R.string.message_search_2);
            }
        }

        teachersStartConversationRecyclerAdapter.filterList(filteredTeachers);
    }

}
