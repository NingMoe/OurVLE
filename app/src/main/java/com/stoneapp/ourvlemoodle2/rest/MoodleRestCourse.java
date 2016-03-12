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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;

import com.stoneapp.ourvlemoodle2.util.GsonExclude;
import com.stoneapp.ourvlemoodle2.util.MoodleConstants;
import com.stoneapp.ourvlemoodle2.models.MoodleCourse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class MoodleRestCourse  {
    private String token;
    private final String format = MoodleConstants.format; // format of output eg json/xml
    private final String function  = MoodleConstants.COURSES_FUNCTION; // api function call
    private String url = MoodleConstants.URL; // domain url

    public MoodleRestCourse(String token) { this.token = token; }

    public ArrayList<MoodleCourse> getCourses(String userid) {
        ArrayList<MoodleCourse> courses = new ArrayList<>();
        try {
            String params = "&"+ URLEncoder.encode("userid", "UTF-8")+ "=" + userid;
            String rest_url = url + "/webservice/rest/server.php" + "?wstoken=" + token
                    + "&wsfunction="+function+"&moodlewsrestformat=" + format;

            HttpURLConnection con;
            try {
                con = (HttpURLConnection) new URL(rest_url+params).openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Accept", "application/xml");
                con.setRequestProperty("Content-Language", "en-US");
                con.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());

                writer.write("");
                writer.flush();
                writer.close();

                Reader reader = new InputStreamReader(con.getInputStream());
                GsonExclude exclude = new GsonExclude();

                Gson gson = new GsonBuilder().addDeserializationExclusionStrategy(exclude)
                        .addSerializationExclusionStrategy(exclude).create();

                courses = gson.fromJson(reader, new TypeToken<List<MoodleCourse>>(){}.getType());

                reader.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return courses;
    }
}
