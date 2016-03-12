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
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

public  class FileOpener {
    public static void openFile(File file,Context context) {
        MimeTypeMap mMime = MimeTypeMap.getSingleton(); // create a new mime type instance
        int pos_dot;

        String file_name = file.toString(); // convert file name to a string

        List<Integer> pointslist = getPoints(file_name,"."); // returns index positions in the string where a full stops are fonde

        pos_dot = file_name.indexOf("."); // gets the position  of the first period in the word
        if (pointslist.size() > 1) // if there are more than one periods
            pos_dot = pointslist.get(pointslist.size()-1); // finds the position of the last period

        String ext = file_name.substring(pos_dot+1, file_name.length()); // extracts file extension
        String mtype = mMime.getMimeTypeFromExtension(ext);
        Intent intent   = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // create a new intent from mime type

        if (ext.equalsIgnoreCase("") || mtype == null) {
            switch (ext) {
                case ".doc":
                    mtype = "application/msword";
                    break;

                case ".docx":
                    mtype = "application/msword";
                    break;

                case ".xls":
                    mtype = "application/vnd.ms-excel";
                    break;

                case "xlsx":
                    mtype = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                    break;

                case ".ppt":
                    mtype = "application/vnd.ms-powerpoint";
                    break;

                case ".pptx":
                    mtype = "application/vnd.ms-powerpoint";
                    break;

                default:
                    mtype = "application/*";
                    break;
            }
        }

        intent.setDataAndType(Uri.fromFile(file), mtype);
        try {
            context.startActivity(intent);
                //context.start
        } catch(android.content.ActivityNotFoundException e) {
                Toast.makeText(context, "Could not open file", Toast.LENGTH_SHORT).show();
        }
    }

    public static List<Integer> getPoints(String string, String substr) {
        int lastIndex = 0;
        int count = 0;
        List<Integer> pointslist = new ArrayList<>();

        while (lastIndex != -1) {
            lastIndex = string.indexOf(substr, lastIndex);

            if (lastIndex != -1) {
                pointslist.add(lastIndex);
                count ++;
                lastIndex += substr.length();
            }
        }

        return pointslist;
    }
}


