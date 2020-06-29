package com.licence.pocketteacher.teacher.profile.followers.blocked_students;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.adapters.BlockedStudentsRecyclerAdapter;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.aiding_classes.Student;
import com.licence.pocketteacher.teacher.MainPageT;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentBlockedLandingPage extends Fragment {
    private View view;
    private ImageView backIV;
    private ImageView searchIV;
    private RecyclerView studentsRV;
    private BlockedStudentsRecyclerAdapter blockedStudentsRecyclerAdapter;

    public static ArrayList<Student> blockedStudents;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_blocked_landing_page, container, false);

        setHasOptionsMenu(true);
        initiateComponents();

        return view;
    }


    private void initiateComponents(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                // Image View
                backIV = view.findViewById(R.id.backIV);
                searchIV = view.findViewById(R.id.searchIV);

                // Recycle View
                studentsRV = view.findViewById(R.id.studentsRV);

                // Array list
                blockedStudents = HelpingFunctions.getAllBlockedStudents(MainPageT.teacher.getUsername());
                final TextView infoTV = view.findViewById(R.id.infoTV);

                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // Array list
                            if(blockedStudents.size() > 0){
                                infoTV.setVisibility(View.INVISIBLE);
                                searchIV.setVisibility(View.VISIBLE);
                            }else{
                                infoTV.setVisibility(View.VISIBLE);
                                searchIV.setVisibility(View.INVISIBLE);
                            }

                            setListeners();
                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }
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

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!HelpingFunctions.isConnected(view.getContext())){
                    Toast.makeText(view.getContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create new fragment and transaction
                Fragment newFragment = new FragmentBlockedNamePage();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.fragmentContainer, newFragment);
                transaction.addToBackStack("search_name");

                transaction.commit();
            }
        });

        // Recycler View
        if(blockedStudents.size() > 0){
            blockedStudentsRecyclerAdapter = new BlockedStudentsRecyclerAdapter(blockedStudents, view.getContext(), SeeBlocked.fragmentManager);
            studentsRV.setAdapter(blockedStudentsRecyclerAdapter);
            studentsRV.setLayoutManager(new LinearLayoutManager(view.getContext()));
        }

        view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }

}