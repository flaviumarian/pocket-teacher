package com.licence.pocketteacher.teacher.folder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.teacher.MainPageT;
import com.licence.pocketteacher.aiding_classes.Subject;

import java.util.ArrayList;
import java.util.Collections;

public class AddSubject extends AppCompatActivity {

    private ImageView backIV;
    private Spinner subjectsSpinner, domainsSpinner;
    private Button requestBttn;
    private Dialog requestPopup, requestSentPopup;

    private ArrayList<String> availableSubjects, availableSubjectDescriptions, availableSubjectImages, availableDomains; // all available options
    private ArrayList<String> currentSubjects; // current options

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        initiateComponents();

    }

    private void initiateComponents(){

        // Toolbar
        Toolbar subjectToolbar = findViewById(R.id.addSubjectToolbar);
        subjectToolbar.setTitle("");
        this.setSupportActionBar(subjectToolbar);

        new Thread(new Runnable() {
            @Override
            public void run() {

                // Image View
                backIV = findViewById(R.id.backIV);

                // Array Lists
                // Subjects
                ArrayList<ArrayList> completeSubjects = HelpingFunctions.getAllSubjects();
                availableSubjects = completeSubjects.get(0);
                availableSubjectDescriptions = completeSubjects.get(1);
                availableSubjectImages = completeSubjects.get(2);
                currentSubjects = new ArrayList<>();
                currentSubjects.addAll(availableSubjects);
                currentSubjects.add(0, "Subject");
                removeExistingSubjects(currentSubjects);

                // Domains
                ArrayList<ArrayList> completeDomains = HelpingFunctions.getAllDomains();
                availableDomains = completeDomains.get(0);
                availableDomains.add(0, "Domain");

                // Spinners
                subjectsSpinner = findViewById(R.id.subjectsSpinner);
                domainsSpinner = findViewById(R.id.domainsSpinner);

                // Button
                requestBttn = findViewById(R.id.requestBttn);

                try{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // Spinners
                            ArrayAdapter<String> adapterSubjects = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, currentSubjects);
                            subjectsSpinner.setAdapter(adapterSubjects);

                            ArrayAdapter<String> adapterDomains = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, availableDomains);
                            domainsSpinner.setAdapter(adapterDomains);

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
                onBackPressed();
            }
        });


        // Spinners
        domainsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(!HelpingFunctions.isConnected(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);

                if(position == 0){
                    currentSubjects.clear();
                    currentSubjects.addAll(availableSubjects);
                    currentSubjects.add(0, "Subject");
                    removeExistingSubjects(currentSubjects);
                    ArrayAdapter<String> adapterSubjects = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, currentSubjects);
                    subjectsSpinner.setAdapter(adapterSubjects);
                    return;
                }

                currentSubjects = HelpingFunctions.getSubjectsForDomain(availableDomains.get(position)).get(0);
                removeExistingSubjects(currentSubjects);

                if(currentSubjects.size() == 0){
                    currentSubjects.add("Subject");
                    Toast.makeText(AddSubject.this, "There are no more available subjects for this domain.", Toast.LENGTH_LONG).show();
                }

                ArrayAdapter<String> adapterSubjects = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, currentSubjects);
                subjectsSpinner.setAdapter(adapterSubjects);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        subjectsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(!HelpingFunctions.isConnected(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Button
        requestBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!HelpingFunctions.isConnected(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                requestPopup = new Dialog(AddSubject.this);
                requestPopup.setContentView(R.layout.popup_request_subject);

                ImageView closePopupIV = requestPopup.findViewById(R.id.closePopupIV);
                closePopupIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestPopup.dismiss();
                    }
                });

                // Remove dialog background
                requestPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                requestPopup.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                requestPopup.show();

                final EditText subjectET = requestPopup.findViewById(R.id.subjectET);
                final EditText domainET = requestPopup.findViewById(R.id.domainET);
                final EditText descriptionET = requestPopup.findViewById(R.id.descriptionET);


                Button sendBttn = requestPopup.findViewById(R.id.sendBttn);
                sendBttn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(!HelpingFunctions.isConnected(getApplicationContext())){
                            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        boolean canSend = true;
                        if(HelpingFunctions.isEditTextEmpty(subjectET)){
                            subjectET.setError("Insert subject title");
                            canSend = false;
                        }

                        if(HelpingFunctions.isEditTextEmpty(domainET)){
                            domainET.setError("Insert domain");
                            canSend = false;
                        }

                        if(canSend){

                            String description;
                            if(HelpingFunctions.isEditTextEmpty(descriptionET)){
                                description = "null";
                            } else{
                                description = descriptionET.getText().toString();
                            }



                            String result =  HelpingFunctions.sendSubjectRequest(MainPageT.teacher.getEmail(), subjectET.getText().toString(), domainET.getText().toString(), description);
                            requestPopup.dismiss();
                            if(result.equals("Request sent.")){
                                requestSentPopup = new Dialog(AddSubject.this);
                                requestSentPopup.setContentView(R.layout.popup_request_subject_sent);

                                ImageView closePopupIV = requestSentPopup.findViewById(R.id.closePopupIV);
                                closePopupIV.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        requestSentPopup.dismiss();

                                    }
                                });

                                // Remove dialog background
                                requestSentPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                requestSentPopup.show();
                            }
                        }
                    }
                });
            }
        });
    }

    private void removeExistingSubjects(ArrayList<String> subjectsToClear){

        for(Subject subject : MainPageT.teacher.getSubjects()){
            subjectsToClear.remove(subject.getSubjectName());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reset, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.refresh:

                if(!HelpingFunctions.isConnected(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    break;
                }

                currentSubjects.clear();
                currentSubjects.addAll(availableSubjects);
                currentSubjects.add(0, "Subject");
                removeExistingSubjects(currentSubjects);
                ArrayAdapter<String> adapterSubjects = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, currentSubjects);
                subjectsSpinner.setAdapter(adapterSubjects);
                subjectsSpinner.setSelection(0);
                domainsSpinner.setSelection(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if(!HelpingFunctions.isConnected(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        int position = (int)subjectsSpinner.getSelectedItemId();


        try {
            if (!currentSubjects.get(position).equals("Subject")) {
                String result = HelpingFunctions.addSubject(MainPageT.teacher.getEmail(), currentSubjects.get(position));

                if (result.equals("Data inserted.")) {
                    int positionSubject = availableSubjects.indexOf(currentSubjects.get(position));

                    // Subject information
                    String subjectName = availableSubjects.get(positionSubject);
                    String subjectDescription = availableSubjectDescriptions.get(positionSubject);
                    String image = availableSubjectImages.get(positionSubject);


                    // Domain information
                    ArrayList<String> information = HelpingFunctions.getDomainForSubject(subjectName);
                    String domain = information.get(0);
                    String domainDescription = information.get(1);


                    MainPageT.teacher.addSubject(subjectName, subjectDescription, domain, domainDescription, image);
                    Collections.sort(MainPageT.teacher.getSubjects());
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        finish();
        overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }

}