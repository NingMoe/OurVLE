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

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.Html;

import com.stoneapp.ourvlemoodle2.models.MoodleDiscussion;
import com.stoneapp.ourvlemoodle2.models.MoodleForum;
import com.stoneapp.ourvlemoodle2.rest.MoodleRestDiscussion;
import com.stoneapp.ourvlemoodle2.activities.PostActivity;
import com.stoneapp.ourvlemoodle2.util.MoodleConstants;
import com.stoneapp.ourvlemoodle2.R;

public class DiscussionSync {
    String token;
    Context context;
    private boolean first_update; // first sync flag

    public DiscussionSync(String token, Context context) {
        this.token = token;
        this.context = context;
        SharedPreferences sharedPref =
                context.getSharedPreferences(MoodleConstants.PREFS_STRING, Context.MODE_PRIVATE);

        first_update = sharedPref.getBoolean(MoodleConstants.FIRST_UPDATE, true);
    }

    public boolean syncDiscussions(ArrayList<String> forumids) {
        ArrayList<MoodleDiscussion> discussions = null;

        MoodleRestDiscussion mrdiscuss = new MoodleRestDiscussion(token);
        discussions = mrdiscuss.getDiscussions(forumids); // get discussions from api call

        // check if there are no discussions
        if (discussions == null)
            return false;

        if (discussions.size() == 0)
            return false;

        MoodleDiscussion discussion;
        List<MoodleDiscussion> saved_discussions;
        List<MoodleDiscussion> allsaved_discussions = MoodleDiscussion.listAll(MoodleDiscussion.class); //get a list of previously stored discussions

        for (int i = 0; i < discussions.size(); i++) {
            discussion = discussions.get(i);

            //String coursename = MoodleCourse.find(MoodleCourse.class,"courseid = ?",discussion.getCourseid()+"").get(0).getShortname();

            //discussion.setCoursename(coursename);

            saved_discussions = MoodleDiscussion.find(MoodleDiscussion.class, "discussionid = ?", discussion.getDiscussionid()+"");

            //whether discussion is from news forum or not
            boolean isNewsDiscussion
                    = MoodleForum.find(MoodleForum.class,"forumid = ?",
                    discussion.getForumid() + "").get(0).getName().toUpperCase().contains("NEWS FORUM");

            // if discussion is already present in database
            if(saved_discussions.size() > 0) {
                discussion.setId(saved_discussions.get(0).getId()); // overwrite previous discussion record
            } else {

                //if discussion is a new discussion and its not the first tie syncing then notify the user
                if(allsaved_discussions != null && allsaved_discussions.size() > 0 && isNewsDiscussion
                        && !first_update) {
                    // addNotification(discussion);
                }
            }

            discussion.save(); // save discussion
        }

        return true;
    }

    public void addNotification (MoodleDiscussion discussion){
        NotificationCompat.Builder mBuilder
                =  new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ourvle_iconsmall)
                        .setContentTitle("New Topic")
                        .setContentText(Html.fromHtml(discussion.getName()).toString().trim());

        //creates an explicit intent for an activity in your app
        Intent resultIntent = new Intent(context,PostActivity.class);
        resultIntent.putExtra("discussionid",discussion.getDiscussionid()+"");
        resultIntent.putExtra("discussionname",discussion.getName());

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder =  TaskStackBuilder.create(context);
        stackBuilder.addParentStack(PostActivity.class);

        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        Notification notification = mBuilder.build();

        notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;

        notification.defaults |= Notification.DEFAULT_SOUND;
        //notification.defaults |= Notification.DEFAULT_VIBRATE;

        NotificationManager mNotificationManager
                = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(discussion.getDiscussionid(), notification);
    }
}

