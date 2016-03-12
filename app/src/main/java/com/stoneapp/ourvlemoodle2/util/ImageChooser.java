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

import java.util.ArrayList;
import java.util.List;

import com.stoneapp.ourvlemoodle2.models.MoodleModule;
import com.stoneapp.ourvlemoodle2.models.MoodleModuleContent;
import com.stoneapp.ourvlemoodle2.R;

public class ImageChooser {
    /**
     * Used to image icon of module
     * @param module
     * @return image
     */
    public static int getImage(MoodleModule module) {
        String modname = module.getModname();

        switch (modname) {
            case "forum":
                return R.drawable.ic_forum_24dp;

            case "assign":
                return R.drawable.ic_assignment_24dp;

            case "label":
                return R.drawable.ic_label_24dp;

            case "page":
                return R.drawable.page_img;

            case "url":
                return R.drawable.ic_link_24dp;

            case "quiz":
                return R.drawable.quiz_img;

            case "folder":
                return R.drawable.ic_folder_24dp;

            case "feedback":
                return R.drawable.ic_feedback_24dp;

            case "resource":
                ArrayList<MoodleModuleContent>contents = module.getContents();
                if (contents == null)
                    return R.drawable.ic_insert_drive_file_24dp;

                if (contents.size() == 0)
                    return R.drawable.ic_insert_drive_file_24dp;

                int pos_dot;
                String file_name = contents.get(0).getFilename();
                List<Integer>pointslist = getpoints(file_name, ".");
                pos_dot = file_name.indexOf(".");
                if (pointslist.size() > 1)
                    pos_dot = pointslist.get(pointslist.size() - 1);

                String ext = file_name.substring(pos_dot+1, file_name.length());
                if (ext.contentEquals("doc") || ext.contentEquals("docx"))
                    return R.drawable.ic_insert_drive_file_24dp;

                if (ext.contentEquals("ppt")  || ext.contentEquals("pptx"))
                    return R.drawable.ic_insert_drive_file_24dp;

                if (ext.contentEquals("xls") || ext.contentEquals("xlsx"))
                    return R.drawable.ic_insert_drive_file_24dp;

                if (ext.contentEquals("pdf"))
                    return R.mipmap.ic_pdf;

                if (ext.contentEquals("zip") || ext.contentEquals("rar") || ext.contentEquals("jar"))
                    return R.drawable.ic_insert_drive_file_24dp;

                if (ext.contentEquals("mp4") || ext.contentEquals("mov") || ext.contentEquals("flv") ||
                        ext.contentEquals("wmv") || ext.contentEquals("mpeg"))
                    return R.drawable.ic_movie_24dp;

                if (ext.contentEquals("mp3") || ext.contentEquals("swf"))
                    return R.drawable.ic_audiotrack_24dp;
        }

        return R.drawable.ic_insert_drive_file_24dp;
    }

    public static List<Integer> getpoints(String string, String substr) {
        int count = 0;
        List<Integer> pointslist = new ArrayList<>();

        int lastIndex = 0;
        while (lastIndex != -1) {
               lastIndex = string.indexOf(substr, lastIndex);

               if (lastIndex != -1) {
                     pointslist.add(lastIndex);
                     count++;
                     lastIndex += substr.length();
              }
        }

        return pointslist;
    }
}

