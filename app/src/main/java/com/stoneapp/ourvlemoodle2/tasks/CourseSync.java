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

import java.util.ArrayList;
import java.util.List;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.stoneapp.ourvlemoodle2.models.MoodleCourse;
import com.stoneapp.ourvlemoodle2.rest.MoodleRestCourse;

public class CourseSync {
    String token;
    ArrayList<MoodleCourse> courses;

    public CourseSync(String token){
        this.token = token;
    }

    public boolean syncCourses(String userid) {


        MoodleRestCourse mcourse = new MoodleRestCourse(token);

        courses = mcourse.getCourses(userid); // gets a list of courses from api call

        //check if there are no courses
        if (courses == null)
            return false;

        if (courses.size() == 0)
            return false;

        ActiveAndroid.beginTransaction();
        try {
            //deleteStaleData();
            for (int i = 0; i < courses.size(); i++) {
                final MoodleCourse course = courses.get(i);

                MoodleCourse.findOrCreateFromJson(course); // saves contact to database
            }
            ActiveAndroid.setTransactionSuccessful();
        }finally {
            ActiveAndroid.endTransaction();
        }

        return true;
    }

    private void deleteStaleData()
    {

        List<MoodleCourse> stale_course = new Select().all().from(MoodleCourse.class).execute();
        for(int i=0;i<stale_course.size();i++)
        {
            if(!doesCourseExistInJson(stale_course.get(i)))
            {
                MoodleCourse.delete(MoodleCourse.class,stale_course.get(i).getId());
            }
        }
    }

    private boolean doesCourseExistInJson(MoodleCourse course)
    {
        return courses.contains(course);
    }
}
