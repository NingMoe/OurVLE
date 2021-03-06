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

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.stoneapp.ourvlemoodle2.models.Course;
import com.stoneapp.ourvlemoodle2.models.Event;
import com.stoneapp.ourvlemoodle2.util.TimeUtils;
import com.stoneapp.ourvlemoodle2.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

    private List<Event> mEvents;
    private Context mContext;

    public static class EventViewHolder extends RecyclerView.ViewHolder{

        TextView tvName;
        TextView tvDesc;
        TextView tvCourseName;
        TextView tvDate;
        ImageView icon;

        public EventViewHolder(View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.eventname);
            tvDesc = (TextView) v.findViewById(R.id.eventdesc);
            tvCourseName = (TextView) v.findViewById(R.id.eventcourse);
            tvDate = (TextView) v.findViewById(R.id.eventdate);
            icon = (ImageView) v.findViewById(R.id.calImg);
        }
    }

    public EventListAdapter(Context context, List<Event> events){
        this.mEvents = new ArrayList<>(events);
        this.mContext = context;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_event, viewGroup, false);

        final EventViewHolder eventViewHolder = new EventViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //logToast.makeText(mContext,"Hello",Toast.LENGTH_SHORT).show();

            }
        });

        return eventViewHolder;
    }

    @Override
    public void onBindViewHolder(EventViewHolder eventViewHolder, int position) {
        if(position>=0)
        {
            final Event event  = mEvents.get(position);

            String eventname = event.getName();

            if (!TextUtils.isEmpty(eventname)) eventViewHolder.tvName.setText(eventname);

            String eventdesc = event.getDescription();
            if (!TextUtils.isEmpty(eventdesc)) {
                eventViewHolder.tvDesc.setText(Html.fromHtml(eventdesc).toString().trim()); //convert html to string
            }

            Course eventCourse = new Select().from(Course.class).where("courseid = ?",event.getCourseid()).executeSingle();

            if(eventCourse!=null)
            {
                if(!TextUtils.isEmpty(eventCourse.getShortname())){
                    eventViewHolder.tvCourseName.setText(eventCourse.getShortname());
                }
            }


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
            {
                eventViewHolder.tvDate.setTextColor(Color.RED);
            }else {
                eventViewHolder.tvDate.setTextColor(Color.BLACK);
            }

            eventViewHolder.tvDate.setText(TimeUtils.getTime(eventdate));
        }

    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public void updateEventList(List<Event> events){
        this.mEvents = new ArrayList<>(events);
        notifyDataSetChanged();
    }
}

