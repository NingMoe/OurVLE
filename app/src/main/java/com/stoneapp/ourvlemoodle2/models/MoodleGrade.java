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

public class MoodleGrade extends SugarRecord<MoodleGrade> {

    @SerializedName("usierid")
    int userid;   //Student ID

    @SerializedName("grade")
    double grade;   //Student grade

    @SerializedName("locked")
    int locked;   //Is the student's grade locked?

    @SerializedName("hidden")
    int hidden; //Is the student's grade hidden?

    @SerializedName("feedback")
    String feedback;    //Feedback from the grader

    @SerializedName("feedbackformat")
    int feedbackformat;   //The feedback format

    @SerializedName("usermodified")
    int usermodified;   //The ID of the last user to modify this student grade

    @SerializedName("str_grade")
    String str_grade;   //A string representation of the grade

    @SerializedName("str_feedback")
    String str_feedback;   //A string representation of the feedback from the grader

    int itemid;
    String courseid;

    public String getCourseid() {
        return courseid;
    }

    public String getStr_grade() {
        return str_grade;
    }

    public String getStr_feedback() {
        return str_feedback;
    }

    public int getUserid() {

        return userid;
    }

    public double getGrade() {
        return grade;
    }

    public int getLocked() {
        return locked;
    }

    public int getHidden() {
        return hidden;
    }

    public int getFeedbackformat() {
        return feedbackformat;
    }

    public String getFeedback() {
        return feedback;
    }

    public int getUsermodified() {
        return usermodified;
    }

    public void setCourseid(String courseid) {
        this.courseid = courseid;
    }

    public int getItemid() {
        return itemid;
    }

    public void setItemid(int itemid) {
        this.itemid = itemid;
    }
}
