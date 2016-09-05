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

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.amulyakhare.textdrawable.TextDrawable;
import com.stoneapp.ourvlemoodle2.adapters.MemberListAdapter;
import com.stoneapp.ourvlemoodle2.models.MoodleMember;
import com.stoneapp.ourvlemoodle2.tasks.MemberSync;
import com.stoneapp.ourvlemoodle2.activities.ProfileActivity;

import com.stoneapp.ourvlemoodle2.R;
import com.stoneapp.ourvlemoodle2.util.ConnectUtils;
import com.stoneapp.ourvlemoodle2.view.NpaLinearLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class MembersFragment extends Fragment
        implements SearchView.OnQueryTextListener {
    private String mCourseid;
    private String mToken;
    private Context mContext;
    private List<MoodleMember> mMembers;
    private MemberListAdapter mMeberListAdapter;
    private MenuItem searchitem;
    private View mRootView;
    private RecyclerView mMemberListView;
    private ProgressBar mProgressBar;
    private SearchView searchView;
    private TextView mTvPlaceHolder;
    private ImageView mImgPlaceHolder;
    private List<MoodleMember> filteredMemList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            this.mCourseid = args.getInt("courseid") + ""; // course id from previous activity
            this.mToken = args.getString("token"); // url token
        }

        mContext = getActivity();

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.frag_member, container, false);

        initViews();


        getMembersFromDatabase();


        sortMembers();


        setUpRecyclerView();

        if (mMembers.size() > 0) {
            mTvPlaceHolder.setVisibility(View.GONE);
            mImgPlaceHolder.setVisibility(View.GONE);
        }

        setUpProgressBar();

        new LoadMembersTask(mContext,mCourseid,mToken).execute(""); // refresh member list

        return mRootView;
    }

    private void getMembersFromDatabase()
    {
        mMembers = new Select().from(MoodleMember.class).where("courseid = ?", mCourseid).execute();
    }

    private void initViews()
    {
        mMemberListView = (RecyclerView) mRootView.findViewById(R.id.member_listview);
        mTvPlaceHolder = (TextView) mRootView.findViewById(R.id.txt_notpresent);
        mImgPlaceHolder = (ImageView) mRootView.findViewById(R.id.no_members);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progressBarMember);
    }

    private void setUpProgressBar()
    {
        mProgressBar.setIndeterminate(true);
        mProgressBar.setVisibility(View.GONE);

    }

    private void sortMembers()
    {
        Collections.sort(mMembers, new Comparator<MoodleMember>() {
            @Override
            public int compare(MoodleMember lhs, MoodleMember rhs) {
                return lhs.getFullname().toLowerCase().trim().compareTo(rhs.getFullname().toLowerCase().trim());
            }
        });
    }

    private void setUpRecyclerView()
    {
        mMeberListAdapter = new MemberListAdapter(mContext, mMembers, mToken);
        mMemberListView.setAdapter(mMeberListAdapter);
        mMemberListView.setHasFixedSize(true);
        mMemberListView.setLayoutManager(new NpaLinearLayoutManager(getActivity()));
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SearchManager searchManager = (SearchManager) mContext.getSystemService(Context.SEARCH_SERVICE);

            searchitem = menu.findItem(R.id.action_search);
            searchView = (SearchView) MenuItemCompat.getActionView(searchitem); // set up search view

            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            searchView.setOnQueryTextListener(this);
            searchView.setQueryHint("Search name");

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    searchView.setQuery("", false); // clears text from search view
                    return true;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        final List<MoodleMember> filteredModelList = filter(mMembers,query );
        mMeberListAdapter.animateTo(filteredModelList,query);
        mMemberListView.scrollToPosition(0);
        return true;

    }

    private List<MoodleMember> filter(List<MoodleMember> models, String query) {
        query = query.toLowerCase();
        final List<MoodleMember> filteredModelList = new ArrayList<>();
        List<MoodleMember> startModelList = new ArrayList<>();
        for (MoodleMember model : models) {
            final String text = model.getFullname().toLowerCase();
            if(text.startsWith(query)){
                startModelList.add(model);
            }else if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        startModelList.addAll(filteredModelList);
        return startModelList;
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

    private class LoadMembersTask extends AsyncTask<String, Integer, Boolean> {
        Context context;
        String courseid;


        public LoadMembersTask(Context context, String courseid, String token) {
            this.context = context;
            this.courseid = courseid;


        }

        @Override
        protected void onPreExecute() {
            mImgPlaceHolder.setVisibility(View.GONE);
            mTvPlaceHolder.setVisibility(View.GONE);

            if (mMembers.size() == 0) { // check if any news are present
                mProgressBar.setVisibility(View.VISIBLE);

            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            final MemberSync msync = new MemberSync(mToken);

            if (!isConnected()) {  // if there is no internet connection
                return false;
            }

            if (!hasInternet()) { // if there is no internet
                return false;
            }


            boolean sync = msync.syncMembers(courseid); //sync members

            return sync;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mProgressBar.setVisibility(View.GONE);
            if (result) {
                getMembersFromDatabase();
                sortMembers();
                mMeberListAdapter.updateMemberList(mMembers);
            }
            if (mMembers.size() == 0) {
                mImgPlaceHolder.setVisibility(View.VISIBLE);
                mTvPlaceHolder.setVisibility(View.VISIBLE);
            }
        }
    }



    public MembersFragment() {/* required empty constructor */}
}
