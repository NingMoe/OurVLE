package com.stoneapp.ourvlemoodle2.activities;

import java.util.List;

import com.stoneapp.ourvlemoodle2.fragments.CourseContentFragment;
import com.stoneapp.ourvlemoodle2.fragments.EventFragment;
import com.stoneapp.ourvlemoodle2.fragments.ForumFragment;
import com.stoneapp.ourvlemoodle2.fragments.MembersFragment;

import com.stoneapp.ourvlemoodle2.models.MoodleSiteInfo;

import com.stoneapp.ourvlemoodle2.tasks.LogoutTask;

import com.stoneapp.ourvlemoodle2.view.SlidingTabLayout;

import com.stoneapp.ourvlemoodle2.R;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;

@SuppressWarnings("FieldCanBeLocal")
public class CourseViewActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener {
    private Bundle extras;
    private String coursefname;
    private String coursename;
    private Long coursepid;
    private int  courseid;
    private String token;
    private MenuItem searchitem;
    private String name;
    private int userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        extras = getIntent().getExtras();
        coursefname = extras.getString("coursefname");
        coursename = extras.getString("coursename");
        coursepid = extras.getLong("coursepid");
        courseid = extras.getInt("courseid");

        setContentView(R.layout.activity_courseview);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.pager);

        setSupportActionBar(toolbar);
        ActionBar abar = getSupportActionBar();
        if (abar != null) {
            abar.setDisplayHomeAsUpEnabled(true);
            abar.setTitle(coursename);
            abar.getThemedContext();
        }

        // first tab inserted so it is set as currently selected by default
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_tab_contents));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_tab_forum));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_tab_events));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_tab_members));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        viewPager.setAdapter(new TabsPagerAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        List<MoodleSiteInfo> sites = MoodleSiteInfo.listAll(MoodleSiteInfo.class);
        token = sites.get(0).getToken();
        name = sites.get(0).getFullname();
        userid = sites.get(0).getUserid();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_coursedetail, menu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            searchitem = menu.findItem(R.id.action_search);
            searchView = (SearchView) MenuItemCompat.getActionView(searchitem);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                onSearchRequested();
                return true;

            case R.id.action_logout:
                new LogoutTask(this).LogOut();
                return true;

            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        searchView.setQuery("", false); // clear search view
    }

    @Override
    public boolean onQueryTextSubmit(String query) { return false; }

    @Override
    public boolean onQueryTextChange(String newText) { return false; }

    private class TabsPagerAdapter extends FragmentPagerAdapter {
        public TabsPagerAdapter(FragmentManager fm) { super(fm); }

        @Override
        public Fragment getItem(int index) {
            switch (index) {
                case 0:
                    CourseContentFragment cfragment = new CourseContentFragment();
                    cfragment.setArguments(extras);
                    return cfragment;

                case 1:
                    ForumFragment frag = new ForumFragment();
                    frag.setArguments(extras);
                    return frag;

                case 2:
                    EventFragment evfrag = new EventFragment();
                    evfrag.setArguments(extras);
                    return evfrag;

                case 3:
                    MembersFragment memFrag = new MembersFragment();
                    memFrag.setArguments(extras);
                    return memFrag;

                default:
                    return null;
            }
        }

        @Override
        public int getCount() { return tabLayout.getTabCount(); }
    }

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private SearchView searchView;
}
