package com.stoneapp.ourvlemoodle2.rest;

import com.stoneapp.ourvlemoodle2.models.MoodleMessage;
import com.stoneapp.ourvlemoodle2.util.GsonExclude;
import com.stoneapp.ourvlemoodle2.util.MoodleConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew on 11/02/2015.
 */
public class RestMessageCall {

    private String token;
    private String url = MoodleConstants.URL;
    private String function = MoodleConstants.MESSAGE_FUNCTION;
    private String format = MoodleConstants.format;

    public RestMessageCall(String token){
        this.token = token;

    }

    public ArrayList<MoodleMessage> getMessageResponse(int touserid,int textformat,String message){
        String url = MoodleConstants.URL;
        String function = MoodleConstants.MESSAGE_FUNCTION;
        String format = MoodleConstants.format;

        ArrayList<MoodleMessage>messages=null;
        try {
            String params = "" + URLEncoder.encode("", "UTF-8");
            params+="&messages[0][touserid]="+ URLEncoder.encode(touserid + "", "UTF-8");
            params+="&messages[0][text]="+URLEncoder.encode(message,"UTF-8");
            params+="&messages[0][textformat]="+URLEncoder.encode(textformat+"","UTF-8");
            params+="&messages[0][clientmsgid]="+URLEncoder.encode("","UTF-8");
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

                messages = gson.fromJson(reader, new TypeToken<List<MoodleMessage>>(){}.getType());
                reader.close();
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                // return null;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                //  return null;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return messages;
    }

    /*public ArrayList<MoodleMessage> getMessageResponse(int touserid,int textformat,String message){


        ArrayList<MoodleMessage>messages=null;
        try {

                //append all necessary params for rest api
                String params = "" + URLEncoder.encode("", "UTF-8");
                params+="&messages[0][touserid]="+ URLEncoder.encode(touserid + "", "UTF-8");
                params+="&messages[0][text]="+URLEncoder.encode(message,"UTF-8");
                params+="&messages[0][textformat]="+URLEncoder.encode(textformat+"","UTF-8");
                params+="&messages[0][clientmsgid]="+URLEncoder.encode("","UTF-8");

                String url_rest = url + "/webservice/rest/server.php" + "?wstoken="
                        + token + "&wsfunction=" + function
                        + "&moodlewsrestformat=" + format; //construct rest api url

                BasicRestCall basicRestCall = new BasicRestCall(url_rest + params);
                InputStreamReader inputStreamReader = basicRestCall.getInputStream(); //get input stream

                if(inputStreamReader == null)
                    return null;

                Reader reader = inputStreamReader;
                GsonExclude exclude = new GsonExclude();

                Gson gson = new GsonBuilder().addDeserializationExclusionStrategy(exclude)
                        .addSerializationExclusionStrategy(exclude).create();

                messages = gson.fromJson(reader, new TypeToken<List<MoodleMessage>>(){}.getType()); //converts json to java objects
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
           return null; //to avoid app crashing
        }
        return messages;
    }*/
}
