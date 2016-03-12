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

import java.util.List;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.CourseViewHolder> {
    private List<MoodleCourse> courses;
    private Context ctxt;
    private String token;
    private long siteid;

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView course_shortname;
        TextView course_fullname;
        ImageView courseIcon;

        public CourseViewHolder(View itemView) {
            super(itemView);

            course_fullname = (TextView)itemView.findViewById(R.id.textview_course_fullname);
            course_shortname = (TextView)itemView.findViewById(R.id.textview_course_shortname);
            courseIcon = (ImageView)itemView.findViewById(R.id.imageView1);
        }
    }

    public CourseListAdapter(List<MoodleCourse> courses, Context ctxt, String token, long siteid) {
        this.courses = courses;
        this.ctxt = ctxt;
        this.token = token;
        this.siteid = siteid;
    }

    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup viewGroup, final int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.course_list_item, viewGroup, false);

        final CourseViewHolder courseViewHolder = new CourseViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = courseViewHolder.getAdapterPosition();

                Intent intent = new Intent(v.getContext(), CourseViewActivity.class);

                intent.putExtra("coursename", courses.get(pos).getShortname());
                intent.putExtra("coursefname", courses.get(pos).getFullname());
                intent.putExtra("courseid", courses.get(pos).getCourseid());
                intent.putExtra("coursepid", courses.get(pos).getId());
                intent.putExtra("token", token);
                intent.putExtra("siteid", siteid);

                v.getContext().startActivity(intent);
            }
        });

        return courseViewHolder;
    }

    @Override
    public void onBindViewHolder(CourseViewHolder courseViewHolder, int position) {
        MoodleCourse course = courses.get(position);

        String course_fullname = course.getFullname();

        if (course_fullname != null) {
            courseViewHolder.course_fullname.setText(course_fullname.trim());
        }

        String course_shortname = course.getShortname();

        if (course_shortname != null) {
            courseViewHolder.course_shortname.setText(course_shortname);
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
        return courses.size();
    }

    public void updateList(List<MoodleCourse> courses) {
        this.courses = courses;
        notifyDataSetChanged();
    }
}

