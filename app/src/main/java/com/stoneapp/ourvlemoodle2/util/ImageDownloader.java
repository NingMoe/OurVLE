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
