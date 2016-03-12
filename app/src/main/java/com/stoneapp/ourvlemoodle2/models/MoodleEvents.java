package com.stoneapp.ourvlemoodle2.models;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

public class MoodleEvents extends SugarRecord<MoodleEvents>{
	
	@SerializedName("events")
	ArrayList<MoodleEvent>events;
	
	@SerializedName("warnings")
	ArrayList<MoodleWarning>warnings;

	public ArrayList<MoodleEvent> getEvents() {
		return events;
	}

	public ArrayList<MoodleWarning> getWarnings() {
		return warnings;
	}

}
