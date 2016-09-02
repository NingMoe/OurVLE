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
import com.orm.dsl.Ignore;

import java.util.List;

public class MoodleMember extends Model{

    @Column(name="memberid")
    @SerializedName("id")
    int memberid ;  //ID of the user

    @SerializedName("username")
    String username; //Username policy is defined in Moodle security config

    @SerializedName("firstname")
    String firstname;   //The first name(s) of the user

    @SerializedName("lastname")
    String lastname;  //The family name of the user

    @Column(name="fullname")
    @SerializedName("fullname")
    String fullname;  //The fullname of the user

    @Column(name="email")
    @SerializedName("email")
    String  email;   //An email address - allow email as root@localhost

    @SerializedName("address")
    String address;   //Postal address

    @SerializedName("phone1")
    String phone1;   //Phone 1

    @SerializedName("phone2")
    String phone2;   //Phone 2

    @SerializedName("icq")
    String icq;    //icq number

    @SerializedName("skype")
    String skype;  //skype id

    @SerializedName("yahoo")
    String yahoo; //yahoo id

    @SerializedName("aim")
    String aim;//aim id

    @SerializedName("msn")
    String msn;  //msn number

    @SerializedName("department")
    String department;  //department

    @SerializedName("institution")
    String institutionOptional;  //institution

    @SerializedName("idnumber")
    String idnumber;  //An arbitrary ID code number perhaps from the institution

    @SerializedName("interests")
    String interests;   //user interests (separated by commas)

    @SerializedName("firstaccess")
    int firstaccess;//first access to the site (0 if never)

    @SerializedName("lastaccess")
    int lastaccess; //last access to the site (0 if never)

    @Column(name="description")
    @SerializedName("description")
    String description;  //User profile description

    @SerializedName("descriptionformat")
    int descriptionformat;  //description format (1 = HTML, 0 = MOODLE, 2 = PLAIN or 4 = MARKDOWN)

    @SerializedName("city")
    String city; //Home city of the user

    @SerializedName("url")
    String  url;  //URL of the user

    @SerializedName("country")
    String country; //Home country code of the user, such as AU or CZ

    @SerializedName("profileimageurlsmall")
    String  profileimageurlsmall; // User image profile URL - small version

    @SerializedName("profileimageurl")
    String profileimageurl;   //User image profile URL - big version


    @Ignore
    @SerializedName("enrolledcourses")
    List<MemberCourse> courses;

    @Column(name="courseid")
    String courseid;

    public void setCourseid(String courseid) {
        this.courseid = courseid;
    }



    public List<MemberCourse> getCourses() {
        return courses;
    }

    public String getCourseid() {
        return courseid;
    }

    public String getProfileimageurl() {
        return profileimageurl;
    }

    public String getProfileimageurlsmall() {
        return profileimageurlsmall;
    }

    public String getCountry() {
        return country;
    }

    public String getUrl() {
        return url;
    }

    public String getCity() {
        return city;
    }

    public int getDescriptionformat() {
        return descriptionformat;
    }

    public String getDescription() {
        return description;
    }

    public int getLastaccess() {
        return lastaccess;
    }

    public int getFirstaccess() {
        return firstaccess;
    }

    public String getInterests() {
        return interests;
    }

    public String getIdnumber() {
        return idnumber;
    }

    public String getInstitutionOptional() {
        return institutionOptional;
    }

    public String getDepartment() {
        return department;
    }

    public String getMsn() {
        return msn;
    }

    public String getAim() {
        return aim;
    }

    public String getYahoo() {
        return yahoo;
    }

    public String getSkype() {
        return skype;
    }

    public String getIcq() {
        return icq;
    }

    public String getPhone2() {
        return phone2;
    }

    public String getPhone1() {
        return phone1;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getFullname() {
        return fullname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getUsername() {
        return username;
    }

    public int getMemberid() {
        return memberid;
    }


    public static MoodleMember findOrCreateFromJson(MoodleMember new_member) {
        int memberid = new_member.getMemberid();
        MoodleMember existingMember =
                new Select().from(MoodleMember.class).where("memberid = ? ", memberid).executeSingle();
        if (existingMember != null) {
            // found and return existing
            //UpdateMember(existingMember,new_member);
            return existingMember;
        } else {
            // create and return new user
            MoodleMember member = new_member;
            member.save();
            return member;
        }
    }

    private static void UpdateMember(MoodleMember old_member,MoodleMember new_member)
    {
        old_member = new_member;
        old_member.save();

    }
}
