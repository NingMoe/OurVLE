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

package com.stoneapp.ourvlemoodle2.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;


@Table(name="MoodleDiscussion")
public class MoodleDiscussion extends Model{

    @Column(name="discussionid")
    @SerializedName("id")
    int discussionid;  //Forum id

    @Column(name="courseid")
    @SerializedName("course")
    int courseid;   //Course id

    @Column(name="forumid")
    @SerializedName("forum")
    int  forumid;  //The forum id

    @Column(name="name")
    @SerializedName("name")
    String name;  //Discussion name

    @SerializedName("userid")
    int userid; //User id

    @SerializedName("groupid")
    int groupid;  //Group id

    @SerializedName("assesed")
    int assesed;  //Is this assessed?

    @Column(name="timemodified")
    @SerializedName("timemodified")
    int timemodified;   //Time modified

    @SerializedName("usermodified")
    int usermodified;  //The id of the user who last modified

    @SerializedName("timestart")
    int timestart;   //Time discussion can start

    @SerializedName("timeend")
    int timeend;   //Time discussion ends

    @SerializedName("firstpost")
    int firstpost;   //The first post in the discussion

    @Column(name="firstuserfullname")
    @SerializedName("firstuserfullname")
    String firstuserfullname;  //The discussion creators fullname

    @SerializedName("firstuserimagealt")
    String firstuserimagealt;   //The discussion creators image alt

    @SerializedName("firstuserpicture")
    int firstuserpicture;   //The discussion creators profile picture

    @SerializedName("firstuseremail")
    String firstuseremail;   //The discussion creators email

    @Column(name="subject")
    @SerializedName("subject")
    String subject;  //The discussion subject

    @SerializedName("numreplies")
    String numreplies;   //The number of replies in the discussion

    @SerializedName("numunread")
    String numunread;   //The number of unread posts, blank if this value is not available due to forum settings.

    @SerializedName("lastpost")
    int lastpost;   //The id of the last post in the discussion

    @SerializedName("lastuserid")
    int lastuserid;  //The id of the user who made the last post

    @Column(name="lastuserfullname")
    @SerializedName("lastuserfullname")
    String lastuserfullname;   //The last person to posts fullname

    @SerializedName("lastuserimagealt")
    String lastuserimagealt;    //The last person to posts image alt

    @SerializedName("lastuserpicture")
    int lastuserpicture;  //The last person to posts profile picture

    @SerializedName("lastuseremail")
    String lastuseremail;   //The last person to posts email

    //String coursename;

    public int getDiscussionid() {
        return discussionid;
    }

    public int getCourseid() {
        return courseid;
    }

    public int getForumid() {
        return forumid;
    }

    public String getName() {
        return name;
    }

    public int getUserid() {
        return userid;
    }

    public int getGroupid() {
        return groupid;
    }

    public int getAssesed() {
        return assesed;
    }

    public int getTimemodified() {
        return timemodified;
    }

    public int getUsermodified() {
        return usermodified;
    }

    public int getTimestart() {
        return timestart;
    }

    public int getTimeend() {
        return timeend;
    }

    public int getFirstpost() {
        return firstpost;
    }

    public String getFirstuserfullname() {
        return firstuserfullname;
    }

    public String getFirstuserimagealt() {
        return firstuserimagealt;
    }

    public int getFirstuserpicture() {
        return firstuserpicture;
    }

    public String getFirstuseremail() {
        return firstuseremail;
    }

    public String getSubject() {
        return subject;
    }

    public String getNumreplies() {
        return numreplies;
    }

    public String getNumunread() {
        return numunread;
    }

    public int getLastpost() {
        return lastpost;
    }

    public int getLastuserid() {
        return lastuserid;
    }

    public String getLastuserfullname() {
        return lastuserfullname;
    }

    public String getLastuserimagealt() {
        return lastuserimagealt;
    }

    public int getLastuserpicture() {
        return lastuserpicture;
    }

    public String getLastuseremail() {
        return lastuseremail;
    }


    public static MoodleDiscussion findOrCreateFromJson(MoodleDiscussion new_discussion) {
        int discussionid = new_discussion.getDiscussionid();
        MoodleDiscussion existingDiscussion =
                new Select().from(MoodleDiscussion.class).where("discussionid = ?", discussionid).executeSingle();
        if (existingDiscussion != null) {
            // found and return existing
            //UpdateDiscussion(existingDiscussion,new_discussion);
            return existingDiscussion;
        } else {
            // create and return new user
            MoodleDiscussion discussion = new_discussion;
            discussion.save();
            return discussion;
        }
    }

    private static void UpdateDiscussion(MoodleDiscussion old_discussion,MoodleDiscussion new_discussion)
    {
        old_discussion = new_discussion;
        old_discussion.save();

    }
}
