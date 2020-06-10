package com.licence.pocketteacher.miscellaneous;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.aiding_classes.Comment;
import com.licence.pocketteacher.aiding_classes.Post;
import com.licence.pocketteacher.aiding_classes.Student;
import com.licence.pocketteacher.aiding_classes.Subject;
import com.licence.pocketteacher.aiding_classes.Teacher;
import com.licence.pocketteacher.aiding_classes.Notification;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;


import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class HelpingFunctions {

    /*                              *** V A L I D A T I O N S  ***                                */
    public static boolean isEditTextEmpty(EditText editText) {
        if (editText.getText().toString().equals("")) {
            return true;
        } else {
            return false;
        }
    }

    public static int passwordConditionsFulfilled(String password) {
        int conditions = 0;
        /* Pattern p1 - symbols
           Pattern p2 - lowercase
           Pattern p3 - uppercase
           Pattern p4 - number
         */

        Pattern p1 = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m1 = p1.matcher(password);
        Pattern p2 = Pattern.compile("\\p{javaLowerCase}+");
        Matcher m2 = p2.matcher(password);
        Pattern p3 = Pattern.compile("\\p{javaUpperCase}+");
        Matcher m3 = p3.matcher(password);
        Pattern p4 = Pattern.compile("\\p{javaDigit}+");
        Matcher m4 = p4.matcher(password);


        if (m1.find()) {
            conditions++;
        }
        if (m2.find()) {
            conditions++;
        }
        if (m3.find()) {
            conditions++;
        }
        if (m4.find()) {
            conditions++;
        }

        return conditions;
    }

    public static boolean isEmailValid(String email) {

        // Email of form NAME@NAME.EXT
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }

    public static boolean emailExists(String email) {
        String result = frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/check_if_email_exists.php", email, "email");

        try {
            return result.equals("Email exists.");
        }catch(Exception e){
            return false;
        }
    }



    /*                                    *** T O O L S ***                                       */
    public static String generateRandomString(int length) {
        String availableCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";

        // Create the string based on the random characters
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            stringBuilder.append(availableCharacters.charAt((int) (availableCharacters.length() * Math.random())));
        }

        return stringBuilder.toString();
    }

    public static String generateRandomPassword() {
        String availableCharacters = "0123456789abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPQRSTUVWXYZ~!@#$%^&*()_+=-.,/;'";

        // Create the string based on the random characters
        StringBuilder stringBuilder = new StringBuilder(15);
        for (int i = 0; i < 15; i++) {
            stringBuilder.append(availableCharacters.charAt((int) (availableCharacters.length() * Math.random())));
        }

        return stringBuilder.toString();
    }

    public static String generateUsername(String firstName, String lastName) {
        String availableCharacters = "0123456789";

        // Create the string based on the random characters
        StringBuilder stringBuilder = new StringBuilder(firstName.length() + lastName.length() + 5);
        stringBuilder.append(firstName.toLowerCase());
        stringBuilder.append(lastName.toLowerCase());
        for (int i = 0; i < 5; i++) {
            stringBuilder.append(availableCharacters.charAt((int) (availableCharacters.length() * Math.random())));
        }

        return stringBuilder.toString();
    }

    public static void sendEmail(final String destinationEmail, String subject, String text) {

        final String subj = subject;
        final String txt = text;

        new Thread(new Runnable() {
            public void run() {
                try {

                    final String emailAddress = "student.mobile.application@gmail.com";
                    final String password = "Tr82SDgg@ljofdg";

                    Properties properties = new Properties();
                    properties.setProperty("mail.transport.protocol", "smtp");
                    properties.put("mail.smtp.auth", "true");
                    properties.put("mail.smtp.starttls.enable", "true");
                    properties.put("mail.smtp.host", "smtp.gmail.com");
                    properties.put("mail.smtp.port", "587");


                    Session session = Session.getInstance(properties, new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(emailAddress, password);
                        }
                    });

                    MimeMessage msg = new MimeMessage(session);
                    msg.setFrom(new InternetAddress(emailAddress));
                    msg.addRecipient(Message.RecipientType.TO, new InternetAddress(destinationEmail));
                    msg.setSubject(subj);
                    msg.setText(txt);

                    Transport.send(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }).start();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private static String getTimeSince(long timeDifference) {
        // timeDifference is in SECONDS

        if (timeDifference / 86400 > 0) {
            return (timeDifference / 86400) + "d";
        }

        if (timeDifference / 3600 > 0) {
            return (timeDifference / 3600) + "h";
        }

        if (timeDifference / 60 > 0) {
            return (timeDifference / 60) + "m";
        }

        return timeDifference + "s";
    }

    /*                                *** Main folder Scripts ***                                 */
    public static String registerUser(String username, String email, String password, String verificationCode, String confirmationCode) {
        return frameForPHPscriptUsageWithFiveParameters("http://pocketteacher.ro/register_user.php", username, "username", email, "email", password, "password", verificationCode, "verification_code", confirmationCode, "email_confirmation_code");
    }

    public static String verifyIfUsernameOrEmailExists(String username, String email) {
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/verify_existing_user.php", username, "username", email, "email");
    }

    public static String verifyTeacherVerificationCode(String codeNumber) {
        return frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/verify_teacher_code.php", codeNumber, "code_number");
    }

    public static String registerUserFacebookGoogle(String username, String firstName, String lastName, String email, String password, String verificationCode) {
        return frameForPHPscriptUsageWithSixParameters("http://pocketteacher.ro/register_user_facebook_google.php", username, "username", firstName, "first_name", lastName, "last_name", email, "email", password, "password", verificationCode, "verification_code");
    }

    public static String requestVerificationCode(String email, String details){
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/request_verification_code.php", email, "email", details, "details");
    }

    public static String verifyEmail(String username, String confirmationCode) {
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/verify_email.php", username, "username", confirmationCode, "confirmation_code");
    }

    public static String sendSubjectRequest(String email, String subject, String domain, String description) {
        return frameForPHPscriptUsageWithFourParameters("http://pocketteacher.ro/request_subject.php", email, "email", subject, "subject_title", domain, "domain_name", description, "description");
    }

    public static String sendReportTeacher(String email, String title, String message) {
        return frameForPHPscriptUsageWithThreeParameters("http://pocketteacher.ro/send_report_teacher.php", email, "email", title, "title", message, "message");
    }

    public static String sendReportStudent(String email, String title, String message) {
        return frameForPHPscriptUsageWithThreeParameters("http://pocketteacher.ro/send_report_student.php", email, "email", title, "title", message, "message");
    }

    public static ArrayList<String> logIn(String loginID, String password) {
        String jsonResult = frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/login.php", loginID, "log_in_id", password, "password");


        try {
            if (jsonResult.equals("Wrong user/email and/or password.")) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        JSONArray jsonArray;
        ArrayList<String> information = new ArrayList<>();
        String username, firstName, lastName, email, status, onboarding, gender, university, profileDescription, profileImage, privacy, autoLogin, student;

        try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            jsonArray = jsonObject.getJSONArray("data");
            JSONObject jo = jsonArray.getJSONObject(0);


            username = jo.getString("username");
            information.add(username);
            firstName = jo.getString("first_name");
            information.add(firstName);
            lastName = jo.getString("last_name");
            information.add(lastName);
            email = jo.getString("email");
            information.add(email);
            status = jo.getString("status");
            information.add(status);
            onboarding = jo.getString("onboarding");
            information.add(onboarding);
            gender = jo.getString("gender");
            information.add(gender);
            university = jo.getString("university");
            information.add(university);
            profileDescription = jo.getString("profile_description");
            information.add(profileDescription);
            profileImage = jo.getString("profile_image");
            information.add(profileImage);
            privacy = jo.getString("privacy");
            information.add(privacy);
            autoLogin = jo.getString("auto_login");
            information.add(autoLogin);
            student = jo.getString("student");
            information.add(student);

        } catch (Exception e) {
            e.printStackTrace();
        }



        return information;
    }

    public static ArrayList<String> logInUserData(String email) {
        String jsonResult = frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/login_user_data.php", email, "email");

        try {
            if (jsonResult.equals("No account.")) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        JSONArray jsonArray;
        ArrayList<String> information = new ArrayList<>();
        String username, firstName, lastName, status, onboarding, gender, university, profileDescription, profileImage, privacy, autoLogin, student;


        try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            jsonArray = jsonObject.getJSONArray("data");
            JSONObject jo = jsonArray.getJSONObject(0);

            username = jo.getString("username");
            information.add(username);
            firstName = jo.getString("first_name");
            information.add(firstName);
            lastName = jo.getString("last_name");
            information.add(lastName);
            status = jo.getString("status");
            information.add(status);
            onboarding = jo.getString("onboarding");
            information.add(onboarding);
            gender = jo.getString("gender");
            information.add(gender);
            university = jo.getString("university");
            information.add(university);
            profileDescription = jo.getString("profile_description");
            information.add(profileDescription);
            profileImage = jo.getString("profile_image");
            information.add(profileImage);
            privacy = jo.getString("privacy");
            information.add(privacy);
            autoLogin = jo.getString("auto_login");
            information.add(autoLogin);
            student = jo.getString("student");
            information.add(student);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return information;
    }

    public static String postFile(String email, String subject, String folder, String title, String description, String filePath) {
        return frameForPHPscriptUsageWithSixParameters("http://pocketteacher.ro/post_file.php", email, "email", subject, "subject", folder, "folder", title, "title", description, "description", filePath, "file_path");
    }

    public static String likePost(String usernamePoster, String subject, String folder, String title, String usernameSender) {
        return frameForPHPscriptUsageWithFiveParameters("http://pocketteacher.ro/like_post.php", usernamePoster, "username_poster", subject, "subject", folder, "folder", title, "title", usernameSender, "username_sender");
    }

    public static String unlikePost(String usernamePoster, String subject, String folder, String title, String usernameSender) {
        return frameForPHPscriptUsageWithFiveParameters("http://pocketteacher.ro/like_cancel_post.php", usernamePoster, "username_poster", subject, "subject", folder, "folder", title, "title", usernameSender, "username_sender");
    }

    public static String likeComment(String comment, String usernameFilePoster, String subject, String folder, String title, String timestamp, String usernameSender, String usernameReceiver) {
        return frameForPHPscriptUsageWithEightParameters("http://pocketteacher.ro/like_comment.php", comment, "comment", usernameFilePoster, "username_file_poster", subject, "subject", folder, "folder", title, "title", timestamp, "timestamp", usernameSender, "username_sender", usernameReceiver, "username_receiver");
    }

    public static String unlikeComment(String comment, String usernameFilePoster, String subject, String folder, String title, String timestamp, String usernameSender, String usernameReceiver) {
        return frameForPHPscriptUsageWithEightParameters("http://pocketteacher.ro/like_cancel_comment.php", comment, "comment", usernameFilePoster, "username_file_poster", subject, "subject", folder, "folder", title, "title", timestamp, "timestamp", usernameSender, "username_sender", usernameReceiver, "username_receiver");
    }

    public static String commentOnPost(String commentText, String usernameFilePoster, String subject, String folder, String title, String usernameSender) {
        return frameForPHPscriptUsageWithSixParameters("http://pocketteacher.ro/comment_on_post.php", commentText, "comment_text", usernameFilePoster, "username_file_poster", subject, "subject", folder, "folder", title, "title", usernameSender, "username_sender");
    }

    public static String followTeacher(String usernameStudent, String usernameTeacher) {
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/follow.php", usernameStudent, "username_student", usernameTeacher, "username_teacher");
    }

    public static String unfollowTeacher(String usernameStudent, String usernameTeacher) {
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/unfollow.php", usernameStudent, "username_student", usernameTeacher, "username_teacher");
    }

    public static String requestFollowTeacher(String usernameStudent, String usernameTeacher) {
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/follow_request.php", usernameStudent, "username_student", usernameTeacher, "username_teacher");
    }

    public static String approveRequest(String usernameStudent, String usernameTeacher) {
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/follow_request_approve.php", usernameStudent, "username_student", usernameTeacher, "username_teacher");
    }

    public static String undoApprovalRequest(String usernameStudent, String usernameTeacher) {
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/follow_request_undo_approval.php", usernameStudent, "username_student", usernameTeacher, "username_teacher");
    }

    public static String declineRequest(String usernameStudent, String usernameTeacher) {
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/follow_request_decline.php", usernameStudent, "username_student", usernameTeacher, "username_teacher");
    }

    public static String approveAllRequests(String usernameTeacher) {
        return frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/follow_request_approve_all.php", usernameTeacher, "username");
    }

    public static String requestUnfollowTeacher(String usernameStudent, String usernameTeacher) {
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/unfollow_request.php", usernameStudent, "username_student", usernameTeacher, "username_teacher");
    }

    public static String blockUser(String usernameTeacher, String usernameStudent) {
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/block_student.php", usernameTeacher, "username_teacher", usernameStudent, "username_student");
    }

    public static String unblockUser(String usernameTeacher, String usernameStudent) {
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/block_student_cancel.php", usernameTeacher, "username_teacher", usernameStudent, "username_student");
    }

    public static String registerToken(String token, String username){
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/register_token.php", token, "token", username, "username");
    }

    public static String deleteToken(String token, String username){
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/delete_token.php", token, "token", username, "username");
    }

    public static String sendNotification(String username, String message){
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/send_notification.php", username, "username", message, "message");
    }

    public static String sendNotificationToAllStudentsApproved(String username, String message){
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/send_notification_approve_all_requests.php", username, "username", message, "message");
    }

    public static String sendNotificationTeacherCommented(String username, String subject, String folder, String title, String message){
        return frameForPHPscriptUsageWithFiveParameters("http://pocketteacher.ro/send_notification_teacher_comment.php", username, "username", subject, "subject", folder, "folder", title, "title", message, "message");
    }

    public static String sendNotificationStudentCommented(String username, String usernamePoster, String subject, String folder, String title, String message){
        return frameForPHPscriptUsageWithSixParameters("http://pocketteacher.ro/send_notification_student_comment.php", username, "username", usernamePoster, "username_poster", subject, "subject", folder, "folder", title, "title", message, "message");
    }

    public static String deleteAccount(String username){
        return frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/delete_account.php", username, "username");
    }


    /*                                  *** Adder Scripts ***                                     */
    public static String addSubject(String email, String newSubject) {
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/adders/add_subject.php", email, "email", newSubject, "new_subject");
    }

    public static String addFolder(String email, String subject, String folderName) {
        return frameForPHPscriptUsageWithThreeParameters("http://pocketteacher.ro/adders/add_folder.php", email, "email", subject, "subject", folderName, "name");
    }


    /*                                 *** Deleter Scripts ***                                    */
    public static String removeSubject(String email, String subject) {
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/deleters/remove_subject.php", email, "email", subject, "subject");
    }

    public static String removeFolder(String email, String subject, String folder) {
        return frameForPHPscriptUsageWithThreeParameters("http://pocketteacher.ro/deleters/remove_folder.php", email, "email", subject, "subject", folder, "folder");
    }

    public static String removeFile(String username, String subject, String folder, String fileTitle, String filePath) {
        return frameForPHPscriptUsageWithFiveParameters("http://pocketteacher.ro/deleters/remove_file.php", username, "username", subject, "subject", folder, "folder", fileTitle, "file_title", filePath, "file_path");
    }

    public static String deleteNotification(String usernamePoster, String subject, String folder, String title, String flag, String usernameSender, String usernameReceiver, String comment) {

        String flagToSend = "-1";

        if (flag.equals("liked your post.")) {
            flagToSend = "0";
        }
        if (flag.equals("commented on your post.")) {
            flagToSend = "1";
        }
        if (flag.equals("liked your comment.")) {
            flagToSend = "2";
        }
        if (flag.equals("commented on a post you commented.")) {
            flagToSend = "3";
        }
        if (flag.equals("approved your follow request.")) {
            flagToSend = "4";
        }

        if (flagToSend.equals("4")) {
            return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/deleters/remove_notification_approved_follow_request.php", usernameSender, "username_teacher", usernameReceiver, "username_student");
        } else {
            return frameForPHPscriptUsageWithEightParameters("http://pocketteacher.ro/deleters/remove_notification.php", usernamePoster, "username_poster", subject, "subject", folder, "folder", title, "title", flagToSend, "flag", comment, "comment", usernameSender, "username_sender", usernameReceiver, "username_receiver");
        }
    }

    public static String deleteAllNotifications(String username) {
        return frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/deleters/remove_notifications.php", username, "username");
    }

    public static String removeComment(String comment, String usernamePoster, String subject, String folder, String title, String timestamp, String usernameSender){
        return frameForPHPscriptUsageWithSevenParameters("http://pocketteacher.ro/deleters/remove_comment.php", comment, "comment", usernamePoster, "username_file_poster", subject, "subject", folder, "folder", title, "title", timestamp, "timestamp", usernameSender, "username_sender");
    }

    public static String deleteProfileImageBasedOnUsername(String username) {
        return frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/deleters/using_username_delete_profile_picture.php", username, "username");
    }


    /*                                  *** Getter Scripts ***                                    */
    public static ArrayList<ArrayList> getSubjectsForDomain(String domain) {
        String jsonResult = frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/get_all_subjects_for_domain.php", domain, "domain");

        ArrayList<String> subjectNames = new ArrayList<>();
        ArrayList<String> subjectDescriptions = new ArrayList<>();

        JSONArray jsonArray;
        String subjectName, subjectDescription;

        try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            int count = 0;
            jsonArray = jsonObject.getJSONArray("data");


            while (count < jsonArray.length()) {
                JSONObject jo = jsonArray.getJSONObject(count);

                subjectName = jo.getString("subject_name");
                subjectNames.add(subjectName);
                subjectDescription = jo.getString("subject_description");
                subjectDescriptions.add(subjectDescription);

                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<ArrayList> information = new ArrayList<>();
        information.add(subjectNames);
        information.add(subjectDescriptions);

        return information;
    }

    public static ArrayList<String> getDomainForSubject(String subject) {
        String jsonResult = frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/get_domain_for_subject.php", subject, "subject");


        JSONArray jsonArray;
        ArrayList<String> information = new ArrayList<>();
        String domainName, domainDescription, domainImage;


        try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            jsonArray = jsonObject.getJSONArray("data");
            JSONObject jo = jsonArray.getJSONObject(0);

            domainName = jo.getString("domain_name");
            information.add(domainName);
            domainDescription = jo.getString("domain_description");
            information.add(domainDescription);
            domainImage = jo.getString("image");
            information.add(domainImage);


        } catch (Exception e) {
            e.printStackTrace();
        }


        return information;
    }

    public static ArrayList<ArrayList> getAllDomains() {
        String jsonResult = frameForPHPscriptUsageWithNoParameters("http://pocketteacher.ro/getters/get_all_domains.php");


        ArrayList<String> domainNames = new ArrayList<>();
        ArrayList<String> domainDescriptions = new ArrayList<>();


        JSONArray jsonArray;
        String domainName, domainDescription;


        try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            int count = 0;
            jsonArray = jsonObject.getJSONArray("data");


            while (count < jsonArray.length()) {
                JSONObject jo = jsonArray.getJSONObject(count);

                domainName = jo.getString("domain_name");
                domainNames.add(domainName);
                domainDescription = jo.getString("domain_description");
                domainDescriptions.add(domainDescription);

                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<ArrayList> information = new ArrayList<>();
        information.add(domainNames);
        information.add(domainDescriptions);

        return information;
    }

    public static ArrayList<ArrayList> getAllSubjects() {
        String jsonResult = frameForPHPscriptUsageWithNoParameters("http://pocketteacher.ro/getters/get_all_subjects.php");


        ArrayList<String> subjectNames = new ArrayList<>();
        ArrayList<String> subjectDescriptions = new ArrayList<>();
        ArrayList<String> subjectImages = new ArrayList<>();


        JSONArray jsonArray;
        String subjectName, subjectDescription, subjectImage;


        try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            int count = 0;
            jsonArray = jsonObject.getJSONArray("data");


            while (count < jsonArray.length()) {
                JSONObject jo = jsonArray.getJSONObject(count);

                subjectName = jo.getString("subject_name");
                subjectNames.add(subjectName);
                subjectDescription = jo.getString("subject_description");
                subjectDescriptions.add(subjectDescription);
                subjectImage = jo.getString("image");
                subjectImages.add(subjectImage);


                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        ArrayList<ArrayList> information = new ArrayList<>();
        information.add(subjectNames);
        information.add(subjectDescriptions);
        information.add(subjectImages);

        return information;
    }

    public static ArrayList<Subject> getAllSubjectsForTeacher(String email) {
        String jsonResult = frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/get_all_subjects_for_teacher.php", email, "email");


        ArrayList<String> subjectNames = new ArrayList<>();
        ArrayList<String> subjectDescriptions = new ArrayList<>();
        ArrayList<String> domainNames = new ArrayList<>();
        ArrayList<String> domainDescriptions = new ArrayList<>();
        ArrayList<String> images = new ArrayList<>();

        JSONArray jsonArray;
        String subjectName, subjectDescription, domainName, domainDescription, image;

        try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            int count = 0;
            jsonArray = jsonObject.getJSONArray("data");


            while (count < jsonArray.length()) {
                JSONObject jo = jsonArray.getJSONObject(count);

                subjectName = jo.getString("subject_name");
                subjectNames.add(subjectName);
                subjectDescription = jo.getString("subject_description");
                subjectDescriptions.add(subjectDescription);
                domainName = jo.getString("domain_name");
                domainNames.add(domainName);
                domainDescription = jo.getString("domain_description");
                domainDescriptions.add(domainDescription);
                image = jo.getString("image");
                images.add(image);

                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<Subject> subjects = new ArrayList<>();
        for (int i = 0; i < subjectNames.size(); i++) {
            subjects.add(new Subject(subjectNames.get(i), subjectDescriptions.get(i), domainNames.get(i), domainDescriptions.get(i), images.get(i)));
        }
        return subjects;

    }

    public static ArrayList<Teacher> getAllPremiumTeachers(String usernameStudent) {
        String jsonResult = frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/get_all_teachers_premium.php", usernameStudent, "username");


        ArrayList<String> usernames = new ArrayList<>();
        ArrayList<String> firstNames = new ArrayList<>();
        ArrayList<String> lastNames = new ArrayList<>();
        ArrayList<String> genders = new ArrayList<>();
        ArrayList<String> universities = new ArrayList<>();
        ArrayList<String> images = new ArrayList<>();

        JSONArray jsonArray;
        String username, firstName, lastName, gender, university, image;

        try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            int count = 0;
            jsonArray = jsonObject.getJSONArray("data");


            while (count < jsonArray.length()) {
                JSONObject jo = jsonArray.getJSONObject(count);

                if (!jo.getString("blocked").equals("1")) {

                    username = jo.getString("username");
                    usernames.add(username);
                    firstName = jo.getString("first_name");
                    if (firstName.equals("null")) {
                        firstName = "";
                    }
                    firstNames.add(firstName);
                    lastName = jo.getString("last_name");
                    if (lastName.equals("null")) {
                        lastName = "";
                    }
                    lastNames.add(lastName);
                    gender = jo.getString("gender");
                    genders.add(gender);
                    university = jo.getString("university");
                    if (university.equals("null")) {
                        university = "";
                    }
                    universities.add(university);
                    image = jo.getString("image");
                    images.add(image);
                }
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<Teacher> teachers = new ArrayList<>();
        for (int i = 0; i < usernames.size(); i++) {
            teachers.add(new Teacher(usernames.get(i), firstNames.get(i), lastNames.get(i), genders.get(i), universities.get(i), images.get(i)));
        }
        return teachers;
    }

    public static ArrayList<Teacher> getAllTeachers(String usernameStudent) {
        String jsonResult = frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/get_all_teachers.php", usernameStudent, "username");


        ArrayList<String> usernames = new ArrayList<>();
        ArrayList<String> firstNames = new ArrayList<>();
        ArrayList<String> lastNames = new ArrayList<>();
        ArrayList<String> genders = new ArrayList<>();
        ArrayList<String> universities = new ArrayList<>();
        ArrayList<String> images = new ArrayList<>();
        ArrayList<String> subjects = new ArrayList<>();
        ArrayList<String> domains = new ArrayList<>();


        JSONArray jsonArray;
        String username, firstName, lastName, gender, university, image, subject, domain;

        try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            int count = 0;
            jsonArray = jsonObject.getJSONArray("data");


            while (count < jsonArray.length()) {
                JSONObject jo = jsonArray.getJSONObject(count);

                if (!jo.getString("blocked").equals("1")) {
                    username = jo.getString("username");
                    usernames.add(username);
                    firstName = jo.getString("first_name");
                    if (firstName.equals("null")) {
                        firstName = "";
                    }
                    firstNames.add(firstName);
                    lastName = jo.getString("last_name");
                    if (lastName.equals("null")) {
                        lastName = "";
                    }
                    lastNames.add(lastName);
                    gender = jo.getString("gender");
                    genders.add(gender);
                    university = jo.getString("university");
                    if (university.equals("null")) {
                        university = "";
                    }
                    universities.add(university);
                    image = jo.getString("image");
                    images.add(image);
                    subject = jo.getString("subject");
                    if (subject.equals("null")) {
                        subject = "";
                    }
                    subjects.add(subject);
                    domain = jo.getString("domain");
                    if (domain.equals("null")) {
                        domain = "";
                    }
                    domains.add(domain);
                }

                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        ArrayList<Teacher> teachers = new ArrayList<>();

        ArrayList<String> usedUsernames = new ArrayList<>();
        int position = 0;
        for (String currentUsername : usernames) {

            if (usedUsernames.contains(currentUsername)) {
                position++;
                continue;
            }
            usedUsernames.add(currentUsername);

            ArrayList<String> teacherSubjectNames = new ArrayList<>();
            ArrayList<String> subjectDomainNames = new ArrayList<>();

            ArrayList<Integer> positionsOfInterest = getSameTeacherPositions(usernames, currentUsername);

            for (int i = 0; i < positionsOfInterest.size(); i++) {
                if (!teacherSubjectNames.contains(subjects.get(positionsOfInterest.get(i)))) {
                    teacherSubjectNames.add(subjects.get(positionsOfInterest.get(i)));
                }
                if (!subjectDomainNames.contains(domains.get(positionsOfInterest.get(i)))) {
                    subjectDomainNames.add(domains.get(positionsOfInterest.get(i)));
                }
            }
            teachers.add(new Teacher(currentUsername, firstNames.get(position), lastNames.get(position), genders.get(position), universities.get(position), images.get(position), teacherSubjectNames, subjectDomainNames));

            position++;
        }


        return teachers;

    }

    public static ArrayList<Teacher> getAllFollowingTeachers(String usernameStudent) {
        String jsonResult = frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/get_all_teachers_following.php", usernameStudent, "username");

        ArrayList<String> usernames = new ArrayList<>();
        ArrayList<String> firstNames = new ArrayList<>();
        ArrayList<String> lastNames = new ArrayList<>();
        ArrayList<String> genders = new ArrayList<>();
        ArrayList<String> universities = new ArrayList<>();
        ArrayList<String> images = new ArrayList<>();
        ArrayList<String> subjects = new ArrayList<>();
        ArrayList<String> domains = new ArrayList<>();


        JSONArray jsonArray;
        String username, firstName, lastName, gender, university, image, subject, domain;

        try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            int count = 0;
            jsonArray = jsonObject.getJSONArray("data");


            while (count < jsonArray.length()) {
                JSONObject jo = jsonArray.getJSONObject(count);


                username = jo.getString("username");
                usernames.add(username);
                firstName = jo.getString("first_name");
                if (firstName.equals("null")) {
                    firstName = "";
                }
                firstNames.add(firstName);
                lastName = jo.getString("last_name");
                if (lastName.equals("null")) {
                    lastName = "";
                }
                lastNames.add(lastName);
                gender = jo.getString("gender");
                genders.add(gender);
                university = jo.getString("university");
                if (university.equals("null")) {
                    university = "";
                }
                universities.add(university);
                image = jo.getString("image");
                images.add(image);
                subject = jo.getString("subject");
                if (subject.equals("null")) {
                    subject = "";
                }
                subjects.add(subject);
                domain = jo.getString("domain");
                if (domain.equals("null")) {
                    domain = "";
                }
                domains.add(domain);


                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        ArrayList<Teacher> teachers = new ArrayList<>();

        ArrayList<String> usedUsernames = new ArrayList<>();
        int position = 0;
        for (String currentUsername : usernames) {

            if (usedUsernames.contains(currentUsername)) {
                position++;
                continue;
            }
            usedUsernames.add(currentUsername);

            ArrayList<String> teacherSubjectNames = new ArrayList<>();
            ArrayList<String> subjectDomainNames = new ArrayList<>();

            ArrayList<Integer> positionsOfInterest = getSameTeacherPositions(usernames, currentUsername);

            for (int i = 0; i < positionsOfInterest.size(); i++) {
                if (!teacherSubjectNames.contains(subjects.get(positionsOfInterest.get(i)))) {
                    teacherSubjectNames.add(subjects.get(positionsOfInterest.get(i)));
                }
                if (!subjectDomainNames.contains(domains.get(positionsOfInterest.get(i)))) {
                    subjectDomainNames.add(domains.get(positionsOfInterest.get(i)));
                }
            }
            teachers.add(new Teacher(currentUsername, firstNames.get(position), lastNames.get(position), genders.get(position), universities.get(position), images.get(position), teacherSubjectNames, subjectDomainNames));

            position++;
        }


        return teachers;
    }

    public static ArrayList<Student> getAllFollowers(String usernameTeacher) {
        String jsonResult = frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/get_all_followers.php", usernameTeacher, "username");

        ArrayList<String> usernames = new ArrayList<>();
        ArrayList<String> firstNames = new ArrayList<>();
        ArrayList<String> lastNames = new ArrayList<>();
        ArrayList<String> genders = new ArrayList<>();
        ArrayList<String> universities = new ArrayList<>();
        ArrayList<String> images = new ArrayList<>();


        JSONArray jsonArray;
        String username, firstName, lastName, gender, university, image;

        try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            int count = 0;
            jsonArray = jsonObject.getJSONArray("data");


            while (count < jsonArray.length()) {
                JSONObject jo = jsonArray.getJSONObject(count);


                username = jo.getString("username");
                usernames.add(username);
                firstName = jo.getString("first_name");
                if (firstName.equals("null")) {
                    firstName = "";
                }
                firstNames.add(firstName);
                lastName = jo.getString("last_name");
                if (lastName.equals("null")) {
                    lastName = "";
                }
                lastNames.add(lastName);
                gender = jo.getString("gender");
                genders.add(gender);
                university = jo.getString("university");
                if (university.equals("null")) {
                    university = "";
                }
                universities.add(university);
                image = jo.getString("image");
                images.add(image);

                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<Student> students = new ArrayList<>();
        for (int i = 0; i < usernames.size(); i++) {
            students.add(new Student(usernames.get(i), firstNames.get(i), lastNames.get(i), genders.get(i), universities.get(i), images.get(i)));
        }

        return students;
    }

    public static ArrayList<Student> getAllFollowingRequestStudents(String usernameTeacher) {
     String jsonResult = frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/get_all_follow_requesting_students.php", usernameTeacher, "username");

     ArrayList<String> usernames = new ArrayList<>();
     ArrayList<String> firstNames = new ArrayList<>();
     ArrayList<String> lastNames = new ArrayList<>();
     ArrayList<String> genders = new ArrayList<>();
     ArrayList<String> universities = new ArrayList<>();
     ArrayList<String> images = new ArrayList<>();


     JSONArray jsonArray;
     String username, firstName, lastName, gender, university, image;

     try {
         JSONObject jsonObject = new JSONObject(jsonResult);
         int count = 0;
         jsonArray = jsonObject.getJSONArray("data");


         while (count < jsonArray.length()) {
             JSONObject jo = jsonArray.getJSONObject(count);


             username = jo.getString("username");
             usernames.add(username);
             firstName = jo.getString("first_name");
             if (firstName.equals("null")) {
                 firstName = "";
             }
             firstNames.add(firstName);
             lastName = jo.getString("last_name");
             if (lastName.equals("null")) {
                 lastName = "";
             }
             lastNames.add(lastName);
             gender = jo.getString("gender");
             genders.add(gender);
             university = jo.getString("university");
             if (university.equals("null")) {
                 university = "";
             }
             universities.add(university);
             image = jo.getString("image");
             images.add(image);

             count++;
         }
     } catch (Exception e) {
         e.printStackTrace();
     }

     ArrayList<Student> students = new ArrayList<>();
     for (int i = 0; i < usernames.size(); i++) {
         students.add(new Student(usernames.get(i), firstNames.get(i), lastNames.get(i), genders.get(i), universities.get(i), images.get(i)));
     }

     return students;
 }

    public static ArrayList<Student> getAllBlockedStudents(String usernameTeacher) {
        String jsonResult = frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/get_all_blocked_students.php", usernameTeacher, "username");

        ArrayList<String> usernames = new ArrayList<>();
        ArrayList<String> firstNames = new ArrayList<>();
        ArrayList<String> lastNames = new ArrayList<>();
        ArrayList<String> genders = new ArrayList<>();
        ArrayList<String> universities = new ArrayList<>();
        ArrayList<String> images = new ArrayList<>();


        JSONArray jsonArray;
        String username, firstName, lastName, gender, university, image, subject, domain;

        try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            int count = 0;
            jsonArray = jsonObject.getJSONArray("data");


            while (count < jsonArray.length()) {
                JSONObject jo = jsonArray.getJSONObject(count);


                username = jo.getString("username");
                usernames.add(username);
                firstName = jo.getString("first_name");
                if (firstName.equals("null")) {
                    firstName = "";
                }
                firstNames.add(firstName);
                lastName = jo.getString("last_name");
                if (lastName.equals("null")) {
                    lastName = "";
                }
                lastNames.add(lastName);
                gender = jo.getString("gender");
                genders.add(gender);
                university = jo.getString("university");
                if (university.equals("null")) {
                    university = "";
                }
                universities.add(university);
                image = jo.getString("image");
                images.add(image);

                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<Student> students = new ArrayList<>();
        for (int i = 0; i < usernames.size(); i++) {
            students.add(new Student(usernames.get(i), firstNames.get(i), lastNames.get(i), genders.get(i), universities.get(i), images.get(i)));
        }

        return students;
    }

    public static Teacher getDisplayedTeacher(String usernameTeacher, String usernameStudent) {
        String jsonResult = frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/getters/get_all_teacher_information_for_profile_display.php", usernameTeacher, "username_teacher", usernameStudent, "username_student");


        Teacher teacher = new Teacher();
        teacher.setUsername(usernameTeacher);

        JSONArray jsonArray;
        String firstName, lastName, gender, university, description, image, privacy, subject, domain, followingStatus, followingRequestStatus, followers;
        ArrayList<String> subjects = new ArrayList<>();
        ArrayList<String> domains = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            int count = 0;
            jsonArray = jsonObject.getJSONArray("data");


            while (count < jsonArray.length()) {
                JSONObject jo = jsonArray.getJSONObject(count);

                if (count == 0) {
                    firstName = jo.getString("first_name");
                    teacher.setFirstName(firstName);
                    lastName = jo.getString("last_name");
                    teacher.setLastName(lastName);
                    gender = jo.getString("gender");
                    teacher.setGender(gender);
                    university = jo.getString("university");
                    teacher.setUniversity(university);
                    description = jo.getString("description");
                    teacher.setProfileDescription(description);
                    image = jo.getString("image");
                    teacher.setProfileImageBase64(image);
                    privacy = jo.getString("privacy");
                    teacher.setPrivacy(privacy);
                    followingStatus = jo.getString("following_status");
                    teacher.setFollowingStatus(followingStatus);
                    followingRequestStatus = jo.getString("following_request_status");
                    teacher.setFollowingRequestStatus(followingRequestStatus);
                    followers = jo.getString("followers");
                    teacher.setFollowers(followers);
                }

                subject = jo.getString("subject");
                if (!subject.equals("null")) {
                    subjects.add(subject);
                    domain = jo.getString("domain");
                    domains.add(domain);
                }

                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        teacher.setSubjectNames(subjects);
        teacher.setDomains(domains);


        return teacher;

    }

    private static ArrayList<Integer> getSameTeacherPositions(ArrayList<String> usernames, String username) {
        ArrayList<Integer> indexList = new ArrayList<>();
        for (int i = 0; i < usernames.size(); i++) {
            if (username.equals(usernames.get(i))) {
                indexList.add(i);
            }
        }
        return indexList;
    }

    public static Subject getFullSubjectDetails(String subject) {
        String jsonResult = frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/get_all_subject_details.php", subject, "subject");

        String subjectName, subjectDescription, subjectImage, domainName, domainDescription, domainImage;
        JSONArray jsonArray;

        Subject subjectInfo = new Subject();

        try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            jsonArray = jsonObject.getJSONArray("data");

            JSONObject jo = jsonArray.getJSONObject(0);

            subjectName = jo.getString("subject_name");
            subjectInfo.setSubjectName(subjectName);
            subjectDescription = jo.getString("subject_description");
            subjectInfo.setSubjectDescription(subjectDescription);
            subjectImage = jo.getString("image_subject");
            subjectInfo.setImage(subjectImage);
            domainName = jo.getString("domain_name");
            subjectInfo.setDomainName(domainName);
            domainDescription = jo.getString("domain_description");
            subjectInfo.setDomainDescription(domainDescription);
            domainImage = jo.getString("image_domain");
            subjectInfo.setDomainImage(domainImage);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return subjectInfo;
    }

    public static ArrayList<String> getFoldersForSubjectAndTeacher(String username, String subject) {
        String jsonResult = frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/getters/get_all_folders.php", username, "username", subject, "subject");


        JSONArray jsonArray;
        ArrayList<String> folderNames = new ArrayList<>();
        String folderName;

        try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            int count = 0;
            jsonArray = jsonObject.getJSONArray("data");


            while (count < jsonArray.length()) {
                JSONObject jo = jsonArray.getJSONObject(count);

                folderName = jo.getString("name");
                folderNames.add(folderName);


                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return folderNames;
    }

    public static ArrayList<ArrayList> getAllFilesForFolder(String usernameWatcher, String usernamePoster, String subject, String folder) {
        String jsonResult = frameForPHPscriptUsageWithFourParameters("http://pocketteacher.ro/getters/get_all_files_for_folder.php", usernameWatcher, "username_watcher", usernamePoster, "username_poster", subject, "subject", folder, "folder");

        ArrayList<String> urls = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> descriptions = new ArrayList<>();
        ArrayList<String> likedStatuses = new ArrayList<>();


        JSONArray jsonArray;
        String url, title, description, likedStatus;

        try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            int count = 0;
            jsonArray = jsonObject.getJSONArray("data");


            while (count < jsonArray.length()) {
                JSONObject jo = jsonArray.getJSONObject(count);

                url = jo.getString("file_url");
                urls.add(url);
                title = jo.getString("title");
                titles.add(title);
                description = jo.getString("description");
                descriptions.add(description);
                likedStatus = jo.getString("liked_status");
                likedStatuses.add(likedStatus);

                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<ArrayList> information = new ArrayList<>();
        information.add(urls);
        information.add(titles);
        information.add(descriptions);
        information.add(likedStatuses);

        return information;
    }

    public static String getLikesForPost(String username, String subject, String folder, String title) {
        return frameForPHPscriptUsageWithFourParameters("http://pocketteacher.ro/getters/get_number_of_likes_for_post.php", username, "username", subject, "subject", folder, "folder", title, "title");
    }

    public static String getCommentsForPost(String username, String subject, String folder, String title) {
        return frameForPHPscriptUsageWithFourParameters("http://pocketteacher.ro/getters/get_number_of_comments_for_post.php", username, "username", subject, "subject", folder, "folder", title, "title");
    }

    public static ArrayList<String> getFileInformation(String username, String subject, String folder, String title) {
        String jsonResult = frameForPHPscriptUsageWithFourParameters("http://pocketteacher.ro/getters/get_file_info.php", username, "username", subject, "subject", folder, "folder", title, "title");

        JSONArray jsonArray;

        ArrayList<String> file = new ArrayList<>();
        String fileUrl, fileTitle, description, timestamp, timeSince;
        long timeDifference;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate = Calendar.getInstance().getTime();

        try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            int count = 0;
            jsonArray = jsonObject.getJSONArray("data");


            while (count < jsonArray.length()) {
                JSONObject jo = jsonArray.getJSONObject(count);

                fileUrl = jo.getString("file_url");
                file.add(fileUrl);
                fileTitle = jo.getString("title");
                file.add(fileTitle);
                description = jo.getString("description");
                file.add(description);

                timestamp = jo.getString("timestamp");
                timeDifference = currentDate.getTime() - format.parse(timestamp).getTime();
                timeDifference /= 1000; // obtain the time in seconds
                timeSince = getTimeSince(timeDifference);
                file.add(timeSince);

                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    public static ArrayList<Comment> getAllCommentsForPost(String usernamePoster, String usernameWatcher, String subject, String folder, String title) {

        String jsonResult = frameForPHPscriptUsageWithFiveParameters("http://pocketteacher.ro/getters/get_all_comments_for_post.php", usernamePoster, "username_poster", usernameWatcher, "username_watcher", subject, "subject", folder, "folder", title, "title");


        ArrayList<String> types = new ArrayList<>();
        ArrayList<String> usernames = new ArrayList<>();
        ArrayList<String> imagesBase64s = new ArrayList<>();
        ArrayList<String> genders = new ArrayList<>();
        ArrayList<String> commentTexts = new ArrayList<>();
        ArrayList<String> timestamps = new ArrayList<>();
        ArrayList<String> timeSinces = new ArrayList<>();
        ArrayList<Integer> numberLikes = new ArrayList<>();
        ArrayList<String> likedStatuses = new ArrayList<>();


        JSONArray jsonArray;
        String type, newUsername, imageBase64, gender, commentText, timestamp, timeSince, numberLike, likedStatus;
        long timeDifference;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate = Calendar.getInstance().getTime();


        try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            int count = 0;
            jsonArray = jsonObject.getJSONArray("data");


            while (count < jsonArray.length()) {
                JSONObject jo = jsonArray.getJSONObject(count);

                type = jo.getString("type");
                types.add(type);
                newUsername = jo.getString("username");
                usernames.add(newUsername);
                imageBase64 = jo.getString("image");
                imagesBase64s.add(imageBase64);
                gender = jo.getString("gender");
                genders.add(gender);
                commentText = jo.getString("comment_text");
                commentTexts.add(commentText);
                timestamp = jo.getString("timestamp");
                timestamps.add(timestamp);
                timeDifference = currentDate.getTime() - format.parse(timestamp).getTime();
                timeDifference /= 1000; // obtain the time in seconds
                timeSince = getTimeSince(timeDifference);
                timeSinces.add(timeSince);
                numberLike = jo.getString("number_likes");
                numberLikes.add(Integer.parseInt(numberLike));
                likedStatus = jo.getString("liked_status");
                likedStatuses.add(likedStatus);

                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        ArrayList<Comment> comments = new ArrayList<>();

        for (int i = 0; i < usernames.size(); i++) {
            comments.add(new Comment(types.get(i), usernames.get(i), imagesBase64s.get(i), genders.get(i), commentTexts.get(i), timeSinces.get(i), timestamps.get(i),numberLikes.get(i), likedStatuses.get(i)));
        }

        return comments;
    }

    public static ArrayList<Post> getAllPosts(String usernameStudent){
        String jsonResult = frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/get_all_posts_for_user.php", usernameStudent, "username");


        ArrayList<String> usernames = new ArrayList<>();
        ArrayList<String> images = new ArrayList<>();
        ArrayList<String> genders = new ArrayList<>();
        ArrayList<String> firstNames = new ArrayList<>();
        ArrayList<String> lastNames = new ArrayList<>();
        ArrayList<String> timestamps = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> subjects = new ArrayList<>();
        ArrayList<String> folders = new ArrayList<>();
        ArrayList<String> descriptions = new ArrayList<>();
        ArrayList<String> likes = new ArrayList<>();
        ArrayList<String> comments = new ArrayList<>();
        ArrayList<String> likedStatuses = new ArrayList<>();
        ArrayList<String> fileUrls = new ArrayList<>();


        JSONArray jsonArray;
        String username, image, gender, firstName, lastName, timestamp, timeSince, title, subject, folder, description, like, comment, likedStatus, fileUrl;

        long timeDifference;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate = Calendar.getInstance().getTime();

        try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            int count = 0;
            jsonArray = jsonObject.getJSONArray("data");

            while (count < jsonArray.length()) {
                JSONObject jo = jsonArray.getJSONObject(count);

                username = jo.getString("username");
                usernames.add(username);
                image = jo.getString("image");
                images.add(image);
                gender = jo.getString("gender");
                genders.add(gender);
                firstName = jo.getString("first_name");
                firstNames.add(firstName);
                lastName = jo.getString("last_name");
                lastNames.add(lastName);

                timestamp = jo.getString("timestamp");
                timeDifference = currentDate.getTime() - format.parse(timestamp).getTime();
                timeDifference /= 1000; // obtain the time in seconds
                timeSince = getTimeSince(timeDifference);
                timestamps.add(timeSince);

                fileUrl = jo.getString("file_url");
                fileUrls.add(fileUrl);
                title = jo.getString("title");
                titles.add(title);
                subject = jo.getString("subject");
                subjects.add(subject);
                folder = jo.getString("folder");
                folders.add(folder);
                description = jo.getString("description");
                descriptions.add(description);
                like = jo.getString("likes");
                likes.add(like);
                comment = jo.getString("comments");
                comments.add(comment);
                likedStatus = jo.getString("liked_status");
                likedStatuses.add(likedStatus);


                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        ArrayList<Post> posts = new ArrayList<>();

        for (int i = 0; i < usernames.size(); i++) {
            String name;
            if(firstNames.get(i).equals("null") || lastNames.get(i).equals("null")){
                name = "Unknown Name";
            } else{
                name = firstNames.get(i) + " " + lastNames.get(i);
            }
            posts.add(new Post(usernames.get(i), images.get(i), genders.get(i), name, timestamps.get(i), titles.get(i), subjects.get(i), folders.get(i), descriptions.get(i), likes.get(i), comments.get(i), likedStatuses.get(i), fileUrls.get(i)));
        }


        return posts;
    }

    public static String getSubjectImage(String subject) {
        return frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/get_subject_image.php", subject, "subject");
    }

    public static String getFollowers(String username) {
        return frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/get_followers.php", username, "username");
    }

    public static String getFollowing(String username) {
        return frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/get_following.php", username, "username");
    }

    public static String getNumberOfFollowRequests(String username) {
        return frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/get_number_of_follow_requests.php", username, "username");
    }

    public static ArrayList<Notification> getAllNotifications(String username) {
        String jsonResult = frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/get_all_notifications.php", username, "username");

        try {
            if (jsonResult.equals("")) {
                return new ArrayList<>();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        ArrayList<String> usernameSenders = new ArrayList<>();
        ArrayList<String> images = new ArrayList<>();
        ArrayList<String> genders = new ArrayList<>();
        ArrayList<String> flags = new ArrayList<>();
        ArrayList<String> timestamps = new ArrayList<>();
        ArrayList<String> usernamePosters = new ArrayList<>();
        ArrayList<String> subjectNames = new ArrayList<>();
        ArrayList<String> folderNames = new ArrayList<>();
        ArrayList<String> posts = new ArrayList<>();
        ArrayList<String> comments = new ArrayList<>();


        JSONArray jsonArray;

        String usernameSender, image, gender, flag, timestamp, timeSince, usernamePoster, subjectName, folderName, post, comment;


        long timeDifference;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate = Calendar.getInstance().getTime();

        try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            int count = 0;
            jsonArray = jsonObject.getJSONArray("data");

            while (count < jsonArray.length()) {
                JSONObject jo = jsonArray.getJSONObject(count);

                usernameSender = jo.getString("username_sender");
                usernameSenders.add(usernameSender);
                image = jo.getString("image");
                images.add(image);
                gender = jo.getString("gender");
                genders.add(gender);
                switch (jo.getString("flag")) {
                    case "0":
                        flag = "liked your post.";
                        break;
                    case "1":
                        flag = "commented on your post.";
                        break;
                    case "2":
                        flag = "liked your comment.";
                        break;
                    case "3":
                        flag = "commented on a post you commented.";
                        break;
                    case "4":
                        flag = "approved your follow request.";
                        break;
                    default:
                        flag = "";
                }
                flags.add(flag);

                timestamp = jo.getString("timestamp");
                timeDifference = currentDate.getTime() - format.parse(timestamp).getTime();
                timeDifference /= 1000; // obtain the time in seconds
                timeSince = getTimeSince(timeDifference);
                timestamps.add(timeSince);

                usernamePoster = jo.getString("username_poster");
                usernamePosters.add(usernamePoster);
                subjectName = jo.getString("subject_name");
                subjectNames.add(subjectName);
                folderName = jo.getString("folder_name");
                folderNames.add(folderName);
                post = jo.getString("post_name");
                posts.add(post);
                comment = jo.getString("comment");
                comments.add(comment);

                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        ArrayList<Notification> notifications = new ArrayList<>();

        for (int i = 0; i < usernameSenders.size(); i++) {
            notifications.add(new Notification(images.get(i), timestamps.get(i), usernameSenders.get(i), genders.get(i), flags.get(i), usernamePosters.get(i), subjectNames.get(i), folderNames.get(i), posts.get(i), comments.get(i)));
        }

        return notifications;
    }

    public static String getNumberOfNotifications (String username){
        return frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/get_number_of_notifications.php", username, "username");
    }

    public static String getLikedStatus(String usernameWatcher, String usernamePoster, String subject, String folder, String title) {
        return frameForPHPscriptUsageWithFiveParameters("http://pocketteacher.ro/getters/get_liked_status.php", usernameWatcher, "username_watcher", usernamePoster, "username_poster", subject, "subject", folder, "folder", title, "title");
    }

    public static String getCommentTimestamp(String comment, String usernameFilePoster, String subject, String folder, String title, String usernameSender){
        return frameForPHPscriptUsageWithSixParameters("http://pocketteacher.ro/getters/get_comment_timestamp.php", comment, "comment", usernameFilePoster, "username_file_poster", subject, "subject", folder, "folder", title, "title", usernameSender, "username_sender");
    }

    public static String getUsernameBasedOnUsername(String username) {
        return frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/using_username_get_username.php", username, "username");
    }

    public static String getFirstNameBasedOnUsername(String username) {
        return frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/using_username_get_first_name.php", username, "username");
    }

    public static String getLastNameBasedOnUsername(String username) {
        return frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/using_username_get_last_name.php", username, "username");
    }

    public static String getConfirmationCodeBasedOnUsername(String username) {
        return frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/using_username_get_conf_code.php", username, "username");
    }

    public static String getOnboardingBasedOnUsername(String username) {
        return frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/using_username_get_onboarding.php", username, "username");
    }

    public static String getPasswordBasedOnUsername(String username) {
        return frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/using_username_get_password.php", username, "username");
    }

    public static String getGenderBasedOnUsername(String username) {
        return frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/using_username_get_gender.php", username, "username");
    }

    public static String getUniversityBasedOnUsername(String username) {
        return frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/using_username_get_university.php", username, "username");
    }

    public static String getProfileDescriptionBasedOnUsername(String username) {
        return frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/using_username_get_description.php", username, "username");
    }

    public static String getProfileImageBasedOnUsername(String username) {
        return frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/using_username_get_profile_image.php", username, "username");
    }

    public static String getPrivacyBasedOnUsername(String username) {
        return frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/using_username_get_privacy.php", username, "username");
    }

    public static String getAutoLoginBasedOnUsername(String username) {
        return frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/using_username_get_auto_login.php", username, "username");
    }

    public static String getEmailBasedOnEmail(String email) {
        return frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/using_email_get_email.php", email, "email");
    }

    public static String getPasswordBasedOnEmail(String email) {
        return frameForPHPscriptUsageWithOneParameter("http://pocketteacher.ro/getters/using_email_get_password.php", email, "email");
    }



    /*                                  *** Setter Scripts ***                                    */
    public static String setEmailBasedOnEmail(String email, String newEmail) {
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/setters/using_email_set_email.php", email, "email", newEmail, "new_email");
    }

    public static String setConfirmationCodeBasedOnEmail(String email, String newEmailConfirmationCode) {
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/setters/using_email_set_email_confirmation_code.php", email, "email", newEmailConfirmationCode, "new_email_confirmation_code");
    }

    public static String setStatusBasedOnEmail(String email, String newStatus) {
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/setters/using_email_set_status.php", email, "email", newStatus, "new_status");
    }

    public static String setPasswordBasedOnEmail(String email, String newPassword) {
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/setters/using_email_set_password.php", email, "email", newPassword, "new_password");
    }

    public static String setUsernameBasedOnUsername(String username, String newUsername) {
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/setters/using_username_set_username.php", username, "username", newUsername, "new_username");
    }

    public static String setFirstNameBasedOnUsername(String username, String newFirstName) {
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/setters/using_username_set_first_name.php", username, "username", newFirstName, "new_first_name");
    }

    public static String setLastNameBasedOnUsername(String username, String newLastName) {
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/setters/using_username_set_last_name.php", username, "username", newLastName, "new_last_name");
    }

    public static String setOnboardingBasedOnUsername(String username, String onboarding) {
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/setters/using_username_set_onboarding.php", username, "username", onboarding, "onboarding");
    }

    public static String setPasswordBasedOnUsername(String username, String newPassword) {
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/setters/using_username_set_password.php", username, "username", newPassword, "new_password");
    }

    public static String setGenderBasedOnUsername(String username, String newGender) {
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/setters/using_username_set_gender.php", username, "username", newGender, "new_gender");
    }

    public static String setUniversityBasedOnUsername(String username, String newUniversity) {
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/setters/using_username_set_university.php", username, "username", newUniversity, "new_university");
    }

    public static String setProfileDescriptionBasedOnUsername(String username, String newDescription) {
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/setters/using_username_set_description.php", username, "username", newDescription, "new_description");
    }

    public static String setProfileImageBasedOnUsername(String username, String imageBase64, String imageName) {
        return frameForPHPscriptUsageWithThreeParameters("http://pocketteacher.ro/setters/using_username_set_profile_picture.php", username, "username", imageBase64, "image_base64", imageName, "image_name");
    }

    public static String setPrivacyBasedOnUsername(String username, String newPrivacy) {
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/setters/using_username_set_privacy.php", username, "username", newPrivacy, "new_privacy");
    }

    public static String setAutoLoginBasedOnUsername(String username, String newAutoLogin) {
        return frameForPHPscriptUsageWithTwoParameters("http://pocketteacher.ro/setters/using_username_set_auto_login.php", username, "username", newAutoLogin, "new_auto_login");
    }


    /*                          *** B A S E 64    C O N V E R T O R S***                          */
    public static String convertImageToBase64(Bitmap bmp) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public static Bitmap convertBase64toImage(String base64) {
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }


    /*                                 *** T E M P L A T E S ***                                  */
    private static String frameForPHPscriptUsageWithNoParameters(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(false);

            // To send the data
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String result;
            while ((result = bufferedReader.readLine()) != null) {
                stringBuilder.append(result + "\n");
            }
            HelpingFunctions.closeReadConnections(bufferedReader, inputStream);

            httpURLConnection.disconnect();


            return stringBuilder.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String frameForPHPscriptUsageWithOneParameter(String urlString, String text, String type) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            // To send the data
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            String data = URLEncoder.encode(type, "UTF-8") + "=" + URLEncoder.encode(text, "UTF-8");
            bufferedWriter.write(data);
            HelpingFunctions.closeWriteConnections(bufferedWriter, outputStream);


            // To obtain results
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String result;
            while ((result = bufferedReader.readLine()) != null) {
                stringBuilder.append(result + "\n");
            }
            HelpingFunctions.closeReadConnections(bufferedReader, inputStream);

            httpURLConnection.disconnect();


            return stringBuilder.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String frameForPHPscriptUsageWithTwoParameters(String urlString, String text1, String type1, String text2, String type2) {
        try {

            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            // To send the data
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            String data = URLEncoder.encode(type1, "UTF-8") + "=" + URLEncoder.encode(text1, "UTF-8") + "&" +
                    URLEncoder.encode(type2, "UTF-8") + "=" + URLEncoder.encode(text2, "UTF-8");
            bufferedWriter.write(data);
            HelpingFunctions.closeWriteConnections(bufferedWriter, outputStream);


            // To obtain results
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String result;
            while ((result = bufferedReader.readLine()) != null) {
                stringBuilder.append(result + "\n");
            }
            HelpingFunctions.closeReadConnections(bufferedReader, inputStream);

            httpURLConnection.disconnect();


            return stringBuilder.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String frameForPHPscriptUsageWithThreeParameters(String urlString, String text1, String type1, String text2, String type2, String text3, String type3) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            // To send the data
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            String data = URLEncoder.encode(type1, "UTF-8") + "=" + URLEncoder.encode(text1, "UTF-8") + "&" +
                    URLEncoder.encode(type2, "UTF-8") + "=" + URLEncoder.encode(text2, "UTF-8") + "&" +
                    URLEncoder.encode(type3, "UTF-8") + "=" + URLEncoder.encode(text3, "UTF-8");
            bufferedWriter.write(data);
            HelpingFunctions.closeWriteConnections(bufferedWriter, outputStream);


            // To obtain results
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String result;
            while ((result = bufferedReader.readLine()) != null) {
                stringBuilder.append(result + "\n");
            }
            HelpingFunctions.closeReadConnections(bufferedReader, inputStream);

            httpURLConnection.disconnect();


            return stringBuilder.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String frameForPHPscriptUsageWithFourParameters(String urlString, String text1, String type1, String text2, String type2, String text3, String type3, String text4, String type4) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            // To send the data
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            String data = URLEncoder.encode(type1, "UTF-8") + "=" + URLEncoder.encode(text1, "UTF-8") + "&" +
                    URLEncoder.encode(type2, "UTF-8") + "=" + URLEncoder.encode(text2, "UTF-8") + "&" +
                    URLEncoder.encode(type3, "UTF-8") + "=" + URLEncoder.encode(text3, "UTF-8") + "&" +
                    URLEncoder.encode(type4, "UTF-8") + "=" + URLEncoder.encode(text4, "UTF-8");

            bufferedWriter.write(data);
            HelpingFunctions.closeWriteConnections(bufferedWriter, outputStream);


            // To obtain results
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String result;
            while ((result = bufferedReader.readLine()) != null) {
                stringBuilder.append(result + "\n");
            }
            HelpingFunctions.closeReadConnections(bufferedReader, inputStream);

            httpURLConnection.disconnect();


            return stringBuilder.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String frameForPHPscriptUsageWithFiveParameters(String urlString, String text1, String type1, String text2, String type2, String text3, String type3, String text4, String type4, String text5, String type5) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            // To send the data
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            String data = URLEncoder.encode(type1, "UTF-8") + "=" + URLEncoder.encode(text1, "UTF-8") + "&" +
                    URLEncoder.encode(type2, "UTF-8") + "=" + URLEncoder.encode(text2, "UTF-8") + "&" +
                    URLEncoder.encode(type3, "UTF-8") + "=" + URLEncoder.encode(text3, "UTF-8") + "&" +
                    URLEncoder.encode(type4, "UTF-8") + "=" + URLEncoder.encode(text4, "UTF-8") + "&" +
                    URLEncoder.encode(type5, "UTF-8") + "=" + URLEncoder.encode(text5, "UTF-8");
            bufferedWriter.write(data);
            HelpingFunctions.closeWriteConnections(bufferedWriter, outputStream);


            // To obtain results
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String result;
            while ((result = bufferedReader.readLine()) != null) {
                stringBuilder.append(result + "\n");
            }
            HelpingFunctions.closeReadConnections(bufferedReader, inputStream);

            httpURLConnection.disconnect();


            return stringBuilder.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String frameForPHPscriptUsageWithSixParameters(String urlString, String text1, String type1, String text2, String type2, String text3, String type3, String text4, String type4, String text5, String type5, String text6, String type6) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            // To send the data
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            String data = URLEncoder.encode(type1, "UTF-8") + "=" + URLEncoder.encode(text1, "UTF-8") + "&" +
                    URLEncoder.encode(type2, "UTF-8") + "=" + URLEncoder.encode(text2, "UTF-8") + "&" +
                    URLEncoder.encode(type3, "UTF-8") + "=" + URLEncoder.encode(text3, "UTF-8") + "&" +
                    URLEncoder.encode(type4, "UTF-8") + "=" + URLEncoder.encode(text4, "UTF-8") + "&" +
                    URLEncoder.encode(type5, "UTF-8") + "=" + URLEncoder.encode(text5, "UTF-8") + "&" +
                    URLEncoder.encode(type6, "UTF-8") + "=" + URLEncoder.encode(text6, "UTF-8");
            bufferedWriter.write(data);
            HelpingFunctions.closeWriteConnections(bufferedWriter, outputStream);


            // To obtain results
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String result;
            while ((result = bufferedReader.readLine()) != null) {
                stringBuilder.append(result + "\n");
            }
            HelpingFunctions.closeReadConnections(bufferedReader, inputStream);

            httpURLConnection.disconnect();


            return stringBuilder.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String frameForPHPscriptUsageWithSevenParameters(String urlString, String text1, String type1, String text2, String type2, String text3, String type3, String text4, String type4, String text5, String type5, String text6, String type6, String text7, String type7) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            // To send the data
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            String data = URLEncoder.encode(type1, "UTF-8") + "=" + URLEncoder.encode(text1, "UTF-8") + "&" +
                    URLEncoder.encode(type2, "UTF-8") + "=" + URLEncoder.encode(text2, "UTF-8") + "&" +
                    URLEncoder.encode(type3, "UTF-8") + "=" + URLEncoder.encode(text3, "UTF-8") + "&" +
                    URLEncoder.encode(type4, "UTF-8") + "=" + URLEncoder.encode(text4, "UTF-8") + "&" +
                    URLEncoder.encode(type5, "UTF-8") + "=" + URLEncoder.encode(text5, "UTF-8") + "&" +
                    URLEncoder.encode(type6, "UTF-8") + "=" + URLEncoder.encode(text6, "UTF-8") + "&" +
                    URLEncoder.encode(type7, "UTF-8") + "=" + URLEncoder.encode(text7, "UTF-8");

            bufferedWriter.write(data);
            HelpingFunctions.closeWriteConnections(bufferedWriter, outputStream);


            // To obtain results
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String result;
            while ((result = bufferedReader.readLine()) != null) {
                stringBuilder.append(result + "\n");
            }
            HelpingFunctions.closeReadConnections(bufferedReader, inputStream);

            httpURLConnection.disconnect();


            return stringBuilder.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String frameForPHPscriptUsageWithEightParameters(String urlString, String text1, String type1, String text2, String type2, String text3, String type3, String text4, String type4, String text5, String type5, String text6, String type6, String text7, String type7, String text8, String type8) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            // To send the data
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            String data = URLEncoder.encode(type1, "UTF-8") + "=" + URLEncoder.encode(text1, "UTF-8") + "&" +
                    URLEncoder.encode(type2, "UTF-8") + "=" + URLEncoder.encode(text2, "UTF-8") + "&" +
                    URLEncoder.encode(type3, "UTF-8") + "=" + URLEncoder.encode(text3, "UTF-8") + "&" +
                    URLEncoder.encode(type4, "UTF-8") + "=" + URLEncoder.encode(text4, "UTF-8") + "&" +
                    URLEncoder.encode(type5, "UTF-8") + "=" + URLEncoder.encode(text5, "UTF-8") + "&" +
                    URLEncoder.encode(type6, "UTF-8") + "=" + URLEncoder.encode(text6, "UTF-8") + "&" +
                    URLEncoder.encode(type7, "UTF-8") + "=" + URLEncoder.encode(text7, "UTF-8") + "&" +
                    URLEncoder.encode(type8, "UTF-8") + "=" + URLEncoder.encode(text8, "UTF-8");

            bufferedWriter.write(data);
            HelpingFunctions.closeWriteConnections(bufferedWriter, outputStream);


            // To obtain results
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String result;
            while ((result = bufferedReader.readLine()) != null) {
                stringBuilder.append(result + "\n");
            }
            HelpingFunctions.closeReadConnections(bufferedReader, inputStream);

            httpURLConnection.disconnect();


            return stringBuilder.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String frameForPHPscriptUsageWithNineParameters(String urlString, String text1, String type1, String text2, String type2, String text3, String type3, String text4, String type4, String text5, String type5, String text6, String type6, String text7, String type7, String text8, String type8, String text9, String type9) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            // To send the data
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            String data = URLEncoder.encode(type1, "UTF-8") + "=" + URLEncoder.encode(text1, "UTF-8") + "&" +
                    URLEncoder.encode(type2, "UTF-8") + "=" + URLEncoder.encode(text2, "UTF-8") + "&" +
                    URLEncoder.encode(type3, "UTF-8") + "=" + URLEncoder.encode(text3, "UTF-8") + "&" +
                    URLEncoder.encode(type4, "UTF-8") + "=" + URLEncoder.encode(text4, "UTF-8") + "&" +
                    URLEncoder.encode(type5, "UTF-8") + "=" + URLEncoder.encode(text5, "UTF-8") + "&" +
                    URLEncoder.encode(type6, "UTF-8") + "=" + URLEncoder.encode(text6, "UTF-8") + "&" +
                    URLEncoder.encode(type7, "UTF-8") + "=" + URLEncoder.encode(text7, "UTF-8") + "&" +
                    URLEncoder.encode(type8, "UTF-8") + "=" + URLEncoder.encode(text8, "UTF-8") + "&" +
                    URLEncoder.encode(type9, "UTF-8") + "=" + URLEncoder.encode(text9, "UTF-8");

            bufferedWriter.write(data);
            HelpingFunctions.closeWriteConnections(bufferedWriter, outputStream);


            // To obtain results
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String result;
            while ((result = bufferedReader.readLine()) != null) {
                stringBuilder.append(result + "\n");
            }
            HelpingFunctions.closeReadConnections(bufferedReader, inputStream);

            httpURLConnection.disconnect();


            return stringBuilder.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void closeWriteConnections(BufferedWriter bufferedWriter, OutputStream outputStream) {
        try {

            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void closeReadConnections(BufferedReader bufferedReader, InputStream inputStream) {
        try {

            bufferedReader.close();
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
