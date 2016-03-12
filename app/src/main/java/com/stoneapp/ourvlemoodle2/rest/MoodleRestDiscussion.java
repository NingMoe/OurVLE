package com.stoneapp.ourvlemoodle2.rest;

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

import com.stoneapp.ourvlemoodle2.models.MoodleDiscussion;
import com.stoneapp.ourvlemoodle2.util.GsonExclude;
import com.stoneapp.ourvlemoodle2.util.MoodleConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class MoodleRestDiscussion {

	String token;
    String url = MoodleConstants.URL;
    String format = MoodleConstants.format;
    String function = MoodleConstants.DISCUSSION_FUNCTION;

    public MoodleRestDiscussion(String token){
		this.token =token;
	}

    public ArrayList<MoodleDiscussion> getDiscussions(List<String> forumids){

        String url = MoodleConstants.URL;
        String format = MoodleConstants.format;
        String function = MoodleConstants.DISCUSSION_FUNCTION;
        ArrayList<MoodleDiscussion> discussions = null;
        MoodleDiscussion discussion;
        //Toast.makeText(context, forumids.get(0), Toast.LENGTH_SHORT).show();
        //Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show();
        try {
            String url_params="";
            for(int i=0;i<forumids.size();i++){

                url_params+="&forumids["+i+"]="+URLEncoder.encode(forumids.get(i),"UTF-8");
            }

            String rest_url = url + "/webservice/rest/server.php" + "?wstoken="
                    + token + "&wsfunction=" + function+ "&moodlewsrestformat=" + format;

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

                discussions = gson.fromJson(reader, new TypeToken<List<MoodleDiscussion>>(){}.getType());
                // discussion = gson.fromJson(reader,MoodleDiscussion.class);
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

        return discussions;
    }
	
	/*public ArrayList<MoodleDiscussion> getDiscussions(List<String> forumids){
		

		ArrayList<MoodleDiscussion> discussions = null;
        try {
            String url_params="";

            for(int i=0;i<forumids.size();i++){

                url_params+="&forumids["+i+"]="+URLEncoder.encode(forumids.get(i),"UTF-8"); //appends url params to url
            }

            String rest_url = url + "/webservice/rest/server.php" + "?wstoken="
                    + token + "&wsfunction=" + function+ "&moodlewsrestformat=" + format; //constructs rest api url

            BasicRestCall basicRestCall = new BasicRestCall(rest_url + url_params);
            InputStreamReader inputStreamReader = basicRestCall.getInputStream(); //get input stream

            if(inputStreamReader == null)
                return null;

            Reader reader = inputStreamReader;
            GsonExclude exclude = new GsonExclude();

            Gson gson = new GsonBuilder().addDeserializationExclusionStrategy(exclude)
                    .addSerializationExclusionStrategy(exclude).create();

            discussions = gson.fromJson(reader, new TypeToken<List<MoodleDiscussion>>(){}.getType()); //converts json to java objects
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
            return null; //to prevent app from crashing
        }
		
		return discussions;
	}*/
		
	
}
