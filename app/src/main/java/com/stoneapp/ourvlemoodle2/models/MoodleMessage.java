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

import com.google.gson.annotations.SerializedName;

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
