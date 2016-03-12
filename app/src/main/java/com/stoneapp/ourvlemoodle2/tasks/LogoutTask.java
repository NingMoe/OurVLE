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

package com.stoneapp.ourvlemoodle2.tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.stoneapp.ourvlemoodle2.models.MoodleDiscussion;
import com.stoneapp.ourvlemoodle2.models.MoodleEvent;
import com.stoneapp.ourvlemoodle2.models.MoodleForum;
import com.stoneapp.ourvlemoodle2.models.MoodleMember;
import com.stoneapp.ourvlemoodle2.models.MoodleModule;
import com.stoneapp.ourvlemoodle2.models.MoodleModuleContent;
import com.stoneapp.ourvlemoodle2.models.MoodlePost;
import com.stoneapp.ourvlemoodle2.models.MoodleSection;
import com.stoneapp.ourvlemoodle2.models.MoodleCourse;
import com.stoneapp.ourvlemoodle2.models.MoodleSiteInfo;
import com.stoneapp.ourvlemoodle2.activities.SignInActivity;
import com.stoneapp.ourvlemoodle2.util.MoodleConstants;
import com.stoneapp.ourvlemoodle2.R;

public class LogoutTask {
    Context context;
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;

    public LogoutTask(Context context) {
        this.context = context;
        sharedPrefs = context.getSharedPreferences(MoodleConstants.PREFS_STRING, Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();
    }

    public void LogOut() {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.action_logout).setMessage(R.string.dialog_logout_message)
                .setPositiveButton(R.string.action_logout, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            clearCache(); //clears all the user data

                            Intent intent = new Intent(context, SignInActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent); //returns the user to the login page
                        }
                    })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { return; } })
                .create();
        dialog.show();
    }

    private void clearCache() {
        //editor.putBoolean(MoodleConstants.ID_PRESENT, true); // resets logged in flag
        //editor.commit();
        sharedPrefs.edit().clear().commit();
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit();

        MoodleSection.deleteAll(MoodleSection.class);
        MoodleModule.deleteAll(MoodleModule.class);
        MoodleModuleContent.deleteAll(MoodleModuleContent.class);
        MoodleSiteInfo.deleteAll(MoodleSiteInfo.class);
        MoodleCourse.deleteAll(MoodleCourse.class);
        MoodleEvent.deleteAll(MoodleEvent.class);
        MoodleMember.deleteAll(MoodleMember.class);
        MoodleForum.deleteAll(MoodleForum.class);
        MoodlePost.deleteAll(MoodlePost.class);
        MoodleDiscussion.deleteAll(MoodleDiscussion.class);
    }
}
