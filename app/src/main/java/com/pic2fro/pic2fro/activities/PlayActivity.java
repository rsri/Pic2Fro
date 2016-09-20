package com.pic2fro.pic2fro.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.pic2fro.pic2fro.R;
import com.pic2fro.pic2fro.controller.PlayAdapter;
import com.pic2fro.pic2fro.controller.VideoAdapter;
import com.pic2fro.pic2fro.util.Constants;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {

    private PlayAdapter playAdapter;
    private VideoAdapter videoAdapter;

    private ImageView replayImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        replayImageView = (ImageView) findViewById(R.id.replay_imageview);
        replayImageView.setOnClickListener(this);
        playAdapter = new PlayAdapter(this);
        videoAdapter = new VideoAdapter(this);
        playAdapter.beginSlideshow();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.player_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            videoAdapter.saveVideo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.REQ_CODE_WRITE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, getString(R.string.need_permission, "write video"), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Please perform the action again.", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    public void onSlideshowEnded() {
        replayImageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void finish() {
        super.finish();
        playAdapter.endSlideshow();
    }

    @Override
    public void onClick(View v) {
        if (v == replayImageView) {
            replayImageView.setVisibility(View.INVISIBLE);
            playAdapter.beginSlideshow();
        }
    }
}
