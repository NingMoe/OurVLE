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

package com.stoneapp.ourvlemoodle2.models;



import android.graphics.PorterDuff;

import java.util.ArrayList;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

public class MoodleSiteInfo extends Model {

    @Column(name="sitename")
    String sitename;

    @Column(name="username")
    String username;

    @Column(name="firstname")
    String firstname;

    @Column(name="lastname")
    String lastname;

    @Column(name="fullname")
    String fullname;

    String lang;

    @Column(name="userid")
    int userid;

    String siteurl;

    @Column(name="userpictureurl")
    String userpictureurl;

    @Ignore
    ArrayList<MoodleFunction> functions;
    int downloadfiles;
    int uploadfiles;
    String release;
    String version;
    String mobilecssurl;

    // Errors. Not to be stored in sql db.
    @Ignore
    String exception;
    @Ignore
    String errorcode;
    @Ignore
    String message;
    @Ignore
    String debuginfo;

    /*
     * SiteInfo is basically an account Token is needed for an account to get
     * new info from Moodle site
     */
    @Column(name="token")
    String token;

    public MoodleSiteInfo() {
    }

    public MoodleSiteInfo(String token) {
        this.token = token;
    }

    /**
     * Set token associated with this account
     */
    public void setToken(String token) {
        this.token = token;
    }

    public String getSitename() {
        return sitename;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFullname() {
        return fullname;
    }

    /**
     * User language
     */
    public String getLang() {
        return lang;
    }

    public int getUserid() {
        return userid;
    }

    public String getSiteurl() {
        return siteurl;
    }

    /**
     * User profile picture. <br/>
     * Warning: This url is the public URL that only works when forcelogin is
     * set to NO and guestaccess is set to YES. In order to retrieve user
     * profile pictures independently of the Moodle config, replace
     * "pluginfile.php" by "webservice/pluginfile.php?token=WSTOKEN&file=" Of
     * course the user can only see profile picture depending on his/her
     * permissions. Moreover it is recommended to use HTTPS too.
     */
    public String getUserpictureurl() {
        return userpictureurl;
    }

    /**
     * functions that are available
     *
     * @return
     */
    public ArrayList<MoodleFunction> getFunctions() {
        return functions;
    }

    /**
     * 1 if users are allowed to download files, 0 if not (Optional)
     */
    public int getDownloadfiles() {
        return downloadfiles;
    }

    /**
     * 1 if users are allowed to upload files, 0 if not (Optional)
     */
    public int getUploadfiles() {
        return uploadfiles;
    }

    /**
     * Moodle release number (Optional)
     */
    public String getRelease() {
        return release;
    }

    /**
     * Moodle version number (Optional)
     */
    public String getVersion() {
        return version;
    }

    /**
     * Mobile custom CSS theme
     */
    public String getMobilecssurl() {
        return mobilecssurl;
    }

    /**
     * Get token associated with this account
     */
    public String getToken() {
        return token;
    }

    /**
     * Exception occurred while retrieving
     *
     * @return
     */
    public String getException() {
        return exception;
    }

    /**
     * Errorcode of error occurred while retrieving
     *
     * @return
     */
    public String getErrorcode() {
        return errorcode;
    }

    /**
     * Message of error occurred while retrieving
     *
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * Debug info on the error occurred
     *
     * @return
     */
    public String getDebuginfo() {
        return debuginfo;
    }

    /**
     * Set error message <br/>
     * <br/>
     * Particularly useful for network failure errors
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Appends to the existing error messages<br/>
     * <br/>
     * Particularly useful for network failure errors
     */
    public void appenedMessage(String message) {
        this.message += message + "\n";
    }

    public static MoodleSiteInfo findOrCreateFromJson(MoodleSiteInfo new_site) {
        int siteid = new_site.getUserid();
        MoodleSiteInfo existingSiteInfo =
                new Select().from(MoodlePost.class).where("userid = ?", siteid).executeSingle();
        if (existingSiteInfo != null) {
            // found and return existing
           // UpdatePost(existingPost,new_post);
            return existingSiteInfo;
        } else {
            // create and return new user
            MoodleSiteInfo siteInfo = new_site;
            siteInfo.save();
            return siteInfo;
        }
    }

    private static void UpdatePost(MoodlePost old_post,MoodlePost new_post)
    {
        old_post = new_post;
        old_post.save();

    }
}
