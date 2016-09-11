package com.pic2fro.pic2fro.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.pic2fro.pic2fro.R;

public class OpeningActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        findViewById(R.id.create_video_btn).setOnClickListener(this);
        findViewById(R.id.views_videos_btn).setOnClickListener(this);
        findViewById(R.id.exit_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.create_video_btn:
                Intent creatorIntent = new Intent(this, CreatorActivity.class);
                startActivity(creatorIntent);
                break;
            case R.id.views_videos_btn:
                Intent videosIntent = new Intent(this, VideosActivity.class);
                startActivity(videosIntent);
                break;
            case R.id.exit_btn:
                finish();
                break;
            default:
                break;
        }
    }
}
