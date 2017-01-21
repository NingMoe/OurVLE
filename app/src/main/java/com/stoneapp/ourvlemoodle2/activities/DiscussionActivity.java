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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.activeandroid.query.Select;
import com.stoneapp.ourvlemoodle2.adapters.DiscussionListAdapter;
import com.stoneapp.ourvlemoodle2.models.Discussion;
import com.stoneapp.ourvlemoodle2.sync.DiscussionSync;
import com.stoneapp.ourvlemoodle2.R;
import com.stoneapp.ourvlemoodle2.util.ConnectUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

@SuppressWarnings("FieldCanBeLocal")
public class DiscussionActivity extends AppCompatActivity
        implements OnRefreshListener {
    private List<Discussion> mDiscussions;
    private int mForumId;
    private DiscussionListAdapter mDiscussionListAdapter;
    private String mForumName;
    private String mToken;
    private ArrayList<String> mForumIds ;
    private RecyclerView mDiscussListView;
    private Toolbar mToolBar;
    private TextView mTvPlaceHolder;
    private ImageView mImgPlaceHolder;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;
    private ActionBar abar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        mForumId = extras.getInt("forumid");
        mForumName = extras.getString("forumname");
        mToken = extras.getString("token");

        setContentView(R.layout.activity_discussions);

        initViews();

        setUpToolBar();

        setUpSwipeRefresh();

        initForumIds();

        getDiscussionsFromDatabase();

        sortDiscussions();

        setUpRecyclerView();

        if (mDiscussions.size() > 0) { // if there are no discussions
            mTvPlaceHolder.setVisibility(View.GONE);
            mImgPlaceHolder.setVisibility(View.GONE);
        }

        setUpProgressBar();

        new LoadDiscussionTask(mToken,mForumIds, this).execute(""); // refresh discussions
    }


    private void initViews()
    {
        mToolBar = (Toolbar)findViewById(R.id.tool_bar);
        mTvPlaceHolder = (TextView)findViewById(R.id.txt_notpresent);
        mImgPlaceHolder = (ImageView)findViewById(R.id.no_topics);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar1);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        mDiscussListView = (RecyclerView)findViewById(R.id.discussionList);
    }

    private void setUpSwipeRefresh()
    {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void setUpProgressBar()
    {
        mProgressBar.setIndeterminate(true);
        mProgressBar.setVisibility(View.GONE);
    }

    private void setUpRecyclerView()
    {
        mDiscussListView.setHasFixedSize(true);
        mDiscussListView.setLayoutManager(new LinearLayoutManager(this));
        mDiscussionListAdapter = new DiscussionListAdapter(this,mDiscussions,mToken);
        mDiscussListView.setAdapter(mDiscussionListAdapter);

    }

    private void sortDiscussions()
    {
        Collections.sort(mDiscussions, new Comparator<Discussion>() {
            @Override
            public int compare(Discussion moodleDiscussion1, Discussion moodleDiscussion2) {
                if (moodleDiscussion1.getTimemodified() < moodleDiscussion2.getTimemodified())
                    return 1;

                return -1;
            }
        });
    }

    private void setUpToolBar()
    {
        setSupportActionBar(mToolBar);
        abar = getSupportActionBar();
        if (abar != null) {
            abar.setDisplayHomeAsUpEnabled(true);
            abar.setTitle(mForumName);
        }
    }

    private void initForumIds()
    {
        mForumIds = new ArrayList<>();
        mForumIds.add(mForumId + "");
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) { return super.onCreateOptionsMenu(menu); }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getDiscussionsFromDatabase()
    {
        mDiscussions = new Select().from(Discussion.class).where("forumid = ?",mForumId).execute();
    }

    private boolean isConnected() {
        return ConnectUtils.isConnected(this);
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



    private class LoadDiscussionTask extends AsyncTask<String, Integer, Boolean> {
        ArrayList<String>forumids;

        private Context context;

        public LoadDiscussionTask(String token, ArrayList<String> forumids, Context context) {
            this.forumids = forumids;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
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

            if (!isConnected()) {  // if there is no internet connection
                return false;
            }

            if (!hasInternet()) { // if there is no internet
                return false;
            }

            return dsync.syncDiscussions(forumids);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mSwipeRefreshLayout.setRefreshing(false);
            mProgressBar.setVisibility(View.GONE);
            if (result) {
                getDiscussionsFromDatabase();
                sortDiscussions();
                mDiscussionListAdapter.updateDiscussionList(mDiscussions);
            }
            if (mDiscussions.size() == 0) {
                mImgPlaceHolder.setVisibility(View.VISIBLE);
                mTvPlaceHolder.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onRefresh() {
        new LoadDiscussionTask(mToken,mForumIds, this).execute(""); // refresh discussions
    }


}
