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

import com.stoneapp.ourvlemoodle2.models.MoodleSection;
import com.stoneapp.ourvlemoodle2.util.GsonExclude;
import com.stoneapp.ourvlemoodle2.util.MoodleConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class MoodleRestCourseContent {
	

	private final String format = MoodleConstants.format;
	private final String function = MoodleConstants.CONTENT_FUNCTION;
	private String URL = MoodleConstants.URL;
	private String token;
	
	public MoodleRestCourseContent(String token){
        this.token = token;
    }

    public ArrayList<MoodleSection> getSections(String courseid){

        ArrayList<MoodleSection> sections = null;

        try {

            String params = "&courseid=" + URLEncoder.encode(courseid, "UTF-8");
            String url_rest = URL + "/webservice/rest/server.php" + "?wstoken="
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

                sections = gson.fromJson(reader, new TypeToken<List<MoodleSection>>(){}.getType());
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

        return sections;
    }

   /* public ArrayList<MoodleSection> getSections(String courseid){
		
		ArrayList<MoodleSection> sections = null;
		
		try {
			
			String params = "&courseid=" + URLEncoder.encode(courseid, "UTF-8"); //appends courseid param to url
			String url_rest = URL + "/webservice/rest/server.php" + "?wstoken="
					+ token + "&wsfunction=" + function
					+ "&moodlewsrestformat=" + format; //constructs rest api url

            BasicRestCall basicRestCall = new BasicRestCall(url_rest + params);
            //InputStreamReader inputStreamReader = basicRestCall.getInputStream(); //get input stream

            //if(inputStreamReader == null)
               // return null;

            Reader reader = basicRestCall.getInputStream();
            GsonExclude exclude = new GsonExclude();
				
            Gson gson = new GsonBuilder().addDeserializationExclusionStrategy(exclude)
                    .addSerializationExclusionStrategy(exclude).create();
				
            sections = gson.fromJson(reader, new TypeToken<List<MoodleSection>>(){}.getType()); //converts json to java objects
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
		
		return sections;
	}*/
}
