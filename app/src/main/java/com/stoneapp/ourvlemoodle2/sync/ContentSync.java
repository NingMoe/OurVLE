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

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.stoneapp.ourvlemoodle2.models.Module;
import com.stoneapp.ourvlemoodle2.models.ModuleContent;
import com.stoneapp.ourvlemoodle2.models.Section;
import com.stoneapp.ourvlemoodle2.rest.RestCourseContent;

public class ContentSync {

    private int courseid;
    private long coursepid;
    private long siteid;
    private Context context;
    private boolean first_update; // flag to check whether its a first sync
    private String token; // url token
    List<Section> mSections;

    public ContentSync(int courseid, long coursepid, long siteid, String token, Context context) {
        this.courseid = courseid;
        this.coursepid = coursepid;
        this.siteid = siteid;
        this.context = context;
        this.token = token;

    }


    public boolean syncContent() {


        RestCourseContent mcontent = new RestCourseContent(token);

        mSections = mcontent.getSections(courseid + ""); // gets the sections from api call



        if (mSections == null) // check if there are no sections
            return false;

        if (mSections.size() == 0)
            return false;



       /* List<Section> oldSections = new Select().all().from(Section.class).execute();

        if(oldSections!=null) {
            for (Section section : oldSections) {
                Section.delete(Section.class, section.getId());
            }
        }*/

        Log.d(courseid+"",mSections.size()+"");

        for (int i = 0; i < mSections.size(); i++) {
            final Section section = mSections.get(i);
            section.setCourseid(courseid);
            section.setParentid(coursepid);

            Section db_section = Section.findOrCreateFromJson(section); // saves contact to database
            syncModules(db_section.getModules(),db_section.getSectionid(),db_section.getId());
        }




       /* ActiveAndroid.beginTransaction();
        try {
            deleteStaleSections();
            for (int i = 0; i < mSections.size(); i++) {
                final Section section = mSections.get(i);
                section.setCourseid(courseid);
                section.setParentid(coursepid);

                Section db_section = Section.findOrCreateFromJson(section); // saves contact to database
                syncModules(db_section.getModules(),db_section.getSectionid(),db_section.getId());
            }
            ActiveAndroid.setTransactionSuccessful();
        }finally {
            ActiveAndroid.endTransaction();
        }*/
        return true;
    }





    private void syncModules(ArrayList<Module>modules, int sectionid, long sectionpid) {
        if (modules == null) // checks if there are no modules
            return;

        //deleteStaleModules(modules);
        for(int i = 0; i < modules.size(); i++) {
            final Module module = modules.get(i);
            module.setCourseid(courseid); // set course id of module
            module.setParentid(sectionpid); // set section database id
            module.setSiteid(siteid);
            module.setSectionid(sectionid); // set section id
            Module db_module = Module.findOrCreateFromJson(module);

            syncModuleContents(db_module.getContents(), db_module.getModuleid(), db_module.getId(), sectionid); //sync module contents
        }

    }

    private void syncModuleContents(ArrayList<ModuleContent> contents, int moduleid, long modulepid, int sectionid) {
        if (contents == null) // checks if there are no contents
            return;

        //deleteStaleContent(contents);

        for (int i = 0; i < contents.size(); i++) {
            final ModuleContent content = contents.get(i);
            content.setParentid(modulepid);
            content.setModuleid(moduleid);
            content.setSectionid(sectionid);
            content.setCourseid(courseid); // set course id
            content.setSiteid(siteid);
            ModuleContent.findOrCreateFromJson(content);
        }
    }

    public ArrayList<Section> getContents() {
        List<Section>sections = getSectionsFromDatabase();
        List<Module>modules;
        List<ModuleContent>contents ;

        for (int i = 0; i < sections.size(); i++) {

            modules = getModulesFromDatabase(sections.get(i));

            for (int j = 0; j < modules.size(); j++) {
                contents = getContentsFromDatabase(modules.get(j));
                modules.get(j).setContents(contents); // sets the content for the module
            }

            sections.get(i).setModules(modules); // set the module for the section
        }

        return new ArrayList<>(sections); // returns a list of sections
    }

    private List<Section> getSectionsFromDatabase()
    {
        return new Select().from(Section.class).where("courseid = ?",courseid).execute();
    }

    private List<Module> getModulesFromDatabase(Section section)
    {
        return new Select().from(Module.class).where("parentid = ?",section.getId()).execute();
    }

    private List<ModuleContent> getContentsFromDatabase(Module module)
    {
        return new Select().from(ModuleContent.class).where("parentid = ?",module.getId()).execute();
    }



    private void deleteStaleSections()
    {

        List<Section> stale_sections = new Select().from(Section.class).where("courseid = ?",courseid).execute();
        for(int i=0;i<stale_sections.size();i++)
        {
            if(!doesSectionExistInJson(stale_sections.get(i)))
            {
                Section.delete(Section.class,stale_sections.get(i).getId());
            }
        }
    }

    private boolean doesSectionExistInJson(Section section)
    {
        for(Section sec : mSections)
        {
            if(sec.getSectionid() == section.getSectionid())
            {
                return true;
            }
        }
        return false;
    }

    private void deleteStaleModules(List<Module> modules)
    {

        List<Module> stale_modules = new Select().from(Module.class).where("courseid = ?",courseid).execute();
        for(int i=0;i<stale_modules.size();i++)
        {
            if(!doesModuleExistInJson(modules,stale_modules.get(i)))
            {
                Module.delete(Module.class,stale_modules.get(i).getId());
            }
        }
    }

    private boolean doesModuleExistInJson(List<Module> modules, Module module)
    {
        for (Module mod : modules)
        {
            if(mod.getModuleid() == module.getModuleid())
            {
                return true;
            }
        }
        return false;
    }

    private void deleteStaleContent(List<ModuleContent> contents)
    {

        List<ModuleContent> stale_contents = new Select().from(ModuleContent.class).where("courseid = ?",courseid).execute();
        for(int i=0;i<stale_contents.size();i++)
        {
            if(!doesContentExistInJson(contents,stale_contents.get(i)))
            {
                ModuleContent.delete(ModuleContent.class,stale_contents.get(i).getId());
            }
        }
    }

    private boolean doesContentExistInJson(List<ModuleContent> contents, ModuleContent content)
    {
        for(ModuleContent cont: contents)
        {
            if(cont.getModuleid() == content.getModuleid())
            {
                return true;
            }
        }
        return false;
    }




}
