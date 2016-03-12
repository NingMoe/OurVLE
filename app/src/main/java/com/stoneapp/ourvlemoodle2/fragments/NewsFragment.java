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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stoneapp.ourvlemoodle2.adapters.DiscussionListAdapter;
import com.stoneapp.ourvlemoodle2.models.MoodleDiscussion;
import com.stoneapp.ourvlemoodle2.models.MoodleForum;
import com.stoneapp.ourvlemoodle2.models.MoodleSiteInfo;
import com.stoneapp.ourvlemoodle2.tasks.DiscussionSync;
import com.stoneapp.ourvlemoodle2.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class NewsFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener{
    private List<MoodleDiscussion> discussions;
    private DiscussionListAdapter list_adapter;
    private String token;
    private ArrayList<String> forumids;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_latest_news, container, false);

        txt_notpresent = (TextView) view.findViewById(R.id.txt_notpresent);
        img_notpresent = (ImageView) view.findViewById(R.id.no_topics);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        news_list = (RecyclerView) view.findViewById(R.id.newsList);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        token = MoodleSiteInfo.listAll(MoodleSiteInfo.class).get(0).getToken(); // url token

        List<MoodleForum>forums  = MoodleForum.listAll(MoodleForum.class); // all forums
        ArrayList<MoodleForum> news_forums = new ArrayList<>();

        int len = forums.size();

        for (int i = 0; i < len; i++) {
            if (forums.get(i).getName().toUpperCase().contains("NEWS FORUM")) //if forum is news forum
                news_forums.add(forums.get(i)); //add forum to list of news forums
        }
        forumids = new ArrayList<>();
        if (news_forums != null && news_forums.size() > 0) {

            len = news_forums.size();

            for (int i = 0; i < len; i++)
                forumids.add(news_forums.get(i).getForumid() + "");
        }

        discussions = MoodleDiscussion.listAll(MoodleDiscussion.class);

        // Order discussion by time modified
        Collections.sort(discussions,new Comparator<MoodleDiscussion>() {
            @Override
            public int compare(MoodleDiscussion lhs, MoodleDiscussion rhs) {
                if (lhs.getTimemodified() < rhs.getTimemodified())
                    return 1;
                else
                    return -1;
            }
        });

        //get the first five
        if (discussions.size() >= 5)
            discussions = discussions.subList(0, 5);

        //if there are no discussions
        if (discussions.size() ==  0) {
            txt_notpresent.setVisibility(View.VISIBLE); //show place holder text
            img_notpresent.setVisibility(View.VISIBLE); //show place holder image
        } else {
            txt_notpresent.setVisibility(View.GONE);  //hide place holder text
            img_notpresent.setVisibility(View.GONE); //hide place holder image
        }

        list_adapter = new DiscussionListAdapter(this.getActivity(), discussions, token);

        news_list.setHasFixedSize(true);
        news_list.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        news_list.setAdapter(list_adapter);

        new LoadLatestDiscussionTask(this.getActivity(),forumids).execute(""); //refresh discussions

        return view;
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
        new LoadLatestDiscussionTask(this.getActivity(), forumids).execute("");
    }

    private class LoadLatestDiscussionTask extends AsyncTask<String, Integer, Boolean>{
        ArrayList<String>forumids;
        Context context;
        DiscussionSync dsync;

        public LoadLatestDiscussionTask(Context context, ArrayList<String>forumids){
            this.context = context;
            this.forumids = forumids;
            dsync = new DiscussionSync(token,context);
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected Boolean doInBackground(String... params) {
           boolean sync = dsync.syncDiscussions(forumids); //syncs discussions
            if(sync) {
                discussions = MoodleDiscussion.listAll(MoodleDiscussion.class); //gets discussions

                //Order discussions by time modified
                Collections.sort(discussions,new Comparator<MoodleDiscussion>() {
                    @Override
                    public int compare(MoodleDiscussion lhs, MoodleDiscussion rhs) {
                        if(lhs.getTimemodified()<rhs.getTimemodified())
                            return 1;
                        else
                            return -1;
                    }
                });

                //get the first five discussions
                if(discussions.size()>=5){
                    discussions = discussions.subList(0,5);
                }
            }
            return sync;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            list_adapter.updateDiscussionList(discussions);
            mSwipeRefreshLayout.setRefreshing(false);
            if(result) {
                txt_notpresent.setVisibility(View.GONE); //hide holder text
                img_notpresent.setVisibility(View.GONE); //hide holder image
                list_adapter.updateDiscussionList(discussions);
            }

            if(discussions.size() == 0) { //if there are no discussions
                txt_notpresent.setVisibility(View.VISIBLE); //show holder text
                img_notpresent.setVisibility(View.VISIBLE); //show holder image
            }
        }
    }

    private View view;
    private RecyclerView news_list;
    private TextView txt_notpresent;
    private ImageView img_notpresent;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public NewsFragment() {/* required empty constructor */}
}
