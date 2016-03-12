package com.stoneapp.ourvlemoodle2.util;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloader {
    public static void download(String fileurl, String filename) {
        HttpURLConnection con;

        try {
            con = (HttpURLConnection)new URL(fileurl).openConnection();
            con.connect(); // creates a new http connection

            File img_file = new File(Environment.getExternalStorageDirectory() + "/OURVLE/");
            img_file.mkdirs(); // makes a folder if one doesn't exist

            InputStream input = con.getInputStream(); //gets the image from input stream
            OutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/OURVLE/." + filename);

            byte data[] = new byte[4096];

            int count;
            while ((count=input.read(data)) !=- 1) {
                out.write(data, 0, count); // write input stream to byte array
            }
            out.close();
            input.close();
        } catch(Exception e) {
            return;
        }
    }
}
