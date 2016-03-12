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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Matthew on 11/22/2015.
 */
public class Connect {

    private Context ctxt;
    ConnectivityManager cm;

    public Connect(Context ctxt){
        this.ctxt = ctxt;
        cm = (ConnectivityManager)ctxt.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * checks whether phone is connected to a network
     * @param ctxt
     * @return isConnected
     */
    public static boolean isConnected(Context ctxt){
        ConnectivityManager cm =
                (ConnectivityManager)ctxt.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    /**
     * checks whether phone has internet connectivity
     * @return
     * @throws Exception
     */
    public static boolean haveInternetConnectivity() throws Exception{
        URL url = new URL("http://www.google.com");

        HttpURLConnection conn = (HttpURLConnection)url.openConnection(); //opens a connection to google.com

        conn.setRequestMethod("GET");

        if(conn.getResponseCode() == 200) //checks if server responds with 200 OK
            return true;   //internet is present
        else{
            return false;  //no internet present
        }
    }
}
