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

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.stoneapp.ourvlemoodle2.BuildConfig;
import com.stoneapp.ourvlemoodle2.R;
import com.stoneapp.ourvlemoodle2.activities.MainActivity;
import com.stoneapp.ourvlemoodle2.models.DiscussionPosts;
import com.stoneapp.ourvlemoodle2.models.Post;
import com.stoneapp.ourvlemoodle2.rest.RestPost;

public class PostSync {
    private String token;
    List<Post> posts;
    Context mContext;



    public PostSync(Context context,String token){
        this.token = token;
        this.mContext = context;
    }

    public boolean syncPosts(String discussionid){
        DiscussionPosts dposts;
        RestPost mrpost = new RestPost(token);
        dposts = mrpost.getDiscussionPosts(discussionid); // gets a list of of discussion posts from api call

        List<Post> notifPosts = new ArrayList<>();
        if (dposts == null) // checks if there are no discussion posts present
            return false;

        if (dposts.getErrorcode() != null) // checks if there is an error with that discussion post
            return false;

        posts = dposts.getPosts();

        if(posts == null)
        {
            return false;
        }

        if(posts.size()==0)
        {
            return false;
        }

        ActiveAndroid.beginTransaction();
        try {
            deleteStaleData();
            for (int i = 0; i < posts.size(); i++) {
                final Post post = posts.get(i);

                if(Post.findOrCreateFromJson(post)==1) // saves contact to database
                {
                    notifPosts.add(post);
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        }finally {
            ActiveAndroid.endTransaction();
        }

        if(notifPosts.size() > 0)
        {
            notifyPosts(notifPosts);
        }

        return true;
    }


    void notifyPosts(List<Post> notifPosts)
    {
        TaskStackBuilder stackBuilder =  TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(MainActivity.class);

        Intent resultIntent = new Intent(mContext, MainActivity.class);
        resultIntent.setAction(BuildConfig.APPLICATION_ID + ".ACTION_OPEN_NEWS");

        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.ic_comment_white_24dp)
                        .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                        .setContentTitle(notifPosts.size() + " New Posts")
                        .setContentIntent(resultPendingIntent)
                        .setAutoCancel(true)
                        .setDefaults(NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_SOUND);


        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        String endTitle = notifPosts.size()>1? " New Posts":" New Post";
        inboxStyle.setBigContentTitle(notifPosts.size() + endTitle);
        for (int i=0; i < notifPosts.size(); i++) {

            inboxStyle.addLine(notifPosts.get(i).getUserfullname() + " " + notifPosts.get(i).getMessage());
        }
        mBuilder.setStyle(inboxStyle);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
    }

    private void deleteStaleData()
    {

        List<Post> stale_posts = new Select().all().from(Post.class).execute();
        for(int i=0;i<stale_posts.size();i++)
        {
            if(!doesPostExistInJson(stale_posts.get(i)))
            {
                Post.delete(Post.class,stale_posts.get(i).getId());
            }
        }
    }

    private boolean doesPostExistInJson(Post post)
    {
        for(Post post1: posts){
            if(post1.getPostid() == post.getPostid())
            {
                return true;
            }
        }
        return false;
    }
}
