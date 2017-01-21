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

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.stoneapp.ourvlemoodle2.R;
import com.stoneapp.ourvlemoodle2.activities.ProfileActivity;
import com.stoneapp.ourvlemoodle2.models.Member;


import java.util.ArrayList;
import java.util.List;

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.MemberViewHolder> {

    private List<Member> mMembers;
    private Context mContext;
    private String mToken;
    private String mFilter = "";



    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView memberpic;

        public MemberViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.member_name);
            memberpic = (ImageView) v.findViewById(R.id.member_img);

        }

    }

    public MemberListAdapter(Context context, List<Member> mMembers, String token) {
        this.mMembers = new ArrayList<>(mMembers);
        this.mContext = context;
        this.mToken = token;
    }

    @Override
    public void onBindViewHolder(MemberViewHolder holder, int position) {

        String name =  mMembers.get(position).getFullname();
        if(!TextUtils.isEmpty(name)) {
            holder.name.setText(mMembers.get(position).getFullname());
        }

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color2 = generator.getColor(name);

        char firstLetter = name.toUpperCase().charAt(0);

        TextDrawable drawable2 = TextDrawable.builder()
                .beginConfig()
                .textColor(Color.WHITE)
                .useFont(Typeface.DEFAULT)
                .toUpperCase()
                .endConfig()
                .buildRound(firstLetter + "", color2);

        holder.memberpic.setImageDrawable(drawable2);
    }

    @Override
    public MemberViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_member, viewGroup, false);

        final MemberViewHolder memberViewHolder = new MemberViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = memberViewHolder.getAdapterPosition();
                if(pos>=0)
                {
                    Member member = mMembers.get(pos);
                    Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                    intent.putExtra("username", member.getFullname());
                    intent.putExtra("email", member.getEmail());
                    intent.putExtra("description", member.getDescription());
                    intent.putExtra("memberid", member.getMemberid());
                    intent.putExtra("token", mToken);

                    v.getContext().startActivity(intent);
                }
            }
        });

        return memberViewHolder;
    }

    @Override
    public int getItemCount() {
        return mMembers.size();
    }

    public void updateMemberList(List<Member> memberList){
        this.mMembers = new ArrayList<>(memberList);
        notifyDataSetChanged();
    }

    public void animateTo(List<Member> models, String filter) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
        this.mFilter = filter;
    }

    private void applyAndAnimateRemovals(List<Member> newModels) {
        for (int i = mMembers.size() - 1; i >= 0; i--) {
            final Member model = mMembers.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Member> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Member model = newModels.get(i);
            if (!mMembers.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Member> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Member model = newModels.get(toPosition);
            final int fromPosition = mMembers.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public Member removeItem(int position) {
        final Member model = mMembers.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int pos, Member model) {
        mMembers.add(pos, model);
        notifyItemInserted(pos);
    }

    public void moveItem(int fromPos, int toPos) {
        final Member model = mMembers.remove(fromPos);
        mMembers.add(toPos, model);
        notifyItemMoved(fromPos, toPos);
    }
}
