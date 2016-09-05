package com.pic2fro.pic2fro.players;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

/**
 * Created by srikaram on 01-Sep-16.
 */
public class AudioPlayer {

    private final MediaPlayer mediaPlayer;

    public AudioPlayer(Context context, String path) {
        try {
            this.mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path);
//            mediaPlayer.setDataSource(context, path);
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
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}
