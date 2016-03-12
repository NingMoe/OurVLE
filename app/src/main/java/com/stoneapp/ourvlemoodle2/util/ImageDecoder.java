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


