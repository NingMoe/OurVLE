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
