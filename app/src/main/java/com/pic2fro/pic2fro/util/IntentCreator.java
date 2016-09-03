package com.pic2fro.pic2fro.util;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.pic2fro.pic2fro.R;

import net.yazeed44.imagepicker.util.Picker;

/**
 * Created by srikaram on 01-Sep-16.
 */
public class IntentCreator {

    public static void launchImagePicker(FragmentActivity activity, Picker.PickListener listener) {
        int permissioCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissioCheck == PackageManager.PERMISSION_GRANTED) {
            launchImagePickerInternal(activity, listener);
        } else {
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.REQ_CODE_PERMISSION);
        }
    }

    private static void launchImagePickerInternal(FragmentActivity activity, Picker.PickListener listener) {
        Picker.Builder builder = new Picker.Builder(activity, listener, R.style.MIP_theme);
        builder.setVideosEnabled(false);
        builder.setPickMode(Picker.PickMode.MULTIPLE_IMAGES);
        builder.setLimit(Constants.MAX_IMAGES);
        builder.setBackBtnInMainActivity(true);
        Picker picker = builder.build();
        picker.startActivity();
    }

    public static void launchAudioPicker(FragmentActivity activity) {
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent, Constants.REQ_CODE_AUDIO_PICK);
    }

    public static void launchAudioRecorder(FragmentActivity activity) {
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        activity.startActivityForResult(intent, Constants.REQ_CODE_AUDIO_RECORD);
    }
}
