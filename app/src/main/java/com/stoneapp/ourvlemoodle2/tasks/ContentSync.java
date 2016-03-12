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

package com.stoneapp.ourvlemoodle2.tasks;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.text.Html;

import java.util.ArrayList;
import java.util.List;

import com.stoneapp.ourvlemoodle2.activities.CourseViewActivity;
import com.stoneapp.ourvlemoodle2.models.MoodleModule;
import com.stoneapp.ourvlemoodle2.models.MoodleModuleContent;
import com.stoneapp.ourvlemoodle2.models.MoodleSection;
import com.stoneapp.ourvlemoodle2.models.MoodleCourse;
import com.stoneapp.ourvlemoodle2.rest.MoodleRestCourseContent;
import com.stoneapp.ourvlemoodle2.util.MoodleConstants;
import com.stoneapp.ourvlemoodle2.R;

public class ContentSync {
    private int courseid;
    private long coursepid;
    private long siteid;
    private Context context;
    private List<MoodleModule> coursemodules;
    private boolean first_update; // flag to check whether its a first sync
    private String token; // url token

    public ContentSync(int courseid, long coursepid, long siteid, String token, Context context) {
        this.courseid = courseid;
        this.coursepid = coursepid;
        this.siteid = siteid;
        this.context = context;
        this.token = token;

        SharedPreferences sharedPref =
                context.getSharedPreferences(MoodleConstants.PREFS_STRING, Context.MODE_PRIVATE);

        first_update = sharedPref.getBoolean(MoodleConstants.FIRST_UPDATE, true);
    }


    public boolean syncContent() {
        ArrayList<MoodleSection> sections;
        MoodleRestCourseContent mcontent = new MoodleRestCourseContent(token);
        sections = mcontent.getSections(courseid + ""); // gets the sections from api call

        if (sections == null) // check if there are no sections
            return false;

        if (sections.size() == 0)
            return false;

        coursemodules = MoodleModule.find(MoodleModule.class, "courseid = ?",courseid+""); // gets a list of modules previously stored

        //delete all data to avoid repeat items left over items
        MoodleSection.deleteAll(MoodleSection.class, "courseid = ?", courseid + "");
        MoodleModule.deleteAll(MoodleModule.class, "courseid = ?", courseid + "");
        MoodleModuleContent.deleteAll(MoodleModuleContent.class, "courseid = ?", courseid + "");

        List<MoodleSection>saved_sections;
        MoodleSection section;

        int sectionslen = sections.size();

        for (int i = 0; i < sectionslen; i++) {
            section = sections.get(i);
            section.setCourseid(courseid);
            section.setParentid(coursepid);

            //check if the section has been saved already
            saved_sections = MoodleSection.find(MoodleSection.class, "sectionid = ?", section.getSectionid() + "");
            if (saved_sections.size() > 0)
                section.setId(saved_sections.get(0).getId());

            section.save();

            syncModules(section.getModules(), section.getSectionid(), section.getId());
        }

        return true;
    }

    private void syncModules(ArrayList<MoodleModule>modules, int sectionid, long sectionpid) {
        if (modules == null) // checks if there are no modules
            return;

        List<MoodleModule> saved_modules;
        MoodleModule module;

        int moduleslen = modules.size();

        for (int i = 0; i < modules.size(); i++) {
            int count = 0; // counter to check if a module has already been stored(no notification needed)

            module = modules.get(i);
            module.setCourseid(courseid); // set course id of module
            module.setParentid(sectionpid); // set section database id
            module.setSiteid(siteid);
            module.setSectionid(sectionid); // set section id
            //saved_modules = MoodleModule.find(MoodleModule.class, "moduleid = ?", module.getModuleid()+"");

            if (coursemodules.size() > 0 && coursemodules != null) { // checks whether there are no previously stored modules

                int coursemoduleslen = coursemodules.size();

                for (int j = 0; j < coursemoduleslen; j++) {
                    if (module.getModuleid() == coursemodules.get(j).getModuleid()) { // checks if module matches one in database
                        count++;
                    }
                }
            }

            // only notify if it isn't the first time loading course modules in the database and if the module is of type resource and it is a new module
            if (coursemodules.size() > 0
                    && coursemodules != null && module.getModname().contentEquals("resource")
                    && count == 0 && !first_update)
                 addNotification(module);

            module.save(); //save the module
            syncModuleContents(module.getContents(), module.getModuleid(), module.getId(), sectionid); //sync module contents
        }
    }

    private void syncModuleContents(ArrayList<MoodleModuleContent> contents, int moduleid, long modulepid, int sectionid) {
        if (contents == null) // checks if there are no contents
            return;

        List<MoodleModuleContent> saved_contents;
        MoodleModuleContent content;

        int len = contents.size();

        for (int i = 0; i < len; i++) {
            content = contents.get(i);
            content.setParentid(modulepid);
            content.setModuleid(moduleid);
            content.setSectionid(sectionid);
            content.setCourseid(courseid); // set course id
            content.setSiteid(siteid);

            // check if content is already stored in database if so it just overwrites the previous one
            saved_contents = MoodleModuleContent.find(MoodleModuleContent.class, "parentid = ?", content.getParentid() + "");
            if (saved_contents.size() > 0)
                content.setId(saved_contents.get(0).getId());

            content.save();
        }
    }

    public ArrayList<MoodleSection> getContents() {
        List<MoodleSection>sections = MoodleSection.find(MoodleSection.class, "courseid = ?", courseid + ""); // gets a list of sections
        List<MoodleModule>modules;
        List<MoodleModuleContent>contents ;

        int sectionslen = sections.size();

        for (int i = 0; i < sectionslen; i++) {
             modules = MoodleModule.find(MoodleModule.class, "parentid = ?", sections.get(i).getId() + "");

            int moduleslen = modules.size();

            for (int j = 0; j < moduleslen; j++) {
                contents = MoodleModuleContent.find(MoodleModuleContent.class, "parentid = ?", modules.get(j).getId() + "");
                modules.get(j).setContents(contents); // sets the content for the module
            }

            sections.get(i).setModules(modules); // set the module for the section
        }

        return new ArrayList<>(sections); // returns a list of sections
    }

    public void addNotification (MoodleModule module) {
        MoodleCourse course = MoodleCourse.find(MoodleCourse.class, "courseid = ?", courseid + "").get(0);

        Intent resultIntent = new Intent(context, CourseViewActivity.class);
        resultIntent.putExtra("coursename", course.getShortname());
        resultIntent.putExtra("coursefname", course.getFullname());
        resultIntent.putExtra("courseid", course.getCourseid());
        resultIntent.putExtra("coursepid", course.getId());

        TaskStackBuilder stackBuilder =  TaskStackBuilder.create(context);
        stackBuilder.addParentStack(CourseViewActivity.class);

        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_school_24dp)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setContentTitle("New Content")
                .setContentText(Html.fromHtml(module.getName()).toString().trim())
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_SOUND)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(Html.fromHtml(module.getName()).toString().trim()))
                .addAction(R.drawable.ic_clear_24dp, "Dismiss", null)
                .addAction(R.drawable.ic_add_24dp, "Download", null);

        NotificationManagerCompat mNotificationManager =
                NotificationManagerCompat.from(context);
        mNotificationManager.notify(module.getModuleid(), mBuilder.build());
    }
}
