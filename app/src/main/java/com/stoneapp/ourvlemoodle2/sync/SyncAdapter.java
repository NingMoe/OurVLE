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

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.os.Bundle;

import com.activeandroid.query.Select;
import com.stoneapp.ourvlemoodle2.models.Course;
import com.stoneapp.ourvlemoodle2.models.Discussion;
import com.stoneapp.ourvlemoodle2.util.MoodleConstants;
import com.stoneapp.ourvlemoodle2.models.Forum;
import com.stoneapp.ourvlemoodle2.models.SiteInfo;

import java.util.ArrayList;
import java.util.List;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    int first_update;
    private String token;
    private List<Course> courses;
    private SharedPreferences sharedPrefs;
    List<SiteInfo> mSites;

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    /**
     * Perform a sync for this account. SyncAdapter-specific parameters may
     * be specified in extras, which is guaranteed to not be null. Invocations
     * of this method are guaranteed to be serialized.
     *
     * @param account    the account that should be synced
     * @param extras     SyncAdapter-specific parameters
     * @param authority  the authority of this sync request
     * @param provider   a ContentProviderClient that points to the ContentProvider for this
     *                   authority
     * @param syncResult SyncAdapter-specific parameters
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        sharedPrefs = super.getContext().getSharedPreferences(MoodleConstants.PREFS_STRING, Context.MODE_PRIVATE);

        first_update = sharedPrefs.getInt(MoodleConstants.FIRST_UPDATE, 404); // flag to check whether this is the first update

        mSites = new Select().all().from(SiteInfo.class).execute();

        if(mSites==null)
            return;
        if(mSites.size()<=0)
            return;

        token = mSites.get(0).getToken(); // gets the url token

        courses = new Select().all().from(Course.class).execute(); // gets all the courses

        updateLatestEvents();
        updateLatestForumPosts();
        updateLatestDiscussionPots();
        updateLatestCourseContent();
        updateMembers();
    }

    private void updateLatestEvents() {
        if(courses.size() > 0 && courses != null) { // checks if there are no courses
            ArrayList<String> courseids = new ArrayList<>();
            for (int i = 0; i < courses.size(); i++)
                courseids.add(courses.get(i).getCourseid() + ""); // appends course ids to list

            // refresh events
            EventSync evsync = new EventSync(super.getContext(), token);
            evsync.syncEvents(courseids); // syncs events
        }
    }

    private void updateLatestForumPosts() {
        ArrayList<String>forumids;
        List<Forum> forums  = new Select().all().from(Forum.class).execute(); // gets a list of all the forums
        ArrayList<Forum>news_forums = new ArrayList<>();

        if(forums != null && forums.size() > 0) { // checks if there are no forums
            for(int i = 0; i < forums.size(); i++) {
                if (forums.get(i).getName().toUpperCase().contains("NEWS FORUM")) // checks if it is a news forum
                    news_forums.add(forums.get(i));
            }

            forumids = new ArrayList<>();

            if(news_forums.size() > 0) {
                for (int i = 0; i < news_forums.size(); i++)
                    forumids.add(news_forums.get(i).getForumid() + ""); // adds all the forums ids of the news forums to list
            }

            DiscussionSync dsync = new DiscussionSync(token, super.getContext());

            boolean sync = dsync.syncDiscussions(forumids); // syncs all forum discussions

            if(sync && first_update == 404)
                sharedPrefs.edit().putInt(MoodleConstants.FIRST_UPDATE, 200); // update first update flag
        }
    }

    private void updateLatestDiscussionPots() {
        ArrayList<String> discussionids = new ArrayList<>();
        List<Discussion> discussions = new Select().all().from(Discussion.class).execute(); // gets a list of all the forum discussions

        if(discussions != null && discussions.size() > 0) { // checks if there are no discussions
            for(int i = 0; i < discussions.size(); i++)
                discussionids.add(discussions.get(i).getDiscussionid() + ""); // adds a discussion id to list

            for(int i = 0; i < discussionids.size(); i++)
                new PostSync(token).syncPosts(discussionids.get(i)); // syncs posts
        }
    }

    private void updateMembers() {
        ArrayList<String>courseids = new ArrayList<>();
        if(courses != null && courses.size() == 0) { // checks if there are no courses
            for(int i = 0; i <courses.size(); i++)
                courseids.add(courses.get(i).getCourseid() + ""); // adds course ids to a list

            for(int i = 0;i < courseids.size();i++)
                new MemberSync(token).syncMembers(courseids.get(i)); // syncs members
        }
    }

    private void updateLatestCourseContent() {
        ArrayList<Integer>courseids;
        ArrayList<Long>coursepids;

        long siteid = mSites.get(0).getId();

        courseids = new ArrayList<>();
        coursepids = new ArrayList<>();

        if(courses.size() > 0 && courses != null) { // checks if there are no courses
            for(int i = 0; i < courses.size(); i++) {
                courseids.add(courses.get(i).getCourseid()); // adds course ids to a list
                coursepids.add(courses.get(i).getId()); // adds course parent ids to list
            }

            for(int i = 0; i < courseids.size(); i++)
                new ContentSync(courseids.get(i), coursepids.get(i), siteid, token, super.getContext())
                .syncContent(); // syncs course content
        }
    }
}
