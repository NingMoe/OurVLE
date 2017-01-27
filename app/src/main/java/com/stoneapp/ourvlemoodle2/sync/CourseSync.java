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

package com.stoneapp.ourvlemoodle2.sync;

import java.util.ArrayList;
import java.util.List;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.stoneapp.ourvlemoodle2.models.Course;
import com.stoneapp.ourvlemoodle2.rest.RestCourse;

public class CourseSync {
    String token;
    ArrayList<Course> courses;

    public CourseSync(String token){
        this.token = token;
    }

    public boolean syncCourses(String userid) {


        RestCourse mcourse = new RestCourse(token);

        courses = mcourse.getCourses(userid); // gets a list of courses from api call

        //check if there are no courses
        if (courses == null)
            return false;

        if (courses.size() == 0)
            return false;

        ActiveAndroid.beginTransaction();
        try {
            deleteStaleData();
            for (int i = 0; i < courses.size(); i++) {
                final Course course = courses.get(i);

                Course.findOrCreateFromJson(course); // saves contact to database
            }
            ActiveAndroid.setTransactionSuccessful();
        }finally {
            ActiveAndroid.endTransaction();
        }

        return true;
    }

    private void deleteStaleData()
    {

        List<Course> stale_course = new Select().all().from(Course.class).execute();
        for(int i=0;i<stale_course.size();i++)
        {
            if(!doesCourseExistInJson(stale_course.get(i)))
            {
                Course.delete(Course.class,stale_course.get(i).getId());
            }
        }
    }

    private boolean doesCourseExistInJson(Course course)
    {
        return courses.contains(course);
    }
}
