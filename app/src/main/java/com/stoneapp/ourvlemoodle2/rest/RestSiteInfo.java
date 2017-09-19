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
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.net.URL;

import com.stoneapp.ourvlemoodle2.models.SiteInfo;
import com.stoneapp.ourvlemoodle2.util.GsonExclude;
import com.stoneapp.ourvlemoodle2.util.MoodleConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RestSiteInfo {
    SiteInfo siteInfo =  new SiteInfo();
    String mSiteFunction = MoodleConstants.SITE_FUNCTION; // api function call
    String mFormat = MoodleConstants.format;
    String mToken ; // url token;


    public RestSiteInfo(String token){

        this.mToken = token;
    }

    public SiteInfo getSiteInfo(){

        try {
            String urlParams = URLEncoder.encode("" ,"UTF-8");

            String url_rest = MoodleConstants.URL + "/webservice/rest/server.php" + "?wstoken="+mToken+
                    "&wsfunction="+mSiteFunction+"&moodlewsrestformat="+mFormat;
            HttpURLConnection con;
            con = (HttpURLConnection) new URL(url_rest+urlParams).openConnection();

            con.setRequestProperty("Accept","application/xml");
            con.setRequestProperty("Content-Language", "en-Us");
            con.setDoOutput(true);

            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write("");
            writer.flush();
            writer.close();

            Reader reader = new InputStreamReader(con.getInputStream());
            GsonExclude exclude = new GsonExclude();
            Gson gson = new GsonBuilder()
                    .addSerializationExclusionStrategy(exclude)
                    .addDeserializationExclusionStrategy(exclude)
                    .create();
            siteInfo = gson.fromJson(reader, SiteInfo.class);
            reader.close();
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        } catch (Exception e){
            return null;
        }

        return siteInfo;
    }



}
