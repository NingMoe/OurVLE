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

import com.amulyakhare.textdrawable.TextDrawable;
import com.stoneapp.ourvlemoodle2.adapters.MemberListAdapter;
import com.stoneapp.ourvlemoodle2.models.MoodleMember;
import com.stoneapp.ourvlemoodle2.tasks.MemberSync;
import com.stoneapp.ourvlemoodle2.activities.ProfileActivity;

import com.stoneapp.ourvlemoodle2.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class MembersFragment extends Fragment
        implements SearchView.OnQueryTextListener {
    private String courseid;
    private String token;
    private Context context;
    private List<MoodleMember> members;
    private MemberListAdapter madapter;
    private MenuItem searchitem;
    private List<MoodleMember> filteredMemList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            this.courseid = args.getInt("courseid") + ""; // course id from previous activity
            this.token = args.getString("token"); // url token
        }

        context = getActivity();

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frag_member, container, false);

        memberList = (RecyclerView) rootView.findViewById(R.id.member_listview);
        txt_notpresent = (TextView) rootView.findViewById(R.id.txt_notpresent);
        img_notpresent = (ImageView) rootView.findViewById(R.id.no_members);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarMember);

        memberList.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        //this.token = MoodleSiteInfo.listAll(MoodleSiteInfo.class).get(0).getToken();

        members = MoodleMember.find(MoodleMember.class, "courseid = ?", courseid);

        Collections.sort(members, new Comparator<MoodleMember>() {
            @Override
            public int compare(MoodleMember lhs, MoodleMember rhs) {
                return lhs.getFullname().toLowerCase().trim().compareTo(rhs.getFullname().toLowerCase().trim());
            }
        });

        if (members.size() > 0) {
            txt_notpresent.setVisibility(View.GONE);
            img_notpresent.setVisibility(View.GONE);
        }

        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        madapter = new MemberListAdapter(context,members,token);
        memberList.setAdapter(madapter);
        new LoadMembersTask(context, courseid, token).execute(""); // refresh member list

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SearchManager searchManager = (SearchManager) context.getSystemService(Context.SEARCH_SERVICE);

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
        final List<MoodleMember> filteredModelList = filter(members,query );
        madapter.animateTo(filteredModelList,query);
        memberList.scrollToPosition(0);
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

    private class LoadMembersTask extends AsyncTask<String, Integer, Boolean> {
        Context context;
        String courseid;
        MemberSync msync;

        public LoadMembersTask(Context context, String courseid, String token) {
            this.context = context;
            this.courseid = courseid;

            msync = new MemberSync(token);
        }

        @Override
        protected void onPreExecute() {
            img_notpresent.setVisibility(View.GONE);
            txt_notpresent.setVisibility(View.GONE);

            if (members.size() == 0)
                progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            //madapter.notifyDataSetChanged();
            madapter.updateMemberList(members);
            progressBar.setVisibility(View.GONE);

            if (aBoolean) {}
            else
                Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show();

            if (members.size() == 0) {
                txt_notpresent.setVisibility(View.VISIBLE);
                img_notpresent.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean sync = msync.syncMembers(courseid); //sync members
            if (sync) {
                members = MoodleMember.find(MoodleMember.class, "courseid = ?", courseid);
                // sorts members in order of firstname in ascending
                Collections.sort(members, new Comparator<MoodleMember>() {
                    @Override
                    public int compare(MoodleMember lhs, MoodleMember rhs) {
                        return lhs.getFullname().toLowerCase().trim().compareTo(rhs.getFullname().toLowerCase().trim());
                    }
                });

                filteredMemList = members; // updates filtered list
            }

            return sync;
        }
    }

    private View rootView;
    private RecyclerView memberList;
    private ProgressBar progressBar;
    private SearchView searchView;
    private TextView txt_notpresent;
    private ImageView img_notpresent;

    public MembersFragment() {/* required empty constructor */}
}
