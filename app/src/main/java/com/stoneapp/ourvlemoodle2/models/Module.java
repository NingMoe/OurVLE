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
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

@Table(name="Module")
public class Module extends Model {

    @Column(name="moduleid")
    @SerializedName("id")
    int moduleid;

    @Column(name="url")
    @SerializedName("url")
    String url;

    @Column(name="name")
    @SerializedName("name")
    String name;

    @Column(name="description")
    @SerializedName("description")
    String description;

    @SerializedName("visible")
    int visible;

    @SerializedName("modicon")
    String modicon;

    @Column(name="modname")
    @SerializedName("modname")
    String modname;

    @SerializedName("modplural")
    String modplural;

    @SerializedName("availablefrom")
    int availablefrom;

    @SerializedName("availableuntil")
    int availableuntil;

    @SerializedName("indent")
    int indent;

    @Ignore
    @SerializedName("contents")
    ArrayList<ModuleContent> contents;

    // Relational parameters
    @Column(name="parentid")
    Long parentid;

    @Column(name="sectionid")
    int sectionid;

    @Column(name="courseid")
    int courseid;

    @Column(name="siteid")
    Long siteid;

    public Module()
    {
        super();
    }

    /**
     * module or activity id
     *
     * @return
     */
    public int getModuleid() {
        return moduleid;
    }

    /**
     * module or activity url
     *
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * activity module name
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * activity description
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * is the module visible
     *
     * @return
     */
    public int getVisible() {
        return visible;
    }

    /**
     * activity icon url
     *
     * @return
     */
    public String getModicon() {
        return modicon;
    }

    /**
     * activity module type
     *
     * @return
     */
    public String getModname() {
        return modname;
    }

    /**
     * activity module plural name
     *
     * @return
     */
    public String getModplural() {
        return modplural;
    }

    /**
     * module availability start date
     *
     * @return
     */
    public int getAvailablefrom() {
        return availablefrom;
    }

    /**
     * module availability end date
     *
     * @return
     */
    public int getAvailableuntil() {
        return availableuntil;
    }

    /**
     * number of identation in the site
     *
     * @return
     */
    public int getIndent() {
        return indent;
    }

    /**
     * List of contents
     *
     * @return
     */
    public ArrayList<ModuleContent> getContents() {
        return contents;
    }

    /**
     * Set list of contents <br/>
     * Used when getting course contents from database
     *
     * @return
     */
    public void setContents(List<ModuleContent> contents) {
        this.contents = new ArrayList<ModuleContent>(contents);
    }

    /**
     * Get the database id of the parent section. Not to be confused with actual
     * sectionid given to a section from Moodle site. This id is given by Sugar
     * db while saving the parent section
     *
     * @return
     */
    public Long getParentid() {
        return parentid;
    }

    /**
     * sectionid of the section to which this Module belongs to. This id is
     * given to a section by Moodle site.
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
     * Set the module parent section db id
     *
     * @param parentid
     */
    public void setParentid(Long parentid) {
        this.parentid = parentid;
    }

    /**
     * Set the module course Moodle id
     *
     * @param courseid
     */
    public void setCourseid(int courseid) {
        this.courseid = courseid;
    }

    /**
     * Set the module parent section Moodle id
     *
     * @param sectionid
     */
    public void setSectionid(int sectionid) {
        this.sectionid = sectionid;
    }

    /**
     * Set the siteid to which this module belong to.
     *
     * @param siteid
     */
    public void setSiteid(Long siteid) {
        this.siteid = siteid;
    }


    public static Module findOrCreateFromJson(Module new_module) {
        int moduleid = new_module.getModuleid();
        Module existingModule =
                new Select().from(Module.class).where("moduleid = ?", moduleid).executeSingle();
        if (existingModule != null) {
            // found and return existing
          //  UpdateModule(existingModule,new_module);
            return existingModule;
        } else {
            // create and return new user
            Module module = new_module;
            module.save();
            return module;
        }
    }

    private static void UpdateModule(Module old_module, Module new_module)
    {
        old_module = new_module;
        old_module.save();

    }

}
