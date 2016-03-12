package com.stoneapp.ourvlemoodle2.models;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import java.util.ArrayList;

/**
 * Created by Matthew on 20/01/2015.
 */
public class MoodleGrades extends SugarRecord<MoodleGrades> {

    @SerializedName("items")
    ArrayList<MoodleGradeItem> items;

    @SerializedName("outcomes")
    ArrayList<MoodleGradeOutcome> outcomes;

    public ArrayList<MoodleGradeItem> getItems() {
        return items;
    }

    public ArrayList<MoodleGradeOutcome> getOutcomes() {
        return outcomes;
    }
//@SerializedName()
}
