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
import com.stoneapp.ourvlemoodle2.adapters.ForumListAdapter;
import com.stoneapp.ourvlemoodle2.models.MoodleForum;
import com.stoneapp.ourvlemoodle2.tasks.ForumSync;
import com.stoneapp.ourvlemoodle2.R;
import com.stoneapp.ourvlemoodle2.util.ConnectUtils;
import com.stoneapp.ourvlemoodle2.view.NpaLinearLayoutManager;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
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
public class ForumFragment extends Fragment implements OnRefreshListener {

    private List<MoodleForum> mForums;
    private int mCourseId = 0;
    private ArrayList<String> mCourseids = new ArrayList<>(); // list of course ids
    private String mToken; // url token
    private ForumListAdapter mForumListAdapter;
    private View mRootView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mForumRecView;
    private TextView mTvPlaceHolder;
    private ImageView mImgPlaceHolder;
    private ProgressBar mProgressBar;


    public ForumFragment() {/* required empty constructor */}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            this.mCourseId  = args.getInt("courseid"); //get course id from previous activity
            this.mToken = args.getString("token"); //gets the token stored in sqlite
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.frag_forum, container, false);

        initViews();

        setUpSwipeRefresh();

      //  mCourseids.add(mCourseId+"");

        getForumsFromDatabase();

        if(mForums.size()>0)
        {
            mTvPlaceHolder.setVisibility(View.GONE);
            mImgPlaceHolder.setVisibility(View.GONE);
        }

        setUpRecyclerView();

        setUpProgressBar();


        new LoadForumTask(mToken, mCourseids, getActivity()).execute(); // refresh forums

        return mRootView;
    }


    private void initViews()
    {
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swiperefresh);
        mForumRecView = (RecyclerView) mRootView.findViewById(R.id.forumList);
        mImgPlaceHolder = (ImageView) mRootView.findViewById(R.id.no_forums);
        mTvPlaceHolder = (TextView)mRootView.findViewById(R.id.txt_notpresent);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progressBar);
    }

    private void setUpSwipeRefresh()
    {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void setUpRecyclerView()
    {
        mForumListAdapter = new ForumListAdapter(getActivity(), mForums, mToken);
        mForumRecView.setLayoutManager(new NpaLinearLayoutManager(getActivity()));
        mForumRecView.setAdapter(mForumListAdapter);

    }

    private void getForumsFromDatabase()
    {
        mForums = new Select().from(MoodleForum.class).where("courseid = ?", mCourseId).execute(); // gets all forums related to the course
    }

    private void setUpProgressBar()
    {
        mProgressBar.setIndeterminate(true);
        mProgressBar.setVisibility(View.GONE);
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



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                mSwipeRefreshLayout.setRefreshing(true);
                new LoadForumTask(mToken, mCourseids, getActivity()).execute(); // refresh content
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class LoadForumTask extends AsyncTask<Void,Void,Boolean> {

        ArrayList<String> courseids;
        Context context;
        private String token;

        public LoadForumTask(String token, ArrayList<String>courseids, Context context) {
            this.token = token;
            this.courseids = courseids;
            this.context =context;
        }

        @Override
        protected void onPreExecute() {
            mImgPlaceHolder.setVisibility(View.GONE);
            mTvPlaceHolder.setVisibility(View.GONE);

            if (mForums.size() == 0) { // check if any news are present
                mProgressBar.setVisibility(View.VISIBLE);
                if (mSwipeRefreshLayout.isRefreshing())
                    mProgressBar.setVisibility(View.GONE);
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            final ForumSync fsync = new ForumSync(token);

            if (!isConnected()) {  // if there is no internet connection
                return false;
            }

            if (!hasInternet()) { // if there is no internet
                return false;
            }

            boolean sync = fsync.syncForums(courseids); // syncs forums

            return sync;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mSwipeRefreshLayout.setRefreshing(false);
            mProgressBar.setVisibility(View.GONE);
            if (result) {
                getForumsFromDatabase();
                mForumListAdapter.updateForumList(mForums);
            }
            if (mForums.size() == 0) {
                mImgPlaceHolder.setVisibility(View.VISIBLE);
                mTvPlaceHolder.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public void onRefresh() {
        new LoadForumTask(mToken,mCourseids,getActivity()).execute();
    }




}
