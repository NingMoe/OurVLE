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
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CourseContentListAdapter
        extends RecyclerView.Adapter<CourseContentListAdapter.CourseContentViewHolder> {
    private List<ContentListItem> list_items;
    private Context ctxt;
    private int TYPE_HEADER = 1 ;
    private int TYPE_ITEM = 0;
    private long courseid;
    private String token;
    private MoodleCourse course;
    private String coursename;
    private File file;
    private CourseContentFragment cfrag;
    private String filter = "";

    public static class CourseContentViewHolder extends RecyclerView.ViewHolder {
        TextView section_name;
        TextView module_name;
        TextView module_description;
        ImageView modimage;

        public CourseContentViewHolder(View itemView) {
            super(itemView);
            section_name = (TextView) itemView.findViewById(R.id.section_heading);
            module_name = (TextView) itemView.findViewById(R.id.module_name);
            module_description = (TextView) itemView.findViewById(R.id.module_summary);
            modimage = (ImageView) itemView.findViewById(R.id.module_img);
        }
    }

    public CourseContentListAdapter(Context ctxt, List<ContentListItem> list_items, String token,
                                    MoodleCourse course, CourseContentFragment cfrag) {
        this.list_items = list_items;
        this.ctxt = ctxt;
        this.course = course;
        this.token = token;
        this.coursename = course.getShortname();
        this.cfrag = cfrag;
       // this.file = file;

    }

    @Override
    public CourseContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final CourseContentViewHolder courseContentViewHolder;
        View view;

        if (viewType == TYPE_HEADER)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_section_header, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_module_item, parent, false);

        courseContentViewHolder = new CourseContentViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = courseContentViewHolder.getAdapterPosition();
                MoodleModule module = list_items.get(position).module;
                if (module == null)
                    return;

                String modurl = module.getUrl();
                if (modurl == null)
                    modurl = MoodleConstants.URL+ "/course/view.php?id=" + courseid;

                Intent intent = new Intent(ctxt, BrowserActivity.class);
                intent.putExtra("url", modurl);

                if (module.getModname().contentEquals("label"))
                    return;

                if (!module.getModname().contentEquals("resource")) {
                    ctxt.startActivity(intent);
                    return;
                }

                if (module.getContents() == null) {
                    ctxt.startActivity(intent);
                    return;
                }

                if (module.getContents().size() == 0) {
                    ctxt.startActivity(intent);
                    return;
                }

                if (ActivityCompat.checkSelfPermission(ctxt.getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                    MoodleModuleContent content = module.getContents().get(0); // gets the content/file
                    String file_path = "/" + coursename + "/"; // place file in course folder
                    String filename = content.getFilename().replace("#", ""); // to fix file opening issues
                    // String filename = "file.ppt";
                    file = new File(Environment.getExternalStoragePublicDirectory("/OURVLE")
                            + file_path + filename); //creates a new file and store it in the directory
                    //cfrag.file = file;
                    //((BrowserActivity)ctxt).setFile(file);
                    cfrag.setFile(file);

                    // checks if the file exists
                    if (!file.exists()) {
                        String file_url = content.getFileurl();
                        file_url = file_url + "&token=" + token;

                        Toast.makeText(ctxt, "Opening File", Toast.LENGTH_SHORT).show();
                        FileUtils.download(ctxt, file_url, file_path, content.getFilename());   //Downloads file if file is not present
                    } else
                        FileUtils.openFile(ctxt, file);   //opens file if file is found
                }

                //if (module.getContents()!=null || module.getContents().size()>0){
                  //  Toast.makeText(ctxt,module.getContents().get(0).getFileurl()+"",Toast.LENGTH_SHORT).show();

                //}
            }


        });

        return courseContentViewHolder;
    }

    @Override
    public void onBindViewHolder(CourseContentViewHolder holder, int position) {
        int type = getItemViewType(position);

        ContentListItem item = list_items.get(position); //gets the list item

        if (type == TYPE_HEADER) { // check if it is of type header
            // checks if the section name is empty or contains a long line
            if (item.section.getName() == null || item.section.getName().contains("____"))
                holder.section_name.setText("");
            else
                holder.section_name.setText(Html.fromHtml(item.section.getName()).toString().trim()); // converts from html to normal string
        } else {
            String modulename = item.module.getName(); // gets the name of the module
            String moduledesc = item.module.getDescription(); // gets the description
            holder.modimage.setImageResource(ImageChooser.getImage(item.module));

            if (modulename == null)
                holder.module_name.setText("");
            else {
                holder.module_name.setText(Html.fromHtml(modulename).toString().trim());

                if (list_items.get(position).module.getModname().contentEquals("label")) { // if item is a label
                    holder.module_name.setTextColor(Color.parseColor("#009900")); // change color to green
                    if (modulename.contains("_____"))
                        holder.module_name.setText("");
                } else
                    holder.module_name.setTextColor(Color.parseColor("#000000")); // set font to black
            }

            if (moduledesc == null)
                holder.module_description.setText("");
            else {
                holder.module_description.setText(Html.fromHtml(moduledesc).toString().trim());

                if (list_items.get(position).module.getModname().contentEquals("label")) {  // if item is a label
                    if (moduledesc.contains("____"))
                        holder.module_description.setText("");
                }
            }
        }
    }

    @Override
    public int getItemCount() { return list_items.size(); }

    @Override
    public int getItemViewType(int position) { return list_items.get(position).type; }

    public void updateContentList(List<ContentListItem> newlist_items)
    {
        this.list_items = new ArrayList<>(newlist_items);
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
