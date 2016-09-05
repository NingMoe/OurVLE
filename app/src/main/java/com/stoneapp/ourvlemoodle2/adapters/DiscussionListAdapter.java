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

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.amulyakhare.textdrawable.TextDrawable;
import com.stoneapp.ourvlemoodle2.activities.PostActivity;
import com.stoneapp.ourvlemoodle2.util.TimeUtils;
import com.stoneapp.ourvlemoodle2.models.MoodleCourse;
import com.stoneapp.ourvlemoodle2.models.MoodleDiscussion;
import com.stoneapp.ourvlemoodle2.R;

import java.util.ArrayList;
import java.util.List;

public class DiscussionListAdapter extends RecyclerView.Adapter<DiscussionListAdapter.DiscussionViewHolder> {
    private Context context;
    private List<MoodleDiscussion> discussionList;
    private String token;

    public static class DiscussionViewHolder extends RecyclerView.ViewHolder {
        private final TextView course_name;
        private final TextView topic_name;
        private final TextView lastposttime;
        private final TextView startname;
        private final TextView lastpostname;
        private final ImageView icon;

        public DiscussionViewHolder(View v, final Context context,
                                    final List<MoodleDiscussion> mDataSet, final String token) {
            super(v);

            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int pos = DiscussionViewHolder.this.getAdapterPosition();

                    Intent intent = new Intent(context, PostActivity.class);

                    intent.putExtra("discussionid", mDataSet.get(pos).getDiscussionid() + "");
                    intent.putExtra("discussionname", mDataSet.get(pos).getName());
                    intent.putExtra("token", token);

                    context.startActivity(intent);
                }

            });

            course_name = (TextView) v.findViewById(R.id.coursename);
            topic_name = (TextView) v.findViewById(R.id.discussion_topicname);
            lastposttime = (TextView) v.findViewById(R.id.lastpostime);
            startname = (TextView) v.findViewById(R.id.startname);
            lastpostname = (TextView) v.findViewById(R.id.lastpostname);
            icon = (ImageView) v.findViewById(R.id.imageView1);
        }

        public TextView getCourseNameView() {
            return course_name;
        }

        public TextView getTopicNameView() {
            return topic_name;
        }

        public TextView getLastPostTimeView() {
            return lastposttime;
        }

        public TextView getStartNameView() {
            return startname;
        }

        public TextView getLastPostNameView() {
            return lastpostname;
        }

        public ImageView getIconView() {
            return icon;
        }
    }

    public DiscussionListAdapter(Context context, List<MoodleDiscussion> discussionList, String token){
        this.context = context;
        this.discussionList = new ArrayList<>(discussionList);
        this.token = token;
    }

    @Override
    public DiscussionViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_discussion_item, viewGroup, false);

        return new DiscussionViewHolder(v, context, discussionList, token);
    }

    @Override
    public void onBindViewHolder(DiscussionViewHolder holder, int position) {

        final MoodleDiscussion discussion = discussionList.get(position);

        String topic_name = discussion.getName();
        if (!TextUtils.isEmpty(topic_name))
            holder.getTopicNameView().setText(topic_name);

        MoodleCourse course = new Select().from(MoodleCourse.class).where("courseid = ?", discussion.getCourseid()).executeSingle();
        if(course!=null)
        {
            String coursename = course.getShortname();
            if (TextUtils.isEmpty(coursename))
                holder.getCourseNameView().setText("N/A");
            else
                holder.getCourseNameView().setText(coursename);
        }else{
            holder.getCourseNameView().setText("N/A");
        }


        String startname = discussion.getFirstuserfullname();
        if (!TextUtils.isEmpty(startname))
            holder.getStartNameView().setText(startname);

        String lastpostname = discussion.getLastuserfullname();
        if (!TextUtils.isEmpty(lastpostname))
            holder.getLastPostNameView().setText(lastpostname);

        int lastposttime = discussion.getTimemodified();

        holder.getLastPostTimeView().setText(TimeUtils.getTime(lastposttime));
    }

    @Override
    public int getItemCount() {
        return discussionList.size();
    }

    public void updateDiscussionList(List<MoodleDiscussion> newDiscussions) {
        this.discussionList = new ArrayList<>(newDiscussions);
        notifyDataSetChanged();
    }
}

