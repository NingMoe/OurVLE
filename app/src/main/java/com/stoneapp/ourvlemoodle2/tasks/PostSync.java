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

package com.stoneapp.ourvlemoodle2.tasks;

import java.util.ArrayList;
import java.util.List;

import com.stoneapp.ourvlemoodle2.models.MoodleDiscussionPosts;
import com.stoneapp.ourvlemoodle2.models.MoodlePost;
import com.stoneapp.ourvlemoodle2.rest.MoodleRestPost;

public class PostSync {
    private String token;

    public PostSync(String token){
        this.token = token;
    }

    public boolean syncPosts(String discussionid){
        MoodleDiscussionPosts dposts;
        MoodleRestPost mrpost = new MoodleRestPost(token);
        dposts = mrpost.getDiscussionPosts(discussionid); // gets a list of of discussion posts from api call

        if (dposts == null) // checks if there are no discussion posts present
            return false;

        if (dposts.getErrorcode() != null) // checks if there is an error with that discussion post
            return false;

        MoodlePost post;
        ArrayList<MoodlePost> posts = dposts.getPosts();
        List<MoodlePost> saved_posts;
        for (int i = 0; i < posts.size(); i++) {
            post = posts.get(i);

            saved_posts = MoodlePost.find(MoodlePost.class, "postid = ?", post.getPostid() + ""); // gets list of post with matching post id

            if (saved_posts.size() > 0) // if there exists a post with matching id
                post.setId(saved_posts.get(0).getId()); // overwrite previously stored post with current one

            post.save(); //save post to database
        }

        return true;
    }
}
