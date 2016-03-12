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

import com.stoneapp.ourvlemoodle2.models.MoodleCourse;
import com.stoneapp.ourvlemoodle2.rest.MoodleRestCourse;

public class CourseSync {
    String token;

    public CourseSync(String token){
        this.token = token;
    }

    public boolean syncCourses(String userid) {
        ArrayList<MoodleCourse> courses;

        MoodleRestCourse mcourse = new MoodleRestCourse(token);

        courses = mcourse.getCourses(userid); // gets a list of courses from api call

        //check if there are no courses
        if (courses == null)
            return false;

        if (courses.size() == 0)
            return false;

        MoodleCourse.deleteAll(MoodleCourse.class); // delete all previous courses to avoid repetition

        MoodleCourse course;

        List<MoodleCourse> saved_courses;

        for (int i = 0; i < courses.size(); i++) {
            course = courses.get(i);

            //check if course is already present in database if so just overwrite previous course
            saved_courses = MoodleCourse.find(MoodleCourse.class, "courseid = ?", course.getCourseid() + "");
            if (saved_courses.size() > 0)
                course.setId(saved_courses.get(0).getId());

            course.save(); // save course in database
        }

        return true;
    }
}
