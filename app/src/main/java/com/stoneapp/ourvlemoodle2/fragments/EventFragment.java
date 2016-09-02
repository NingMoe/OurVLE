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

import java.util.ArrayList;
import java.util.List;

import com.activeandroid.query.Select;
import com.stoneapp.ourvlemoodle2.adapters.EventListAdapter;
import com.stoneapp.ourvlemoodle2.models.MoodleEvent;
import com.stoneapp.ourvlemoodle2.models.MoodleCourse;
import com.stoneapp.ourvlemoodle2.tasks.EventSync;
import com.stoneapp.ourvlemoodle2.R;
import com.stoneapp.ourvlemoodle2.util.ConnectUtils;
import com.stoneapp.ourvlemoodle2.view.NpaLinearLayoutManager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("FieldCanBeLocal")
public class EventFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener {

    private EventListAdapter mEventAdapter;
    private Context mContext;
    private List<MoodleEvent> mEvents;
    private String mToken;
    private String mCourseid;
    private View mRootView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mEventListView;
    private ProgressBar mProgressBar;
    private TextView mTvPlaceHolder;
    private ImageView mImgPlaceHolder;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            this.mCourseid = args.getInt("courseid") + "";
            this.mToken = args.getString("token");
        }

        mContext = getActivity();

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.frag_event, container, false);

        initViews();

        setUpSwipeRefresh();

        //Searches database for all events matching courseid
        getEventsFromDatabase();

        if (mEvents.size() > 0) { // check if there are any events
            mImgPlaceHolder.setVisibility(View.GONE);
            mTvPlaceHolder.setVisibility(View.GONE);
        }

        setUpRecyclerView();

        setUpProgressBar();


        new LoadEventTask(mContext,mCourseid,mToken).execute();

        return mRootView;
    }

    private void initViews()
    {
        mTvPlaceHolder = (TextView) mRootView.findViewById(R.id.txt_notpresent);
        mImgPlaceHolder = (ImageView) mRootView.findViewById(R.id.no_events);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progressEvent);
        mEventListView = (RecyclerView) mRootView.findViewById(R.id.eventList);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swiperefresh);
    }

    private void getEventsFromDatabase()
    {
        mEvents = new Select().from(MoodleEvent.class).where("courseid = ?", mCourseid).execute();
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
        mEventAdapter = new EventListAdapter(mContext, mEvents);
        mEventListView.setHasFixedSize(true);
        mEventListView.setLayoutManager(new NpaLinearLayoutManager(getActivity()));
        mEventListView.setAdapter(mEventAdapter);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                mSwipeRefreshLayout.setRefreshing(true);
                new LoadEventTask(getActivity(), mCourseid, mToken).execute(); // refresh content
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class LoadEventTask extends AsyncTask<Void,Void,Boolean> {

        ArrayList<String> courseids;
        String courseid;
        Context context;

        public LoadEventTask(Context context, String courseid, String token) {
            this.context = context;
            this.courseid = courseid;

            courseids = new ArrayList<>();
            courseids.add(courseid);
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
            final EventSync evsync = new EventSync(context, mToken);

            if (!isConnected()) {  // if there is no internet connection
                return false;
            }

            if (!hasInternet()) { // if there is no internet
                return false;
            }

            boolean sync = evsync.syncEvents(courseids); // sync events

            return sync;
        }



        @Override
        protected void onPostExecute(Boolean result) {
            mSwipeRefreshLayout.setRefreshing(false);
            mProgressBar.setVisibility(View.GONE);
            if (result) {
                getEventsFromDatabase();
                mEventAdapter.updateEventList(mEvents);
            }
            if (mEvents.size() == 0) {
                mImgPlaceHolder.setVisibility(View.VISIBLE);
                mTvPlaceHolder.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onRefresh() {
        new LoadEventTask(mContext,mCourseid,mToken).execute(); // refresh content
    }



    public EventFragment() {/* required empty constructor */}
}
