package com.stoneapp.ourvlemoodle2.models;

import java.io.File;

/**
 * Created by stone on 1/22/17.
 */

public class FileObject {

    File file;
    String path;
    String downloadUrl;
    String filename;


    public FileObject(String path, String downloadUrl,String filename) {
        this.path = path;
        this.downloadUrl = downloadUrl;
        this.filename = filename;
    }

    public FileObject(File file, String path, String downloadUrl,String filename) {
        this.file = file;
        this.path = path;
        this.downloadUrl = downloadUrl;
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
