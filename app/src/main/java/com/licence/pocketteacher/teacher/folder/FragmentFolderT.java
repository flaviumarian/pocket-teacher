package com.licence.pocketteacher.teacher.folder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.teacher.MainPageT;
import com.licence.pocketteacher.aiding_classes.Subject;
import com.licence.pocketteacher.teacher.folder.subject.SubjectPage;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class FragmentFolderT extends Fragment {

    private View view;
    private TextView infoTV, subjectsTV;
    private ListView subjectsLV;
    private CardView addRemoveSubjectC, removeSubjectC, addSubjectC;

    private boolean emptyList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_folder_t, container, false);

        initiateComponents();

        return view;
    }

    private void initiateComponents() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                // Text View
                infoTV = view.findViewById(R.id.infoTV);
                subjectsTV = view.findViewById(R.id.subjectsTV);

                // List View
                subjectsLV = view.findViewById(R.id.subjectsLV);

                // Card Views
                addRemoveSubjectC = view.findViewById(R.id.addRemoveSubjectC);
                removeSubjectC = view.findViewById(R.id.removeSubjectC);
                addSubjectC = view.findViewById(R.id.addSubjectC);

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

        // List View
        ArrayList<String> subjectNames = new ArrayList<>();
        ArrayList<String> domainNames = new ArrayList<>();


        for (Subject subject : MainPageT.teacher.getSubjects()) {
            subjectNames.add(subject.getSubjectName());
            domainNames.add(subject.getDomainName());
        }

        if (subjectNames.size() == 0) {
            infoTV.setVisibility(View.VISIBLE);
            subjectsTV.setVisibility(View.INVISIBLE);
            emptyList = true;
        } else {
            infoTV.setVisibility(View.INVISIBLE);
            subjectsTV.setVisibility(View.VISIBLE);
            emptyList = false;
        }


        SubjectsAdapter subjectsAdapter = new SubjectsAdapter(view.getContext(), subjectNames, domainNames);
        subjectsLV.setAdapter(subjectsAdapter);
        HelpingFunctions.setListViewHeightBasedOnChildren(subjectsLV);


        // Card Views
        addRemoveSubjectC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;


                // SHOW
                if (addSubjectC.getAlpha() == 0) {
                    if(!emptyList) {
                        removeSubjectC.animate().translationYBy(-width/3).alpha(1).setDuration(300);
                    }
                    addSubjectC.animate().translationXBy(-width/3).alpha(1).setDuration(300);
                    return;
                }

                // HIDE
                if (addSubjectC.getAlpha() == 1) {
                    if(!emptyList){
                        removeSubjectC.animate().translationYBy(width/3).alpha(0).setDuration(300);
                    }
                    addSubjectC.animate().translationXBy(width/3).alpha(0).setDuration(300);
                }
            }
        });

        removeSubjectC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), RemoveSubject.class);
                startActivityForResult(intent, 1);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });

        addSubjectC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), AddSubject.class);
                startActivityForResult(intent, 0);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });

        view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

    }


    /*                                   *** A D A P T O R  ***                                   */
    class SubjectsAdapter extends BaseAdapter {

        private ArrayList<String> subjectNames, domainNames;
        private LayoutInflater inflater;


        SubjectsAdapter(Context context, ArrayList<String> subjectNames, ArrayList<String> domainNames) {
            inflater = LayoutInflater.from(context);
            this.subjectNames = subjectNames;
            this.domainNames = domainNames;
        }


        @Override
        public int getCount() {
            return subjectNames.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_custom_subject_display, null);

                TextView subjectTV = convertView.findViewById(R.id.subjectTV);
                TextView domainTV = convertView.findViewById(R.id.domainTV);
                ImageView endIV = convertView.findViewById(R.id.endIV);

                subjectTV.setText(subjectNames.get(position));
                domainTV.setText(domainNames.get(position));
                endIV.setImageResource(R.drawable.ic_navigate_next_black_24dp);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), SubjectPage.class);
                    intent.putExtra("subjectName", MainPageT.teacher.getSubjects().get(position).getSubjectName());
                    startActivityForResult(intent, 2);
                    getActivity().overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                }
            });

            return convertView;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            addRemoveSubjectC.performClick();

            if (resultCode == Activity.RESULT_OK) {

                emptyList = false;
                infoTV.setVisibility(View.INVISIBLE);
                subjectsTV.setVisibility(View.VISIBLE);

                ArrayList<String> subjectNames = new ArrayList<>();
                ArrayList<String> domainNames = new ArrayList<>();

                for (Subject subject : MainPageT.teacher.getSubjects()) {
                    subjectNames.add(subject.getSubjectName());
                    domainNames.add(subject.getDomainName());
                }


                SubjectsAdapter subjectsAdapter = new SubjectsAdapter(view.getContext(), subjectNames, domainNames);
                subjectsLV.setAdapter(subjectsAdapter);
                HelpingFunctions.setListViewHeightBasedOnChildren(subjectsLV);
            }
        }

        if (requestCode == 1) {

            addRemoveSubjectC.performClick();

            if (resultCode == Activity.RESULT_OK) {

                ArrayList<String> subjectNames = new ArrayList<>();
                ArrayList<String> domainNames = new ArrayList<>();

                for (Subject subject : MainPageT.teacher.getSubjects()) {
                    subjectNames.add(subject.getSubjectName());
                    domainNames.add(subject.getDomainName());
                }

                if (subjectNames.size() == 0) {
                    infoTV.setVisibility(View.VISIBLE);
                    subjectsTV.setVisibility(View.INVISIBLE);
                    emptyList = true;
                } else {
                    infoTV.setVisibility(View.INVISIBLE);
                    subjectsTV.setVisibility(View.VISIBLE);
                    emptyList = false;
                }


                SubjectsAdapter subjectsAdapter = new SubjectsAdapter(view.getContext(), subjectNames, domainNames);
                subjectsLV.setAdapter(subjectsAdapter);
                HelpingFunctions.setListViewHeightBasedOnChildren(subjectsLV);
            }

        }

        if (requestCode == 2) {
            if (addSubjectC.getAlpha() > 0) {
                addRemoveSubjectC.performClick();
            }
        }


    }

    @Override
    public void onResume() {
        super.onResume();

        // Notification Badge
        MainPageT.resetBadge();
    }
}
