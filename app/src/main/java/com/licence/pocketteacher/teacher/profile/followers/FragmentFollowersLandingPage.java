package com.licence.pocketteacher.teacher.profile.followers;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.aiding_classes.Student;
import com.licence.pocketteacher.teacher.MainPageT;
import com.licence.pocketteacher.teacher.profile.followers.blocked_students.SeeBlocked;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentFollowersLandingPage extends Fragment {
    private View view;
    private ImageView backIV;
    private ImageView searchIV;
    private Button goPremiumBttn;
    private RecyclerView teachersRV;
    private StudentsRecyclerAdapter studentsRecyclerAdapter;

    public static ArrayList<Student> followers;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_followers_landing_page, container, false);

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

                // Array list
                followers = HelpingFunctions.getAllFollowers(MainPageT.teacher.getUsername());
                final TextView infoTV = view.findViewById(R.id.infoTV);
                final LinearLayout linearLayout = view.findViewById(R.id.linearLayout);

                // Button
                goPremiumBttn = view.findViewById(R.id.goPremiumBttn);

                try{
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // Array list
                            if(followers.size() > 0){
                                infoTV.setVisibility(View.INVISIBLE);
                                linearLayout.setVisibility(View.INVISIBLE);
                                searchIV.setVisibility(View.VISIBLE);
                            }else{
                                infoTV.setVisibility(View.VISIBLE);
                                linearLayout.setVisibility(View.VISIBLE);
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
                Fragment newFragment = new FragmentFollowersNamePage();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.fragmentContainer, newFragment);
                transaction.addToBackStack("search_name");

                transaction.commit();
            }
        });

        // Recycler View
        if(followers.size() > 0){
            studentsRecyclerAdapter = new StudentsRecyclerAdapter(followers, view.getContext());
            teachersRV.setAdapter(studentsRecyclerAdapter);
            teachersRV.setLayoutManager(new LinearLayoutManager(view.getContext()));
        }

        // Button
        goPremiumBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), GoPremium.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });
    }



    /*                      *** T O O L B A R    M E N U ***                         */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_followers, menu);

        MenuItem item = menu.getItem(0).getSubMenu().getItem(0);
        SpannableString s = new SpannableString("See blocked");
        s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);
        item.setTitle(s);

        item = menu.getItem(0).getSubMenu().getItem(1);
        s = new SpannableString("Privacy");
        s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);
        item.setTitle(s);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.seeBlocked:
                Intent intent = new Intent(view.getContext(), SeeBlocked.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                break;
            case R.id.privacy:
                String privacy;
                if(MainPageT.teacher.getPrivacy().equals("0")){
                    privacy = "public";
                }else{
                    privacy = "private";
                }
                Snackbar.make(view, "Your account is currently " + privacy + ". If you wish to change that, go to settings.", Snackbar.LENGTH_SHORT).show();

                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
