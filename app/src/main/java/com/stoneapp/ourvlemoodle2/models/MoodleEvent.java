package com.stoneapp.ourvlemoodle2.models;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

public class MoodleEvent extends SugarRecord<MoodleEvent>{
	
	@SerializedName("id")
	int eventid;//event id
	
	@SerializedName("name")
	String name;//event name
	
	@SerializedName("description")
	String description;   //Optional //Description
	
	@SerializedName("format")
	int format;   //description format (1 = HTML, 0 = MOODLE, 2 = PLAIN or 4 = MARKDOWN)
	
	@SerializedName("courseid")
	int courseid;  //course id
	
	@SerializedName("groupid")
	int groupid;   //group id
	
	@SerializedName("userid")
	int userid;  //user id
	
	@SerializedName("repeatid")
	int repeatid;   //repeat id
	
	@SerializedName("modulename")
	String modulename;  //  Optional //module name
	
	@SerializedName("instance")
	int instanceid;   //instance id
	
	@SerializedName("eventtype")
	String eventtype;  //Event type
	
	@SerializedName("timestart")
	int timestart;  //timestart
	
	@SerializedName("timeduration")
	int timeduration;   //time duration
	
	@SerializedName("visible")
	int visible;  //visible
	
	@SerializedName("uuid")
	String uuid;  //  Optional //unique id of ical events
	
	@SerializedName("sequence")
	int sequence;  //sequence
	
	@SerializedName("timemodified")
	int timemodified; //time modified
	
	@SerializedName("subscriptionid")
	int subscriptionid;

    String coursename;

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }

    public String getCoursename() {

        return coursename;
    }

    public int getEventid() {
		return eventid;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public int getFormat() {
		return format;
	}

	public int getCourseid() {
		return courseid;
	}

	public int getGroupid() {
		return groupid;
	}

	public int getUserid() {
		return userid;
	}

	public int getRepeatid() {
		return repeatid;
	}

	public String getModulename() {
		return modulename;
	}

	public int getInstanceid() {
		return instanceid;
	}

	public String getEventtype() {
		return eventtype;
	}

	public int getTimestart() {
		return timestart;
	}

	public int getTimeduration() {
		return timeduration;
	}

	public int getVisible() {
		return visible;
	}

	public String getUuid() {
		return uuid;
	}

	public int getSequence() {
		return sequence;
	}

	public int getTimemodified() {
		return timemodified;
	}

	public int getSubscriptionid() {
		return subscriptionid;
	}
	

}
