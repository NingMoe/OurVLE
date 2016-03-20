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
import com.stoneapp.ourvlemoodle2.models.MoodleCourse;
import com.stoneapp.ourvlemoodle2.models.MoodleModule;
import com.stoneapp.ourvlemoodle2.models.MoodleModuleContent;
import com.stoneapp.ourvlemoodle2.util.FileUtils;
import com.stoneapp.ourvlemoodle2.util.ImageChooser;
import com.stoneapp.ourvlemoodle2.util.MoodleConstants;
import com.stoneapp.ourvlemoodle2.util.SettingsUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CourseContentListAdapter
        extends RecyclerView.Adapter<CourseContentListAdapter.CourseContentViewHolder> {
    private static List<ContentListItem> list_items;
    private static Context context;
    private static final int TYPE_HEADER = 1 ;
    private static long courseid;
    private static String token;
    private static String coursename;
    private static File file;
    private static CourseContentFragment cfrag;
    private String filter = "";
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0x1;

    public static class CourseContentViewHolder extends RecyclerView.ViewHolder {
        private final TextView section_name;
        private final TextView module_name;
        private final TextView module_description;
        private final ImageView modimage;

        public CourseContentViewHolder(View v) {
            super(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = CourseContentViewHolder.this.getAdapterPosition();

                    MoodleModule module = list_items.get(position).module;
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
            });

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

        private static void openWebpage(MoodleModule module) {
            String url = module.getUrl();
            if (TextUtils.isEmpty(url))
                // course container webpage
                url = MoodleConstants.URL + "/course/view.php?id=" + courseid;

            Intent intent;

            if (SettingsUtils.shouldOpenLinksExternally(context)) {
                Uri webpage = Uri.parse(url);
                intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);

                    return;
                }
            }
            else {
                intent = new Intent(context, BrowserActivity.class);
                intent.putExtra("url", url);
            }

            context.startActivity(intent);
        }

        private static void downloadResource(MoodleModule module) {
            if (module.getContents() == null) {
                openWebpage(module);
                return;
            }

            if (module.getContents().size() == 0) {
                openWebpage(module);
                return;
            }

            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                MoodleModuleContent content = module.getContents().get(0); // gets the content/file
                String file_path = "/" + coursename + "/"; // place file in course folder
                String filename = content.getFilename().replace("#", ""); // to fix file opening issues
                file = new File(Environment.getExternalStoragePublicDirectory("/OURVLE")
                        + file_path + filename); //creates a new file and store it in the directory
                cfrag.setFile(file);

                if (file.exists()) {
                    FileUtils.openFile(context, file);
                } else {
                    String file_url = content.getFileurl() + "&token=" + token;

                    Toast.makeText(context, "Opening file", Toast.LENGTH_SHORT).show();
                    FileUtils.download(context, file_url, file_path, content.getFilename());
                }
            } else
                cfrag.requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    public CourseContentListAdapter(Context context, List<ContentListItem> list_items, String token,
                                    long courseid, String coursename, CourseContentFragment cfrag) {
        CourseContentListAdapter.list_items = list_items;
        CourseContentListAdapter.context = context;
        CourseContentListAdapter.token = token;
        CourseContentListAdapter.courseid = courseid;
        CourseContentListAdapter.coursename = coursename;
        CourseContentListAdapter.cfrag = cfrag;
    }

    @Override
    public CourseContentViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int layout =
                viewType == TYPE_HEADER ? R.layout.list_section_header : R.layout.list_module_item;

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);

        return new CourseContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CourseContentViewHolder viewHolder, int position) {
        int type = getItemViewType(position);

        ContentListItem item = list_items.get(position);

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
                } else
                    viewHolder.getModuleNameView().setTextColor(Color.BLACK);
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
    public int getItemCount() { return list_items.size(); }

    @Override
    public int getItemViewType(int position) { return list_items.get(position).type; }

    public void updateContentList(List<ContentListItem> newlist_items) {
        list_items = new ArrayList<>(newlist_items);
        notifyDataSetChanged();
    }

    public void animateTo(List<ContentListItem> models,String filter) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
        this.filter = filter;
    }

    private void applyAndAnimateRemovals(List<ContentListItem> newModels) {
        for (int i = list_items.size() - 1; i >= 0; i--) {
            final ContentListItem model = list_items.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<ContentListItem> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final ContentListItem model = newModels.get(i);
            if (!list_items.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<ContentListItem> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final ContentListItem model = newModels.get(toPosition);
            final int fromPosition = list_items.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public ContentListItem removeItem(int position) {
        final ContentListItem model = list_items.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, ContentListItem model) {
        list_items.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final ContentListItem model = list_items.remove(fromPosition);
        list_items.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
