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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsUtils {
    public static final String PREF_SYNC = "pref_sync";
    public static final String PREF_SYNC_CALENDAR = "pref_sync_calendar";
    public static final String PREF_SYNC_INTERVAL = "pref_sync_interval";
    public static final String PREF_SHOW_NOTIFICATIONS = "pref_show_notifications";
    public static final String PREF_PLAY_NOTIFICATION_SOUNDS = "pref_play_notification_sound";
    public static final String PREF_NOTIFICATION_VIBRATE = "pref_notification_vibrate";
    public static final String PREF_OPEN_LINKS_EXTERNALLY = "pref_open_links_externally";

    public static boolean shouldSync(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_SYNC, false);
    }

    public static long getSyncInterval(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(sp.getString(PREF_SYNC_INTERVAL, "4"));
    }

    public static boolean shouldSyncCalendar(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_SYNC_CALENDAR, false);
    }

    public static boolean shouldShowNotifications(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_SHOW_NOTIFICATIONS, true);
    }

    public static boolean shouldPlayNotificationSound(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_PLAY_NOTIFICATION_SOUNDS, false);
    }

    public static boolean shouldOpenLinksExternally(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_OPEN_LINKS_EXTERNALLY, false);
    }

    public static boolean shouldNotificationVibrate(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_NOTIFICATION_VIBRATE, false);
    }
}
