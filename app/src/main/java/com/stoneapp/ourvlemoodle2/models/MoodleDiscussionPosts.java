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

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

public class MoodleDiscussionPosts  extends SugarRecord<MoodleDiscussionPosts>{

    @SerializedName("posts")
    ArrayList<MoodlePost>posts;

    @SerializedName("warning")
    ArrayList<MoodleWarning>warnings;

    @SerializedName("exception")
    String exception;

    @SerializedName("errorcode")
    String errorcode;

    @SerializedName("message")
    String message;

    public String getException() {
        return exception;
    }

    public String getErrorcode() {
        return errorcode;
    }

    public String getMessage() {
        return message;
    }

    public String getDebuginfo() {
        return debuginfo;
    }

    @SerializedName("debuginfo")
    String debuginfo;

    public ArrayList<MoodlePost> getPosts() {
        return posts;
    }

    public ArrayList<MoodleWarning> getWarnings() {
        return warnings;
    }
}
