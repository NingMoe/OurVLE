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

public class MoodlePost extends SugarRecord<MoodlePost> {

     @SerializedName("id")
     int  postid; //Post id
     @SerializedName("discussion")
     int  discussionid; //Discussion id
     @SerializedName("parentid")
     int parentid;  //Parent id
     @SerializedName("userid")
     int  userid ; //User id
     @SerializedName("created")
     int created;  //Creation time
     @SerializedName("modified")
     int  modified; //Time modified
     @SerializedName("mailed")
     int mailed ;   //Mailed?
     @SerializedName("subject")
     String  subject;  //The post subject
     @SerializedName("message")
     String message ;  //The post message
     @SerializedName("messageformat")
     int messageformat ;   //The post message format
     @SerializedName("messagetrust")
     int messagetrust;    //Can we trust?
     @SerializedName("attachment")
     String  attachment;   //Attachments
     @SerializedName("totalscore")
     int totalscore;  //The post message total score
     @SerializedName("mailnow")
     int  mailnow ;

     @SerializedName("cantreply")
     boolean canreply;    //The user can reply to posts?
     @SerializedName("postread")
     boolean postread;   //The post was read
     @SerializedName("userfullname")
     String userfullname;

     public int getPostid() {
        return postid;
    }
    public int getDiscussionid() {
        return discussionid;
    }
    public int getParentid() {
        return parentid;
    }
    public int getUserid() {
        return userid;
    }
    public int getCreated() {
        return created;
    }
    public int getModified() {
        return modified;
    }
    public int getMailed() {
        return mailed;
    }
    public String getSubject() {
        return subject;
    }
    public String getMessage() {
        return message;
    }
    public int getMessageformat() {
        return messageformat;
    }
    public int getMessagetrust() {
        return messagetrust;
    }
    public String getAttachment() {
        return attachment;
    }
    public int getTotalscore() {
        return totalscore;
    }
    public int getMailnow() {
        return mailnow;
    }
    public boolean isCanreply() {
        return canreply;
    }
    public boolean isPostread() {
        return postread;
    }
    public String getUserfullname() {
        return userfullname;
    }

}
