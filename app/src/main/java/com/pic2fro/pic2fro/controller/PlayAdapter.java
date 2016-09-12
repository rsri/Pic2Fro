package com.pic2fro.pic2fro.controller;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.pic2fro.pic2fro.R;
import com.pic2fro.pic2fro.activities.PlayActivity;
import com.pic2fro.pic2fro.model.DataHolder;
import com.pic2fro.pic2fro.players.AudioPlayer;
import com.pic2fro.pic2fro.players.SlideshowPlayer;
import com.pic2fro.pic2fro.util.Constants;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by srikaram on 01-Sep-16.
 */
public class PlayAdapter {

    private final PlayActivity activity;

    private final AudioPlayer audioPlayer;
    private final SlideshowPlayer slideshowPlayer;
    private final double time;
    private final int count;

    private Timer timer = new Timer();

    public PlayAdapter(PlayActivity activity) {
        this.activity = activity;
        audioPlayer = new AudioPlayer(activity, DataHolder.getAudioPath());
        slideshowPlayer = new SlideshowPlayer((ImageView) activity.findViewById(R.id.player_imageview));
        Bundle extras = activity.getIntent().getExtras();
        time = Double.parseDouble(extras.getString(Constants.ARG_TIME).substring(0,3));
        count = Integer.parseInt(extras.getString(Constants.ARG_COUNT));
    }

    private int counter = 0;

    public void beginSlideshow() {
        long period = (long) (time * 1000.0);
        audioPlayer.play();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (counter == DataHolder.imageCount() * count) {
                    endSlideshow();
                    return;
                }
                Bitmap image = DataHolder.getImageAt(counter % DataHolder.imageCount());
                slideshowPlayer.changeImage(image);
                counter++;
            }
        }, 0, period);
        hideSystemUi();
    }

    private void hideSystemUi() {
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    public void endSlideshow() {
        timer.cancel();
        audioPlayer.stop();
        showSystemUi();
    }

    private void showSystemUi() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        });
    }
}
