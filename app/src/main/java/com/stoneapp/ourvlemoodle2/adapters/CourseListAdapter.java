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

package com.stoneapp.ourvlemoodle2.adapters;

import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.graphics.Typeface;

import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.stoneapp.ourvlemoodle2.activities.CourseViewActivity;
import com.stoneapp.ourvlemoodle2.models.MoodleCourse;
import com.stoneapp.ourvlemoodle2.R;

import java.util.ArrayList;
import java.util.List;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.CourseViewHolder> {
    private List<MoodleCourse> courses;
    private Context context;
    private String token;
    private long siteid;

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        private final TextView course_shortname;
        private final TextView course_fullname;
        private final ImageView courseIcon;

        public CourseViewHolder(View v, final List<MoodleCourse> courses, final String token, final long siteid) {
            super(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = CourseViewHolder.this.getAdapterPosition();

                    if(pos>=0)
                    {
                        Intent intent = new Intent(v.getContext(), CourseViewActivity.class);

                        intent.putExtra("coursename", courses.get(pos).getShortname());
                        intent.putExtra("coursefname", courses.get(pos).getFullname());
                        intent.putExtra("courseid", courses.get(pos).getCourseid());
                        intent.putExtra("coursepid", courses.get(pos).getId());
                        intent.putExtra("token", token);
                        intent.putExtra("siteid", siteid);

                        v.getContext().startActivity(intent);
                    }

                }
            });

            course_fullname = (TextView) v.findViewById(R.id.textview_course_fullname);
            course_shortname = (TextView) v.findViewById(R.id.textview_course_shortname);
            courseIcon = (ImageView) v.findViewById(R.id.imageView1);
        }

        public TextView getCourseShortnameView() {
            return course_shortname;
        }

        public TextView getCourseFullnameView() {
            return course_fullname;
        }

        public ImageView getCourseIconView() {
            return courseIcon;
        }
    }

    public CourseListAdapter(Context context, List<MoodleCourse> courses, String token, long siteid) {
        this.courses = new ArrayList<>(courses);
        this.context = context;
        this.token = token;
        this.siteid = siteid;
    }

    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup viewGroup, final int position) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.course_list_item, viewGroup, false);

        return new CourseViewHolder(v, courses, token, siteid);
    }

    @Override
    public void onBindViewHolder(CourseViewHolder courseViewHolder, int position) {
        MoodleCourse course = courses.get(position);

        String course_fullname = course.getFullname();

        if (course_fullname != null) {
            courseViewHolder.getCourseFullnameView().setText(course_fullname.trim());
        }

        String course_shortname = course.getShortname();

        if (course_shortname != null) {
            courseViewHolder.getCourseShortnameView().setText(course_shortname);
        }

        char firstLetter = course_shortname.toUpperCase().charAt(0);

        ColorGenerator generator = ColorGenerator.MATERIAL;

        int color2 = generator.getColor(course_shortname);

        TextDrawable drawable2 =
                TextDrawable.builder()
                        .beginConfig()
                        .textColor(Color.WHITE)
                        .useFont(Typeface.DEFAULT)
                        .toUpperCase()
                        .endConfig()
                        .buildRound(firstLetter + "", color2);

        courseViewHolder.getCourseIconView().setImageDrawable(drawable2);
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public void updateList(List<MoodleCourse> courses) {
        this.courses = new ArrayList<>(courses);
        notifyDataSetChanged();
    }
}

