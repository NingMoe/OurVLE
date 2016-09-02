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
import com.activeandroid.query.Select;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

public class MoodlePost extends Model {


     @Column(name="postid")
     @SerializedName("id")
     int  postid; //Post id

     @Column(name="discussionid")
     @SerializedName("discussion")
     int  discussionid; //Discussion id

     @SerializedName("parentid")
     int parentid;  //Parent id
     @SerializedName("userid")
     int  userid ; //User id

     @Column(name="created")
     @SerializedName("created")
     int created;  //Creation time

     @Column(name="modified")
     @SerializedName("modified")
     int  modified; //Time modified
     @SerializedName("mailed")
     int mailed ;   //Mailed?

     @Column(name="subject")
     @SerializedName("subject")
     String  subject;  //The post subject

     @Column(name="message")
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

     @Column(name="userfullname")
     @SerializedName("userfullname")
     String userfullname;

    public int getPostid() {
        return postid;
    }

    public void setPostid(int postid) {
        this.postid = postid;
    }

    public int getDiscussionid() {
        return discussionid;
    }

    public void setDiscussionid(int discussionid) {
        this.discussionid = discussionid;
    }

    public int getParentid() {
        return parentid;
    }

    public void setParentid(int parentid) {
        this.parentid = parentid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public int getModified() {
        return modified;
    }

    public void setModified(int modified) {
        this.modified = modified;
    }

    public int getMailed() {
        return mailed;
    }

    public void setMailed(int mailed) {
        this.mailed = mailed;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMessageformat() {
        return messageformat;
    }

    public void setMessageformat(int messageformat) {
        this.messageformat = messageformat;
    }

    public int getMessagetrust() {
        return messagetrust;
    }

    public void setMessagetrust(int messagetrust) {
        this.messagetrust = messagetrust;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public int getTotalscore() {
        return totalscore;
    }

    public void setTotalscore(int totalscore) {
        this.totalscore = totalscore;
    }

    public int getMailnow() {
        return mailnow;
    }

    public void setMailnow(int mailnow) {
        this.mailnow = mailnow;
    }

    public boolean isCanreply() {
        return canreply;
    }

    public void setCanreply(boolean canreply) {
        this.canreply = canreply;
    }

    public boolean isPostread() {
        return postread;
    }

    public void setPostread(boolean postread) {
        this.postread = postread;
    }

    public String getUserfullname() {
        return userfullname;
    }

    public void setUserfullname(String userfullname) {
        this.userfullname = userfullname;
    }

    public static MoodlePost findOrCreateFromJson(MoodlePost new_post) {
        int postid = new_post.getPostid();
        MoodlePost existingPost =
                new Select().from(MoodlePost.class).where("postid = ?", postid).executeSingle();
        if (existingPost != null) {
            // found and return existing
            //UpdatePost(existingPost,new_post);
            return existingPost;
        } else {
            // create and return new user
            MoodlePost post = new_post;
            post.save();
            return post;
        }
    }

    private static void UpdatePost(MoodlePost old_post,MoodlePost new_post)
    {
        old_post = new_post;
        old_post.save();

    }
}
