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

package com.stoneapp.ourvlemoodle2.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.stoneapp.ourvlemoodle2.adapters.CourseListAdapter;
import com.stoneapp.ourvlemoodle2.models.MoodleCourse;
import com.stoneapp.ourvlemoodle2.models.MoodleSiteInfo;
import com.stoneapp.ourvlemoodle2.tasks.CourseSync;
import com.stoneapp.ourvlemoodle2.R;
import com.stoneapp.ourvlemoodle2.view.DividerItemDecoration;

import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class CourseListFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener {
    private List<MoodleCourse> courses ;
    private String token;
    private int userid;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String name;
    private CourseListAdapter courseListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_course_list, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        RecyclerView courseList = (RecyclerView) view.findViewById(R.id.courseList);

        List<MoodleSiteInfo> sites =  MoodleSiteInfo.listAll(MoodleSiteInfo.class); // the moodle site info from database
        courses = MoodleCourse.listAll(MoodleCourse.class); // the moodle courses
        name = sites.get(0).getFullname();  // full name from first site info
        long siteid = sites.get(0).getId();
        token = sites.get(0).getToken(); // the url token
        userid = sites.get(0).getUserid();

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        courseListAdapter = new CourseListAdapter(getActivity(), courses, token, siteid);

        courseList.setHasFixedSize(true);
        courseList.setLayoutManager(new LinearLayoutManager(getActivity()));
        courseList.setAdapter(courseListAdapter);

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected())
            // refreshes courses list
            new LoadCoursesTask(getActivity(), userid, token).execute("");

        return view;
    }

    @Override
    public void onRefresh() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected())
            new LoadCoursesTask(getActivity(), userid, token).execute("");
    }

    private class LoadCoursesTask extends AsyncTask<String, Integer, Boolean> {
        int userid;
        CourseSync csync;
        Context context;

        public LoadCoursesTask(Context context, int userid, String token) {
            this.userid = userid;
            csync = new CourseSync(token);
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean sync = csync.syncCourses(userid + "");

            if (sync)
                courses = MoodleCourse.listAll(MoodleCourse.class); // populate course list

            return sync;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            courseListAdapter.updateList(courses);
            mSwipeRefreshLayout.setRefreshing(false);
            if (!result)
                Toast.makeText(context, "Failed to Update", Toast.LENGTH_SHORT).show();
        }
    }

    public CourseListFragment() {/* required empty constructor */}
}
