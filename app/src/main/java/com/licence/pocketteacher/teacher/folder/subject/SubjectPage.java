package com.licence.pocketteacher.teacher.folder.subject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.teacher.MainPageT;
import com.licence.pocketteacher.aiding_classes.Subject;
import com.licence.pocketteacher.teacher.folder.subject.files.FilesPage;

import java.util.ArrayList;
import java.util.Collections;

public class SubjectPage extends AppCompatActivity {

    private ImageView backIV;
    private TextView infoTV, foldersTV;
    private ListView foldersLV;
    private CardView addRemoveFolderC, removeFolderC, addFolderC;
    public static String subjectName;
    public static ArrayList<String> folders;
    private Subject popupSubject;
    private Dialog subjectDetailsPopup, domainDetailsPopup, reportPopup, reportSentPopup;

    private boolean emptyList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_page);

        getSubjectIntent();
        initiateComponents();
        setListeners();

    }

    private void getSubjectIntent() {

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            subjectName = bundle.getString("subjectName");
        }
    }

    private void initiateComponents() {

        // Toolbar
        Toolbar subjectToolbar = findViewById(R.id.subjectToolbar);
        subjectToolbar.setTitle("");
        this.setSupportActionBar(subjectToolbar);

        // Image View
        backIV = findViewById(R.id.backIV);
        ImageView subjectLogoIV = findViewById(R.id.subjectLogoIV);

        for (Subject sbj : MainPageT.teacher.getSubjects()) {
            if (sbj.getSubjectName().equals(subjectName)) {
                popupSubject = sbj; // for easy access of information in the popup
                if (!sbj.getImage().equals("")) {
                    subjectLogoIV.setImageBitmap(HelpingFunctions.convertBase64toImage(sbj.getImage()));
                } else {
                    subjectLogoIV.setImageResource(R.drawable.ic_help_black_24dp);
                }
                break;
            }
        }

        // Text Views
        TextView subjectNameTV = findViewById(R.id.subjectNameTV);
        subjectNameTV.setText(subjectName);
        infoTV = findViewById(R.id.infoTV);
        foldersTV = findViewById(R.id.foldersTV);

        // List View
        foldersLV = findViewById(R.id.foldersLV);


        // Card Views
        addRemoveFolderC = findViewById(R.id.addRemoveFolderC);
        removeFolderC = findViewById(R.id.removeFolderC);
        addFolderC = findViewById(R.id.addFolderC);

        // ArrayList
        folders = HelpingFunctions.getFoldersForSubjectAndTeacher(MainPageT.teacher.getUsername(), subjectName);
        Collections.sort(folders);

    }

    // set all listeners
    private void setListeners() {

        // Image View
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // List View
        if (folders.size() == 0) {
            infoTV.setVisibility(View.VISIBLE);
            foldersTV.setVisibility(View.INVISIBLE);
            emptyList = true;
        } else {
            infoTV.setVisibility(View.INVISIBLE);
            foldersTV.setVisibility(View.VISIBLE);
            emptyList = false;
        }
        FoldersAdapter foldersAdapter = new FoldersAdapter(SubjectPage.this, folders);
        foldersLV.setAdapter(foldersAdapter);
        HelpingFunctions.setListViewHeightBasedOnChildren(foldersLV);


        // Card Views
        addRemoveFolderC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;

                // SHOW
                if (addFolderC.getAlpha() == 0) {
                    if (!emptyList) {
                        removeFolderC.animate().translationYBy(-width/3).alpha(1).setDuration(300);
                    }
                    addFolderC.animate().translationXBy(-width/3).alpha(1).setDuration(300);
                    return;
                }

                // HIDE
                if (addFolderC.getAlpha() == 1) {
                    if (!emptyList) {
                        removeFolderC.animate().translationYBy(width/3).alpha(0).setDuration(300);
                    }
                    addFolderC.animate().translationXBy(width/3).alpha(0).setDuration(300);
                }
            }
        });

        removeFolderC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RemoveFolder.class);
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });

        addFolderC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddFolder.class);
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });
    }


    /*                                   *** A D A P T O R S  ***                                 */
    class FoldersAdapter extends BaseAdapter {

        private ArrayList<String> folders;
        private LayoutInflater inflater;


        FoldersAdapter(Context context, ArrayList<String> folders) {
            inflater = LayoutInflater.from(context);
            this.folders = folders;
        }


        @Override
        public int getCount() {
            return folders.size();
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
                convertView = inflater.inflate(R.layout.list_custom_folder, null);

                TextView folderTV = convertView.findViewById(R.id.folderTV);
                ImageView endIV = convertView.findViewById(R.id.endIV);

                folderTV.setText(folders.get(position));
                endIV.setImageResource(R.drawable.ic_navigate_next_black_24dp);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), FilesPage.class);
                    intent.putExtra("folderName", folders.get(position));
                    startActivityForResult(intent, 2);
                    overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                }
            });

            return convertView;
        }
    }

    class DomainAdapter extends BaseAdapter {

        private ArrayList<String> subjects;
        private LayoutInflater inflater;


        DomainAdapter(Context context, ArrayList<String> subjects) {
            inflater = LayoutInflater.from(context);
            this.subjects = subjects;
        }

        @Override
        public int getCount() {
            return subjects.size();
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
                convertView = inflater.inflate(R.layout.list_custom_folder, null);

                final TextView folderTV = convertView.findViewById(R.id.folderTV);
                ImageView endIV = convertView.findViewById(R.id.endIV);

                folderTV.setText(subjects.get(position));
                endIV.setImageResource(0);
            }

            return convertView;
        }
    }



    /*                      *** T O O L B A R    M E N U ***                         */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_subject, menu);

        // Setting up menu options
        MenuItem item = menu.getItem(0).getSubMenu().getItem(1);
        SpannableString s = new SpannableString("Report");
        s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
        s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);
        item.setTitle(s);

        item = menu.getItem(0).getSubMenu().getItem(0);
        s = new SpannableString("Details");
        s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);
        item.setTitle(s);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        ImageView closePopupIV;

        switch (id) {
            case R.id.details:
                subjectDetailsPopup = new Dialog(SubjectPage.this);
                subjectDetailsPopup.setContentView(R.layout.popup_subject_information);

                closePopupIV = subjectDetailsPopup.findViewById(R.id.closePopupIV);
                closePopupIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        subjectDetailsPopup.dismiss();
                    }
                });

                // Remove dialog background
                subjectDetailsPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                subjectDetailsPopup.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                subjectDetailsPopup.show();

                ImageView popupSubjectLogoIV = subjectDetailsPopup.findViewById(R.id.popupSubjectLogoIV);
                TextView popupSubjectNameTV = subjectDetailsPopup.findViewById(R.id.popupSubjectNameTV);
                TextView popupDomainNameTV = subjectDetailsPopup.findViewById(R.id.popupDomainNameTV);
                ImageView popupDomainInfoIV = subjectDetailsPopup.findViewById(R.id.popupDomainInfoIV);
                TextView popupDescriptionTV = subjectDetailsPopup.findViewById(R.id.popupDescriptionTV);

                popupSubjectLogoIV.setImageBitmap(HelpingFunctions.convertBase64toImage(popupSubject.getImage()));
                popupSubjectNameTV.setText(popupSubject.getSubjectName());
                popupDomainNameTV.setText(popupSubject.getDomainName());
                popupDescriptionTV.setText(popupSubject.getSubjectDescription());

                popupDomainInfoIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        subjectDetailsPopup.dismiss();

                        // Domain information
                        domainDetailsPopup = new Dialog(SubjectPage.this);
                        domainDetailsPopup.setContentView(R.layout.popup_domain_information);

                        ImageView closePopupIV = domainDetailsPopup.findViewById(R.id.closePopupIV);
                        closePopupIV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                domainDetailsPopup.dismiss();
                            }
                        });

                        // Remove dialog background
                        domainDetailsPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        domainDetailsPopup.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        domainDetailsPopup.show();

                        ImageView popupDomainLogoIV = domainDetailsPopup.findViewById(R.id.popupDomainLogoIV);
                        TextView popupDomainNameTV = domainDetailsPopup.findViewById(R.id.popupDomainNameTV);
                        TextView popupDomainDescriptionTV = domainDetailsPopup.findViewById(R.id.popupDomainDescriptionTV);
                        ListView subjectsLV = domainDetailsPopup.findViewById(R.id.subjectsLV);

                        // information
                        ArrayList<String> information = HelpingFunctions.getDomainForSubject(popupSubject.getSubjectName());
                        popupDomainNameTV.setText(information.get(0));
                        popupDomainDescriptionTV.setText(information.get(1));
                        popupDomainLogoIV.setImageBitmap(HelpingFunctions.convertBase64toImage(information.get(2)));

                        // list
                        ArrayList<ArrayList> information2 = HelpingFunctions.getSubjectsForDomain(information.get(0));
                        Collections.sort(information2.get(0));
                        DomainAdapter domainAdapter = new DomainAdapter(SubjectPage.this, information2.get(0));
                        subjectsLV.setAdapter(domainAdapter);
                        HelpingFunctions.setListViewHeightBasedOnChildren(subjectsLV);

                    }
                });


                break;
            case R.id.report:
                reportPopup = new Dialog(SubjectPage.this);
                reportPopup.setContentView(R.layout.popup_report);

                closePopupIV = reportPopup.findViewById(R.id.closePopupIV);
                closePopupIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reportPopup.dismiss();
                    }
                });

                // Remove dialog background
                reportPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                reportPopup.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                reportPopup.show();

                final EditText titleET = reportPopup.findViewById(R.id.titleET);
                final EditText messageET = reportPopup.findViewById(R.id.messageET);
                Button sendBttn = reportPopup.findViewById(R.id.sendBttn);

                sendBttn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean canSend = true;

                        if (HelpingFunctions.isEditTextEmpty(titleET)) {
                            titleET.setError("Insert title");
                            canSend = false;
                        }
                        if (HelpingFunctions.isEditTextEmpty(messageET)) {
                            messageET.setError("Insert message");
                            canSend = false;
                        }

                        if (canSend) {

                            String result = HelpingFunctions.sendReportTeacher(MainPageT.teacher.getEmail(), titleET.getText().toString(), messageET.getText().toString());
                            reportPopup.dismiss();
                            if (result.equals("Report sent.")) {
                                reportSentPopup = new Dialog(SubjectPage.this);
                                reportSentPopup.setContentView(R.layout.popup_report_sent);

                                ImageView closePopupIV = reportSentPopup.findViewById(R.id.closePopupIV);
                                closePopupIV.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        reportSentPopup.dismiss();

                                    }
                                });

                                // Remove dialog background
                                reportSentPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                reportSentPopup.show();
                            }

                        }
                    }
                });
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            addRemoveFolderC.performClick();
            if (resultCode == Activity.RESULT_OK) {
                // new folder added
                emptyList = false;
                infoTV.setVisibility(View.INVISIBLE);
                foldersTV.setVisibility(View.VISIBLE);

                Collections.sort(folders);
                FoldersAdapter foldersAdapter = new FoldersAdapter(SubjectPage.this, folders);
                foldersLV.setAdapter(foldersAdapter);
                HelpingFunctions.setListViewHeightBasedOnChildren(foldersLV);
            }
        }

        if (requestCode == 1) {
            addRemoveFolderC.performClick();
            if (resultCode == Activity.RESULT_OK) {
                // folder(s) removed
                if (folders.size() == 0) {
                    emptyList = true;
                    infoTV.setVisibility(View.VISIBLE);
                    foldersTV.setVisibility(View.INVISIBLE);
                } else {
                    Collections.sort(folders);
                }
                FoldersAdapter foldersAdapter = new FoldersAdapter(SubjectPage.this, folders);
                foldersLV.setAdapter(foldersAdapter);
                HelpingFunctions.setListViewHeightBasedOnChildren(foldersLV);
            }
        }

        if(requestCode == 2){
            if (addFolderC.getAlpha() > 0) {
                addRemoveFolderC.performClick();
            }
        }
    }

    @Override
    public void onBackPressed() {

        finish();
        overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }
}
