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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;

public class ImageDecoder {
    public static Bitmap decodeImage(File file) {
        try {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, opt);

            int scale =1;
            while (opt.outHeight/scale/2 >= 70 && opt.outWidth/scale/2 >= 70) {
                scale*=2;
            }

            BitmapFactory.Options opt2 = new BitmapFactory.Options();
            opt2.inSampleSize = scale;

            return  BitmapFactory.decodeStream(new FileInputStream(file), null, opt2);
        } catch (Exception e) {}
        return null;
    }
}


