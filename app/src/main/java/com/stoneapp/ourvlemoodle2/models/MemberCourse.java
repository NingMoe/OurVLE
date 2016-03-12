package com.stoneapp.ourvlemoodle2.models;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

/**
 * Created by Matthew on 10/02/2015.
 */
public class MemberCourse extends SugarRecord<MemberCourse> {


    @SerializedName("id")
    int courseid;

    @SerializedName("fullname")
    String fullname;

    @SerializedName("shortname")
    String shortname;

    int memberid;

    public int getCourseId() {
            return courseid;
        }

    public String getFullname() {
            return fullname;
        }

    public String getShortname() {
            return shortname;
        }

    public int getMemberid() {
        return memberid;
    }

    public void setMemberid(int userid) {
        this.memberid = memberid;
    }
}
