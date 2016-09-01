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

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

public class MoodleModuleContent extends Model{


    @Column(name="type")
    @SerializedName("type")
    String type;

    @Column(name="filename")
    @SerializedName("filename")
    String filename;

    @SerializedName("filepath")
    String filepath;

    @SerializedName("filesize")
    int filesize;

    @Column(name="fileurl")
    @SerializedName("fileurl")
    String fileurl;

    @SerializedName("content")
    String content;

    @SerializedName("timecreated")
    int timecreated;

    @SerializedName("timemodified")
    int timemodified;

    @SerializedName("sortorder")
    int sortorder;

    @SerializedName("userid")
    int userid;

    @SerializedName("author")
    String author;

    @SerializedName("license")
    String license;

    // Relational parameters
    @Column(name="parentid")
    Long parentid;

    @Column(name="moduleid")
    int moduleid;

    @Column(name="sectionid")
    int sectionid;

    @Column(name="courseid")
    int courseid;

    @Column(name="siteid")
    Long siteid;

    /**
     * Get content type
     *
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * Get filename as saved in Moodle
     *
     * @return
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Get filepath
     *
     * @return
     */
    public String getFilepath() {
        return filepath;
    }

    /**
     * Get filesize
     *
     * @return
     */
    public int getFilesize() {
        return filesize;
    }

    /**
     * Get file download url. <br/>
     * <b>Note:</b> token must be appended as a param to download
     *
     * @return
     */
    public String getFileurl() {
        return fileurl;
    }

    public String getContent() {
        return content;
    }

    public int getTimecreated() {
        return timecreated;
    }

    public int getTimemodified() {
        return timemodified;
    }

    public int getSortorder() {
        return sortorder;
    }

    /**
     * Get author userid
     * @return
     */
    public int getUserid() {
        return userid;
    }

    /**
     * Get author name
     * @return
     */
    public String getAuthor() {
        return author;
    }

    public String getLicense() {
        return license;
    }

    /**
     * Get the database id of the parent module. Not to be confused with actual
     * moduleid given to a module by Moodle site. This id is given by Sugar db
     * while saving the parent module
     *
     * @return
     */
    public Long getParentid() {
        return parentid;
    }

    /**
     * moduleid of the module to which this module content belongs to. This id
     * is given to a module by Moodle site.
     *
     * @return
     */
    public int getModuleid() {
        return moduleid;
    }

    /**
     * sectionid of the section to which this module content content belongs to.
     * This id is given to a section by Moodle site.
     *
     * @return
     */
    public int getSectionid() {
        return sectionid;
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
     * Set the content parent module db id
     *
     * @param parentid
     */
    public void setParentid(Long parentid) {
        this.parentid = parentid;
    }

    /**
     * Set the content course Moodle id
     *
     * @param courseid
     */
    public void setCourseid(int courseid) {
        this.courseid = courseid;
    }

    /**
     * Set the content parent module section Moodle id
     *
     * @param sectionid
     */
    public void setSectionid(int sectionid) {
        this.sectionid = sectionid;
    }

    /**
     * Set the content parent module Moodle id
     *
     * @param moduleid
     */
    public void setModuleid(int moduleid) {
        this.moduleid = moduleid;
    }

    /**
     * Set the siteid to which this modulecontent belong to.
     *
     * @param siteid
     */
    public void setSiteid(Long siteid) {
        this.siteid = siteid;
    }

    public static MoodleModuleContent findOrCreateFromJson(MoodleModuleContent new_content) {

        MoodleModuleContent existingContent =
                new Select().from(MoodleModuleContent.class).where("moduleid = ?",new_content.getModuleid()).executeSingle();
        if (existingContent != null) {
            // found and return existing
            UpdateContent(existingContent,new_content);
            return existingContent;
        } else {
            // create and return new user
            MoodleModuleContent content = new_content;
            content.save();
            return content;
        }
    }

    private static void UpdateContent(MoodleModuleContent old_content,MoodleModuleContent new_contnent)
    {
        old_content = new_contnent;
        old_content.save();

    }



}
