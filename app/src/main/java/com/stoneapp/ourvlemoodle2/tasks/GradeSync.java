package com.stoneapp.ourvlemoodle2.tasks;

import com.stoneapp.ourvlemoodle2.models.MoodleGrade;
import com.stoneapp.ourvlemoodle2.models.MoodleGradeItem;
import com.stoneapp.ourvlemoodle2.models.MoodleGrades;
import com.stoneapp.ourvlemoodle2.rest.MoodleRestGrades;

import java.util.ArrayList;

public class GradeSync {
    String token;
    String courseid;
    public GradeSync(String token){
        this.token = token;
    }

    public boolean syncGrades(String courseid) {
        this.courseid =courseid;
        MoodleGrades mgrades;
        MoodleRestGrades mrgrades = new MoodleRestGrades(token);
        mgrades = mrgrades.getGrades(courseid);

        if (mgrades == null)
            return false;

       ArrayList<MoodleGradeItem> gritems = mgrades.getItems();
       MoodleGradeItem item;
       for (int i = 0; i < gritems.size(); i++) {
           item = gritems.get(i);
           //item.setCourseid(courseid);
           item.save();
           syncGrades(item);
       }

        return true;
    }

    private void syncGrades(MoodleGradeItem item) {
        ArrayList<MoodleGrade>grades = item.getGrades();
        MoodleGrade grade;
        for (int i = 0; i < grades.size(); i++) {
            grade = grades.get(i);
           // grade.setItemid(item.getActivityid());
            grade.setCourseid(courseid);
            grade.save();
        }
    }
}
