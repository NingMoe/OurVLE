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
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.stoneapp.ourvlemoodle2.R;
import com.stoneapp.ourvlemoodle2.models.Post;
import com.stoneapp.ourvlemoodle2.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostListViewHolder> {

    private List<Post> mPosts;
    private Context mContext;

    public static class PostListViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthor;
        TextView tvSubject;
        TextView tvMessage;
        TextView tvPostTime;
        ImageView postImage;

        public PostListViewHolder(View v) {
            super(v);

            tvAuthor = (TextView) v.findViewById(R.id.postUser);
            tvSubject = (TextView) v.findViewById(R.id.postTitle);
            tvMessage = (TextView) v.findViewById(R.id.postMessage);
            tvPostTime = (TextView) v.findViewById(R.id.postDate);
            postImage = (ImageView) v.findViewById(R.id.postImage);
        }
    }

    public PostListAdapter(Context context, List<Post> posts) {
        this.mPosts = new ArrayList<>(posts);
        this.mContext = context;
    }

    @Override
    public PostListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_post_item, viewGroup, false);
        PostListViewHolder postListViewHolder = new PostListViewHolder(v);

        return postListViewHolder;
    }

    @Override
    public void onBindViewHolder(PostListViewHolder holder, int position) {

        if(position>=0)
        {
            Post post = mPosts.get(position);

            String subject = post.getSubject();
            if (!TextUtils.isEmpty(subject)) {
                holder.tvSubject.setText(subject);
            }

            String username = post.getUserfullname();
            if (!TextUtils.isEmpty(username)) {
                holder.tvAuthor.setText(username);
            }

            String message = post.getMessage();
            if (!TextUtils.isEmpty(message)) {
                holder.tvMessage.setText(message);
            }

            if (!TextUtils.isEmpty(message)) {
                holder.tvMessage.setText(Html.fromHtml(message));
            }

            int time = post.getModified();
            holder.tvPostTime.setText(TimeUtils.getTime(time));

            char firstLetter = username.toUpperCase().charAt(0);
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color2 = generator.getColor(username);
            TextDrawable drawable2 = TextDrawable.builder()
                    .beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT)
                    .toUpperCase()
                    .endConfig()
                    .buildRound(firstLetter + "", color2);

            holder.postImage.setImageDrawable(drawable2);
        }


    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public void updatePosts(List<Post> newPosts) {
        mPosts = new ArrayList<>(newPosts);
        notifyDataSetChanged();
    }
}
