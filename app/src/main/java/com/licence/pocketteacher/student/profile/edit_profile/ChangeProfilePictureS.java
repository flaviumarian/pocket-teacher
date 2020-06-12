package com.licence.pocketteacher.student.profile.edit_profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.student.MainPageS;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChangeProfilePictureS extends AppCompatActivity {


    private ImageView backIV, profilePictureIV;
    private ListView selectLV;

    private String currentBase64Image, newBase64Image, newPhotoPath;

    private static final int IMG_PICK_CODE = 110;
    public static final int TAKE_PHOTO_REQ = 111;
    public static final int CAMERA_PERM_CODE = 100;
    public static final int READ_STORAGE_REQ = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_picture_s);

        initiateComponents();
        setListeners();
    }


    private void initiateComponents() {

        // Image View
        backIV = findViewById(R.id.backIV);
        profilePictureIV = findViewById(R.id.profilePictureIV);

        // List View
        selectLV = findViewById(R.id.selectLV);

        // String

        currentBase64Image = MainPageS.student.getProfileImageBase64();
        newBase64Image = currentBase64Image;
        if (currentBase64Image.equals("")) {
            switch (MainPageS.student.getGender()) {
                case "0":
                    profilePictureIV.setImageResource(R.drawable.profile_picture_male);
                    break;
                case "1":
                    profilePictureIV.setImageResource(R.drawable.profile_picture_female);
                    break;
                case "2":
                    profilePictureIV.setImageResource(0);
                    break;
            }
        } else {
            profilePictureIV.setImageBitmap(HelpingFunctions.convertBase64toImage(currentBase64Image));
        }
    }

    private void setListeners() {

        // Image View
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        // List View
        int[] selectImages = {R.drawable.ic_camera_alt_black_24dp, R.drawable.ic_wallpaper_black_24dp, R.drawable.ic_remove_circle_black_24dp};
        String[] names = {"Take Picture", "Choose from library", "Remove current picture"};

        ChangeProfilePictureS.SelectAdapter selectAdapter = new ChangeProfilePictureS.SelectAdapter(getApplicationContext(), selectImages, names);
        selectLV.setAdapter(selectAdapter);

    }

    /*                                   *** A D A P T O R  ***                                   */
    class SelectAdapter extends BaseAdapter {

        private int[] images;
        private String[] names;
        private LayoutInflater inflater;


        SelectAdapter(Context context, int[] images, String[] names) {
            inflater = LayoutInflater.from(context);
            this.images = images;
            this.names = names;
        }


        @Override
        public int getCount() {
            return images.length;
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
                convertView = inflater.inflate(R.layout.list_custom_settings, null);

                ImageView startIV = convertView.findViewById(R.id.startIV);
                TextView textView = convertView.findViewById(R.id.textView);
                ImageView endIV = convertView.findViewById(R.id.endIV);

                startIV.setImageResource(images[position]);
                textView.setText(names[position]);
                endIV.setImageResource(R.drawable.ic_navigate_next_black_24dp);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == 0) {
                        askCameraPermissions();
                    }
                    if (position == 1) {
                        askStoragePermissions();
                    }
                    if (position == 2) {

                        newBase64Image = "";
                        switch (MainPageS.student.getGender()) {
                            case "0":
                                profilePictureIV.setImageResource(R.drawable.profile_picture_male);
                                break;
                            case "1":
                                profilePictureIV.setImageResource(R.drawable.profile_picture_female);
                                break;
                            case "2":
                                profilePictureIV.setImageResource(0);
                                break;
                        }
                    }
                }
            });

            return convertView;
        }
    }

    /*                                *** PERMISSIONS CHECK ***                               */
    private void askStoragePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_REQ);
        } else {
            openGalleryIntent();
        }
    }

    private void askCameraPermissions() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERM_CODE);
        } else {
            openCameraIntent();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            // GALLERY
            if (requestCode == READ_STORAGE_REQ) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGalleryIntent();
                } else {
                    // no permission granted
                    Snackbar.make(getCurrentFocus(), "Storage permission is needed.", Snackbar.LENGTH_SHORT).show();
                }
            }

            // CAMERA
            if (requestCode == CAMERA_PERM_CODE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    openCameraIntent();
                } else {
                    // permission not granted
                    Snackbar.make(getCurrentFocus(), "Camera and storage permissions are needed.", Snackbar.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*                             *** OPEN CAMERA/GALLERY ***                              */
    private void openGalleryIntent() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, IMG_PICK_CODE);
    }

    private void openCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Check if the device has a camera
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";

            // android:requestLegacyExternalStorage="true" in manifest is so that it works on API 29 too
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Camera/");
            File image = new File(storageDir, imageFileName + ".jpg");

            newPhotoPath = image.getAbsolutePath();

            Uri photoURI = FileProvider.getUriForFile(this, "com.licence.android.fileprovider", image);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, TAKE_PHOTO_REQ);

        }
    }


    /*                             *** AFTER CHOICE/IMAGE TAKING ***                      */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // GALLERY
        if (requestCode == IMG_PICK_CODE && resultCode == RESULT_OK) {

            Uri contentUri = data.getData();
            String newPhotoPath = getRealPathFromURI(contentUri);
            Bitmap newBitmap;

            try {
                newBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentUri);
                newBitmap = handleRotationIssues(newBitmap, newPhotoPath);
                newBase64Image = HelpingFunctions.convertImageToBase64(newBitmap);
                profilePictureIV.setImageBitmap(newBitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // CAMERA
        if (requestCode == TAKE_PHOTO_REQ && resultCode == Activity.RESULT_OK && newPhotoPath != null) {

            /* https://developer.android.com/training/camera/photobasics
             * galleryAddPic*/
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(newPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);

            try {

                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentUri);
                imageBitmap = handleRotationIssues(imageBitmap, newPhotoPath);
                profilePictureIV.setImageBitmap(imageBitmap);
                newBase64Image = HelpingFunctions.convertImageToBase64(imageBitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getRealPathFromURI (Uri contentUri) {
        String path = null;
        String[] proj = { MediaStore.MediaColumns.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(column_index);
        }
        cursor.close();
        return path;
    }

    private Bitmap handleRotationIssues(Bitmap bitmap, String path) {
        try {
            ExifInterface ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap;
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }

            return rotatedBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public String resizeBase64Image(String base64image) {
        byte[] encodeByte = Base64.decode(base64image.getBytes(), Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        Bitmap image = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length, options);


        if (image.getHeight() <= 400 && image.getWidth() <= 400) {
            return base64image;
        }


        // Scale width and height to maintain a good ratio
        int width = image.getWidth();
        int height = image.getHeight();

        int factor = width / 150;
        width = width / factor;
        height = height / factor;



        image = Bitmap.createScaledBitmap(image, width, height, false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);

        byte[] b = baos.toByteArray();
        System.gc();
        return Base64.encodeToString(b, Base64.NO_WRAP);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(!newBase64Image.equals(currentBase64Image)){

            MainPageS.student.setProfileImageBase64(newBase64Image); // in case the user gets to the profile before the thread is finished

            Thread thread = new Thread(new Runnable(){
                @Override
                public void run(){
                    if (newBase64Image.equals("")) {
                        HelpingFunctions.deleteProfileImageBasedOnUsername(MainPageS.student.getUsername());
                        MainPageS.student.setProfileImageBase64(newBase64Image);
                    }else{
                        newBase64Image = resizeBase64Image(newBase64Image);

                        if (!currentBase64Image.equals(newBase64Image)) {
                            MainPageS.student.setProfileImageBase64(newBase64Image);
                            if (newBase64Image.equals("")) {
                                HelpingFunctions.deleteProfileImageBasedOnUsername(MainPageS.student.getUsername());
                            }

                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                            String imageName = "JPEG_" + timeStamp + "_";

                            // Adding the image in the DB, deletes if already existing
                            HelpingFunctions.setProfileImageBasedOnUsername(MainPageS.student.getUsername(), newBase64Image, imageName);
                        }
                    }
                }
            });
            thread.start();
        }


        finish();
        overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }
}
