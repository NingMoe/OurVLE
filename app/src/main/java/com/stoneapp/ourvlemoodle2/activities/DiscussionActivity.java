package com.stoneapp.ourvlemoodle2.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.stoneapp.ourvlemoodle2.adapters.DiscussionListAdapter;
import com.stoneapp.ourvlemoodle2.models.MoodleDiscussion;
import com.stoneapp.ourvlemoodle2.tasks.DiscussionSync;
import com.stoneapp.ourvlemoodle2.R;

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
import android.widget.Toast;

@SuppressWarnings("FieldCanBeLocal")
public class DiscussionActivity extends AppCompatActivity
        implements OnRefreshListener {
    private List<MoodleDiscussion> discussions;
    private int forumid;
    private DiscussionListAdapter discuss_adapter;
    private String forumname;
    private String token;
    private ArrayList<String> forumids ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        forumid = extras.getInt("forumid");
        forumname = extras.getString("forumname");
        token = extras.getString("token");

        setContentView(R.layout.activity_discussions);
        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        tv_notpresent = (TextView)findViewById(R.id.txt_notpresent);
        img_notpresent = (ImageView)findViewById(R.id.no_topics);
        progressBar = (ProgressBar)findViewById(R.id.progressBar1);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        discussList = (RecyclerView)findViewById(R.id.discussionList);

        setSupportActionBar(toolbar);
        abar = getSupportActionBar();
        if (abar != null) {
            abar.setDisplayHomeAsUpEnabled(true);
            abar.setTitle(forumname);
        }

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        forumids = new ArrayList<>();
        forumids.add(forumid + "");

        discussions = MoodleDiscussion.find(MoodleDiscussion.class, "forumid = ?", forumid + "");

        Collections.sort(discussions, new Comparator<MoodleDiscussion>() {
            @Override
            public int compare(MoodleDiscussion moodleDiscussion1, MoodleDiscussion moodleDiscussion2) {
                if (moodleDiscussion1.getTimemodified() < moodleDiscussion2.getTimemodified())
                    return 1;

                return -1;
            }
        });

        if (discussions.size() > 0) { // if there are no discussions
            tv_notpresent.setVisibility(View.GONE);
            img_notpresent.setVisibility(View.GONE);
        }

        discussList.setHasFixedSize(true);
        discussList.setLayoutManager(new LinearLayoutManager(this));
        discuss_adapter = new DiscussionListAdapter(this, discussions, token);
        discussList.setAdapter(discuss_adapter);

        new LoadDiscussionTask(token, forumids, this).execute(""); // refresh discussions
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

    private class LoadDiscussionTask extends AsyncTask<String, Integer, Boolean> {
        ArrayList<String>forumids;
        DiscussionSync dsync;
        private Context context;
        private List<MoodleDiscussion> new_discussions;

        public LoadDiscussionTask(String token, ArrayList<String> forumids, Context context) {
            this.forumids = forumids;
            dsync = new DiscussionSync(token,context);
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            tv_notpresent.setVisibility(View.GONE);
            img_notpresent.setVisibility(View.GONE);

            if (discussions.size() == 0) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            return dsync.syncDiscussions(forumids);
        }

        @Override
        protected void onPostExecute(Boolean status) {
            if(status) {
                discussions = MoodleDiscussion.find(MoodleDiscussion.class, "forumid = ?",forumid+"");
                //discussions = new_discussions;

                //Order discussions by time modified
                Collections.sort(discussions, new Comparator<MoodleDiscussion>() {
                    @Override
                    public int compare(MoodleDiscussion moodleDiscussion1, MoodleDiscussion moodleDiscussion2) {
                        if (moodleDiscussion1.getTimemodified() < moodleDiscussion2.getTimemodified())
                            return 1;

                        return -1;
                    }
                });
            } else {
                if(discussions.size() == 0){ //if there are no discussions
                    tv_notpresent.setVisibility(View.VISIBLE);
                    img_notpresent.setVisibility(View.VISIBLE);
                } else
                    Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show();
            }

            mSwipeRefreshLayout.setRefreshing(false);
            discuss_adapter.updateDiscussionList(discussions); //update list view
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh() {
        new LoadDiscussionTask(token, forumids, this).execute(""); // refresh discussions
    }

    private RecyclerView discussList;
    private Toolbar toolbar;
    private TextView tv_notpresent;
    private ImageView img_notpresent;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar progressBar;
    private ActionBar abar;
}
