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

package com.stoneapp.ourvlemoodle2.adapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.stoneapp.ourvlemoodle2.R;
import com.stoneapp.ourvlemoodle2.activities.BrowserActivity;
import com.stoneapp.ourvlemoodle2.fragments.CourseContentFragment;
import com.stoneapp.ourvlemoodle2.models.ContentListItem;
import com.stoneapp.ourvlemoodle2.models.Module;
import com.stoneapp.ourvlemoodle2.models.ModuleContent;
import com.stoneapp.ourvlemoodle2.util.FileUtils;
import com.stoneapp.ourvlemoodle2.util.ImageChooser;
import com.stoneapp.ourvlemoodle2.util.MoodleConstants;
import com.stoneapp.ourvlemoodle2.util.SettingsUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CourseContentListAdapter
        extends RecyclerView.Adapter<CourseContentListAdapter.CourseContentViewHolder> {


    private List<ContentListItem> mListItems;
    private Context mContext;
    private final int TYPE_HEADER = 1 ;
    private long mCourseId;
    private String mToken;
    private String mCourseName;
    private File mFile;
    private CourseContentFragment mCFrag;
    private String filter = "";
    private final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0x1;

    public static class CourseContentViewHolder extends RecyclerView.ViewHolder {

        private final TextView section_name;
        private final TextView module_name;
        private final TextView module_description;
        private final ImageView modimage;

        public CourseContentViewHolder(View v) {
            super(v);

            section_name = (TextView) v.findViewById(R.id.section_heading);
            module_name = (TextView) v.findViewById(R.id.module_name);
            module_description = (TextView) v.findViewById(R.id.module_summary);
            modimage = (ImageView) v.findViewById(R.id.module_img);




        }

        public TextView getSectionNameView() {
            return section_name;
        }

        public TextView getModuleNameView() {
            return module_name;
        }

        public TextView getModuleDescriptionView() {
            return module_description;
        }

        public ImageView getModImageView() {
            return modimage;
        }


    }

    public CourseContentListAdapter(Context context, List<ContentListItem> list_items, String token,
                                    long courseid, String coursename, CourseContentFragment cfrag) {
        this.mListItems = new ArrayList<>(list_items);
        this.mContext = context;
        this.mToken = token;
        this.mCourseId = courseid;
        this.mCourseName = coursename;
        this.mCFrag = cfrag;
    }

    @Override
    public CourseContentViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int layout =
                viewType == TYPE_HEADER ? R.layout.list_section_header : R.layout.list_module_item;

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);

        final CourseContentViewHolder courseContentViewHolder = new CourseContentViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = courseContentViewHolder.getAdapterPosition();
                if(position>=0)
                {
                    Module module = mListItems.get(position).module;
                    if (module == null)
                        return;

                    switch (module.getModname()) {
                        case "label": /* do nothing */
                            break;

                        case "resource":
                            downloadResource(module);
                            break;

                        default:
                            openWebpage(module);
                            break;
                    }
                }


            }
        });

        return courseContentViewHolder;
    }

    @Override
    public void onBindViewHolder(CourseContentViewHolder viewHolder, int position) {
        int type = getItemViewType(position);

        ContentListItem item = mListItems.get(position);

        if (type == TYPE_HEADER) {
            // checks if the section name is not empty and doesn't contain a long line
            if (!TextUtils.isEmpty(item.section.getName()) && !item.section.getName().contains("____"))
                viewHolder.getSectionNameView().setText(Html.fromHtml(item.section.getName()).toString().trim()); // converts from html to normal string
        } else {
            String modulename = item.module.getName();
            String moduledesc = item.module.getDescription();

            viewHolder.getModImageView().setImageResource(ImageChooser.getImage(item.module));

            if (!TextUtils.isEmpty(modulename)) {
                viewHolder.getModuleNameView().setText(Html.fromHtml(modulename).toString().trim());

                if (item.module.getModname().contentEquals("label")) {
                    viewHolder.getModuleNameView().setTextColor(Color.parseColor("#009900")); // change color to green
                    if (modulename.contains("_____"))
                        viewHolder.getModuleNameView().setText("");
                } else {
                    viewHolder.getModuleNameView().setTextColor(Color.BLACK);
                }
            }

            if (!TextUtils.isEmpty(moduledesc)) {
                viewHolder.getModuleDescriptionView().setText(Html.fromHtml(moduledesc).toString().trim());

                if (item.module.getModname().contentEquals("label")) {
                    if (moduledesc.contains("____"))
                        viewHolder.getModuleDescriptionView().setText("");
                }
            }
        }
    }

    @Override
    public int getItemCount() { return mListItems.size(); }

    @Override
    public int getItemViewType(int position) { return mListItems.get(position).type; }

    public void updateContentList(List<ContentListItem> newlist_items) {
        this.mListItems = new ArrayList<>(newlist_items);
        notifyDataSetChanged();
    }

    private void openWebpage(Module module) {
        String url = module.getUrl();
        if (TextUtils.isEmpty(url))
            // course container webpage
            url = MoodleConstants.URL + "/course/view.php?id=" + mCourseId;

        Intent intent;

        if (SettingsUtils.shouldOpenLinksExternally(mContext)) {
            Uri webpage = Uri.parse(url);
            intent = new Intent(Intent.ACTION_VIEW, webpage);
            if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                mContext.startActivity(intent);

                return;
            }
        }
        else {
            intent = new Intent(mContext, BrowserActivity.class);
            intent.putExtra("url", url);
        }

        mContext.startActivity(intent);
    }

    private void downloadResource(Module module) {
        if (module.getContents() == null) {
            openWebpage(module);
            return;
        }

        if (module.getContents().size() == 0) {
            openWebpage(module);
            return;
        }

        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            ModuleContent content = module.getContents().get(0); // gets the content/file
            String file_path = "/" + mCourseName + "/"; // place file in course folder
            String filename = content.getFilename().replace("#", ""); // to fix file opening issues
            mFile = new File(Environment.getExternalStoragePublicDirectory("/OURVLE")
                    + file_path + filename); //creates a new file and store it in the directory
            mCFrag.setFile(mFile);

            if (mFile.exists()) {
                Toast.makeText(mContext, "Opening file", Toast.LENGTH_SHORT).show();
                FileUtils.openFile(mContext, mFile);
            } else {
                String file_url = content.getFileurl() + "&token=" + mToken;

                Toast.makeText(mContext, "Downloading file", Toast.LENGTH_SHORT).show();
                FileUtils.downloadFile(mContext, file_url, file_path, content.getFilename());
            }
        } else
            mCFrag.requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
    }

    public void animateTo(List<ContentListItem> models,String filter) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
        this.filter = filter;
    }

    private void applyAndAnimateRemovals(List<ContentListItem> newModels) {
        for (int i = mListItems.size() - 1; i >= 0; i--) {
            final ContentListItem model = mListItems.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<ContentListItem> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final ContentListItem model = newModels.get(i);
            if (!mListItems.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<ContentListItem> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final ContentListItem model = newModels.get(toPosition);
            final int fromPosition = mListItems.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public ContentListItem removeItem(int position) {
        final ContentListItem model = mListItems.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, ContentListItem model) {
        mListItems.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final ContentListItem model = mListItems.remove(fromPosition);
        mListItems.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }


}
