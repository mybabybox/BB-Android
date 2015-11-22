package com.babybox.util;

import android.net.Uri;

import java.io.File;

public class SelectedImage implements Comparable<SelectedImage> {
    public Integer index;
    public String path;

    public SelectedImage(int index, String path) {
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
    public int compareTo(SelectedImage o) {
        return this.index.compareTo(o.index);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;

        if (other == this)
            return true;

        if (!(other instanceof SelectedImage))
            return false;

        SelectedImage o = (SelectedImage) other;
        return this.index.equals(o.index);
    }
}