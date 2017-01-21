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
import java.net.URL;
import java.net.URLEncoder;

import com.stoneapp.ourvlemoodle2.models.DiscussionPosts;
import com.stoneapp.ourvlemoodle2.util.GsonExclude;
import com.stoneapp.ourvlemoodle2.util.MoodleConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RestPost {

    private String token;
    private String url = MoodleConstants.URL;
    private String function = MoodleConstants.POSTS_FUNCTION;
    private String format = MoodleConstants.format;

    public RestPost(String token){
        this.token = token;
    }

    public DiscussionPosts getDiscussionPosts(String discussionid){
        String url = MoodleConstants.URL;
        String function = MoodleConstants.POSTS_FUNCTION;
        String format = MoodleConstants.format;
        DiscussionPosts dposts = null;

        try {
            String url_params = "&discussionid=" +URLEncoder.encode(discussionid,"UTF-8");
            String rest_url = url +"/webservice/rest/server.php" + "?wstoken="
                    + token + "&wsfunction=" + function
                    + "&moodlewsrestformat=" + format;
            HttpURLConnection con;
            try {
                con = (HttpURLConnection) new URL(rest_url + url_params).openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Accept","application/xml");
                con.setRequestProperty("Content-Language", "en-Us");
                con.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write("");
                writer.flush();
                writer.close();

                Reader reader = new InputStreamReader(con.getInputStream());
                GsonExclude exclude = new GsonExclude();

                Gson gson = new GsonBuilder().addDeserializationExclusionStrategy(exclude)
                        .addSerializationExclusionStrategy(exclude).create();

                dposts= gson.fromJson(reader, DiscussionPosts.class);
                reader.close();
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return dposts;

    }


    /*public DiscussionPosts getDiscussionPosts(String discussionid){


        DiscussionPosts dposts = null;

        try {
                String url_params = "&discussionid=" +URLEncoder.encode(discussionid,"UTF-8"); //appends params to url
                String rest_url = url +"/webservice/rest/server.php" + "?wstoken="
                        + token + "&wsfunction=" + function
                        + "&moodlewsrestformat=" + format; //constructs rest api url

                BasicRestCall basicRestCall = new BasicRestCall(rest_url + url_params);
                InputStreamReader inputStreamReader = basicRestCall.getInputStream(); //get input stream

                if(inputStreamReader == null)
                    return null;

                Reader reader = inputStreamReader;
                GsonExclude exclude = new GsonExclude();

                Gson gson = new GsonBuilder().addDeserializationExclusionStrategy(exclude)
                        .addSerializationExclusionStrategy(exclude).create();

                dposts= gson.fromJson(reader, DiscussionPosts.class); //converts gson to java object
                reader.close();

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return dposts;

    }*/
}
