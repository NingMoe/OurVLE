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
import com.stoneapp.ourvlemoodle2.models.MoodlePost;
import com.stoneapp.ourvlemoodle2.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostListViewHolder> {
    private List<MoodlePost> postList;
    private Context context;

    public static class PostListViewHolder extends RecyclerView.ViewHolder {
        private final TextView username;
        private final TextView subject;
        private final TextView message;
        private final TextView posttime;
        private final ImageView postImage;

        public PostListViewHolder(View v) {
            super(v);

            username = (TextView) v.findViewById(R.id.postUser);
            subject = (TextView) v.findViewById(R.id.postTitle);
            message = (TextView) v.findViewById(R.id.postMessage);
            posttime = (TextView) v.findViewById(R.id.postDate);
            postImage = (ImageView) v.findViewById(R.id.postImage);
        }

        public TextView getUserNameView() {
            return username;
        }

        public TextView getSubjectView() {
            return subject;
        }

        public TextView getMessageView() {
            return message;
        }

        public ImageView getPostImageView() {
            return postImage;
        }

        public TextView getPostTimeView() {
            return posttime;
        }
    }

    public PostListAdapter(Context context, List<MoodlePost> postList) {
        this.postList = new ArrayList<>(postList);
        this.context = context;
    }

    @Override
    public PostListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_post_item, viewGroup, false);

        return new PostListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PostListViewHolder holder, int position) {

        if(position>=0)
        {
            MoodlePost post = postList.get(position);

            String subject = post.getSubject();
            if (!TextUtils.isEmpty(subject))
                holder.getSubjectView().setText(subject);


            String username = post.getUserfullname();
            if (!TextUtils.isEmpty(username))
                holder.getUserNameView().setText(username);

            String message = post.getMessage();
            if (!TextUtils.isEmpty(message))
                holder.getMessageView().setText(message);

            //Extracts image from string to show in text view
            CharSequence format_message = Html.fromHtml(message, new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(String source) {
                    try {
                        //InputStream is = (InputStream) new URL(source).getContent();
                        //Drawable d = Drawable.createFromStream(is, "sc name");
                        //d.setBounds(0,0,50,50);
                        //return d;
                        Drawable drawFromPath;
                        int path =
                                context.getResources().getIdentifier(source, "drawable",
                                        context.getPackageName());
                        drawFromPath = ContextCompat.getDrawable(context, path);
                        drawFromPath.setBounds(0, 0, drawFromPath.getIntrinsicWidth(),
                                drawFromPath.getIntrinsicHeight());
                        return drawFromPath;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }, null);

            if (!TextUtils.isEmpty(message))
                holder.getMessageView().setText(format_message);

            int time = post.getModified();
            holder.getPostTimeView().setText(TimeUtils.getTime(time));

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

            holder.getPostImageView().setImageDrawable(drawable2);
        }


    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void updatePosts(List<MoodlePost> newPosts) {
        postList = new ArrayList<>(newPosts);
        notifyDataSetChanged();
    }
}
