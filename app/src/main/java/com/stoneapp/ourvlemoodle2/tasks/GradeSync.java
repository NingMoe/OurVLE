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
