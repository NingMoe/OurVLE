package com.stoneapp.ourvlemoodle2.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Matthew on 11/02/2015.
 */
public class MoodleMessage {
    @SerializedName("msgid")
    int msgid;  //test this to know if it succeeds:  id of the created message if it succeeded, -1 when failed

    @SerializedName("clientmsgid")
    String clientmsgid; //your own id for the message

    @SerializedName("errormessage")
    String errormessage; //error message - if it failed

    public String getClientmsgid() {
        return clientmsgid;
    }

    public int getMsgid() {
        return msgid;
    }

    public String getErrormessage() {
        return errormessage;
    }
}
