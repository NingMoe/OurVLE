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

package com.stoneapp.ourvlemoodle2.sync;

import java.util.ArrayList;
import java.util.List;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.stoneapp.ourvlemoodle2.BuildConfig;
import com.stoneapp.ourvlemoodle2.R;
import com.stoneapp.ourvlemoodle2.activities.MainActivity;
import com.stoneapp.ourvlemoodle2.models.Discussion;
import com.stoneapp.ourvlemoodle2.models.Event;
import com.stoneapp.ourvlemoodle2.rest.RestDiscussion;

public class DiscussionSync {
    String token;
    Context context;
    List<Discussion> discussions;

    public DiscussionSync(String token, Context context) {
        this.token = token;
        this.context = context;

    }

    public boolean syncDiscussions(ArrayList<String> forumids) {

        RestDiscussion mrdiscuss = new RestDiscussion(token);
        discussions = mrdiscuss.getDiscussions(forumids); // get discussions from api call

        List<Discussion> notifDiscussions = new ArrayList<>();
        // check if there are no discussions
        if (discussions == null)
            return false;

        if (discussions.size() == 0)
            return false;

        ActiveAndroid.beginTransaction();
        try {
            deleteStaleData();
            for (int i = 0; i < discussions.size(); i++) {
                final Discussion discussion = discussions.get(i);

                if(Discussion.findOrCreateFromJson(discussion)==0)
                {
                    notifDiscussions.add(discussion);
                } // saves contact to database
            }
            ActiveAndroid.setTransactionSuccessful();
        }finally {
            ActiveAndroid.endTransaction();
        }

        if(notifDiscussions.size() >0)
        {
            notifyDiscussions(notifDiscussions);
        }

        return true;
    }

    void notifyDiscussions(List<Discussion> notifDiscussions)
    {
        TaskStackBuilder stackBuilder =  TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);

        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setAction(BuildConfig.APPLICATION_ID + ".ACTION_OPEN_NEWS");

        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_people_white_24dp)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setContentTitle(notifDiscussions.size() + " New Discussions")
                        .setContentIntent(resultPendingIntent)
                        .setAutoCancel(true)
                        .setDefaults(NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_SOUND);


        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        String endTitle = notifDiscussions.size()>1? " New Discussions":" New Discussion";
        inboxStyle.setBigContentTitle(notifDiscussions.size() + endTitle);
        for (int i=0; i < notifDiscussions.size(); i++) {

            inboxStyle.addLine(notifDiscussions.get(i).getName());
        }
        mBuilder.setStyle(inboxStyle);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
    }

    private void deleteStaleData()
    {

        List<Discussion> stale_discussions = new Select().all().from(Discussion.class).execute();
        for(int i=0;i<stale_discussions.size();i++)
        {
            if(!doesDiscussionExistInJson(stale_discussions.get(i)))
            {
                Discussion.delete(Discussion.class,stale_discussions.get(i).getId());
            }
        }
    }

    private boolean doesDiscussionExistInJson(Discussion discussion)
    {

        for (Discussion discuss : discussions){
            if(discussion.getDiscussionid() == discuss.getDiscussionid())
            {
                return true;
            }
        }
        return false;
    }

}

