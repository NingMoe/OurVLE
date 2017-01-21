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

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.stoneapp.ourvlemoodle2.models.Discussion;
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

                Discussion.findOrCreateFromJson(discussion); // saves contact to database
            }
            ActiveAndroid.setTransactionSuccessful();
        }finally {
            ActiveAndroid.endTransaction();
        }

        return true;
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
        return discussions.contains(discussion);
    }

}

