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

import java.io.File;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class FileDownloader extends AppCompatActivity {
    File file;
    Context context;
    SharedPreferences prefs;
    boolean isexternal = false;
    

    public FileDownloader(Context context) {
        this.context = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //isexternal = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_key_storage",false);

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public long download(String file_url, String file_path, String filename) {
        if(!isexternal){
            file = new File(Environment.getExternalStoragePublicDirectory("/OURVLE") + file_path);
        }else{
            file = new File(Environment.getExternalStorageDirectory()+"/OURVLE"+file_path);
        }



        if (!file.exists())
            file.mkdirs();

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Request req = new Request(Uri.parse(file_url));
        try {
            if(!isexternal)
                req.setDestinationInExternalPublicDir("/OURVLE", file_path + filename);
            else
                req.setDestinationInExternalFilesDir(context,"/OURVLE", file_path + filename);

        } catch(Exception exec) {
            Toast.makeText(context, "No Storage Found", Toast.LENGTH_SHORT).show();
            return 0;
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        req.setTitle(filename);
        req.setDescription("File Download");

        return manager.enqueue(req);
    }
}
