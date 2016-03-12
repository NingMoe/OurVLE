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
