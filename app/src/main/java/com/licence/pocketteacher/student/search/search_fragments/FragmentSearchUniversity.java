package com.licence.pocketteacher.student.search.search_fragments;

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
import com.licence.pocketteacher.student.search.FragmentSearchS;
import com.licence.pocketteacher.student.search.TeachersRecyclerAdapter;
import com.licence.pocketteacher.aiding_classes.Teacher;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class FragmentSearchUniversity extends Fragment {

    private View view;
    private EditText searchET;
    private Button cancelBttn;
    private TextView nameTV, subjectTV, domainTV;
    private RecyclerView teachersRV;
    private TeachersRecyclerAdapter teachersRecyclerAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_teachers_university, container, false);

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
        subjectTV = view.findViewById(R.id.subjectTV);
        domainTV = view.findViewById(R.id.domainTV);

        // Recycler View
        teachersRV = view.findViewById(R.id.teachersRV);

    }

    private  void setListeners(){

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
                // Create new fragment and transaction
                Fragment newFragment = new FragmentSearchS();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.fragmentContainer, newFragment);
                transaction.addToBackStack(null);

                transaction.commit();
            }
        });

        // Recycler View
        ArrayList<Teacher> emptyTeachers = new ArrayList<>();
        teachersRecyclerAdapter = new TeachersRecyclerAdapter(emptyTeachers, view.getContext());
        teachersRV.setAdapter(teachersRecyclerAdapter);
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
                Fragment newFragment = new FragmentSearchName();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.fragmentContainer, newFragment);
                transaction.addToBackStack(null);

                transaction.commit();
            }
        });
        subjectTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new FragmentSearchSubject();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.fragmentContainer, newFragment);
                transaction.addToBackStack(null);

                transaction.commit();
            }
        });
        domainTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new FragmentSearchDomain();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.fragmentContainer, newFragment);
                transaction.addToBackStack(null);


                transaction.commit();
            }
        });


    }

    private void filter(String text) {
        ArrayList<Teacher> filteredTeachers = new ArrayList<>();

        if (!text.equals("No text")) {
            for (Teacher teacher : FragmentSearchName.teachers) {
                if (teacher.getUniversity().toLowerCase().contains(text.toLowerCase())) {
                    filteredTeachers.add(teacher);
                }
            }
        }

        teachersRecyclerAdapter.filterList(filteredTeachers);
    }
}