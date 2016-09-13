package com.pic2fro.pic2fro.model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srikaram on 01-Sep-16.
 */
public class DataHolder {

    private static List<Bitmap> images = new ArrayList<>(4);
    private static Pair<String, Integer> audioPath = Pair.create(null, null);
    private static int[] spinnerPos = new int[2];

    public static void addImages(List<Bitmap> imageList) {
        images.addAll(imageList);
    }

    public static void removeImage(int pos) {
        validatePos(pos);
        images.remove(pos);
    }

    public static void setSpinnerPos(int countSpinnerPos, int timeSpinnerPos) {
        spinnerPos[0] = countSpinnerPos;
        spinnerPos[1] = timeSpinnerPos;
    }

    public static int[] getSpinnerPos() {
        return spinnerPos;
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

    public static void setAudioPath(String path, int source) {
        audioPath = Pair.create(path, source);
    }

    public static String getAudioPath() {
        return audioPath.first;
    }

    public static int getAudioSource() {
        return audioPath.second == null ? -1 : audioPath.second;
    }

    private static void validatePos(int pos) {
        if (pos < 0 || pos >= images.size()) {
            throw new IndexOutOfBoundsException("pos : " + pos + ", size : " + images.size());
        }
    }

    public static void clear() {
        for (Bitmap image : images) {
            image.recycle();
        }
        images.clear();
        audioPath = Pair.create(null, null);
        spinnerPos[0] = spinnerPos[1] = 0;
    }
}
