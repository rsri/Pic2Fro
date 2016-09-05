package com.pic2fro.pic2fro.model;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srikaram on 01-Sep-16.
 */
public class DataHolder {

    private static List<Bitmap> images = new ArrayList<>(4);
    private static String audioPath;

    public static void addImages(List<Bitmap> imageList) {
        images.addAll(imageList);
    }

    public static void insertImage(Bitmap entry) {
        images.add(entry);
    }

    public static void removeImage(int pos) {
        validatePos(pos);
        images.remove(pos);
    }

    public static int imageCount() {
        return images.size();
    }

    public static Bitmap getImageAt(int pos) {
        validatePos(pos);
        return images.get(pos);
    }

    public static List<Bitmap> getImages() {
        return images;
    }

    public static void setAudioPath(String path) {
        audioPath = path;
    }

    public static String getAudioPath() {
        return audioPath;
    }

    private static void validatePos(int pos) {
        if (pos < 0 || pos >= images.size()) {
            throw new IndexOutOfBoundsException("pos : " + pos + ", size : " + images.size());
        }
    }
}
