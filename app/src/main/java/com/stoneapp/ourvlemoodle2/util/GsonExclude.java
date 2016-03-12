package com.stoneapp.ourvlemoodle2.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.annotations.SerializedName;

public class GsonExclude implements ExclusionStrategy {
    @Override
    public boolean shouldSkipClass(Class<?> arg0) {
        return false;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes field) {
        SerializedName sname = field.getAnnotation(SerializedName.class);
        if(sname != null)
            return false;

        return true;
    }
}
