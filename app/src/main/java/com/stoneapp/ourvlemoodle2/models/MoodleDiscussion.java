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

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

public class MoodleDiscussion extends SugarRecord<MoodleDiscussion>{
    @SerializedName("id")
    int discussionid;  //Forum id

    @SerializedName("course")
    int courseid;   //Course id

    @SerializedName("forum")
    int  forumid;  //The forum id

    @SerializedName("name")
    String name;  //Discussion name

    @SerializedName("userid")
    int userid; //User id

    @SerializedName("groupid")
    int groupid;  //Group id

    @SerializedName("assesed")
    int assesed;  //Is this assessed?

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

    @SerializedName("firstuserfullname")
    String firstuserfullname;  //The discussion creators fullname

    @SerializedName("firstuserimagealt")
    String firstuserimagealt;   //The discussion creators image alt

    @SerializedName("firstuserpicture")
    int firstuserpicture;   //The discussion creators profile picture

    @SerializedName("firstuseremail")
    String firstuseremail;   //The discussion creators email

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

   /* public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }*/
}
