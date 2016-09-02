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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.stoneapp.ourvlemoodle2.adapters.DiscussionListAdapter;
import com.stoneapp.ourvlemoodle2.models.MoodleCourse;
import com.stoneapp.ourvlemoodle2.models.MoodleDiscussion;
import com.stoneapp.ourvlemoodle2.models.MoodleEvent;
import com.stoneapp.ourvlemoodle2.models.MoodleForum;
import com.stoneapp.ourvlemoodle2.models.MoodleSiteInfo;
import com.stoneapp.ourvlemoodle2.tasks.DiscussionSync;
import com.stoneapp.ourvlemoodle2.R;
import com.stoneapp.ourvlemoodle2.tasks.ForumSync;
import com.stoneapp.ourvlemoodle2.util.ConnectUtils;
import com.stoneapp.ourvlemoodle2.view.NpaLinearLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class NewsFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener{


    private List<MoodleDiscussion> mDiscussions;
    private DiscussionListAdapter mDiscussionListAdapter;
    private String mToken;
    private ArrayList<String> mForumIds;
    private View mRootView;
    private RecyclerView mNewsListView;
    private TextView mTvPlaceHolder;
    private ImageView mImgPlaceHolder;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;
    private ArrayList<String> mCourseIds = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.frag_latest_news, container, false);

        initViews();

        setUpSwipeRefresh();

        List<MoodleSiteInfo> sites = new Select().all().from(MoodleSiteInfo.class).execute();
        mToken = sites.get(0).getToken(); // url token

        getDiscussionsFromDatabase();

        sortDiscussions();

        initCourseIds();

        //if there are no discussions
        if (mDiscussions.size()> 0) {
            mTvPlaceHolder.setVisibility(View.GONE); //show place holder text
            mImgPlaceHolder.setVisibility(View.GONE); //show place holder image
        }

        setUpRecyclerView();

        setUpProgressBar();


        new LoadLatestDiscussionTask(this.getActivity(),mForumIds).execute(""); //refresh discussions

        return mRootView;
    }

    private void getDiscussionsFromDatabase()
    {
        mDiscussions = new Select().all().from(MoodleDiscussion.class).execute();
    }

    private void initViews()
    {
        mTvPlaceHolder = (TextView) mRootView.findViewById(R.id.txt_notpresent);
        mImgPlaceHolder = (ImageView) mRootView.findViewById(R.id.no_topics);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swiperefresh);
        mNewsListView = (RecyclerView) mRootView.findViewById(R.id.newsList);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progressBar);
    }

    private void sortDiscussions()
    {
        // Order discussion by time modified
        Collections.sort(mDiscussions,new Comparator<MoodleDiscussion>() {
            @Override
            public int compare(MoodleDiscussion lhs, MoodleDiscussion rhs) {
                if (lhs.getTimemodified() < rhs.getTimemodified())
                    return 1;
                else
                    return -1;
            }
        });

        //get the first five
        if (mDiscussions.size() >= 5)
            mDiscussions = mDiscussions.subList(0, 5);

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
        mDiscussionListAdapter = new DiscussionListAdapter(this.getActivity(), mDiscussions, mToken);
        mNewsListView.setHasFixedSize(true);
        mNewsListView.setLayoutManager(new NpaLinearLayoutManager(getActivity()));
        mNewsListView.setAdapter(mDiscussionListAdapter);

    }

    private void initCourseIds()
    {
        List<MoodleCourse> courses = new Select().all().from(MoodleCourse.class).execute();
        for(int i=0;i<courses.size();i++)
        {
            mCourseIds.add(courses.get(i).getCourseid()+"");
        }
    }

    private void initForumIds()
    {
        List<MoodleForum>forums  = new Select().all().from(MoodleForum.class).execute(); // all forums
        ArrayList<MoodleForum> news_forums = new ArrayList<>();

        int len = forums.size();

        for (int i = 0; i < len; i++) {
            if (forums.get(i).getName().toUpperCase().contains("NEWS")) //if forum is news forum
                news_forums.add(forums.get(i)); //add forum to list of news forums
        }
        mForumIds = new ArrayList<>();
        if (news_forums != null && news_forums.size() > 0) {

            len = news_forums.size();

            for (int i = 0; i < len; i++)
                mForumIds.add(news_forums.get(i).getForumid() + "");
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {

        new LoadLatestDiscussionTask(this.getActivity(), mForumIds).execute("");
    }

    private class LoadLatestDiscussionTask extends AsyncTask<String, Integer, Boolean>{
        ArrayList<String>forumids;
        Context context;


        public LoadLatestDiscussionTask(Context context, ArrayList<String>forumids){
            this.context = context;
            this.forumids = forumids;

        }

        @Override
        protected void onPreExecute()
        {
            mImgPlaceHolder.setVisibility(View.GONE);
            mTvPlaceHolder.setVisibility(View.GONE);

            if (mDiscussions.size() == 0) { // check if any news are present
                mProgressBar.setVisibility(View.VISIBLE);
                if (mSwipeRefreshLayout.isRefreshing())
                    mProgressBar.setVisibility(View.GONE);
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            DiscussionSync dsync = new DiscussionSync(mToken,context);
            ForumSync forumSync = new ForumSync(mToken);

            if (!isConnected()) {  // if there is no internet connection
                return false;
            }

            if (!hasInternet()) { // if there is no internet
                return false;
            }

            boolean forums_present = forumSync.syncForums(mCourseIds);
            if(!forums_present)
            {
                return false;
            }
            initForumIds();
            boolean sync = dsync.syncDiscussions(mForumIds); //syncs discussions
            return sync;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            mSwipeRefreshLayout.setRefreshing(false);
            mProgressBar.setVisibility(View.GONE);
            if (result) {
                getDiscussionsFromDatabase();
                sortDiscussions();
                mDiscussionListAdapter.updateDiscussionList(mDiscussions);
            } else {
                if (mDiscussions.size() == 0) {
                    mImgPlaceHolder.setVisibility(View.VISIBLE);
                    mTvPlaceHolder.setVisibility(View.VISIBLE);
                }
            }
        }
    }



    public NewsFragment() {/* required empty constructor */}
}
