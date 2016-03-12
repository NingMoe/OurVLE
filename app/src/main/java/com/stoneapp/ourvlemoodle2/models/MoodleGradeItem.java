package com.stoneapp.ourvlemoodle2.models;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import java.util.ArrayList;

/**

 * Created by Matthew on 20/01/2015.
 */
public class MoodleGradeItem extends SugarRecord<MoodleGradeItem> {

    @SerializedName("activityid")
    String courseid;    //The ID of the activity or "course" for the course grade item

    @SerializedName("itemnumber")
    int itemnumber;    //Will be 0 unless the module has multiple grades

    @SerializedName("scaleid")
    int scaleid;  //The ID of the custom scale or 0

    @SerializedName("name")
    String name;  //The module name

    @SerializedName("grademin")
    double grademin;    //Minimum grade

    @SerializedName("grademax")
    double grademax;   //Maximum grade

    @SerializedName("gradepass")
    double gradepass;    //The passing grade threshold

    @SerializedName("locked")
    int locked;   //Is the grade item locked?

    @SerializedName("hidden")
    int hidden;  //Is the grade item hidden?

    @SerializedName("grades")
    ArrayList<MoodleGrade>grades;


    public String getCourseid() {
        return courseid;
    }


    public int getItemnumber() {
        return itemnumber;
    }



    public int getScaleid() {
        return scaleid;
    }

    public double getGrademin() {
        return grademin;
    }

    public String getName() {
        return name;
    }

    public double getGrademax() {
        return grademax;
    }

    public double getGradepass() {
        return gradepass;
    }

    public int getLocked() {
        return locked;
    }

    public int getHidden() {
        return hidden;
    }

    public ArrayList<MoodleGrade> getGrades() {
        return grades;
    }
}
