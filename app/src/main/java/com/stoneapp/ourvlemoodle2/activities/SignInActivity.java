/*
 * Copyright 2016 Matthew Stone and Romario Maxwell.
 *
 * This file is part of OurVLE.
 *
 * OurVLE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OurVLE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OurVLE.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.stoneapp.ourvlemoodle2.activities;

import com.stoneapp.ourvlemoodle2.util.ImageDownloader;
import com.stoneapp.ourvlemoodle2.util.MoodleConstants;
import com.stoneapp.ourvlemoodle2.models.MoodleCourse;
import com.stoneapp.ourvlemoodle2.models.MoodleSiteInfo;
import com.stoneapp.ourvlemoodle2.models.MoodleToken;
import com.stoneapp.ourvlemoodle2.rest.MoodleLogin;
import com.stoneapp.ourvlemoodle2.rest.MoodleRestCourse;
import com.stoneapp.ourvlemoodle2.rest.MoodleRestSiteInfo;
import com.stoneapp.ourvlemoodle2.tasks.ForumSync;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.content.Context;

import android.os.Bundle;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.stoneapp.ourvlemoodle2.R;

import java.util.ArrayList;
import java.util.List;

public class SignInActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_MULTIPLE = 0x1;

    private EditText mUserId;
    private EditText mUserPass;
    private Button mBtn;
    private ProgressBar mProgressBar;
    private TextView mTvLoginHelp;
    private TextInputLayout mTvUserLabel;
    private TextInputLayout mTvUserPassLabel;
    private ImageView mImageIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final List<String> permissions = new ArrayList<String>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.WRITE_CALENDAR);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissions.size() > 0) {
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[permissions.size()]),
                    MY_PERMISSIONS_REQUEST_MULTIPLE);
        }

        setContentView(R.layout.activity_sign_in);

        mBtn = (Button) findViewById(R.id.button);
        mUserId = (EditText) findViewById(R.id.editTextUser);
        mUserPass = (EditText) findViewById(R.id.editPassword);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBarSignIn);
        mTvLoginHelp = (TextView) findViewById(R.id.login_help);
        mTvUserLabel = (TextInputLayout) findViewById(R.id.idnum_float_label);
        mTvUserPassLabel = (TextInputLayout) findViewById(R.id.password_float_label);
        mImageIcon = (ImageView) findViewById(R.id.imgView);
        mProgressBar.setIndeterminate(true);

        mUserPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    InputMethodManager inputManager = (InputMethodManager) SignInActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
//                    inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    signin(v);
                    handled = true;
                }
                return handled;
            }
        });

        mTvLoginHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHelpDialog(SignInActivity.this);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                                @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_MULTIPLE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void signin(View v) {
        InputMethodManager inputManager = (InputMethodManager) SignInActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        mUserId.setError(null);
        mUserPass.setError(null);

        String id = mUserId.getText().toString();
        String password = mUserPass.getText().toString();

        View focusView;

        if (TextUtils.isEmpty(id)) {
            mUserId.setError("This field is required");
            focusView = mUserId;
            focusView.requestFocus();
        }

        if (TextUtils.isEmpty(password)) {
            mUserPass.setError("Enter password");
            focusView = mUserPass;
            focusView.requestFocus();
        }

        mUserPass.setText("");

        new LoginTask(id, password).execute("");
    }

    private void showHelpDialog(Context context) {
        final String MITS_HELPDESK = "https://support.mona.uwi.edu/";
        final String message = "Student users should note that your password is now the same as" +
                " your OURVLE/DOMAIN password. If your OURVLE/DOMAIN password is your date of" +
                " birth, please use the format YYYYMMDD. e.g. John Brown is a student with id" +
                " number 89876543. John was born on January 3, 1989. In this case John would" +
                " enter: 89876543 in the slot for User ID and, 19890103 in the slot for Password." +
                "If you do not remember your OURVLE/DOMAIN password or it has expired, please" +
                " contact the MITS Helpdesk at extension 2992 or (876) 927-2148. You may also" +
                " email the helpdesk or visit the UWI Mona Live Support page (link below) to" +
                " request a password reset";

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Login Help").setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { return; } })
                .create();
        dialog.show();
    }

    public class LoginTask extends AsyncTask<String, Integer, Boolean> {
        private String user_name;
        private String user_passwd;
        private String token;
        private String error = "";
        private String name = "";
        private MoodleSiteInfo siteInfo;
        private ArrayList<MoodleCourse> courses;

        public LoginTask(String user_name, String user_passwd) {
            this.user_name = user_name;
            this.user_passwd = user_passwd;
        }

        @Override
        protected void onPreExecute() {
            CharSequence login_status = "Logging in...";

            Toast toast = Toast.makeText(getApplicationContext(), login_status, Toast.LENGTH_SHORT);
            toast.show();

            mProgressBar.setVisibility(View.VISIBLE);

            mUserId.setVisibility(View.GONE);
            mUserPass.setVisibility(View.GONE);
            mBtn.setVisibility(View.GONE);
            mTvLoginHelp.setVisibility(View.GONE);
            mTvUserPassLabel.setVisibility(View.GONE);
            mTvUserLabel.setVisibility(View.GONE);
            mImageIcon.setVisibility(View.GONE);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if (!getToken())
                return false;

            if (!getSiteInfo())
                return false;

            if (!getCourses())
                return false;

            return true;
        }

        private boolean getToken() {
            MoodleLogin login = new MoodleLogin(user_name, user_passwd); // logs into the database using the api and gets the user token
            MoodleToken mtoken = login.getToken();

            // if there is no token
            // usually occurs if the user is not a valid user or lack of internet connectivity
            if (mtoken.getToken() == null)
            {
                this.error = mtoken.getError();
                return false;
            }


            this.token = mtoken.getToken(); // gets the string of characters that represent the token

            return true;
        }

        private boolean getSiteInfo(){
            MoodleRestSiteInfo sInfo = new MoodleRestSiteInfo(token);
            siteInfo = sInfo.getSiteInfo();

            if (siteInfo.getFullname() == null) // if no site info is present
                return false;

            siteInfo.setToken(token); // sets the token for the site info model
            siteInfo.save(); // save the user profile data

            this.name = siteInfo.getFullname();

            return true;
        }



        private boolean getCourses() {
            MoodleRestCourse mcourse= new MoodleRestCourse(token);
            courses  = mcourse.getCourses(siteInfo.getUserid()+  ""); // gets courses from api call

            if (courses == null) // if there are no courses
            {
                error = "No courses registered";
                return false;
            }


            if (courses.size() == 0)
            {
                error = "No courses registered";
                return false;
            }


            MoodleCourse course;
            for (int i = 0; i < courses.size(); i++) {
                course = courses.get(i);
                course.setSiteid(siteInfo.getId());
                course.setIsUserCourse(true);
                course.save(); // saves courses to database
            }

            return true;
        }


        @Override
        protected void onPostExecute(Boolean status) {
            mProgressBar.setVisibility(View.GONE);

            if (status) { // if the user has successfully logged on
                SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences(MoodleConstants.PREFS_STRING, Context.MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
                prefsEditor.putBoolean(MoodleConstants.ID_PRESENT, false); // to save that the user has successfully logged in
                prefsEditor.commit();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getApplicationContext().startActivity(intent);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),"Failed "+error, Toast.LENGTH_SHORT);
                toast.show();

                mProgressBar.setVisibility(View.GONE);

                mUserId.setVisibility(View.VISIBLE);
                mUserPass.setVisibility(View.VISIBLE);
                mBtn.setVisibility(View.VISIBLE);
                mTvLoginHelp.setVisibility(View.VISIBLE);
                mTvUserLabel.setVisibility(View.VISIBLE);
                mTvUserPassLabel.setVisibility(View.VISIBLE);
                mImageIcon.setVisibility(View.VISIBLE);
            }
        }
    }


}
