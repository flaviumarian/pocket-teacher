package com.licence.pocketteacher.teacher.folder.subject.files;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.teacher.MainPageT;
import com.licence.pocketteacher.teacher.folder.subject.SubjectPage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddFile extends AppCompatActivity {

    private ImageView backIV;
    private EditText titleET, descriptionET;
    private TextView info2TV;
    private Button chooseFileBttn, postBttn;
    private Request request;
    private boolean fileGood = false;
    private String filePath;
    private Dialog goBackPopup;

    public static final int STORAGE_READ_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_file);

        initiateComponents();
        setListeners();

    }

    private void initiateComponents() {

        // Image View
        backIV = findViewById(R.id.backIV);

        // Edit Texts
        titleET = findViewById(R.id.titleET);
        descriptionET = findViewById(R.id.descriptionET);

        // Text View
        info2TV = findViewById(R.id.info2TV);

        // Buttons
        chooseFileBttn = findViewById(R.id.chooseFileBttn);
        postBttn = findViewById(R.id.postBttn);

    }

    private void setListeners() {

        // Image View
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Edit Text
        descriptionET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // only 10 rows allowed
                if (descriptionET.getLayout().getLineCount() > 10) {
                    descriptionET.getText().delete(descriptionET.getText().length() - 1, descriptionET.getText().length());
                }
            }
        });


        // Buttons
        chooseFileBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    askPermissions();
                    return;
                }

                // if permission is granted it can continue
                chooseFile();
            }
        });

        postBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!HelpingFunctions.isConnected(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean canPost = true;

                if (HelpingFunctions.isEditTextEmpty(titleET)) {
                    titleET.setError("Insert title");
                    canPost = false;
                } else {
                    for (String title : FilesPage.fileNames) {
                        if (title.equalsIgnoreCase(titleET.getText().toString())) {
                            titleET.setError("Title already exists");
                            canPost = false;
                            break;
                        }
                    }
                }
                if (HelpingFunctions.isEditTextEmpty(descriptionET)) {
                    descriptionET.setError("Insert description");
                    canPost = false;
                }


                if (canPost) {
                    final ProgressDialog loading = ProgressDialog.show(AddFile.this, "Please wait", "Posting...", true);
                    new Thread() {
                        @Override
                        public void run() {

                            uploadFile();
                            String pathToSend;
                            if (fileGood) {
                                pathToSend = "http://pocketteacher.ro/uploads/" + filePath;
                            } else {
                                pathToSend = "null";
                            }

                            String result = HelpingFunctions.postFile(MainPageT.teacher.getEmail(), SubjectPage.subjectName, FilesPage.folderName, titleET.getText().toString().replace("'", "\\\'"), descriptionET.getText().toString().replace("'", "\\\'"), pathToSend);
                            if (result.equals("Error occured.")) {
                                Snackbar.make(getCurrentFocus(), "An error occured, please try again later.", Snackbar.LENGTH_SHORT).show();
                                return;
                            }

                            FilesPage.fileNames.add(0, titleET.getText().toString());
                            FilesPage.likedStatuses.add(0, "");
                            FilesPage.likes.add(0, 0);
                            FilesPage.comments.add(0, 0);
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                            overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);

                            try {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loading.dismiss();
                                    }
                                });
                            } catch (final Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        });

    }

    private void askPermissions(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_READ_REQUEST_CODE);
        } else {
            chooseFile();
        }

    }

    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {

            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), 1);

        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadFile() {

        if (!fileGood) {
            return;
        }

        try {
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getMimeType(String path) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();


                    try {
                        File f = FileUtils.getFile(AddFile.this, uri);
                        String content_type = getMimeType(f.getPath());
                        if (content_type == null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    info2TV.setText(R.string.message_add_file_6);
                                    info2TV.setTextColor(Color.RED);
                                }
                            });

                            fileGood = false; // file chosen is an image and can't be posted.
                            return;
                        }

                        int fileSize = Integer.parseInt(String.valueOf(f.length() / 1024));
                        if (fileSize > 10000) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    info2TV.setText(R.string.message_add_file_4);
                                    info2TV.setTextColor(Color.RED);
                                }
                            });

                            fileGood = false;
                            return;
                        }

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                info2TV.setText(R.string.message_add_file_5);
                                info2TV.setTextColor(Color.BLACK);
                            }
                        });


                        fileGood = true;


                        // Setting the name of the file to be uploaded in the database based on the current date
                        String file_path = f.getAbsolutePath();
                        String file_type = file_path.substring(file_path.lastIndexOf('.') + 1).trim();
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                        filePath = timeStamp + "." + file_type;


                        RequestBody file_body = RequestBody.create(MediaType.parse(content_type), f);
                        RequestBody request_body = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("type", content_type)
                                .addFormDataPart("uploaded_file", filePath, file_body)
                                .build();

                        request = new Request.Builder()
                                .url("http://pocketteacher.ro/upload_file_to_server.php")
                                .post(request_body)
                                .build();
                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(AddFile.this, "Error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

            t.start();
        }
    }


    public static class FileUtils {

        public static final String AUTHORITY = "com.licence.pocketteacher";

        private FileUtils() {
        } //private constructor to enforce Singleton pattern

        /**
         * @return Whether the URI is a local one.
         */
        public static boolean isLocal(String url) {
            if (url != null && !url.startsWith("http://") && !url.startsWith("https://")) {
                return true;
            }
            return false;
        }


        public static boolean isLocalStorageDocument(Uri uri) {
            return AUTHORITY.equals(uri.getAuthority());
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         * @author paulburke
         */
        public static boolean isExternalStorageDocument(Uri uri) {
            return "com.android.externalstorage.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         * @author paulburke
         */
        public static boolean isDownloadsDocument(Uri uri) {
            return "com.android.providers.downloads.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         * @author paulburke
         */
        public static boolean isMediaDocument(Uri uri) {
            return "com.android.providers.media.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is Google Photos.
         */
        public static boolean isGooglePhotosUri(Uri uri) {
            return "com.google.android.apps.photos.content".equals(uri.getAuthority());
        }

        /**
         * Get the value of the data column for this Uri. This is useful for
         * MediaStore Uris, and other file-based ContentProviders.
         *
         * @param context       The context.
         * @param uri           The Uri to query.
         * @param selection     (Optional) Filter used in the query.
         * @param selectionArgs (Optional) Selection arguments used in the query.
         * @return The value of the _data column, which is typically a file path.
         * @author paulburke
         */
        public static String getDataColumn(Context context, Uri uri, String selection,
                                           String[] selectionArgs) {

            Cursor cursor = null;
            final String column = "_data";
            final String[] projection = {
                    column
            };

            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                        null);
                if (cursor != null && cursor.moveToFirst()) {

                    final int column_index = cursor.getColumnIndexOrThrow(column);
                    return cursor.getString(column_index);
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            return null;
        }

        /**
         * Get a file path from a Uri. This will get the the path for Storage Access
         * Framework Documents, as well as the _data field for the MediaStore and
         * other file-based ContentProviders.<br>
         * <br>
         * Callers should check whether the path is local before assuming it
         * represents a local file.
         *
         * @param context The context.
         * @param uri     The Uri to query.
         * @author paulburke
         * @see #isLocal(String)
         * @see #getFile(Context, Uri)
         */
        public static String getPath(final Context context, final Uri uri) {

            // DocumentProvider
            if (DocumentsContract.isDocumentUri(context, uri)) {
                // LocalStorageProvider
                if (isLocalStorageDocument(uri)) {
                    // The path is the id
                    return DocumentsContract.getDocumentId(uri);
                }

                // ExternalStorageProvider
                else if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }

                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }

                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }

            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {

                // Return the remote address
                if (isGooglePhotosUri(uri))
                    return uri.getLastPathSegment();

                return getDataColumn(context, uri, null, null);
            }

            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }

            return null;
        }

        /**
         * Convert Uri into File, if possible.
         *
         * @return file A local file that the Uri was pointing to, or null if the
         * Uri is unsupported or pointed to a remote resource.
         * @author paulburke
         * @see #getPath(Context, Uri)
         */
        public static File getFile(Context context, Uri uri) {
            if (uri != null) {
                String path = getPath(context, uri);
                if (path != null && isLocal(path)) {
                    return new File(path);
                }
            }
            return null;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_READ_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseFile();
            } else {
                // no permission granted
                Snackbar.make(getCurrentFocus(), "Storage permission is needed.", Snackbar.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onBackPressed() {

        if (!HelpingFunctions.isConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!HelpingFunctions.isEditTextEmpty(titleET) || !HelpingFunctions.isEditTextEmpty(descriptionET) || fileGood) {
            goBackPopup = new Dialog(AddFile.this);
            goBackPopup.setContentView(R.layout.popup_go_back);

            ImageView closePopupIV = goBackPopup.findViewById(R.id.closePopupIV);
            closePopupIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBackPopup.dismiss();
                }
            });

            // Remove dialog background
            goBackPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            goBackPopup.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            goBackPopup.show();


            Button yesButton = goBackPopup.findViewById(R.id.yesBttn);
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBackPopup.dismiss();
                    finish();
                    overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
                }
            });
        } else {
            finish();
            overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
        }
    }


}
