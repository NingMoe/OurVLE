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

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.stoneapp.ourvlemoodle2.models.MoodleForum;
import com.stoneapp.ourvlemoodle2.models.MoodleCourse;
import com.stoneapp.ourvlemoodle2.rest.MoodleRestForum;

public class ForumSync {

    private String token; //url token
    List<MoodleForum> forums;

    public ForumSync(String token){
        this.token = token;
    }

    public boolean syncForums(ArrayList<String> courseids){

        MoodleRestForum mrforum = new MoodleRestForum(token);

        forums = mrforum.getForums(courseids); // gets forums from api call

        if (forums  == null) // if there are no forums
            return false;

        if (forums.size() == 0 )
            return false;



        ActiveAndroid.beginTransaction();
        try {
            deleteStaleData();
            for (int i = 0; i < forums.size(); i++) {
                final MoodleForum forum = forums.get(i);

                MoodleCourse forumCourse = new Select().from(MoodleCourse.class).where("courseid = ?",forum.getCourseid()).executeSingle();
                if(forumCourse!=null)
                {
                    forum.setCoursename(forumCourse.getShortname());
                }

                MoodleForum.findOrCreateFromJson(forum); // saves contact to database
            }
            ActiveAndroid.setTransactionSuccessful();
        }finally {
            ActiveAndroid.endTransaction();
        }

        return true;
    }

    private void deleteStaleData()
    {

        List<MoodleForum> stale_forums = new Select().all().from(MoodleForum.class).execute();
        for(int i=0;i<stale_forums.size();i++)
        {
            if(!doesForumExistInJson(stale_forums.get(i)))
            {
                MoodleForum.delete(MoodleForum.class,stale_forums.get(i).getId());
            }
        }
    }

    private boolean doesForumExistInJson(MoodleForum forum)
    {
        return forums.contains(forum);
    }
}
