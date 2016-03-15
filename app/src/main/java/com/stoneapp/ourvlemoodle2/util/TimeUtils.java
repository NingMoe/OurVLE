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

import java.util.Calendar;

public class TimeUtils {
    public static String getTime(int time) {
        Calendar cal = Calendar.getInstance(); //create a new calendar instance
        Calendar cal_current = Calendar.getInstance();

        cal.setTimeInMillis((long)time * 1000); //set calendar date to the time of the event
        cal_current.setTimeInMillis(System.currentTimeMillis()); //set calendar date to the current date and time

        int current_year = cal_current.get(Calendar.YEAR);
        int event_year = cal.get(Calendar.YEAR);

        int day_current = cal_current.get(Calendar.DAY_OF_MONTH); //get the current day
        int minutes = cal.get(Calendar.MINUTE); //get the event time in minute

        int current_month = cal_current.get(Calendar.MONTH);

        int event_month = cal.get(Calendar.MONTH);

        String strminute;
        if(minutes < 10)
            strminute = "0" + String.valueOf(minutes);
        else
            strminute = String.valueOf(minutes);

        int hour = cal.get(Calendar.HOUR);
        String strhour;
        if(hour == 0)
            strhour = "12";
        else
            strhour = String.valueOf(hour);

        int day = cal.get(Calendar.DAY_OF_MONTH);

        String strday = String.valueOf(day);

        boolean today = false;
        boolean tommorow = false;

        if(day == day_current && current_month == event_month && current_year == event_year) { //if the event is today
            today = true;
            tommorow = false;
        }

        if(day_current == day - 1 && current_month == event_month && current_year == event_year) { //if the event is tommorrow
            today = false;
            tommorow = true;
        }
        String am_pm;
        if(cal.get(Calendar.AM_PM) == Calendar.PM)
            am_pm= "PM";
        else
            am_pm = "AM";

        int month = cal.get(Calendar.MONTH);
        String str_month = getMonth(month);
        String date_month = str_month.substring(0, 3);

        String stryear = "";

        int year = cal.get(Calendar.YEAR);

        int curr_year = cal_current.get(Calendar.YEAR);
        if(curr_year != year)
            stryear = " '" + String.valueOf(year).substring(2, 4);

        String final_date;

        if(today)
            final_date = "Today" + " " +strhour + ":" + strminute + " " + am_pm;
        else if(tommorow)
            final_date = "Tommorow" + " " + strhour + ":" + strminute + " " + am_pm;
        else
            final_date = String.valueOf(day) + " " + date_month + stryear + " " + strhour +
                    ":" + strminute + " " + am_pm;

        return final_date;
    }

    public static String getMonth(int month) {
        switch(month) {
        case 0:
            return "January";
        case 1:
            return "February";
        case 2:
            return  "March";
        case 3:
            return "April";
        case 4:
            return "May";
        case 5:
            return "June";
        case 6:
            return "July";
        case 7:
            return "August";
        case 8:
            return "September";
        case 9:
            return "October";
        case 10:
            return "November";
        case 11:
            return "December";
        default:
            return "???";
        }
    }

}
