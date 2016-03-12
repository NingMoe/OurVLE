package com.stoneapp.ourvlemoodle2.tasks;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.text.Html;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.stoneapp.ourvlemoodle2.BuildConfig;
import com.stoneapp.ourvlemoodle2.activities.MainActivity;
import com.stoneapp.ourvlemoodle2.models.MoodleCourse;
import com.stoneapp.ourvlemoodle2.models.MoodleEvent;
import com.stoneapp.ourvlemoodle2.models.MoodleEvents;
import com.stoneapp.ourvlemoodle2.rest.MoodleRestEvent;
import com.stoneapp.ourvlemoodle2.R;
import com.stoneapp.ourvlemoodle2.util.MoodleConstants;

public class EventSync {
    private final boolean first_update;
    String token;
    Context context;

    public EventSync(String token, Context context) {
        this.token = token;
        this.context = context;

        SharedPreferences sharedPref =
                context.getSharedPreferences(MoodleConstants.PREFS_STRING, Context.MODE_PRIVATE);

        first_update = sharedPref.getBoolean(MoodleConstants.FIRST_UPDATE, true); // first sync flag
    }

    public boolean syncEvents(ArrayList<String> courseids) {
        MoodleRestEvent mrevent  = new MoodleRestEvent(token);
        MoodleEvents events = mrevent.getEvents(courseids); // get events from api call

        // check if events present
        if (events == null)
            return false;

        ArrayList<MoodleEvent> mevents = events.getEvents();
        if (mevents == null)
            return false;

        if(mevents.size() == 0){
            return false;
        }

        List<MoodleEvent>saved_events = MoodleEvent.listAll(MoodleEvent.class); // gets previously stored events
        MoodleEvent.deleteAll(MoodleEvent.class);

        MoodleEvent event;

        String eventCourseName;
        for (int i = 0; i < mevents.size(); i++) {
            event = mevents.get(i);

            eventCourseName = MoodleCourse.find(MoodleCourse.class, "courseid = ?", event.getCourseid() + "").get(0).getShortname();

            if (eventCourseName != null)
                event.setCoursename(eventCourseName); // set the course name of the event

            int count = 0; // counter to check new events

            if (saved_events != null && saved_events.size() > 0) {
                for (int j = 0; j < saved_events.size(); j++) {
                    if (event.getEventid() == saved_events.get(j).getEventid()) // check if event already exists
                        count++;
                }
            }

            if (count == 0 && !first_update) { //if event is a new event
                if(!isEventInCal(context,event.getEventid()+""))
                    addCalendarEvent(event); //add event to user calendar
                addNotification(event); // notify user about event
            }

            event.save();
        }

        return true;
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

    public void addCalendarEvent(MoodleEvent event) {
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

    public void addNotification (MoodleEvent event) {
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

