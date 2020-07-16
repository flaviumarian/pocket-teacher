package com.licence.pocketteacher.student.profile.following;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.student.MainPageS;
import com.licence.pocketteacher.adapters.TeachersRecyclerAdapter;
import com.licence.pocketteacher.aiding_classes.Teacher;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentFollowingLandingPage extends Fragment {

    private View view;
    private ImageView backIV;
    private ImageView searchIV;
    private RecyclerView teachersRV;
    private TeachersRecyclerAdapter teachersRecyclerAdapter;

    public static ArrayList<Teacher> followingTeachers;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_following_landing_page, container, false);

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
                searchIV = view.findViewById(R.id.searchIV);


                // Recycle View
                teachersRV = view.findViewById(R.id.teachersRV);

                // Array List
                followingTeachers = HelpingFunctions.getAllFollowingTeachers(MainPageS.student.getUsername());
                final TextView infoTV = view.findViewById(R.id.infoTV);



                try{
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // Array List
                            if(followingTeachers.size() > 0){
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
                // Create new fragment and transaction
                Fragment newFragment = new FragmentFollowingNamePage();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.fragmentContainer, newFragment);
                transaction.addToBackStack("search_name");

                transaction.commit();
            }
        });

        // Recycler View
        if(followingTeachers.size() > 0){
            teachersRecyclerAdapter = new TeachersRecyclerAdapter(followingTeachers, view.getContext());
            teachersRV.setAdapter(teachersRecyclerAdapter);
            teachersRV.setLayoutManager(new LinearLayoutManager(view.getContext()));
        }

        view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }
}