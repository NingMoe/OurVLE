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
import com.stoneapp.ourvlemoodle2.R;
import com.stoneapp.ourvlemoodle2.activities.ProfileActivity;
import com.stoneapp.ourvlemoodle2.models.MoodleMember;


import java.util.ArrayList;
import java.util.List;

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.MemberViewHolder> {
    private List<MoodleMember> memberList;
    private Context context;
    private String token;
    private String filter = "";

    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final ImageView memberpic;

        public MemberViewHolder(View v, final Context context, final List<MoodleMember> mDataSet,
                                final String token) {
            super(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = MemberViewHolder.this.getAdapterPosition();
                    if(pos>=0)
                    {
                        MoodleMember member = mDataSet.get(pos);

                        Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                        intent.putExtra("username", member.getFullname());
                        intent.putExtra("email", member.getEmail());
                        intent.putExtra("description", member.getDescription());
                        intent.putExtra("memberid", member.getMemberid());
                        intent.putExtra("token", token);

                        v.getContext().startActivity(intent);
                    }

                }
            });

            name = (TextView) v.findViewById(R.id.member_name);
            memberpic = (ImageView) v.findViewById(R.id.member_img);
        }

        public TextView getTextView() {
            return name;
        }

        public ImageView getImageView() {
            return memberpic;
        }
    }

    public MemberListAdapter(Context context, List<MoodleMember> memberList, String token) {
        this.memberList = new ArrayList<>(memberList);
        this.context = context;
        this.token = token;
    }

    @Override
    public void onBindViewHolder(MemberViewHolder holder, int position) {
        holder.getTextView().setText(memberList.get(position).getFullname());

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color2 = generator.getColor(memberList.get(position).getFullname());
        //char firstLetter = memberList.get(position).getFirstname().charAt(0);

        String name = memberList.get(position).getFullname();

        char firstLetter = name.toUpperCase().charAt(0);

        TextDrawable drawable2 = TextDrawable.builder()
                .beginConfig()
                .textColor(Color.WHITE)
                .useFont(Typeface.DEFAULT)
                .toUpperCase()
                .endConfig()
                .buildRound(firstLetter + "", color2);

        holder.getImageView().setImageDrawable(drawable2);
    }

    @Override
    public MemberViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_member, viewGroup, false);

        return new MemberViewHolder(v, context, memberList, token);
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public void updateMemberList(List<MoodleMember> memberList){
        this.memberList = new ArrayList<>(memberList);
        notifyDataSetChanged();
    }

    public void animateTo(List<MoodleMember> models, String filter) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
        this.filter = filter;
    }

    private void applyAndAnimateRemovals(List<MoodleMember> newModels) {
        for (int i = memberList.size() - 1; i >= 0; i--) {
            final MoodleMember model = memberList.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<MoodleMember> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final MoodleMember model = newModels.get(i);
            if (!memberList.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<MoodleMember> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final MoodleMember model = newModels.get(toPosition);
            final int fromPosition = memberList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public MoodleMember removeItem(int position) {
        final MoodleMember model = memberList.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int pos, MoodleMember model) {
        memberList.add(pos, model);
        notifyItemInserted(pos);
    }

    public void moveItem(int fromPos, int toPos) {
        final MoodleMember model = memberList.remove(fromPos);
        memberList.add(toPos, model);
        notifyItemMoved(fromPos, toPos);
    }
}
