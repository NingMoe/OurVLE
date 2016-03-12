package com.stoneapp.ourvlemoodle2.rest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import com.stoneapp.ourvlemoodle2.models.MoodleDiscussionPosts;
import com.stoneapp.ourvlemoodle2.util.GsonExclude;
import com.stoneapp.ourvlemoodle2.util.MoodleConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MoodleRestPost {
	
	private String token;
    private String url = MoodleConstants.URL;
    private String function = MoodleConstants.POSTS_FUNCTION;
    private String format = MoodleConstants.format;

    public MoodleRestPost(String token){
		this.token = token;
	}

    public MoodleDiscussionPosts getDiscussionPosts(String discussionid){
        String url = MoodleConstants.URL;
        String function = MoodleConstants.POSTS_FUNCTION;
        String format = MoodleConstants.format;
        MoodleDiscussionPosts dposts = null;

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

                dposts= gson.fromJson(reader, MoodleDiscussionPosts.class);
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
	
	
	/*public MoodleDiscussionPosts getDiscussionPosts(String discussionid){


		MoodleDiscussionPosts dposts = null;
		
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

                dposts= gson.fromJson(reader, MoodleDiscussionPosts.class); //converts gson to java object
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
