package com.stoneapp.ourvlemoodle2.fragments;

import java.util.ArrayList;
import java.util.List;

import com.stoneapp.ourvlemoodle2.adapters.ForumListAdapter;
import com.stoneapp.ourvlemoodle2.models.MoodleForum;
import com.stoneapp.ourvlemoodle2.tasks.ForumSync;
import com.stoneapp.ourvlemoodle2.R;

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
import android.widget.TextView;

@SuppressWarnings("FieldCanBeLocal")
public class ForumFragment extends Fragment
        implements OnRefreshListener {
    private List<MoodleForum> forums;
    int courseid = 0;
    private ArrayList<String> courseids; // list of course ids
    private String token; // url token
    private ForumListAdapter forumListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            this.courseid  = args.getInt("courseid"); //get course id from previous activity
            // this.coursename  = getArguments().getString("coursename"); //get course name from previous activity
            this.token = args.getString("token"); //gets the token stored in sqlite
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frag_forum, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        forumRecView = (RecyclerView) rootView.findViewById(R.id.forumList);

       // List<MoodleSiteInfo> sites = MoodleSiteInfo.listAll(MoodleSiteInfo.class);

        //token = sites.get(0).getToken();
        courseids = new ArrayList<>();

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        forums = MoodleForum.find(MoodleForum.class, "courseid = ?", courseid + ""); // gets all forums related to the course

        forumListAdapter = new ForumListAdapter(getActivity(), forums, token);
        forumRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
        forumRecView.setAdapter(forumListAdapter);

        new LoadForumTask(token, courseids, getActivity()).execute(""); // refresh forums

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
                new LoadForumTask(token, courseids, getActivity()).execute(""); // refresh content
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class LoadForumTask extends AsyncTask<String, Integer, Boolean> {
        ForumSync fsync;
        ArrayList<String> courseids;
        Context context;

        public LoadForumTask(String token, ArrayList<String>courseids, Context context) {
            fsync = new ForumSync(token);
            this.courseids = courseids;
            this.context =context;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean sync = fsync.syncForums(courseids); // syncs forums
            if (sync)
                forums = MoodleForum.find(MoodleForum.class, "courseid = ?", courseid + ""); // update forums list

            return sync;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            forumListAdapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);

            if (!result) {}
        }

    }

    @Override
    public void onRefresh() {
        new LoadForumTask(token, courseids, getActivity()).execute(""); // refresh content
    }

    private View rootView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView forumRecView;

    public ForumFragment() {/* required empty constructor */}
}
