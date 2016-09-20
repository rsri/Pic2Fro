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
import com.pic2fro.pic2fro.util.Util;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * Created by srikaram on 06-Sep-16.
 */
public class VideoAdapter {

    private final FragmentActivity activity;

    public VideoAdapter(FragmentActivity activity) {
        this.activity = activity;
    }

    private DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    AlertDialog alertDialog = ((AlertDialog) dialog);
                    String fileName = ((EditText) alertDialog.findViewById(android.R.id.edit)).getText().toString();
                    fileName = FilenameUtils.getBaseName(fileName).trim();
                    processFileName(fileName);
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        }
    };

    private void processFileName(final String fileName) {
        if (!new File(Util.buildVideoName(fileName)).exists()) {
            doSave(fileName);
        } else {
            AlertDialog.Builder alBuilder = new AlertDialog.Builder(activity);
            alBuilder.setMessage(R.string.video_exists);
            alBuilder.setPositiveButton(R.string.overwrite, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doSave(fileName);
                }
            });

            alBuilder.setNegativeButton(R.string.rechoose, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    IntentCreator.launchVideoSaverDialog(activity, clickListener);
                }
            });

            alBuilder.setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            alBuilder.show();
        }
    }

    private void doSave(String fileName) {
        Bundle extras = activity.getIntent().getExtras();
        if (extras != null && !fileName.isEmpty()) {
            double time = Double.parseDouble(extras.getString(Constants.ARG_TIME).substring(0, 3));
            int count = Integer.parseInt(extras.getString(Constants.ARG_COUNT));
            VideoSaver videoSaver = new VideoSaver(time, count, fileName);
            videoSaver.execute(DataHolder.getImages().toArray(new Bitmap[DataHolder.imageCount()]));
        }
    }

    public void saveVideo() {
        IntentCreator.launchVideoSaverDialog(activity, clickListener);
    }

    public void shareVideo(String savedPath) {
        IntentCreator.launchVideoSharer(activity, Uri.fromFile(new File(savedPath)));
    }

    private void onVideoSaved(final String savedPath) {
        if (savedPath == null) {
            Toast.makeText(activity, R.string.error_video, Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(activity);
        alBuilder.setMessage(activity.getString(R.string.saved, savedPath));
        alBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alBuilder.setNegativeButton(R.string.share, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                shareVideo(savedPath);
            }
        });
        alBuilder.show();
//        Toast.makeText(activity, activity.getString(R.string.saved, savedPath), Toast.LENGTH_SHORT).show();
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
                String finalPath = Util.buildVideoName(fileName);
                Context context = VideoAdapter.this.activity;
                String videoTempPath = VideoCreator.constructVoicelessVideo(context, fileName, Arrays.asList(params), time, count);
                VideoCreator.createFullVideo(finalPath, context, fileName, DataHolder.getAudioPath(), videoTempPath);
                return finalPath;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
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
            File[] children = dir.listFiles();
            for (File child : children) {
                boolean success = deleteDir(child);
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
