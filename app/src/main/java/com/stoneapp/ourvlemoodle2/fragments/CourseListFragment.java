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

import com.activeandroid.query.Select;
import com.stoneapp.ourvlemoodle2.adapters.CourseListAdapter;
import com.stoneapp.ourvlemoodle2.models.MoodleCourse;
import com.stoneapp.ourvlemoodle2.models.MoodleSiteInfo;
import com.stoneapp.ourvlemoodle2.tasks.CourseSync;
import com.stoneapp.ourvlemoodle2.R;
import com.stoneapp.ourvlemoodle2.util.ConnectUtils;
import com.stoneapp.ourvlemoodle2.view.DividerItemDecoration;
import com.stoneapp.ourvlemoodle2.view.NpaLinearLayoutManager;

import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class CourseListFragment extends Fragment
         {
    private List<MoodleCourse> mCourses ;
    private String mToken;
    private int mUserid;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CourseListAdapter mCourseListAdapter;
    private RecyclerView mCourseListView;
    private View mRootView;
    private long mSiteId;
    private List<MoodleSiteInfo> mSites;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.frag_course_list, container, false);

        mSites = new Select().all().from(MoodleSiteInfo.class).execute(); // the moodle site info from database

        initViews();

        intiSiteInfo();

        setUpSwipeRefresh();

        getCoursesFromDatabase(); // the moodle courses

        setUpRecyclerView();

        new LoadCoursesTask(getActivity(), mUserid, mToken).execute();

        return mRootView;
    }

    private void intiSiteInfo()
    {
        mSiteId = mSites.get(0).getId();
        mToken = mSites.get(0).getToken(); // the url token
        mUserid = mSites.get(0).getUserid();
    }


    private void getCoursesFromDatabase()
    {
        mCourses = new Select().all().from(MoodleCourse.class).execute();
    }

    private void initViews()
    {
       // mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swiperefresh);
        mCourseListView = (RecyclerView) mRootView.findViewById(R.id.courseList);
    }

    private void setUpSwipeRefresh()
    {
       // mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        //mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void setUpRecyclerView()
    {
        mCourseListAdapter = new CourseListAdapter(getActivity(), mCourses, mToken, mSiteId);
        mCourseListView.setHasFixedSize(true);
        mCourseListView.setLayoutManager(new NpaLinearLayoutManager(getActivity()));
        mCourseListView.setAdapter(mCourseListAdapter);
    }

    private boolean isConnected() {
        return ConnectUtils.isConnected(getActivity());
    }

    private static boolean hasInternet()
    {
        boolean hasInternet;

        try {
            hasInternet = ConnectUtils.haveInternetConnectivity();
        } catch(Exception e) {
            hasInternet = false;
        }

        return  hasInternet;
    }

   // @Override
    //public void onRefresh() {
     //   new LoadCoursesTask(getActivity(), mUserid, mToken).execute();
    //}

    private class LoadCoursesTask extends AsyncTask<Void,Void, Boolean> {
        int userid;
        String token;
        Context context;

        public LoadCoursesTask(Context context, int userid, String token) {
            this.userid = userid;
            this.token = token;
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            CourseSync csync = new CourseSync(token);

            if (!isConnected()) {  // if there is no internet connection
                return false;
            }

            if (!hasInternet()) { // if there is no internet
                return false;
            }

            boolean sync = csync.syncCourses(userid + "");

            return sync;
        }

        @Override
        protected void onPostExecute(Boolean result) {
         //   mSwipeRefreshLayout.setRefreshing(false);
            if (result)
            {
                getCoursesFromDatabase();
                mCourseListAdapter.updateList(mCourses);
            }

        }
    }

    public CourseListFragment() {/* required empty constructor */}
}
