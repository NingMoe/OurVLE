package com.stoneapp.ourvlemoodle2.tasks;

import com.stoneapp.ourvlemoodle2.models.MoodleMember;
import com.stoneapp.ourvlemoodle2.rest.MoodleRestMembers;

import java.util.ArrayList;
import java.util.List;

public class MemberSync {
    private String token;

    public MemberSync(String token) {
        this.token = token;
    }

    public boolean syncMembers(String courseid) {
        MoodleRestMembers mrmembers = new MoodleRestMembers(token);

        ArrayList<MoodleMember> members = mrmembers.getMembers(courseid); // gets a list of members from api call

        if (members == null)
            return false;

        if (members.size() == 0)
            return false;

        List<MoodleMember> saved_members;

        MoodleMember member;

        int len = members.size();

        for (int i = 0; i < len; i++) {
            member = members.get(i);
            member.setCourseid(courseid);

            saved_members = MoodleMember.find(MoodleMember.class, "memberid = ? and courseid = ?", member.getMemberid() + "", courseid); // gets a list of members matching the current member
            if (saved_members.size() > 0) //if a matching member is found
                member.setId(saved_members.get(0).getId()); // overwrite previously stored member with matching member id

            member.save();
        }

        return true;
    }
}
