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

package com.stoneapp.ourvlemoodle2.models;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import java.util.ArrayList;

public class MoodleGrades extends SugarRecord<MoodleGrades> {

    @SerializedName("items")
    ArrayList<MoodleGradeItem> items;

    @SerializedName("outcomes")
    ArrayList<MoodleGradeOutcome> outcomes;

    public ArrayList<MoodleGradeItem> getItems() {
        return items;
    }

    public ArrayList<MoodleGradeOutcome> getOutcomes() {
        return outcomes;
    }
//@SerializedName()
}
