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
import com.orm.dsl.Ignore;

public class MoodleForum extends SugarRecord<MoodleForum>{

    // since id is a reserved field in SugarRecord
        @SerializedName("id")
        int forumid;

        @SerializedName("course")
        String courseid;

        @SerializedName("name")
        String name;

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

        public String getCoursename() {
            return coursename;
        }

        public void setCoursename(String coursename) {
            this.coursename = coursename;
        }

        public int getForumid() {
            return forumid;
        }

        public String getCourseid() {
            return courseid;
        }

        public String getName() {
            return name;
        }

        public String getIntro() {
            return intro;
        }

        public String getIntroformat() {
            return introformat;
        }


        public int getAssessed() {
            return assessed;
        }

        public int getAssesstimestart() {
            return assesstimestart;
        }

        public int getAssesstimefinish() {
            return assesstimefinish;
        }

        public int getScale() {
            return scale;
        }

        public int getMaxbytes() {
            return maxbytes;
        }

        public int getMaxattachments() {
            return maxattachments;
        }


        public int getForcesubscribe() {
            return forcesubscribe;
        }


        public int getTrackingtype() {
            return trackingtype;
        }


        public int getRsstype() {
            return rsstype;
        }


        public int getRssarticles() {
            return rssarticles;
        }


        public int getTimemodified() {
            return timemodified;
        }


        public int getWarnafter() {
            return warnafter;
        }


        public int getBlockafter() {
            return blockafter;
        }


        public int getBlockperiod() {
            return blockperiod;
        }


        public int getCompletiondiscussions() {
            return completiondiscussions;
        }


        public int getCompletionreplies() {
            return completionreplies;
        }


        public int getCompletionposts() {
            return completionposts;
        }


        public int getCmid() {
            return cmid;
        }


        public long getSiteid() {
            return siteid;
        }


        public void setSiteid(long siteid) {
            this.siteid = siteid;
        }

        public String getDebuginfo() {
            return debuginfo;
        }

        public String getMessage() {
            return message;
        }

        public String getErrorcode() {
            return errorcode;
        }

        public String getException() {
            return exception;
        }
}
