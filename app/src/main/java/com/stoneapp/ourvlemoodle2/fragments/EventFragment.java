package com.stoneapp.ourvlemoodle2.fragments;

import java.util.ArrayList;
import java.util.List;

import com.stoneapp.ourvlemoodle2.adapters.EventListAdapter;
import com.stoneapp.ourvlemoodle2.models.MoodleEvent;
import com.stoneapp.ourvlemoodle2.models.MoodleCourse;
import com.stoneapp.ourvlemoodle2.tasks.EventSync;
import com.stoneapp.ourvlemoodle2.R;

import android.content.Context;
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
    private EventListAdapter eventadapter;
    private Context context;
    private List<MoodleEvent>mevents;

    private String token;
    private String courseid;
    private String eventcourse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            this.courseid = args.getInt("courseid") + "";
            this.token = args.getString("token");
        }

        context = getActivity();

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frag_event, container, false);

        tv_notpresent = (TextView) rootView.findViewById(R.id.txt_notpresent);
        img_notpresent = (ImageView) rootView.findViewById(R.id.no_events);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressEvent);
        eventList = (RecyclerView) rootView.findViewById(R.id.eventList);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);

        //Searches database for all events matching courseid
        mevents = MoodleEvent.find(MoodleEvent.class, "courseid = ?", courseid);

        eventadapter = new EventListAdapter(mevents, context);

        eventcourse = MoodleCourse.find(MoodleCourse.class, "courseid = ?", courseid).get(0).getShortname(); // gets event course

        if (mevents.size() > 0) { // check if there are any events
            img_notpresent.setVisibility(View.GONE);
            tv_notpresent.setVisibility(View.GONE);
        }

        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        eventList.setHasFixedSize(true);
        eventList.setLayoutManager(new LinearLayoutManager(getActivity()));
        eventList.setAdapter(eventadapter);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        //refreshes events
        new LoadEventTask(courseid, token, context).execute("");

        return rootView;
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
                new LoadEventTask(courseid, token, getActivity()).execute(""); // refresh content
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class LoadEventTask extends AsyncTask<String, Integer, Boolean> {
        EventSync evsync;
        ArrayList<String> courseids;
        String courseid;
        Context context;

        public LoadEventTask(String courseid, String token, Context context){
            this.context = context;
            this.courseid = courseid;
            evsync = new EventSync(token,context);
            courseids = new ArrayList<>();
            courseids.add(courseid);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean sync = evsync.syncEvents(courseids); // sync events
            if (sync)
                mevents = MoodleEvent.find(MoodleEvent.class, "courseid = ?", courseid);

            return sync;
        }

        @Override
        protected void onPreExecute() {
            img_notpresent.setVisibility(View.INVISIBLE);
            tv_notpresent.setVisibility(View.INVISIBLE);

            if (mevents.size() == 0) { // check if any events are present
                progressBar.setVisibility(View.VISIBLE);
                if (mSwipeRefreshLayout.isRefreshing())
                    progressBar.setVisibility(View.GONE);
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            eventadapter.updateEventList(mevents);
            progressBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setRefreshing(false);

            if (!result)
                Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show();

            if (mevents.size() == 0) { // if any events are present
                img_notpresent.setVisibility(View.VISIBLE);
                tv_notpresent.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onRefresh() {
        new LoadEventTask(courseid, token, context).execute(""); // refresh content
    }

    private View rootView;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView eventList;
    private ProgressBar progressBar;
    private TextView tv_notpresent;
    private ImageView img_notpresent;

    public EventFragment() {/* required empty constructor */}
}
