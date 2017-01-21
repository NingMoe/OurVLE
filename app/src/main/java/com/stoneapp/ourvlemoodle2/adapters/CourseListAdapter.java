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
import com.stoneapp.ourvlemoodle2.models.Course;
import com.stoneapp.ourvlemoodle2.R;

import java.util.ArrayList;
import java.util.List;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.CourseViewHolder> {


    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvShortName;
        private final TextView tvFullName;
        private final ImageView courseIcon;

        public CourseViewHolder(View v) {
            super(v);

            tvFullName = (TextView) v.findViewById(R.id.textview_course_fullname);
            tvShortName = (TextView) v.findViewById(R.id.textview_course_shortname);
            courseIcon = (ImageView) v.findViewById(R.id.imageView1);
        }
    }



    private List<Course> mCourses;
    private Context mContext;
    private String mToken;
    private long mSiteId;

    public CourseListAdapter(Context context, List<Course> courses, String token, long siteid) {
        this.mCourses = new ArrayList<>(courses);
        this.mContext = context;
        this.mToken = token;
        this.mSiteId = siteid;
    }

    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup viewGroup, final int position) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.course_list_item, viewGroup, false);

        final CourseViewHolder courseViewHolder = new CourseViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = courseViewHolder.getAdapterPosition();

                Intent intent = new Intent(view.getContext(), CourseViewActivity.class);

                intent.putExtra("coursename", mCourses.get(pos).getShortname());
                intent.putExtra("coursefname", mCourses.get(pos).getFullname());
                intent.putExtra("courseid", mCourses.get(pos).getCourseid());
                intent.putExtra("coursepid", mCourses.get(pos).getId());
                intent.putExtra("token", mToken);
                intent.putExtra("siteid", mSiteId);

                view.getContext().startActivity(intent);
            }
        });

        return courseViewHolder;
    }

    @Override
    public void onBindViewHolder(CourseViewHolder courseViewHolder, int position) {
        Course course = mCourses.get(position);

        String course_fullname = course.getFullname();

        if (course_fullname != null) {
            courseViewHolder.tvFullName.setText(course_fullname.trim());
        }

        String course_shortname = course.getShortname();

        if (course_shortname != null) {
            courseViewHolder.tvShortName.setText(course_shortname);
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

        courseViewHolder.courseIcon.setImageDrawable(drawable2);
    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    public void updateList(List<Course> courses) {
        this.mCourses = new ArrayList<>(courses);
        notifyDataSetChanged();
    }
}

