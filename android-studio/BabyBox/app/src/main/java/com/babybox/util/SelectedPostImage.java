package com.babybox.util;

import android.net.Uri;

import java.io.File;

public class SelectedPostImage implements Comparable<SelectedPostImage> {


    public Integer index;
    public String path;

    public SelectedPostImage(int index, String path) {
        this.index = index;
        this.path = path;
    }

    public File getFile() {
        return new File(path);
    }

    public Uri getPathUri() {
        return Uri.parse(path);
    }

    @Override
    public int compareTo(SelectedPostImage o) {
        return this.index.compareTo(o.index);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;

        if (other == this)
            return true;

        if (!(other instanceof SelectedPostImage))
            return false;

        SelectedPostImage o = (SelectedPostImage) other;
        return this.index.equals(o.index);
    }
}