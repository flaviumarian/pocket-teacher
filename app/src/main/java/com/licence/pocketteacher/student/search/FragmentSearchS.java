package com.licence.pocketteacher.student.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.adapters.TeachersRecyclerAdapter;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.student.MainPageS;
import com.licence.pocketteacher.student.search.search_fragments.FragmentSearchName;
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

public class FragmentSearchS extends Fragment {

    private View view;
    private TextView recommendedTV;
    private ImageView searchIV;
    private RecyclerView teachersRV;
    private TeachersRecyclerAdapter teachersRecyclerAdapter;
    private ArrayList<Teacher> premiumTeachers;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_s, container, false);

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
                searchIV = view.findViewById(R.id.searchIV);

                // Text View
                recommendedTV = view.findViewById(R.id.recommendedTV);

                // Recycle View
                teachersRV = view.findViewById(R.id.teachersRV);

                // Array list
                premiumTeachers = HelpingFunctions.getAllPremiumTeachers(MainPageS.student.getUsername());

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

    private void setListeners(){

        // Image View
        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!HelpingFunctions.isConnected(view.getContext())){
                    Toast.makeText(view.getContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create new fragment and transaction
                Fragment newFragment = new FragmentSearchName();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.fragmentContainer, newFragment, "SEARCH_NAME");
                transaction.addToBackStack(null);

                transaction.commit();
            }
        });

        // Recycler View
        if(premiumTeachers.size() > 0){
            recommendedTV.setVisibility(View.VISIBLE);
            teachersRecyclerAdapter = new TeachersRecyclerAdapter(premiumTeachers, view.getContext());
        }


        teachersRV.setAdapter(teachersRecyclerAdapter);
        teachersRV.setLayoutManager(new LinearLayoutManager(view.getContext()));

        view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Notification Badge
        MainPageS.resetBadge();
    }
}