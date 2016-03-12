package com.stoneapp.ourvlemoodle2.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.stoneapp.ourvlemoodle2.adapters.CourseListAdapter;
import com.stoneapp.ourvlemoodle2.models.MoodleCourse;
import com.stoneapp.ourvlemoodle2.models.MoodleSiteInfo;
import com.stoneapp.ourvlemoodle2.tasks.CourseSync;
import com.stoneapp.ourvlemoodle2.R;
import com.stoneapp.ourvlemoodle2.view.DividerItemDecoration;

import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class CourseListFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener {
    private List<MoodleCourse> courses ;
    private String token;
    private int userid;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String name;
    private CourseListAdapter courseListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_course_list, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        RecyclerView courseList = (RecyclerView) view.findViewById(R.id.courseList);

        List<MoodleSiteInfo> sites =  MoodleSiteInfo.listAll(MoodleSiteInfo.class); // the moodle site info from database
        courses = MoodleCourse.listAll(MoodleCourse.class); // the moodle courses
        name = sites.get(0).getFullname();  // full name from first site info
        long siteid = sites.get(0).getId();
        token = sites.get(0).getToken(); // the url token
        userid = sites.get(0).getUserid();

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        courseListAdapter = new CourseListAdapter(courses, this.getActivity(), token, siteid);

        courseList.setHasFixedSize(true);
        courseList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        courseList.setAdapter(courseListAdapter);

        // refreshes courses list
        new LoadCoursesTask(userid, token, this.getActivity()).execute("");

        return view;
    }

    @Override
    public void onRefresh() {
        new LoadCoursesTask(userid, token, this.getActivity()).execute("");
    }

    private class LoadCoursesTask extends AsyncTask<String, Integer, Boolean> {
        int userid;
        CourseSync csync;
        Context context;

        public LoadCoursesTask(int userid, String token, Context context) {
            this.userid = userid;
            csync = new CourseSync(token);
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean sync = csync.syncCourses(userid + "");

            if (sync)
                courses = MoodleCourse.listAll(MoodleCourse.class); // populate course list

            return sync;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            courseListAdapter.updateList(courses);
            mSwipeRefreshLayout.setRefreshing(false);
            if (!result)
                Toast.makeText(context, "Failed to Update", Toast.LENGTH_SHORT).show();
        }
    }

    public CourseListFragment() {/* required empty constructor */}
}
