package com.pic2fro.pic2fro.players;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import net.yazeed44.imagepicker.model.ImageEntry;

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
