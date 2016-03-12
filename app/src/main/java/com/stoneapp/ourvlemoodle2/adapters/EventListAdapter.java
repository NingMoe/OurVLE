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
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.stoneapp.ourvlemoodle2.util.TimeDate;
import com.stoneapp.ourvlemoodle2.models.MoodleEvent;
import com.stoneapp.ourvlemoodle2.R;

import java.util.Calendar;
import java.util.List;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {
    public static class EventViewHolder extends RecyclerView.ViewHolder{
        private TextView event_name;
        private TextView event_desc;
        private TextView event_course;
        private TextView event_date;
        private ImageView icon;

        public EventViewHolder(View itemView) {
            super(itemView);

            event_name = (TextView) itemView.findViewById(R.id.eventname);
            event_desc = (TextView) itemView.findViewById(R.id.eventdesc);
            event_course = (TextView) itemView.findViewById(R.id.eventcourse);
            event_date = (TextView) itemView.findViewById(R.id.eventdate);
            icon = (ImageView) itemView.findViewById(R.id.calImg);
        }
    }

    private List<MoodleEvent> events;
    private Context ctxt;

    public EventListAdapter(List<MoodleEvent>events, Context ctxt){
        this.events = events;
        this.ctxt = ctxt;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View  view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_event,viewGroup,false);

        EventViewHolder eventViewHolder = new EventViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
            }
        });

        return eventViewHolder;
    }

    @Override
    public void onBindViewHolder(EventViewHolder eventViewHolder, int position) {
        final MoodleEvent event  = events.get(position);

        String eventname = event.getName();

        if (eventname == null)
            eventViewHolder.event_name.setText("");
        else
            eventViewHolder.event_name.setText(eventname);

        String eventdesc = event.getDescription();
        if (eventdesc == null)
            eventViewHolder.event_desc.setText("");
        else
            eventViewHolder.event_desc.setText(Html.fromHtml(eventdesc).toString().trim()); //convert html to string

        if(event.getCoursename()!=null)
            eventViewHolder.event_course.setText(event.getCoursename());
        else
            eventViewHolder.event_course.setText("");


        final int eventdate = event.getTimestart();

        Calendar cal = Calendar.getInstance();   //instantiates a new calendar instance
        cal.setTimeInMillis((long)eventdate * 1000); //sets the time of the calendar to the event date
        Calendar cal_now = Calendar.getInstance();
        cal_now.setTimeInMillis(System.currentTimeMillis()); //sets time of calendar to current date
        int day_now = cal_now.get(Calendar.DAY_OF_MONTH); //get the current day
        final int day_date = cal.get(Calendar.DAY_OF_MONTH); //get the event date day

        int event_month = cal.get(Calendar.MONTH);
        int now_month = cal_now.get(Calendar.MONTH);

        int event_year = cal.get(Calendar.YEAR);
        int now_year = cal.get(Calendar.YEAR);

        final int month = cal.get(Calendar.MONTH);
        if (day_now  == day_date && event_month== now_month && event_year == now_year) //if the event date is the current date
            eventViewHolder.event_date.setTextColor(Color.RED);
        else
            eventViewHolder.event_date.setTextColor(Color.BLACK);

        eventViewHolder.event_date.setText(TimeDate.getTime(eventdate));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void updateEventList(List<MoodleEvent> events){
        this.events = events;
        notifyDataSetChanged();
    }
}

