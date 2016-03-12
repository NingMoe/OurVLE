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
import java.net.URL;

import com.stoneapp.ourvlemoodle2.util.MoodleConstants;
import com.stoneapp.ourvlemoodle2.models.MoodleSiteInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MoodleRestSiteInfo {
    String sitefunction = MoodleConstants.SITE_FUNCTION; // api function call
    String format = MoodleConstants.format;
    String token ; // url token;
    MoodleSiteInfo siteInfo = new MoodleSiteInfo();

    public MoodleRestSiteInfo(String token){

        this.token = token;
    }

    public MoodleSiteInfo getSiteInfo(){
        try {

            String params = "" + URLEncoder.encode("","UTF-8");
            String url_rest = MoodleConstants.URL + "/webservice/rest/server.php" + "?wstoken="+token+
                    "&wsfunction="+sitefunction+"&moodlewsrestformat="+format;
            HttpURLConnection con;
            try {
                con = (HttpURLConnection) new URL(url_rest+params).openConnection();

                con.setRequestProperty("Accept","application/xml");
                con.setRequestProperty("Content-Language", "en-Us");
                con.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write("");
                writer.flush();
                writer.close();

                Reader reader = new InputStreamReader(con.getInputStream());
                Gson gson = new GsonBuilder().create();
                siteInfo = gson.fromJson(reader, MoodleSiteInfo.class);
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

        if (siteInfo == null){
            return new MoodleSiteInfo();
        }
        return siteInfo;
    }

    /*public MoodleSiteInfo getSiteInfo(){

        try {

            String params = "" + URLEncoder.encode("","UTF-8");
            String url_rest = MoodleConstants.URL + "/webservice/rest/server.php" + "?wstoken="+token+
                                "&wsfunction="+sitefunction+"&moodlewsrestformat="+format; //constructs url for rest api


            BasicRestCall basicRestCall = new BasicRestCall(url_rest + params);
           // InputStreamReader inputStreamReader = basicRestCall.getInputStream(); //get input stream

           // if(inputStreamReader == null)
               // return null;

            Reader reader = basicRestCall.getInputStream();
            Gson gson = new GsonBuilder().create();
            siteInfo = gson.fromJson(reader, MoodleSiteInfo.class); //converts json to java object
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
            return null; //to avoid app crashes
        }

        if (siteInfo == null){
            return new MoodleSiteInfo();
        }

        return siteInfo;
    }*/

}
