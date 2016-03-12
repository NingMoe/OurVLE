package com.stoneapp.ourvlemoodle2.rest;

import com.stoneapp.ourvlemoodle2.models.MoodleGrades;
import com.stoneapp.ourvlemoodle2.util.GsonExclude;
import com.stoneapp.ourvlemoodle2.util.MoodleConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Matthew on 20/01/2015.
 */
public class MoodleRestGrades {
    String token;
    public MoodleRestGrades(String token){
        this.token = token;
    }

    public MoodleGrades getGrades(String courseid){
        MoodleGrades mgrades = null;
        String params = "";
        String url = MoodleConstants.URL;
        String function = MoodleConstants.GRADES_FUNCTION;
        String format = MoodleConstants.format;

        try{
            params+="courseid="+ URLEncoder.encode(courseid,"UTF-8");
            String rest_url = url + "/webservice/rest/server.php"+"?wstoken="+token
                    +"&wsfunction="+function+"&moodlewsrestformat="+format;

            HttpURLConnection con;
            try {
                //Handler handle =new Handler();
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
                mgrades = gson.fromJson(reader,MoodleGrades.class);

                reader.close();
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }

        return mgrades;
    }
}
