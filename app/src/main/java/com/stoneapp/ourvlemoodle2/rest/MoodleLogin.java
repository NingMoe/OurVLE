package com.stoneapp.ourvlemoodle2.rest;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.stoneapp.ourvlemoodle2.util.MoodleConstants;
import com.stoneapp.ourvlemoodle2.models.MoodleToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.util.Log;

public class MoodleLogin {
    private String usrname;
    private String passwd;
    private MoodleToken token = new MoodleToken();
    private final String SERVICE = MoodleConstants.SERVICE;

    public MoodleLogin(String usrname, String passwd){
        this.usrname = usrname;
        this.passwd = passwd;
    }

    public MoodleToken getToken() {
        String urlParams= "";
        String URL = MoodleConstants.URL + "/login/token.php";
        try {
            urlParams = "username=" + URLEncoder.encode(usrname ,"UTF-8")+
                    "&password=" + URLEncoder.encode(passwd,"UTF-8");
        } catch(UnsupportedEncodingException e){
            Log.d("MoodleToken", "Error");
            e.printStackTrace();
        }

        HttpURLConnection con;

        try {
            con = (HttpURLConnection)new URL(URL+ "?" + urlParams + "&service=" + SERVICE).openConnection();
            con.setRequestProperty("Accept", "application/xml");
            con.setRequestProperty("Content-Language","en-US");
            con.setDoOutput(true);

            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write("");
            writer.flush();
            writer.close();

            Reader reader = new InputStreamReader(con.getInputStream());
            Gson gson = new GsonBuilder().create();
            token = gson.fromJson(reader, MoodleToken.class);
            reader.close();
        } catch(Exception e) {
            token.appenedError("\n" + SERVICE + " : " + e.getMessage());
        }

        return token;
    }
}

