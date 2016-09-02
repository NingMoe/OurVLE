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
import com.orm.dsl.Ignore;

public class MoodleForum extends Model{


    @Column(name="forumid")
    @SerializedName("id")
    int forumid;

    @Column(name="courseid")
    @SerializedName("course")
    String courseid;

    @Column(name="name")
    @SerializedName("name")
    String name;

    @Column(name="intro")
    @SerializedName("intro")
    String intro;

    @SerializedName("introformat")
    String introformat;

    @SerializedName("assessed")
    int assessed;

    @SerializedName("assesstimestart")
    int assesstimestart;

    @SerializedName("assesstimefinish")
    int assesstimefinish;

    @SerializedName("scale")
    int scale;

    @SerializedName("maxbytes")
    int maxbytes;

    @SerializedName("maxattachments")
    int maxattachments;

    @SerializedName("forcesubscribe")
    int forcesubscribe;

    @SerializedName("trackingtype")
    int trackingtype;

    @SerializedName("rsstype")
    int rsstype;

    @SerializedName("rssarticles")
    int rssarticles;

    @SerializedName("timemodified")
    int timemodified;

    @SerializedName("warnafter")
    int warnafter;

    @SerializedName("blockafter")
    int blockafter;

    @SerializedName("blockperiod")
    int blockperiod;

    @SerializedName("completiondiscussions")
    int completiondiscussions;

    @SerializedName("completionreplies")
    int completionreplies;

    @SerializedName("completionposts")
    int completionposts;

    @SerializedName("cmid")
    int cmid;

    @Ignore
    @SerializedName("exception")
    String exception;

    @Ignore
    @SerializedName("errorcode")
    String errorcode;

    @Ignore
    @SerializedName("message")
    String message;



    @Ignore
    @SerializedName("debuginfo")
    String debuginfo;


    long siteid;
    String coursename;

    public int getForumid() {
        return forumid;
    }

    public void setForumid(int forumid) {
        this.forumid = forumid;
    }

    public String getCourseid() {
        return courseid;
    }

    public void setCourseid(String courseid) {
        this.courseid = courseid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getIntroformat() {
        return introformat;
    }

    public void setIntroformat(String introformat) {
        this.introformat = introformat;
    }

    public int getAssessed() {
        return assessed;
    }

    public void setAssessed(int assessed) {
        this.assessed = assessed;
    }

    public int getAssesstimestart() {
        return assesstimestart;
    }

    public void setAssesstimestart(int assesstimestart) {
        this.assesstimestart = assesstimestart;
    }

    public int getAssesstimefinish() {
        return assesstimefinish;
    }

    public void setAssesstimefinish(int assesstimefinish) {
        this.assesstimefinish = assesstimefinish;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public int getMaxbytes() {
        return maxbytes;
    }

    public void setMaxbytes(int maxbytes) {
        this.maxbytes = maxbytes;
    }

    public int getMaxattachments() {
        return maxattachments;
    }

    public void setMaxattachments(int maxattachments) {
        this.maxattachments = maxattachments;
    }

    public int getForcesubscribe() {
        return forcesubscribe;
    }

    public void setForcesubscribe(int forcesubscribe) {
        this.forcesubscribe = forcesubscribe;
    }

    public int getTrackingtype() {
        return trackingtype;
    }

    public void setTrackingtype(int trackingtype) {
        this.trackingtype = trackingtype;
    }

    public int getRsstype() {
        return rsstype;
    }

    public void setRsstype(int rsstype) {
        this.rsstype = rsstype;
    }

    public int getRssarticles() {
        return rssarticles;
    }

    public void setRssarticles(int rssarticles) {
        this.rssarticles = rssarticles;
    }

    public int getTimemodified() {
        return timemodified;
    }

    public void setTimemodified(int timemodified) {
        this.timemodified = timemodified;
    }

    public int getWarnafter() {
        return warnafter;
    }

    public void setWarnafter(int warnafter) {
        this.warnafter = warnafter;
    }

    public int getBlockafter() {
        return blockafter;
    }

    public void setBlockafter(int blockafter) {
        this.blockafter = blockafter;
    }

    public int getBlockperiod() {
        return blockperiod;
    }

    public void setBlockperiod(int blockperiod) {
        this.blockperiod = blockperiod;
    }

    public int getCompletiondiscussions() {
        return completiondiscussions;
    }

    public void setCompletiondiscussions(int completiondiscussions) {
        this.completiondiscussions = completiondiscussions;
    }

    public int getCompletionreplies() {
        return completionreplies;
    }

    public void setCompletionreplies(int completionreplies) {
        this.completionreplies = completionreplies;
    }

    public int getCompletionposts() {
        return completionposts;
    }

    public void setCompletionposts(int completionposts) {
        this.completionposts = completionposts;
    }

    public int getCmid() {
        return cmid;
    }

    public void setCmid(int cmid) {
        this.cmid = cmid;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDebuginfo() {
        return debuginfo;
    }

    public void setDebuginfo(String debuginfo) {
        this.debuginfo = debuginfo;
    }

    public long getSiteid() {
        return siteid;
    }

    public void setSiteid(long siteid) {
        this.siteid = siteid;
    }

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }


    public static MoodleForum findOrCreateFromJson(MoodleForum new_forum) {
        int forumid = new_forum.getForumid();
        MoodleForum existingForum =
                new Select().from(MoodleForum.class).where("forumid = ?", forumid).executeSingle();
        if (existingForum != null) {
            // found and return existing
            UpdateForum(existingForum,new_forum);
            return existingForum;
        } else {
            // create and return new user
            MoodleForum forum = new_forum;
            forum.save();
            return forum;
        }
    }

    private static void UpdateForum(MoodleForum old_forum,MoodleForum new_forum)
    {
        old_forum = new_forum;
        old_forum.save();

    }
}
