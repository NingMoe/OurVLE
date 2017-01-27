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

package com.stoneapp.ourvlemoodle2.sync;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.text.Html;

import java.util.Calendar;
import java.util.List;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.stoneapp.ourvlemoodle2.BuildConfig;
import com.stoneapp.ourvlemoodle2.activities.MainActivity;
import com.stoneapp.ourvlemoodle2.models.Course;
import com.stoneapp.ourvlemoodle2.models.Event;
import com.stoneapp.ourvlemoodle2.models.Events;
import com.stoneapp.ourvlemoodle2.rest.RestEvent;
import com.stoneapp.ourvlemoodle2.R;

public class EventSync {
    String token;
    Context context;
    List<Event> mevents;

    public EventSync(Context context, String token) {
        this.token = token;
        this.context = context;
    }

    public boolean syncEvents(List<String> courseids) {
        RestEvent mrevent  = new RestEvent(token);
        Events events = mrevent.getEvents(courseids); // get events from api call

        // check if events present
        if (events == null)
            return false;

        mevents = events.getEvents();
        if (mevents == null)
            return false;

        if(mevents.size() == 0){
            return false;
        }


        ActiveAndroid.beginTransaction();
        try {
            deleteStaleData();
            for (int i = 0; i < mevents.size(); i++) {
                final Event event = mevents.get(i);
                Course eventCourse = new Select().from(Course.class).where("courseid = ?",event.getCourseid()).executeSingle();
                if(eventCourse!=null)
                {
                    event.setCoursename(eventCourse.getShortname());
                }

                Event.findOrCreateFromJson(event); // saves contact to database
            }
            ActiveAndroid.setTransactionSuccessful();
        }finally {
            ActiveAndroid.endTransaction();
        }

        return true;
    }

    private void deleteStaleData()
    {

        List<Event> stale_events = new Select().all().from(Event.class).execute();
        for(int i=0;i<stale_events.size();i++)
        {
            if(!doesEventExistInJson(stale_events.get(i)))
            {
                Event.delete(Event.class,stale_events.get(i).getId());
            }
        }
    }

    private boolean doesEventExistInJson(Event event)
    {
        return mevents.contains(event);
    }

    public boolean isEventInCal(Context context, String cal_meeting_id) {
        Cursor cursor = context.getContentResolver().query(
                Uri.parse("content://com.android.calendar/events"),
                new String[] { "_id" }, " _id = ? ",
                new String[] { cal_meeting_id }, null);

        if (cursor.moveToFirst()) {
            return true;
        }
        return false;
    }

    public void addCalendarEvent(Event event) {
        Calendar cal = Calendar.getInstance();
        Uri EVENTS_URI = Uri.parse("content://com.android.calendar/" + "events"); //creates a new uri for calendar
        ContentResolver cr = context.getContentResolver();

        // event insert
        ContentValues values = new ContentValues();
        values.put("calendar_id", 1);
        values.put("_id",event.getEventid());
        values.put("title", event.getName());
        values.put("allDay", 0);
        values.put("dtstart", (long)event.getTimestart() * 1000);
        values.put("dtend", (long)event.getTimestart() * 1000 + ((long)event.getTimeduration() * 1000));
        values.put("description", Html.fromHtml(event.getDescription()).toString().trim());
        values.put("hasAlarm", 1);
        values.put("eventTimezone", "UTC/GMT -5:00");
        Uri calevent = cr.insert(EVENTS_URI, values);

        // reminder insert
        Uri REMINDERS_URI = Uri.parse("content://com.android.calendar/" + "reminders");
        values = new ContentValues();
        ContentValues values2 = new ContentValues();
        ContentValues values3 = new ContentValues();

        values.put("event_id", Long.parseLong(calevent.getLastPathSegment()));
        values.put("method", 1);
        values.put("minutes", 10);

        values2.put("event_id", Long.parseLong(calevent.getLastPathSegment()));
        values2.put("method", 1);
        values2.put("minutes", 60);

        values3.put("event_id", Long.parseLong(calevent.getLastPathSegment()));
        values3.put("method", 1);
        values3.put("minutes", 60 * 24);

        cr.insert(REMINDERS_URI, values);
        cr.insert(REMINDERS_URI, values2);
        cr.insert(REMINDERS_URI, values3);
    }

    private String getCalendarUriBase(Context context) {
        Activity act = (Activity)context;
        String calendarUriBase = null;
        Uri calendars = Uri.parse("content://calendar/calendars");
        Cursor managedCursor = null;
        try {
            managedCursor = act.managedQuery(calendars, null, null, null, null);
        } catch (Exception e) {}
        if (managedCursor != null) {
            calendarUriBase = "content://calendar/";
        } else {
            calendars = Uri.parse("content://com.android.calendar/calendars");
            try {
                managedCursor = act.managedQuery(calendars, null, null, null, null);
            } catch (Exception e) {}
            if (managedCursor != null) {
                calendarUriBase = "content://com.android.calendar/";
            }
        }

        return calendarUriBase;
    }

    public void addNotification (Event event) {
        TaskStackBuilder stackBuilder =  TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);

        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setAction(BuildConfig.APPLICATION_ID + ".ACTION_OPEN_EVENTS");

        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_event_available_24dp)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setContentTitle(event.getName())
                .setContentText(Html.fromHtml(event.getDescription()).toString().trim())
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_SOUND)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(Html.fromHtml(event.getDescription()).toString().trim()))
                .addAction(R.drawable.ic_clear_24dp, "Dismiss", null)
                .addAction(R.drawable.ic_add_24dp, "Add Event", null);

        NotificationManagerCompat mNotificationManager =
                NotificationManagerCompat.from(context);

        mNotificationManager.notify(event.getEventid(), mBuilder.build());
    }
}

