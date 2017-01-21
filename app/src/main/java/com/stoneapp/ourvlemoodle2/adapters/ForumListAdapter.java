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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.stoneapp.ourvlemoodle2.R;
import com.stoneapp.ourvlemoodle2.activities.DiscussionActivity;
import com.stoneapp.ourvlemoodle2.models.Forum;

import java.util.ArrayList;
import java.util.List;

public class ForumListAdapter extends RecyclerView.Adapter<ForumListAdapter.ForumViewHolder> {

    private Context mContext;
    private List<Forum> mForums;
    private String mToken;


    public static class ForumViewHolder extends RecyclerView.ViewHolder{
        TextView tvName;
        TextView tvDesc;
        ImageView forum_img;


        public ForumViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView)itemView.findViewById(R.id.forum_name);
            tvDesc = (TextView)itemView.findViewById(R.id.forum_desc);
            forum_img = (ImageView)itemView.findViewById(R.id.forum_img);
        }
    }



    public ForumListAdapter(Context ctxt, List<Forum> forums, String token) {
        this.mContext = ctxt;
        this.mForums = new ArrayList<>(forums);
        this.mToken = token;
    }

    @Override
    public ForumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_forum_item, parent, false);
        final ForumViewHolder forumViewHolder = new ForumViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = forumViewHolder.getAdapterPosition();
                if(position>=0)
                {
                    Forum forum = mForums.get(position);
                    Intent intent = new Intent(mContext, DiscussionActivity.class);
                    intent.putExtra("forumid", forum.getForumid());
                    intent.putExtra("forumname", forum.getName());
                    intent.putExtra("token", mToken);
                    mContext.startActivity(intent);
                }

            }
        });
        return forumViewHolder;
    }

    @Override
    public void onBindViewHolder(ForumViewHolder holder, int position) {

        final Forum forum = mForums.get(position);

        if (forum.getName() != null) holder.tvName.setText(forum.getName());

        if (forum.getIntro() != null) {
            holder.tvDesc.setText(Html.fromHtml(forum.getIntro()).toString().trim()); // converts html to normal string
        }

        String forumname = forum.getName();
        char firstLetter = forumname.toUpperCase().charAt(0);
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color2 = generator.getColor(forumname);
        TextDrawable drawable2 = TextDrawable.builder()
                .beginConfig()
                .textColor(Color.WHITE)
                .useFont(Typeface.DEFAULT)
                .toUpperCase()
                .endConfig()
                .buildRound(firstLetter + "", color2);

        holder.forum_img.setImageDrawable(drawable2);
    }

    public void updateForumList(List<Forum> newForums) {
        this.mForums = new ArrayList<>(newForums);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mForums.size();
    }
}
