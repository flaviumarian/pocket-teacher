package com.licence.pocketteacher.student.search.search_fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.student.MainPageS;
import com.licence.pocketteacher.student.search.FragmentSearchS;
import com.licence.pocketteacher.adapters.TeachersRecyclerAdapter;
import com.licence.pocketteacher.aiding_classes.Teacher;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class FragmentSearchName extends Fragment {

    private View view;
    private EditText searchET;
    private Button cancelBttn;
    private TextView subjectTV, domainTV, universityTV, infoTV;
    private RecyclerView teachersRV;
    private TeachersRecyclerAdapter teachersRecyclerAdapter;

    public static ArrayList<Teacher> teachers;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_teachers_name, container, false);

        setHasOptionsMenu(true);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        initiateComponents();

        return view;

    }

    private void initiateComponents() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                // Edit Text
                searchET = view.findViewById(R.id.searchET);

                // Button
                cancelBttn = view.findViewById(R.id.cancelBttn);

                // Text Views
                subjectTV = view.findViewById(R.id.subjectTV);
                domainTV = view.findViewById(R.id.domainTV);
                universityTV = view.findViewById(R.id.universityTV);
                infoTV = view.findViewById(R.id.infoTV);

                // Recycler View
                teachersRV = view.findViewById(R.id.teachersRV);

                // Array list
                teachers = HelpingFunctions.getAllTeachers(MainPageS.student.getUsername());

                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            setListeners();

                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }).start();
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
        universityTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new FragmentSearchUniversity();
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
            for (Teacher teacher : teachers) {
                if (teacher.getUsername().toLowerCase().contains(text.toLowerCase()) || teacher.getFirstName().toLowerCase().contains(text.toLowerCase()) || teacher.getLastName().toLowerCase().contains(text.toLowerCase())) {
                    filteredTeachers.add(teacher);
                }
            }

            if(filteredTeachers.size() == 0){

                infoTV.setVisibility(View.VISIBLE);
                infoTV.setText(R.string.message_search_2);
            }
        }

        teachersRecyclerAdapter.filterList(filteredTeachers);
    }
}