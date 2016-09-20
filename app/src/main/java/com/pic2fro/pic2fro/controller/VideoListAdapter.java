package com.pic2fro.pic2fro.controller;

import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pic2fro.pic2fro.R;
import com.pic2fro.pic2fro.util.Constants;
import com.pic2fro.pic2fro.util.IntentCreator;
import com.pic2fro.pic2fro.util.Util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by srikaram on 10-Sep-16.
 */
public class VideoListAdapter extends BaseAdapter {

    private final FragmentActivity activity;
    private final List<File> videoFiles = new ArrayList<>();

    public VideoListAdapter(FragmentActivity activity) {
        this.activity = activity;
        fetchVideoFiles();
    }

    @Override
    public void notifyDataSetChanged() {
        fetchVideoFiles();
        super.notifyDataSetChanged();
    }

    private void fetchVideoFiles() {
        File file = new File(Util.getOurFolder());
        if (!file.exists()) {
            System.out.println("No videos found");
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".mp4");
                }
            });
            videoFiles.clear();
            videoFiles.addAll(Arrays.asList(files));
        }
    }

    @Override
    public int getCount() {
        return videoFiles.size();
    }

    @Override
    public Object getItem(int position) {
        return videoFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return videoFiles.get(position).length();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final File file = videoFiles.get(pos);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(activity, R.layout.videos_list_item, null);

            viewHolder = new ViewHolder();
            viewHolder.videoName = (TextView) convertView.findViewById(R.id.video_name_tv);
            viewHolder.removeButton = (ImageButton) convertView.findViewById(R.id.video_remove_btn);
            viewHolder.removeButton.setBackgroundColor(ContextCompat.getColor(activity, R.color.light_red));
            viewHolder.shareButton = (ImageButton) convertView.findViewById(R.id.video_share_btn);
            viewHolder.shareButton.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.holo_green_light));

            viewHolder.videoName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentCreator.launchVideoPlayer(activity, file);
                }
            });
            viewHolder.removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    file.delete();
                    notifyDataSetChanged();
                }
            });

            viewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentCreator.launchVideoSharer(VideoListAdapter.this.activity, Uri.fromFile(file));
                }
            });

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.videoName.setText(file.getName());
        return convertView;
    }

    private class ViewHolder {
        TextView videoName;
        ImageButton removeButton;
        ImageButton shareButton;
    }
}
