package com.pic2fro.pic2fro.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.util.TypedValue;

import java.io.File;

/**
 * Created by srikaram on 11-Sep-16.
 */
public class Util {

    private static final Paint EMPTY_PAINT = new Paint();

    public static String getOurFolder() {
        File root = Environment.getExternalStorageDirectory();
        File savePath = new File(root.getAbsolutePath() + "/Pic2Fro");
        savePath.mkdir();
        return savePath.getAbsolutePath();
    }

    public static float dpTopx(float dp, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static Bitmap getResizedBitmap(Bitmap src, int desWidth, int desHeight, Bitmap reUseBitmap) {
        Bitmap dest;
        if (reUseBitmap == null) {
            dest = Bitmap.createBitmap(desWidth, desHeight, Bitmap.Config.ARGB_8888);
        } else {
            dest = reUseBitmap;
        }
        int diffHeight = desHeight - src.getHeight();
        Canvas canvas = new Canvas(dest);
        canvas.save();
        canvas.drawColor(Color.GRAY);
        RectF destRect = new RectF(0f, diffHeight/3, desWidth, desHeight-(diffHeight/3));
        canvas.drawBitmap(src, null, destRect, EMPTY_PAINT);
        canvas.restore();
        return dest;
    }

    public static String buildVideoName(String fileName) {
        return Util.getOurFolder() + "/" + fileName + ".mp4";
    }
}
