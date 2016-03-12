package com.stoneapp.ourvlemoodle2.models;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by Matthew on 24/01/2015.
 */
public class MoodleMember extends SugarRecord<MoodleMember> {

    @SerializedName("id")
    int memberid ;  //ID of the user

    @SerializedName("username")
    String username; //Username policy is defined in Moodle security config

    @SerializedName("firstname")
    String firstname;   //The first name(s) of the user

    @SerializedName("lastname")
    String lastname;  //The family name of the user

    @SerializedName("fullname")
    String fullname;  //The fullname of the user

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
}
