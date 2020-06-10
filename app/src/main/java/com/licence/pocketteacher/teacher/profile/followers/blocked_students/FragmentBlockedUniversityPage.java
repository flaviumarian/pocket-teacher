package com.licence.pocketteacher.teacher.profile.followers.blocked_students;

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

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.aiding_classes.Student;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class FragmentBlockedUniversityPage extends Fragment {
    private View view;
    private EditText searchET;
    private Button cancelBttn;
    private TextView nameTV;
    private RecyclerView studentsRV;
    private BlockedStudentsRecyclerAdapter blockedStudentsRecyclerAdapter;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_blocked_university_page, container, false);

        setHasOptionsMenu(true);
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

        // Recycler View
        studentsRV = view.findViewById(R.id.studentsRV);

    }

    private  void setListeners(){

        // Button
        cancelBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Create new fragment and transaction
                Fragment newFragment = new FragmentBlockedLandingPage();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.fragmentContainer, newFragment);
                transaction.addToBackStack("landing_page");

                transaction.commit();
            }
        });

        // Recycler View
        ArrayList<Student> emptyStudents = new ArrayList<>();
        blockedStudentsRecyclerAdapter = new BlockedStudentsRecyclerAdapter(emptyStudents, view.getContext(), SeeBlocked.fragmentManager);
        studentsRV.setAdapter(blockedStudentsRecyclerAdapter);
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
                    filter(s.toString());
                } else {
                    filter("No text");
                }
            }
        });

        // Text Views
        nameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new FragmentBlockedNamePage();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.fragmentContainer, newFragment);
                transaction.addToBackStack("search_name");

                transaction.commit();
            }
        });
    }

    private void filter(String text) {
        ArrayList<Student> filteredStudents = new ArrayList<>();

        if (!text.equals("No text")) {
            for (Student student : FragmentBlockedLandingPage.blockedStudents) {
                if (student.getUniversity().toLowerCase().contains(text.toLowerCase())) {
                    filteredStudents.add(student);
                }
            }
        }

        blockedStudentsRecyclerAdapter.filterList(filteredStudents);
    }
}
