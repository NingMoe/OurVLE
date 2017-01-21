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
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.stoneapp.ourvlemoodle2.adapters.EventListAdapter;
import com.stoneapp.ourvlemoodle2.models.Event;
import com.stoneapp.ourvlemoodle2.models.Course;
import com.stoneapp.ourvlemoodle2.models.SiteInfo;
import com.stoneapp.ourvlemoodle2.sync.EventSync;
import com.stoneapp.ourvlemoodle2.R;
import com.stoneapp.ourvlemoodle2.util.ConnectUtils;
import com.stoneapp.ourvlemoodle2.view.NpaLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class CalendarFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener {

    private List<Event> mEvents = new ArrayList<>();
    private List<Course> mCourses;
    private EventListAdapter mEventListAdapter;
    private List<String> mCourseids = new ArrayList<>();
    private String mToken;
    private RecyclerView mEventListView;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTvPlaceHolder;
    private ImageView mImgPlaceHolder;
    private View mRootView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.frag_all_events, container, false);

        initViews();

        setUpSwipeRefresh();

        getEventsFromDatabase();

        if(mEvents.size()>0)
        {
            mTvPlaceHolder.setVisibility(View.GONE);
            mImgPlaceHolder.setVisibility(View.GONE);
        }

        mCourses = new Select().all().from(Course.class).execute();
        List<SiteInfo> sites = new Select().all().from(SiteInfo.class).execute();
        mToken = sites.get(0).getToken(); // url token

        initCourseIds();

        setUpRecyclerView();

        setUpProgressBar();

        new LoadAllEventsTask(getActivity(), mCourseids, mToken).execute(); // refresh events

        return mRootView;
    }


    private void initViews()
    {
        mTvPlaceHolder = (TextView) mRootView.findViewById(R.id.txt_notpresent);
        mImgPlaceHolder = (ImageView) mRootView.findViewById(R.id.no_events);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swiperefresh);
        mEventListView = (RecyclerView) mRootView.findViewById(R.id.eventList);
        mProgressBar = (ProgressBar)mRootView.findViewById(R.id.progressEvent);
    }

    private void setUpSwipeRefresh()
    {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void setUpProgressBar()
    {
        mProgressBar.setIndeterminate(true);
        mProgressBar.setVisibility(View.GONE);
    }

    private void setUpRecyclerView()
    {
        mEventListAdapter = new EventListAdapter(getActivity(), mEvents);
        mEventListView.setHasFixedSize(true);
        mEventListView.setLayoutManager(new NpaLinearLayoutManager(getActivity()));
        mEventListView.setAdapter(mEventListAdapter);
    }

    private void initCourseIds()
    {
        for(int i=0;i<mCourses.size();i++)
        {
            mCourseids.add(mCourses.get(i).getCourseid()+"");
        }
    }

    private void getEventsFromDatabase()
    {
        mEvents = new Select().all().from(Event.class).execute();
    }

    private boolean isConnected() {
        return ConnectUtils.isConnected(mRootView.getContext());
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





    private class LoadAllEventsTask extends AsyncTask<Void,Void, Boolean> {

        List<String> courseids;
        Context context;
        String token;

        public LoadAllEventsTask(Context context,List<String>courseids, String token) {
            this.context = context;
            this.courseids = new ArrayList<>(courseids);
            this.token = token;

        }

        @Override
        protected void onPreExecute() {
            mImgPlaceHolder.setVisibility(View.GONE);
            mTvPlaceHolder.setVisibility(View.GONE);

            if (mEvents.size() == 0) { // check if any news are present
                mProgressBar.setVisibility(View.VISIBLE);
                if (mSwipeRefreshLayout.isRefreshing())
                    mProgressBar.setVisibility(View.GONE);
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            EventSync evsync = new EventSync(context, token);

            if (!isConnected()) {  // if there is no internet connection
                return false;
            }

            if (!hasInternet()) { // if there is no internet
                return false;
            }

            boolean sync = evsync.syncEvents(courseids);

            return sync;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            mSwipeRefreshLayout.setRefreshing(false);
            mProgressBar.setVisibility(View.GONE);
            if (result) {
                getEventsFromDatabase();
                mEventListAdapter.updateEventList(mEvents);
            } else {
                if (mEvents.size() == 0) {
                    mImgPlaceHolder.setVisibility(View.VISIBLE);
                    mTvPlaceHolder.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onRefresh() {

        new LoadAllEventsTask(getActivity(), mCourseids, mToken).execute(); // refresh events
    }


    public CalendarFragment() {/* required empty constructor */}
}
