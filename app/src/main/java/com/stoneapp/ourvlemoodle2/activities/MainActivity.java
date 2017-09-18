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

package com.stoneapp.ourvlemoodle2.activities;

import com.activeandroid.query.Select;
import com.stoneapp.ourvlemoodle2.BuildConfig;
import com.stoneapp.ourvlemoodle2.activities.settings.SettingsActivity;
import com.stoneapp.ourvlemoodle2.fragments.CalendarFragment;
import com.stoneapp.ourvlemoodle2.fragments.CourseListFragment;
import com.stoneapp.ourvlemoodle2.fragments.NewsFragment;
import com.stoneapp.ourvlemoodle2.models.SiteInfo;
import com.stoneapp.ourvlemoodle2.R;

import com.stoneapp.ourvlemoodle2.util.SettingsUtils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        setSupportActionBar(toolbar);

        viewPager.setAdapter(new TabPagerAdapter(getSupportFragmentManager()));

        tabLayout.setupWithViewPager(viewPager);

        String action = getIntent().getAction();
        if (action != null){
            if(action.equals(BuildConfig.APPLICATION_ID + ".ACTION_OPEN_EVENTS"))
            {
                viewPager.setCurrentItem(1);
            }

            if(action.equals(BuildConfig.APPLICATION_ID + ".ACTION_OPEN_NEWS"))
            {
                viewPager.setCurrentItem(2);
            }
        }


        // Create account, if needed
        CreateSyncAccount(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Create an entry for this application in the system account list, if it isn't already there.
     *
     * @param context Context
     */
    public static void CreateSyncAccount(Context context) {
        final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";
        // Value below must match the account type specified in res/xml/syncadapter.xml
        final String ACCOUNT_TYPE = BuildConfig.APPLICATION_ID;
        // hours in seconds
        final long SYNC_INTERVAL = SettingsUtils.getSyncInterval(context) * 60L * 60L;

        List<SiteInfo> sites = new Select().all().from(SiteInfo.class).execute();
        String accountName = sites.get(0).getUsername();

        if (TextUtils.isEmpty(accountName))
            accountName = "OurVLE User";

        // Create account, if it's missing. (Either first run, or user has deleted account.)
        Account account = new Account(accountName, ACCOUNT_TYPE);

        AccountManager accountManager =
                (AccountManager) context.getSystemService(ACCOUNT_SERVICE);

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(account, null, null)) {
            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(account, AUTHORITY, true);
            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.
            ContentResolver.addPeriodicSync(account, AUTHORITY, Bundle.EMPTY, SYNC_INTERVAL);
        }
    }

    public class TabPagerAdapter extends FragmentPagerAdapter {
        private CharSequence[] tabTitles = {
                getString(R.string.tab_title_courses),
                getString(R.string.tab_title_calendar),
                getString(R.string.tab_title_news)
        };

        public TabPagerAdapter(FragmentManager fm) { super(fm); }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new CourseListFragment();

                case 1:
                    return new CalendarFragment();

                case 2:
                    return new NewsFragment();

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
