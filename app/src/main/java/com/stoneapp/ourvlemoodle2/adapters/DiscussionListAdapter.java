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

import android.support.v7.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.stoneapp.ourvlemoodle2.activities.PostActivity;
import com.stoneapp.ourvlemoodle2.models.Course;
import com.stoneapp.ourvlemoodle2.models.Discussion;
import com.stoneapp.ourvlemoodle2.util.TimeUtils;
import com.stoneapp.ourvlemoodle2.R;

import java.util.ArrayList;
import java.util.List;

public class DiscussionListAdapter extends RecyclerView.Adapter<DiscussionListAdapter.DiscussionViewHolder> {

    private Context mContext;
    private List<Discussion> mDiscussions;
    private String mToken;

    public static class DiscussionViewHolder extends RecyclerView.ViewHolder {

        TextView tvCourseName;
        TextView tvTopic;
        TextView tvLastPostTime;
        TextView tvStartName;
        TextView tvLastPostName;
        ImageView icon;

        public DiscussionViewHolder(View view) {
            super(view);
            tvCourseName = (TextView) view.findViewById(R.id.coursename);
            tvTopic = (TextView) view.findViewById(R.id.discussion_topicname);
            tvLastPostTime = (TextView) view.findViewById(R.id.lastpostime);
            tvStartName = (TextView) view.findViewById(R.id.startname);
            tvLastPostName = (TextView) view.findViewById(R.id.lastpostname);
            icon = (ImageView) view.findViewById(R.id.imageView1);
        }
    }

    public DiscussionListAdapter(Context context, List<Discussion> discussionList, String token){
        this.mContext = context;
        this.mDiscussions = new ArrayList<>(discussionList);
        this.mToken = token;
    }

    @Override
    public DiscussionViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_discussion_item, viewGroup, false);

        final DiscussionViewHolder discussionViewHolder = new DiscussionViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int pos = discussionViewHolder.getAdapterPosition();

                Intent intent = new Intent(mContext, PostActivity.class);
                intent.putExtra("discussionid", mDiscussions.get(pos).getDiscussionid() + "");
                intent.putExtra("discussionname", mDiscussions.get(pos).getName());
                intent.putExtra("token", mToken);

                mContext.startActivity(intent);
            }

        });

        return discussionViewHolder;
    }

    @Override
    public void onBindViewHolder(DiscussionViewHolder holder, int position) {

        final Discussion discussion = mDiscussions.get(position);

        String topic_name = discussion.getName();

        if (!TextUtils.isEmpty(topic_name)) holder.tvTopic.setText(topic_name);

        Course course = new Select().from(Course.class).where("courseid = ?",
                discussion.getCourseid()).executeSingle();

        if(course!=null)
        {
            String coursename = course.getShortname();
            if (TextUtils.isEmpty(coursename))
                holder.tvCourseName.setText("N/A");
            else
                holder.tvCourseName.setText(coursename);
        }else{
            holder.tvCourseName.setText("N/A");
        }


        String startname = discussion.getFirstuserfullname();
        if (!TextUtils.isEmpty(startname)) holder.tvStartName.setText(startname);

        String lastpostname = discussion.getLastuserfullname();
        if (!TextUtils.isEmpty(lastpostname)) holder.tvLastPostName.setText(lastpostname);

        int lastposttime = discussion.getTimemodified();


        holder.tvLastPostTime.setText(TimeUtils.getTime(lastposttime));
    }

    @Override
    public int getItemCount() {
        return mDiscussions.size();
    }

    public void updateDiscussionList(List<Discussion> newDiscussions) {
        this.mDiscussions = new ArrayList<>(newDiscussions);
        notifyDataSetChanged();
    }
}

