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

public class ConnectUtils {

    public ConnectUtils() {

    }

    /**
     * checks whether phone is connected to a network
     * @return isConnected
     */
     public static boolean isConnected(Context context) {
            final ConnectivityManager cm =
                    (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }

        /**
         * checks whether phone has internet connectivity
         * @return
         * @throws Exception
         */
        public static boolean haveInternetConnectivity() throws Exception {
            final URL url = new URL("http://www.google.com");

            final HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) // checks if server responds with 200 OK
                return true;   // internet is present
            else{
                return false;  // no internet present
            }
        }
}
