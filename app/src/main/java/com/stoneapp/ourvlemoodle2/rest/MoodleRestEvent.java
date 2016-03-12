package com.stoneapp.ourvlemoodle2.rest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.stoneapp.ourvlemoodle2.models.MoodleEvents;
import com.stoneapp.ourvlemoodle2.util.GsonExclude;
import com.stoneapp.ourvlemoodle2.util.MoodleConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class MoodleRestEvent {

    String token;
    String url = MoodleConstants.URL; //website url
    String function = MoodleConstants.EVENTS_FUNCTION; //rest api function
    String format = MoodleConstants.format; //format of output

    public MoodleRestEvent(String token){
        this.token = token;
    }

    public MoodleEvents getEvents(ArrayList<String> courseids) {
        long today_millis = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(today_millis);
        long timestart = timestamp.getTime() / 1000;
        MoodleEvents events = null;
        String url = MoodleConstants.URL;
        String function = MoodleConstants.EVENTS_FUNCTION;
        String format = MoodleConstants.format;
        String params = "";
        try {
            params+="&options[timeend]="+URLEncoder.encode(today_millis+"","UTF-8");
            params+="&options[timestart]="+URLEncoder.encode(timestart+"","UTF-8");

            for(int i = 0; i < courseids.size();i++){
                params+="&events[courseids]["+i+"]=" + URLEncoder.encode(courseids.get(i),"UTF-8");
            }
            String url_rest = url + "/webservice/rest/server.php" + "?wstoken="
                    + token + "&wsfunction=" + function
                    + "&moodlewsrestformat=" + format;
            HttpURLConnection con;
            try {
                con = (HttpURLConnection) new URL(url_rest + params).openConnection();
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

                events = gson.fromJson(reader, MoodleEvents.class);
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

        return events;
    }
}
