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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.stoneapp.ourvlemoodle2.adapters.EventListAdapter;
import com.stoneapp.ourvlemoodle2.models.MoodleEvent;
import com.stoneapp.ourvlemoodle2.models.MoodleCourse;
import com.stoneapp.ourvlemoodle2.models.MoodleSiteInfo;
import com.stoneapp.ourvlemoodle2.tasks.EventSync;
import com.stoneapp.ourvlemoodle2.R;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class CalendarFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener {
    private List<MoodleEvent> mevents;
    private EventListAdapter eventListAdapter;
    private ArrayList<String> courseids;
    private List<MoodleCourse> courses;
    private String token;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_all_events, container, false);

        txt_notpresent = (TextView) view.findViewById(R.id.txt_notpresent);
        img_notpresent = (ImageView) view.findViewById(R.id.no_events);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        eventList = (RecyclerView) view.findViewById(R.id.eventList);
        progressbar = (ProgressBar)view.findViewById(R.id.progressEvent);

        txt_notpresent.setVisibility(View.GONE);
        img_notpresent.setVisibility(View.GONE);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        progressbar.setIndeterminate(true);
        progressbar.setVisibility(View.GONE);

        courses = MoodleCourse.listAll(MoodleCourse.class);
        token = MoodleSiteInfo.listAll(MoodleSiteInfo.class).get(0).getToken(); // url token

        courseids = new ArrayList<>();

        for (int i = 0; i < courses.size(); i++)
            courseids.add(courses.get(i).getCourseid() + "");

        mevents = MoodleEvent.listAll(MoodleEvent.class); //get all events

        eventListAdapter = new EventListAdapter(mevents, this.getActivity());

        eventList.setHasFixedSize(true);
        eventList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        eventList.setAdapter(eventListAdapter);

        new LoadAllEventsTask(courseids, token, this.getActivity()).execute(""); // refresh events

        return view;
    }

    private class LoadAllEventsTask extends AsyncTask<String,Integer,Boolean> {
        EventSync evsync;
        ArrayList<String> courseids;
        Context context;

        public LoadAllEventsTask(ArrayList<String>courseids, String token, Context context) {
            this.context = context;
            this.courseids = courseids;

            evsync = new EventSync(token,context);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean sync = evsync.syncEvents(courseids);
            if (sync)
                mevents = MoodleEvent.listAll(MoodleEvent.class);
                //Collections.reverse(mevents);

            return sync;
        }

        @Override
        protected void onPreExecute() {
            txt_notpresent.setVisibility(View.GONE);
            img_notpresent.setVisibility(View.GONE);

            if (mevents.size() == 0) {  //check if any events are present
                progressbar.setVisibility(View.VISIBLE);

                if (mSwipeRefreshLayout.isRefreshing())
                    progressbar.setVisibility(View.GONE);
            }
        }
        @Override
        protected void onPostExecute(Boolean result) {
            eventListAdapter.updateEventList(mevents);
            progressbar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setRefreshing(false);

            if (!result) {
                if (mevents.size() > 0)
                    Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show();
            }

            if (mevents.size() == 0) { //if any events are present
                txt_notpresent.setVisibility(View.VISIBLE);
                img_notpresent.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onRefresh() {
        new LoadAllEventsTask(courseids, token, this.getActivity()).execute(""); // refresh events
    }

    private RecyclerView eventList;
    private ProgressBar progressbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView txt_notpresent;
    private ImageView img_notpresent;
    private View view;

    public CalendarFragment() {/* required empty constructor */}
}
