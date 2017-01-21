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

import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.Context;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.text.TextUtils;
import android.support.design.widget.FloatingActionButton;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.stoneapp.ourvlemoodle2.models.Member;
import com.stoneapp.ourvlemoodle2.models.MoodleMessage;
import com.stoneapp.ourvlemoodle2.models.SiteInfo;
import com.stoneapp.ourvlemoodle2.rest.RestMessageCall;
import com.stoneapp.ourvlemoodle2.R;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class ProfileActivity extends AppCompatActivity {
    private int memberid;
    private int clientid;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        String username = extras.getString("username");
        String email = extras.getString("email");
        String description = extras.getString("description");
        token = extras.getString("token");
        memberid = extras.getInt("memberid");

        setContentView(R.layout.activity_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tv_email = (TextView) findViewById(R.id.profile_email);
        tv_description = (TextView) findViewById(R.id.profile_description);
        fab = (FloatingActionButton)findViewById(R.id.fab);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(username);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Member member =
                new Select().from(Member.class).where("memberid = ?", memberid).executeSingle();




        List<SiteInfo> sites = new Select().all().from(SiteInfo.class).execute();
        clientid = sites.get(0).getUserid();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(v.getContext());
                View promptsView = li.inflate(R.layout.dialog_send_message, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        v.getContext());

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                 editTextMessage = (EditText)promptsView.findViewById(R.id.editTextMessage);
                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",null)
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                final AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {

                        Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                // TODO Do something

                                String message = editTextMessage.getText().toString().trim();

                                if(!message.equals("")){
                                    new SendMessageTask(memberid, clientid, message, view.getContext()).execute("");
                                    alertDialog.dismiss();
                                }else{
                                    Toast.makeText(view.getContext(),"All fields must be filled",Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                });

                // show it
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();
            }
        });

        if (!TextUtils.isEmpty(email))
            tv_email.setText(email);

        if (!TextUtils.isEmpty(description))
            tv_description.setText(description);
    }

    private class SendMessageTask extends AsyncTask<String, Integer, Boolean>{
        int memberid;
        int clientid;
        String message;
        Context context;

        public SendMessageTask(int memberid, int clientid, String message, Context context) {
            this.memberid = memberid;
            this.clientid = clientid;
            this.message = message;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(context, "Please Wait", "Sending message", true);
            progressDialog.setCancelable(true); // allow dialog to be removed by back button
        }

        @Override
        protected Boolean doInBackground(String... params) {
            RestMessageCall messageCall = new RestMessageCall(token);
            ArrayList<MoodleMessage>messages = messageCall.getMessageResponse(memberid, 2, message);
            if (messages == null)  // check if there are no messages
                return false;

            if (messages.get(0).getMsgid() == -1)  // check if error occurred
                return false;

            if (messages.size() == 0)
                return false;

           return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressDialog.dismiss();

            if (aBoolean) {
                Toast.makeText(context, "Message sent", Toast.LENGTH_SHORT).show();
                editTextMessage.setText("");
            } else
                Toast.makeText(context, "Message failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private TextView tv_email;
    private TextView tv_description;
    private Toolbar toolbar;
    private EditText editMessage;
    private ProgressDialog progressDialog;
    private ImageButton btn;
    private FloatingActionButton fab;
    private  EditText editTextMessage;
}
