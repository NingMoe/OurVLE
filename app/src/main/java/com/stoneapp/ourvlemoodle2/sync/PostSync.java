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

package com.stoneapp.ourvlemoodle2.sync;

import java.util.List;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.stoneapp.ourvlemoodle2.models.DiscussionPosts;
import com.stoneapp.ourvlemoodle2.models.Post;
import com.stoneapp.ourvlemoodle2.rest.RestPost;

public class PostSync {
    private String token;
    List<Post> posts;



    public PostSync(String token){
        this.token = token;
    }

    public boolean syncPosts(String discussionid){
        DiscussionPosts dposts;
        RestPost mrpost = new RestPost(token);
        dposts = mrpost.getDiscussionPosts(discussionid); // gets a list of of discussion posts from api call

        if (dposts == null) // checks if there are no discussion posts present
            return false;

        if (dposts.getErrorcode() != null) // checks if there is an error with that discussion post
            return false;

        posts = dposts.getPosts();

        if(posts == null)
        {
            return false;
        }

        if(posts.size()==0)
        {
            return false;
        }

        ActiveAndroid.beginTransaction();
        try {
          //  deleteStaleData();
            for (int i = 0; i < posts.size(); i++) {
                final Post post = posts.get(i);

                Post.findOrCreateFromJson(post); // saves contact to database
            }
            ActiveAndroid.setTransactionSuccessful();
        }finally {
            ActiveAndroid.endTransaction();
        }

        return true;
    }

    private void deleteStaleData()
    {

        List<Post> stale_posts = new Select().all().from(Post.class).execute();
        for(int i=0;i<stale_posts.size();i++)
        {
            if(!doesPostExistInJson(stale_posts.get(i)))
            {
                Post.delete(Post.class,stale_posts.get(i).getId());
            }
        }
    }

    private boolean doesPostExistInJson(Post post)
    {
        return posts.contains(post);
    }
}
