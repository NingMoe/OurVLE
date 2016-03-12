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

package com.stoneapp.ourvlemoodle2.rest;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.stoneapp.ourvlemoodle2.util.MoodleConstants;
import com.stoneapp.ourvlemoodle2.models.MoodleToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.util.Log;

public class MoodleLogin {
    private String usrname;
    private String passwd;
    private MoodleToken token = new MoodleToken();
    private final String SERVICE = MoodleConstants.SERVICE;

    public MoodleLogin(String usrname, String passwd){
        this.usrname = usrname;
        this.passwd = passwd;
    }

    public MoodleToken getToken() {
        String urlParams= "";
        String URL = MoodleConstants.URL + "/login/token.php";
        try {
            urlParams = "username=" + URLEncoder.encode(usrname ,"UTF-8")+
                    "&password=" + URLEncoder.encode(passwd,"UTF-8");
        } catch(UnsupportedEncodingException e){
            Log.d("MoodleToken", "Error");
            e.printStackTrace();
        }

        HttpURLConnection con;

        try {
            con = (HttpURLConnection)new URL(URL+ "?" + urlParams + "&service=" + SERVICE).openConnection();
            con.setRequestProperty("Accept", "application/xml");
            con.setRequestProperty("Content-Language","en-US");
            con.setDoOutput(true);

            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write("");
            writer.flush();
            writer.close();

            Reader reader = new InputStreamReader(con.getInputStream());
            Gson gson = new GsonBuilder().create();
            token = gson.fromJson(reader, MoodleToken.class);
            reader.close();
        } catch(Exception e) {
            token.appenedError("\n" + SERVICE + " : " + e.getMessage());
        }

        return token;
    }
}

