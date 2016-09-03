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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;


import com.activeandroid.query.Select;
import com.stoneapp.ourvlemoodle2.adapters.PostListAdapter;
import com.stoneapp.ourvlemoodle2.models.MoodlePost;
import com.stoneapp.ourvlemoodle2.tasks.PostSync;
import com.stoneapp.ourvlemoodle2.R;
import com.stoneapp.ourvlemoodle2.util.ConnectUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("FieldCanBeLocal")
public class PostActivity  extends AppCompatActivity
        implements OnRefreshListener {
    private String mDiscussionId;
    private List<MoodlePost> mPosts;
    private PostListAdapter mPostListAdapter;
    private String mDiscussionName;
    private String mToken;
    private Toolbar mToolBar;
    private TextView mTvPlaceHolder;
    private ImageView mImgPlaceHolder;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;
    private RecyclerView mPostListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        mDiscussionId = extras.getString("discussionid");
        mDiscussionName = extras.getString("discussionname");
        mToken = extras.getString("token");

        setContentView(R.layout.activity_post);

        initViews();

        setUpToolBar();

        setUpSwipeRefresh();

        // get posts from database
        getPostsFromDatabase();

        sortPosts();

        setUpRecyclerView();

        //if there are no posts
        if (mPosts.size() > 0) {
            mTvPlaceHolder.setVisibility(View.GONE);
            mImgPlaceHolder.setVisibility(View.GONE);
        }

        setUpProgressBar();

        new LoadPostsTask(mDiscussionId, mToken, this).execute(); // refresh posts
    }

    private void getPostsFromDatabase()
    {
        mPosts = new Select().from(MoodlePost.class).where("discussionid = ?",mDiscussionId).execute();
    }



    private void initViews()
    {
        mToolBar = (Toolbar)findViewById(R.id.tool_bar);
        mTvPlaceHolder = (TextView) findViewById(R.id.txt_notpresent);
        mImgPlaceHolder = (ImageView) findViewById(R.id.no_posts);
        mPostListView = (RecyclerView)findViewById(R.id.postList);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBarPost);

    }

    private void setUpToolBar()
    {
        setSupportActionBar(mToolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mDiscussionName);
        }
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
        mPostListView.setLayoutManager(new LinearLayoutManager(this));
        mPostListView.setHasFixedSize(true);
        mPostListAdapter = new PostListAdapter(this, mPosts);
        mPostListView.setAdapter(mPostListAdapter);
    }

    private void sortPosts()
    {
        //Order posts by time created
        Collections.sort(mPosts, new Comparator<MoodlePost>() {
            @Override
            public int compare(MoodlePost lhs, MoodlePost rhs) {
                if (lhs.getCreated() > rhs.getCreated())
                    return 1;
                else
                    return -1;
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

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


    private class LoadPostsTask extends AsyncTask<Void,Void, Boolean> {

        String discussionid;
        String token;
        Context context;

        public LoadPostsTask(String discussionid, String token, Context context) {
            this.discussionid = discussionid;
            this.token = token;
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            mImgPlaceHolder.setVisibility(View.GONE);
            mTvPlaceHolder.setVisibility(View.GONE);

            if (mPosts.size() == 0) { // check if any news are present
                mProgressBar.setVisibility(View.VISIBLE);
                if (mSwipeRefreshLayout.isRefreshing())
                    mProgressBar.setVisibility(View.GONE);
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            final PostSync psync = new PostSync(token);

            if (!isConnected()) {  // if there is no internet connection
                return false;
            }

            if (!hasInternet()) { // if there is no internet
                return false;
            }

            boolean sync = psync.syncPosts(discussionid); // syncs posts

            return sync;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mSwipeRefreshLayout.setRefreshing(false);
            mProgressBar.setVisibility(View.GONE);
            if (result) {
                getPostsFromDatabase();
                sortPosts();
                mPostListAdapter.updatePosts(mPosts);
            }
            if (mPosts.size() == 0) {
                mImgPlaceHolder.setVisibility(View.VISIBLE);
                mTvPlaceHolder.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public void onRefresh() {
        new LoadPostsTask(mDiscussionId,mToken, this).execute(); // refresh posts
    }


}
