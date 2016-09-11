package com.pic2fro.pic2fro.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.pic2fro.pic2fro.R;
import com.pic2fro.pic2fro.controller.VideoListAdapter;

public class VideosActivity extends AppCompatActivity {

    private ListView videosList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        videosList = (ListView) findViewById(R.id.videos_lv);
        videosList.setEmptyView(findViewById(android.R.id.empty));
        VideoListAdapter adapter = new VideoListAdapter(this);
        videosList.setAdapter(adapter);
    }
}
