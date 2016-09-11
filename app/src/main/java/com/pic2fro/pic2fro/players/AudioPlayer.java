package com.pic2fro.pic2fro.players;

import android.media.MediaPlayer;
import android.support.v4.app.FragmentActivity;

import java.io.IOException;

/**
 * Created by srikaram on 01-Sep-16.
 */
public class AudioPlayer {

    private MediaPlayer mediaPlayer;

    public AudioPlayer(final FragmentActivity activity, String path) {
        try {
            this.mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void play() {
        mediaPlayer.start();
    }

    public void stop() {
        if (mediaPlayer == null) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        mediaPlayer = null;
    }
}
