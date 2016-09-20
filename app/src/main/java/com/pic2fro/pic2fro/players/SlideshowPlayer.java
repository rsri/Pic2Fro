package com.pic2fro.pic2fro.players;

import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;

/**
 * Created by srikaram on 01-Sep-16.
 */
public class SlideshowPlayer {

    private final ImageView hostImageView;
    private final Handler handler = new Handler();

    public SlideshowPlayer(ImageView imageView) {
        this.hostImageView = imageView;
    }

    public void changeImage(final Bitmap image) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                hostImageView.setImageBitmap(image);
//                Animation anim = AnimationUtils.loadAnimation(hostImageView.getContext(), android.R.anim.slide_in_left);
//                hostImageView.startAnimation(anim);
            }
        });
    }
}
