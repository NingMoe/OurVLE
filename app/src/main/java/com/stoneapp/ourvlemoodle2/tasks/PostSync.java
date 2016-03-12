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
