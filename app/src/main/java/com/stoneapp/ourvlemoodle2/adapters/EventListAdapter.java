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
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stoneapp.ourvlemoodle2.models.Event;
import com.stoneapp.ourvlemoodle2.util.TimeUtils;
import com.stoneapp.ourvlemoodle2.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {
    private List<Event> events;
    private Context context;

    public static class EventViewHolder extends RecyclerView.ViewHolder{
        private final TextView event_name;
        private final TextView event_desc;
        private final TextView event_course;
        private final TextView event_date;
        private final ImageView icon;

        public EventViewHolder(View v) {
            super(v);

            event_name = (TextView) v.findViewById(R.id.eventname);
            event_desc = (TextView) v.findViewById(R.id.eventdesc);
            event_course = (TextView) v.findViewById(R.id.eventcourse);
            event_date = (TextView) v.findViewById(R.id.eventdate);
            icon = (ImageView) v.findViewById(R.id.calImg);
        }

        public TextView getEventNameView() {
            return event_name;
        }

        public TextView getEventDescView() {
            return event_desc;
        }

        public TextView getEventCourseView() {
            return event_course;
        }

        public TextView getEventDateView() {
            return event_date;
        }

        public ImageView getIconView() {
            return icon;
        }
    }

    public EventListAdapter(Context context, List<Event> events){
        this.events = new ArrayList<>(events);
        this.context = context;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_event, viewGroup, false);

        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EventViewHolder eventViewHolder, int position) {
        if(position>=0)
        {
            final Event event  = events.get(position);

            String eventname = event.getName();

            if (!TextUtils.isEmpty(eventname))
                eventViewHolder.getEventNameView().setText(eventname);

            String eventdesc = event.getDescription();
            if (!TextUtils.isEmpty(eventdesc))
                eventViewHolder.getEventDescView().setText(Html.fromHtml(eventdesc).toString().trim()); //convert html to string

            if(!TextUtils.isEmpty(event.getCoursename()))
                eventViewHolder.getEventCourseView().setText(event.getCoursename());

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
            if (day_now == day_date && event_month== now_month && event_year == now_year) //if the event date is the current date
                eventViewHolder.getEventDateView().setTextColor(Color.RED);
            else
                eventViewHolder.getEventDateView().setTextColor(Color.BLACK);

            eventViewHolder.getEventDateView().setText(TimeUtils.getTime(eventdate));
        }

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void updateEventList(List<Event> events){
        this.events = new ArrayList<>(events);
        notifyDataSetChanged();
    }
}

