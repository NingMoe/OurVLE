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

import com.stoneapp.ourvlemoodle2.adapters.CourseContentListAdapter;
import com.stoneapp.ourvlemoodle2.models.ContentListItem;
import com.stoneapp.ourvlemoodle2.models.FileObject;
import com.stoneapp.ourvlemoodle2.models.Module;
import com.stoneapp.ourvlemoodle2.models.Section;
import com.stoneapp.ourvlemoodle2.sync.ContentSync;
import com.stoneapp.ourvlemoodle2.R;
import com.stoneapp.ourvlemoodle2.util.ConnectUtils;
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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
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
    private int mCourseid;
    private long mCoursepid;
    private String mCourseName;
    private long mSiteId;
    private String mToken;
    private CourseContentListAdapter mContentListAdapter;
    private ArrayList<ContentListItem> mItems = new ArrayList<>();
    private ArrayList<Section>mSections;
    public FileObject mFile;
    private MenuItem searchitem;
    private static int TYPE_HEADER = 1;
    private static int TYPE_MODULE = 0;
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0x1;
    private View mRootView;
    private RecyclerView mContentListView;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SearchView searchView;
    private TextView mTvPlaceHolder;
    private ImageView mImgPlaceHolder;
    private ContentSync mContentSync;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            this.mCourseName = args.getString("coursename");
            this.mCoursepid = args.getLong("coursepid");
            this.mCourseid = args.getInt("courseid");
            this.mToken = args.getString("token");
            this.mSiteId = args.getLong("siteid");
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context context = getActivity();

        mRootView = inflater.inflate(R.layout.frag_course_content, container, false);
        mContentSync = new ContentSync(mCourseid, mCoursepid, mSiteId, mToken, context);

        initViews();

        setUpSwipeRefresh();

        getContentFromDatabase();

        setUpRecyclerView();

        //checks if any content is present
        if (mItems.size() > 0) {
            mImgPlaceHolder.setVisibility(View.GONE);
            mTvPlaceHolder.setVisibility(View.GONE);
        }

        //register download completion receiver
        getActivity().registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        setUpProgressBar();


        new LoadContentTask(context).execute();

        return mRootView;
    }

    private void initViews()
    {
        mTvPlaceHolder = (TextView) mRootView.findViewById(R.id.txt_notpresent);
        mImgPlaceHolder = (ImageView) mRootView.findViewById(R.id.no_content);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swiperefresh);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progressBarContent);
        mContentListView = (RecyclerView) mRootView.findViewById(R.id.coursecontent_listview);
    }

    private void setUpProgressBar()
    {
        mProgressBar.setIndeterminate(true);
        mProgressBar.setVisibility(View.GONE);
    }

    private void setUpSwipeRefresh()
    {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void setUpRecyclerView()
    {
        mContentListView.setHasFixedSize(true);
        //mContentListView.setLayoutManager(new NpaLinearLayoutManager(getActivity()));
        mContentListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mContentListAdapter = new CourseContentListAdapter(getActivity(),mItems, mToken, mCourseid, mCourseName, this);
        mContentListView.setAdapter(mContentListAdapter);
    }

    private void getContentFromDatabase()
    {
        mSections = mContentSync.getContents(); // sync content
        sectionsToListItems(mSections); // adds content to list items
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



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Context context = mRootView.getContext();
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
                new LoadContentTask(getActivity()).execute(); // refreshes content
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
                    if (mFile.getFile().exists()) {
                        Toast.makeText(getActivity(), "Opening file", Toast.LENGTH_SHORT).show();
                        FileUtils.openFile(getActivity(), mFile.getFile());
                    }
                }
            }
    };

    public void sectionsToListItems(ArrayList<Section> sections) { // maps courses contents to list items
        if (sections == null)
            return;

        int sectionslen = sections.size();

        mItems.clear(); // clears list
        for (int i = 0; i < sectionslen; i++) {
            Section section =sections.get(i); // gets current section
            ArrayList<Module> modules = section.getModules(); // gets modules from section
            if (modules.size() > 0) {
                ContentListItem secitem = new ContentListItem();
                secitem.section = sections.get(i);
                secitem.type = TYPE_HEADER; // set type to header
                mItems.add(secitem);

                int moduleslen = modules.size();

                for (int j = 0; j < moduleslen; j++) {
                    ContentListItem moditem = new ContentListItem();
                    moditem.section = section;

                    moditem.module = modules.get(j);

                    if (moditem.module.getModname().equals("forum"))
                        continue;

                   // moditem.section = section;
                    moditem.type = TYPE_MODULE; // set type
                    mItems.add(moditem);
                }
            }
        }
    }

    private class LoadContentTask extends AsyncTask<Void,Void, Boolean> {
        Context context;
        String text="";

        public LoadContentTask(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            mImgPlaceHolder.setVisibility(View.GONE);
            mTvPlaceHolder.setVisibility(View.GONE);

            if (mItems.size() == 0) { // check if any news are present
                mProgressBar.setVisibility(View.VISIBLE);
                if (mSwipeRefreshLayout.isRefreshing())
                    mProgressBar.setVisibility(View.GONE);
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if (!isConnected()) {  // if there is no internet connection
                return false;
            }

            if (!hasInternet()) { // if there is no internet
                return false;
            }

            boolean sync = mContentSync.syncContent(); // syncs content


            return sync;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mSwipeRefreshLayout.setRefreshing(false);
            mProgressBar.setVisibility(View.GONE);
            if (result) {
                getContentFromDatabase();
                mContentListAdapter.updateContentList(mItems);
            }else{

            }
            if (mItems.size() == 0) {
                mImgPlaceHolder.setVisibility(View.VISIBLE);
                mTvPlaceHolder.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onRefresh()
    {

        new LoadContentTask(getActivity()).execute(); // refresh content
    }

    public void setFile(FileObject file) {
        this.mFile = file;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {

        final List<ContentListItem> filteredModelList = filter(mItems,query );
        mContentListAdapter.animateTo(filteredModelList,query);
        mContentListView.scrollToPosition(0);
        return true;
    }

    private List <ContentListItem> filter(List<ContentListItem> models, String query) {
        query = query.toLowerCase();
        final List<ContentListItem> filteredModelList = new ArrayList<>();
        List<ContentListItem> startModelList = new ArrayList<>();
        for (ContentListItem model : models) {

            if(model.type  == TYPE_HEADER)
                if(model.section.getName().contains(query))
                    filteredModelList.add(model);
            if (model.type == TYPE_MODULE) {
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

                    if(mFile!=null)
                    {
                        File file = new File(Environment.getExternalStoragePublicDirectory("/OURVLE")
                                + mFile.getPath() + mFile.getFilename());
                        mFile.setFile(file);
                        //Toast.makeText(getActivity(), "Downloading file", Toast.LENGTH_SHORT).show();
                        String download_url = mFile.getDownloadUrl();
                        String fixed_url = download_url.replaceAll("https","http");
                        //Toast.makeText(getActivity(),download_url,Toast.LENGTH_SHORT).show();
                        FileUtils.downloadFile(getActivity(),
                                fixed_url, mFile.getPath(),mFile.getFilename());
                    }


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



    public CourseContentFragment() {/* required empty constructor */}
}

