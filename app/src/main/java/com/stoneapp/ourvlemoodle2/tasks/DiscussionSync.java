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

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.stoneapp.ourvlemoodle2.models.MoodleCourse;
import com.stoneapp.ourvlemoodle2.models.MoodleDiscussion;
import com.stoneapp.ourvlemoodle2.models.MoodleForum;
import com.stoneapp.ourvlemoodle2.rest.MoodleRestDiscussion;
import com.stoneapp.ourvlemoodle2.activities.PostActivity;
import com.stoneapp.ourvlemoodle2.util.MoodleConstants;
import com.stoneapp.ourvlemoodle2.R;
import com.stoneapp.ourvlemoodle2.util.SettingsUtils;

public class DiscussionSync {
    String token;
    Context context;
    List<MoodleDiscussion> discussions;

    public DiscussionSync(String token, Context context) {
        this.token = token;
        this.context = context;

    }

    public boolean syncDiscussions(ArrayList<String> forumids) {

        MoodleRestDiscussion mrdiscuss = new MoodleRestDiscussion(token);
        discussions = mrdiscuss.getDiscussions(forumids); // get discussions from api call

        // check if there are no discussions
        if (discussions == null)
            return false;

        if (discussions.size() == 0)
            return false;

        ActiveAndroid.beginTransaction();
        try {
            deleteStaleData();
            for (int i = 0; i < discussions.size(); i++) {
                final MoodleDiscussion discussion = discussions.get(i);

                MoodleDiscussion.findOrCreateFromJson(discussion); // saves contact to database
            }
            ActiveAndroid.setTransactionSuccessful();
        }finally {
            ActiveAndroid.endTransaction();
        }

        return true;
    }

    private void deleteStaleData()
    {

        List<MoodleDiscussion> stale_discussions = new Select().all().from(MoodleDiscussion.class).execute();
        for(int i=0;i<stale_discussions.size();i++)
        {
            if(!doesDiscussionExistInJson(stale_discussions.get(i)))
            {
                MoodleDiscussion.delete(MoodleDiscussion.class,stale_discussions.get(i).getId());
            }
        }
    }

    private boolean doesDiscussionExistInJson(MoodleDiscussion discussion)
    {
        return discussions.contains(discussion);
    }

}

