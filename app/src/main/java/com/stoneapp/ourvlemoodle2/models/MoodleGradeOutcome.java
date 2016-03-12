package com.stoneapp.ourvlemoodle2.models;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import java.util.ArrayList;

/**
 * Created by Matthew on 20/01/2015.
 */
public class MoodleGradeOutcome extends SugarRecord<MoodleGradeOutcome> {

    @SerializedName("activityid")
    String activityid;   //The ID of the activity or "course" for the course grade item

    @SerializedName("itemnumber")
    int itemnumber;    //Will be 0 unless the module has multiple grades

    @SerializedName("scaleid")
    int scaleid; //The ID of the custom scale or 0

    @SerializedName("name")
    String name;    //The module name

    @SerializedName("locked")
    int locked;    //Is the grade item locked?

    @SerializedName("hidden")
    int hidden;  //Is the grade item hidden?

    @SerializedName("grades")
    ArrayList<MoodleGrade>grades;

    public ArrayList<MoodleGrade> getGrades() {
        return grades;
    }

    public int getHidden() {
        return hidden;
    }

    public int getLocked() {
        return locked;
    }

    public String getName() {
        return name;
    }

    public int getScaleid() {
        return scaleid;
    }

    public int getItemnumber() {
        return itemnumber;
    }

    public String getActivityid() {
        return activityid;
    }


}
