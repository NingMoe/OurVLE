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
