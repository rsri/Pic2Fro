package com.pic2fro.pic2fro.controller;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.pic2fro.pic2fro.activities.CreatorActivity;
import com.pic2fro.pic2fro.activities.PlayActivity;
import com.pic2fro.pic2fro.model.DataHolder;

/**
 * Created by srikaram on 01-Sep-16.
 */
public class AudioAdapter implements LoaderManager.LoaderCallbacks<Cursor> {

    private final PlayActivity activity;
    private Uri audioUri;

    public AudioAdapter(PlayActivity activity) {
        this.activity = activity;
    }

    public void setAudioUri(Uri uri) {
        audioUri = uri;
        activity.getLoaderManager().initLoader(0, null, this);
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
        DataHolder.setAudioPath(audioPath);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
