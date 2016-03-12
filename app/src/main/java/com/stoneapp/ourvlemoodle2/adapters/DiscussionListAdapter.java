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

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;

import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.stoneapp.ourvlemoodle2.activities.PostActivity;
import com.stoneapp.ourvlemoodle2.util.TimeDate;
import com.stoneapp.ourvlemoodle2.models.MoodleCourse;
import com.stoneapp.ourvlemoodle2.models.MoodleDiscussion;
import com.stoneapp.ourvlemoodle2.R;

import java.util.List;

public class DiscussionListAdapter extends RecyclerView.Adapter<DiscussionListAdapter.DiscussionViewHolder> {
    public static class DiscussionViewHolder extends RecyclerView.ViewHolder {
        TextView course_name;
        TextView topic_name;
        TextView lastposttime;
        TextView startname;
        TextView lastpostname;
        ImageView numreplies;
        ImageView icon;

        public DiscussionViewHolder(View itemView) {
            super(itemView);

            course_name = (TextView)itemView.findViewById(R.id.coursename);
            topic_name = (TextView)itemView.findViewById(R.id.discussion_topicname);
            lastposttime = (TextView)itemView.findViewById(R.id.lastpostime);
            startname = (TextView)itemView.findViewById(R.id.startname);
            lastpostname = (TextView)itemView.findViewById(R.id.lastpostname);
           // numreplies = (ImageView)itemView.findViewById(R.id.numreplies);
            icon = (ImageView) itemView.findViewById(R.id.imageView1);
        }
    }

    private Context ctxt;
    private List<MoodleDiscussion> discussionList;
    private String token;

    public DiscussionListAdapter(Context ctxt, List<MoodleDiscussion> discussionList, String token){
        this.ctxt = ctxt;
        this.discussionList = discussionList;
        this.token = token;
    }

    @Override
    public DiscussionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_discussion_item, viewGroup, false);

        final DiscussionViewHolder discussionViewHolder = new DiscussionViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final int position = discussionViewHolder.getAdapterPosition();

                Intent intent = new Intent(ctxt, PostActivity.class);

                intent.putExtra("discussionid", discussionList.get(position).getDiscussionid() + "");
                intent.putExtra("discussionname", discussionList.get(position).getName());
                intent.putExtra("token", token);

                ctxt.startActivity(intent);
            }

        });

        return discussionViewHolder;
    }

    @Override
    public void onBindViewHolder(DiscussionViewHolder holder, int position) {
        final MoodleDiscussion discussion = discussionList.get(position);

        String topic_name = discussion.getName();
        if (topic_name == null)
            holder.topic_name.setText("");
        else
            holder.topic_name.setText(topic_name);

        String coursename = MoodleCourse.find(MoodleCourse.class, "courseid = ?", discussion.getCourseid() + "").get(0).getShortname();
        if (coursename == null)
            holder.course_name.setText("N/A");
        else
            holder.course_name.setText(coursename);

        String startname = discussion.getFirstuserfullname();
        if (startname == null)
            holder.startname.setText("");
        else
            holder.startname.setText(startname);

        String numreplies = discussion.getNumreplies().toString();

        TextDrawable drawable2 = TextDrawable.builder().beginConfig()
                .textColor(Color.WHITE)
                .useFont(Typeface.DEFAULT)
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(numreplies + "", Color.parseColor("#F44336"));
     
                //.buildRound(discussion.getNumreplies(),ContextCompat.getColor(ctxt, R.color.colorAccent));
       // holder.numreplies.setImageDrawable(drawable2);

        String lastpostname = discussion.getLastuserfullname();
        if (lastpostname == null)
            holder.lastpostname.setText("");
        else
            holder.lastpostname.setText(lastpostname);

        int lastposttime = discussion.getTimemodified();

        holder.lastposttime.setText(TimeDate.getTime(lastposttime));
    }

    @Override
    public int getItemCount() {
        return discussionList.size();
    }

    public void updateDiscussionList(List<MoodleDiscussion> newDiscussions) {
        this.discussionList = newDiscussions;
        notifyDataSetChanged();
    }
}

