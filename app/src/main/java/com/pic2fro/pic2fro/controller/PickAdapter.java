package com.pic2fro.pic2fro.controller;

import com.pic2fro.pic2fro.activities.CreatorActivity;
import com.pic2fro.pic2fro.model.DataHolder;

import net.yazeed44.imagepicker.model.ImageEntry;
import net.yazeed44.imagepicker.util.Picker;

import java.util.ArrayList;

/**
 * Created by srikaram on 01-Sep-16.
 */
public class PickAdapter implements Picker.PickListener {

    private final CreatorActivity activity;

    public PickAdapter(CreatorActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onPickedSuccessfully(ArrayList<ImageEntry> images) {
        DataHolder.addImages(images);
        activity.refreshImages(images);
    }

    @Override
    public void onCancel() {
    }

    public void removeImage(int pos) {
        DataHolder.removeImage(pos);
        if (DataHolder.imageCount() == 0) {
            activity.toggleAddImageTextView(true);
        }
    }
}
