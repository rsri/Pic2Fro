package com.pic2fro.pic2fro.util;

import android.content.Context;
import android.os.Environment;
import android.util.TypedValue;

import java.io.File;

/**
 * Created by srikaram on 11-Sep-16.
 */
public class Util {

    public static String getOurFolder() {
        File root = Environment.getExternalStorageDirectory();
        File savePath = new File(root.getAbsolutePath() + "/Pic2Fro");
        savePath.mkdir();
        return savePath.getAbsolutePath();
    }

    public static float dpTopx(float dp, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
