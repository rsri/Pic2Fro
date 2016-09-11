package com.pic2fro.pic2fro.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by srikaram on 01-Sep-16.
 */
public class Constants {

    public static final int MAX_IMAGES = 4;
    public static final int REQ_CODE_AUDIO_PICK = 1;
    public static final int REQ_CODE_AUDIO_RECORD = 2;
    public static final int REQ_CODE_READ_PERMISSION = 3;
    public static final int REQ_CODE_WRITE_PERMISSION = 4;
    public static final String ARG_TIME = "time";
    public static final String ARG_COUNT = "count";

    public static final String SAVE_PATH;
    public static final int VIDEO_HEIGHT = 240;
    public static final int VIDEO_WIDTH = 320;


    static {
        File root = Environment.getExternalStorageDirectory();
        File savePath = new File(root.getAbsolutePath() + "/Pic2Fro");
        savePath.mkdir();
        SAVE_PATH = savePath.getAbsolutePath();
    }
}
