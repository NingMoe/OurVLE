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

package com.stoneapp.ourvlemoodle2.models;

import java.util.ArrayList;
import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

public class MoodleSection extends Model{

    @Column(name="sectionid")
    @SerializedName("id")
    int sectionid;

    @Column(name="name")
    @SerializedName("name")
    String name;

    @SerializedName("visible")
    int visible;

    @Column(name="summary")
    @SerializedName("summary")
    String summary;

    @SerializedName("summaryformat")
    int summaryformat;

    @Ignore
    @SerializedName("modules")
    ArrayList<MoodleModule> modules;

    // Relational parameters
    @Column(name="parentid")
    Long parentid;

    @Column(name="courseid")
    int courseid;

    @Column(name="siteid")
    Long siteid;

    /**
     * Section ID
     *
     * @return
     */
    public int getSectionid() {
        return sectionid;
    }

    /**
     * Section name
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Is the section visible
     *
     * @return
     */
    public int getVisible() {
        return visible;
    }

    /**
     * Section description
     *
     * @return
     */
    public String getSummary() {
        return summary;
    }

    /**
     * summary format (1 = HTML, 0 = MOODLE, 2 = PLAIN or 4 = MARKDOWN)
     *
     * @return
     */
    public int getSummaryformat() {
        return summaryformat;
    }

    /**
     * list of modules in this section
     *
     * @return
     */
    public ArrayList<MoodleModule> getModules() {
        return modules;
    }

    /**
     * Set the list of modules in this section <br/>
     * Used while fetching course contents from database.
     *
     * @return
     */
    public void setModules(List<MoodleModule> modules) {
        this.modules = new ArrayList<MoodleModule>(modules);
    }

    /**
     * Get the database id of the parent course. Not to be confused with actual
     * courseid from Moodle site. This id is given by Sugar db while saving the
     * parent
     *
     * @return
     */
    public Long getParentid() {
        return parentid;
    }

    /**
     * courseid of the course to which this section belongs to. This id is given
     * to a course by Moodle site
     *
     * @return
     */
    public int getCourseid() {
        return courseid;
    }

    /**
     * Get the siteid of the Moodle site to which this section belong to. siteid
     * is given to an user account by sugar db on successful login
     *
     * @return
     */
    public Long getSiteid() {
        return siteid;
    }

    /**
     * Set the course db id
     *
     * @param parentid
     */
    public void setParentid(Long parentid) {
        this.parentid = parentid;
    }

    /**
     * Set the course Moodle id
     *
     * @param courseid
     */
    public void setCourseid(int courseid) {
        this.courseid = courseid;
    }

    /**
     * Set the siteid to which this course content belong to.
     *
     * @param siteid
     */
    public void setSiteid(Long siteid) {
        this.siteid = siteid;
    }

    public static MoodleSection findOrCreateFromJson(MoodleSection new_section) {
        int sectionid = new_section.getSectionid();
        MoodleSection existingSection =
                new Select().from(MoodleSection.class).where("sectionid = ?", sectionid).executeSingle();
        if (existingSection != null) {
            // found and return existing
            UpdateSection(existingSection,new_section);
            return existingSection;
        } else {
            // create and return new user
            MoodleSection section = new_section;
            section.save();
            return section;
        }
    }

    private static void UpdateSection(MoodleSection old_section,MoodleSection new_section)
    {
        old_section = new_section;
        old_section.save();

    }





}
