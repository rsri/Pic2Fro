package com.pic2fro.pic2fro.controller;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.pic2fro.pic2fro.activities.CreatorActivity;
import com.pic2fro.pic2fro.model.DataHolder;
import com.pic2fro.pic2fro.util.Constants;

import net.yazeed44.imagepicker.model.ImageEntry;
import net.yazeed44.imagepicker.util.Picker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by srikaram on 01-Sep-16.
 */
public class CreationAdapter implements Picker.PickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private final CreatorActivity activity;
    private Uri audioUri;

    public CreationAdapter(CreatorActivity activity) {
        this.activity = activity;
    }

    public void refreshOnCreate() {
        refreshImages();
        activity.refreshAudio(DataHolder.getAudioSource());
        restoreSpinnerInfo();
    }

    @Override
    public void onPickedSuccessfully(ArrayList<ImageEntry> images) {
        List<Bitmap> bitmaps = fetchBitmap(images);
        DataHolder.addImages(bitmaps);
        refreshImages();
    }

    public void refreshImages() {
        activity.refreshImages(DataHolder.getImages());
    }

    private void restoreSpinnerInfo() {
        int[] spinnerInfo = DataHolder.getSpinnerPos();
        activity.restoreSpinnerInfo(spinnerInfo[0], spinnerInfo[1]);
    }

    private List<Bitmap> fetchBitmap(List<ImageEntry> images) {
        List<Bitmap> bitmaps = new ArrayList<>(images.size());
        for (ImageEntry image : images) {
            bitmaps.add(BitmapFactory.decodeFile(image.path));
//            bitmaps.add(decodeBitmap(image.path, Constants.VIDEO_WIDTH, Constants.VIDEO_HEIGHT));
        }
        return bitmaps;
    }

    private Bitmap decodeBitmap(String path, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    @Override
    public void onCancel() {
    }

    public void removeImage(int pos) {
        DataHolder.removeImage(pos);
        refreshImages();
    }

    public void setAudioFile(File file) {
        DataHolder.setAudioPath(file.getAbsolutePath(), Constants.SOURCE_AUDIO_PICKER);
        activity.refreshAudio(DataHolder.getAudioSource());
    }

    public void setAudioUri(Uri uri) {
        audioUri = uri;
        activity.getSupportLoaderManager().initLoader(0, null, this);
    }

    private String getPathFromCursor(Cursor cursor) {
        int colIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(colIndex);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projs = {MediaStore.Audio.Media.DATA};
        return new CursorLoader(activity, audioUri, projs, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String audioPath = null;
        try {
            audioPath = getPathFromCursor(data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (audioPath == null) {
            audioPath = audioUri.getPath();
        }
        DataHolder.setAudioPath(audioPath, Constants.SOURCE_AUDIO_RECORDER);
        activity.refreshAudio(DataHolder.getAudioSource());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public int getAllowedCount() {
        return Constants.MAX_IMAGES - DataHolder.imageCount();
    }

    public void deleteAll() {
        DataHolder.clear();
    }

    public void setSpinnerPos(int countSpinnerPos, int timeSpinnerPos) {
        DataHolder.setSpinnerPos(countSpinnerPos, timeSpinnerPos);
    }

}
