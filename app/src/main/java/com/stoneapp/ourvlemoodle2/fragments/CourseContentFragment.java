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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.activeandroid.query.Select;
import com.stoneapp.ourvlemoodle2.adapters.CourseContentListAdapter;
import com.stoneapp.ourvlemoodle2.models.ContentListItem;
import com.stoneapp.ourvlemoodle2.models.MoodleCourse;
import com.stoneapp.ourvlemoodle2.models.MoodleModule;
import com.stoneapp.ourvlemoodle2.models.MoodleSection;
import com.stoneapp.ourvlemoodle2.tasks.ContentSync;
import com.stoneapp.ourvlemoodle2.R;
import com.stoneapp.ourvlemoodle2.util.FileUtils;
import com.stoneapp.ourvlemoodle2.view.NpaLinearLayoutManager;

import android.Manifest;
import android.app.DownloadManager;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
public class CourseContentFragment extends Fragment
        implements
        OnRefreshListener,
        SearchView.OnQueryTextListener,
        ActivityCompat.OnRequestPermissionsResultCallback {
    private String coursename;
    private String coursefname;
    private int courseid;
    private long coursepid;
    private long siteid;
    private String token;

    private CourseContentListAdapter cadapter;
    private ArrayList<ContentListItem> items = new ArrayList<>();
    private List<ContentListItem> filtered_items;

    private ArrayList<MoodleSection>sections;
    public File file;
    private MenuItem searchitem;

    private static int TYPE_HEADER = 1;
    private static int TYPE_MODULE = 0;
    private MoodleCourse course;
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0x1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            this.coursefname = args.getString("coursefname");
            this.coursename = args.getString("coursename");
            this.coursepid = args.getLong("coursepid");
            this.courseid = args.getInt("courseid");
            this.token = args.getString("token");
            this.siteid = args.getLong("siteid");
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context context = getActivity();

        rootView = inflater.inflate(R.layout.frag_course_content, container, false);

        txt_notpresent = (TextView) rootView.findViewById(R.id.txt_notpresent);
        img_notpresent = (ImageView) rootView.findViewById(R.id.no_content);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarContent);
        contentList = (RecyclerView) rootView.findViewById(R.id.coursecontent_listview);

        //List<MoodleSiteInfo> sites = MoodleSiteInfo.listAll(MoodleSiteInfo.class); //retrieves site info

       /// course = MoodleCourse.find(MoodleCourse.class, "courseid = ?", courseid + "").get(0);
        course = new Select().from(MoodleCourse.class).where("courseid = ?", courseid+"").executeSingle();

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(this);

       // String name =  sites.get(0).getFirstname().charAt(0)+sites.get(0).getLastname();
        ContentSync csync = new ContentSync(courseid, coursepid, siteid, token, context);

        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        sections = csync.getContents(); // sync content

        sectionsToListItems(sections); // adds content to list items

        contentList.setHasFixedSize(true);
        contentList.setLayoutManager(new NpaLinearLayoutManager(getActivity()));

        cadapter = new CourseContentListAdapter(getActivity(), items, token, courseid, course.getShortname(), this);
        contentList.setAdapter(cadapter);

        //checks if any content is present
        if (items.size() > 0) {
            img_notpresent.setVisibility(View.GONE);
            txt_notpresent.setVisibility(View.GONE);
        }

        //register download completion receiver
        getActivity().registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected())
            //refreshes content
            new LoadContentTask(courseid, coursepid, siteid, context).execute("");

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
             Context context = getActivity();
            SearchManager searchManager = (SearchManager) context.getSystemService(Context.SEARCH_SERVICE);

            searchitem = menu.findItem(R.id.action_search);
            searchView = (SearchView) MenuItemCompat.getActionView(searchitem); // set up search view

            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            searchView.setOnQueryTextListener(this);
            searchView.setQueryHint("Search name"); // sets the hint text

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
            case R.id.action_refresh:
                mSwipeRefreshLayout.setRefreshing(true);
                new LoadContentTask(courseid, coursepid, siteid, getActivity()).execute(""); // refreshes content
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            getActivity().unregisterReceiver(onComplete); //unregister broadcast receiver
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //register download completed receiver
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                Toast.makeText(getActivity(), "Download completed", Toast.LENGTH_SHORT).show();
                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    if (file.exists()) {
                        Toast.makeText(getActivity(), "Opening file", Toast.LENGTH_SHORT).show();
                        FileUtils.openFile(getActivity(), file);
                    }
                }
            }
    };

    public void sectionsToListItems(ArrayList<MoodleSection> sections) { // maps courses contents to list items
        if (sections == null)
            return;

        int sectionslen = sections.size();

        items.clear(); // clears list
        for (int i = 0; i < sectionslen; i++) {
            MoodleSection section =sections.get(i); // gets current section
            ArrayList<MoodleModule> modules = section.getModules(); // gets modules from section
            if (modules.size() > 0) {
                ContentListItem secitem = new ContentListItem();
                secitem.section = sections.get(i);
                secitem.type = TYPE_HEADER; // set type to header
                items.add(secitem);

                int moduleslen = modules.size();

                for (int j = 0; j < moduleslen; j++) {
                    ContentListItem moditem = new ContentListItem();
                    moditem.section = section;

                    moditem.module = modules.get(j);

                    if (moditem.module.getModname().equals("forum"))
                        continue;

                   // moditem.section = section;
                    moditem.type = TYPE_MODULE; // set type
                    items.add(moditem);
                }
            }
        }
    }

    private class LoadContentTask extends AsyncTask<String, Integer, Boolean> {
        Context context;
        ContentSync csync;

        public LoadContentTask(int courseid, long coursepid, long siteid, Context context) {
            this.context = context;
            csync = new ContentSync(courseid, coursepid, siteid, token, context);
        }

        @Override
        protected void onPreExecute() {
            txt_notpresent.setVisibility(View.GONE);
            img_notpresent.setVisibility(View.GONE);

            if(items.size() == 0) { // checks if list is empty
                if(!mSwipeRefreshLayout.isRefreshing())
                    progressBar.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setEnabled(false);
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean sync = csync.syncContent(); // syncs content

            ArrayList<MoodleSection>sections = csync.getContents();

            sectionsToListItems(sections);



            return sync;
        }

        @Override
        protected void onPostExecute(Boolean status) {
            mSwipeRefreshLayout.setRefreshing(false);

            progressBar.setVisibility(View.GONE);
            if (status) {
                cadapter.updateContentList(items); // update list view
            }
            else
                Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show();

            if (items.size() == 0) {
                txt_notpresent.setVisibility(View.VISIBLE);
                img_notpresent.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onRefresh() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected())
            new LoadContentTask(courseid, coursepid, siteid, getActivity()).execute(""); // refresh content
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {

        final List<ContentListItem> filteredModelList = filter(items,query );
        cadapter.animateTo(filteredModelList,query);
        contentList.scrollToPosition(0);
        return true;
    }

    private List <ContentListItem> filter(List<ContentListItem> models, String query) {
        query = query.toLowerCase();
        boolean headerAdded = true;
        final List<ContentListItem> filteredModelList = new ArrayList<>();
        List<ContentListItem> startModelList = new ArrayList<>();
        for (ContentListItem model : models) {

            if(model.type  == TYPE_HEADER)
                if(model.section.getName().contains(query))
                    filteredModelList.add(model);
            if (model.type == TYPE_MODULE) {
                headerAdded = true;
                final String text = model.module.getName().toLowerCase();
                // final String desc = model.module.getDescription().toLowerCase();

                if (text.contains(query)) {
                    filteredModelList.add(model);
                }
            }
        }

        return filteredModelList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private View rootView;
    private RecyclerView contentList;
    private ProgressBar progressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SearchView searchView;
    private TextView txt_notpresent;
    private ImageView img_notpresent;

    public CourseContentFragment() {/* required empty constructor */}
}

