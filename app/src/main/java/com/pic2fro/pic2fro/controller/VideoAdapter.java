package com.pic2fro.pic2fro.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;

import com.pic2fro.pic2fro.R;
import com.pic2fro.pic2fro.creators.VideoCreator;
import com.pic2fro.pic2fro.model.DataHolder;
import com.pic2fro.pic2fro.util.Constants;
import com.pic2fro.pic2fro.util.IntentCreator;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.Arrays;

/**
 * Created by srikaram on 06-Sep-16.
 */
public class VideoAdapter {

    private final FragmentActivity activity;
    private boolean needsShare = false;

    public VideoAdapter(FragmentActivity activity) {
        this.activity = activity;
    }

    public void saveVideo(final boolean needsShare) {
        IntentCreator.launchVideoSaverDialog(activity, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        VideoAdapter.this.needsShare = needsShare;
                        AlertDialog alertDialog = ((AlertDialog) dialog);
                        String fileName = ((EditText) alertDialog.findViewById(android.R.id.edit)).getText().toString();
                        fileName = FilenameUtils.getBaseName(fileName);
                        Bundle extras = activity.getIntent().getExtras();
                        if (extras != null && !fileName.isEmpty()) {
                            double time = Double.parseDouble(extras.getString(Constants.ARG_TIME).substring(0, 3));
                            int count = Integer.parseInt(extras.getString(Constants.ARG_COUNT));
                            VideoSaver videoSaver = new VideoSaver(time, count, fileName);
                            videoSaver.execute(DataHolder.getImages().toArray(new Bitmap[DataHolder.imageCount()]));
                        }
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        });
    }

    public void shareVideo(String savedPath) {
        IntentCreator.launchVideoSharer(activity, Uri.fromFile(new File(savedPath)));
    }

    private void onVideoSaved(String savedPath) {
        if (savedPath == null) {
            Toast.makeText(activity, R.string.error_video, Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(activity, activity.getString(R.string.saved, savedPath), Toast.LENGTH_SHORT).show();
        if (needsShare) {
            shareVideo(savedPath);
            needsShare = false;
        }
    }

    private class VideoSaver extends AsyncTask<Bitmap, Void, String> {

        private ProgressDialog progressDialog;

        private final double time;
        private final int count;
        private final String fileName;

        public VideoSaver(double time, int count, String fileName) {
            this.time = time;
            this.count = count;
            this.fileName = fileName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            FragmentActivity activity = VideoAdapter.this.activity;
            progressDialog = ProgressDialog.show(activity, activity.getString(R.string.saving), activity.getString(R.string.saving_msg), true, false);
        }

        @Override
        protected String doInBackground(Bitmap... params) {
            try {
                String finalPath = Constants.SAVE_PATH + "/" + fileName + ".mp4";
                Context context = VideoAdapter.this.activity;
                String videoTempPath = VideoCreator.constructVoicelessVideo(context, fileName, Arrays.asList(params), time, count);
                VideoCreator.createFullVideo(finalPath, context, fileName, DataHolder.getAudioPath(), videoTempPath);
                return finalPath;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                clearCache();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            onVideoSaved(s);
        }
    }

    public void clearCache() {
        try {
            File dir = activity.getCacheDir();
            deleteDir(dir);
            dir = activity.getExternalCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            // Do nothing.
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else {
            return dir != null && dir.isFile() && dir.delete();
        }
    }
}
