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
    private Context ctxt;
    private String token;
    private String filter = "";

    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView memberpic;

        public MemberViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.member_name);
            memberpic = (ImageView)itemView.findViewById(R.id.member_img);
        }
    }

    public MemberListAdapter(Context ctxt, List<MoodleMember> memberList, String token) {
        this.memberList = new ArrayList<>(memberList);
        this.ctxt = ctxt;
        this.token = token;
    }

    @Override
    public void onBindViewHolder(MemberViewHolder holder, int position) {
        holder.name.setText(memberList.get(position).getFullname());

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

        holder.memberpic.setImageDrawable(drawable2);
    }

    @Override
    public MemberViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_member, parent, false);
        final MemberViewHolder memberViewHolder = new MemberViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoodleMember member = memberList.get(memberViewHolder.getAdapterPosition());
                Intent intent = new Intent(parent.getContext(), ProfileActivity.class);
                intent.putExtra("username", member.getFullname()) ;
                intent.putExtra("email", member.getEmail());
                intent.putExtra("description", member.getDescription());
                intent.putExtra("memberid",member.getMemberid());
                intent.putExtra("token", token);

                parent.getContext().startActivity(intent);
            }
        });

        return memberViewHolder;
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public void updateMemberList(List<MoodleMember>memberList){
        this.memberList = new ArrayList<>(memberList);
        notifyDataSetChanged();
    }

    public void animateTo(List<MoodleMember> models,String filter) {
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

    public void addItem(int position, MoodleMember model) {
        memberList.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final MoodleMember model = memberList.remove(fromPosition);
        memberList.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
