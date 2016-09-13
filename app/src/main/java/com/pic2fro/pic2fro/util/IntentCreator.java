package com.pic2fro.pic2fro.util;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import com.pic2fro.pic2fro.R;
import com.pic2fro.pic2fro.views.FileChooser;

import net.yazeed44.imagepicker.util.Picker;

/**
 * Created by srikaram on 01-Sep-16.
 */
public class IntentCreator {

    public static void launchImagePicker(FragmentActivity activity, Picker.PickListener listener, int imageCount) {
        int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            launchImagePickerInternal(activity, listener, imageCount);
        } else {
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.REQ_CODE_READ_PERMISSION_IMAGES);
        }
    }

    public static void launchVideoSaverDialog(FragmentActivity activity, DialogInterface.OnClickListener listener) {
        int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            launchVideoSaverDialogInternal(activity, listener);
        } else {
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQ_CODE_WRITE_PERMISSION);
        }
    }

    private static void launchVideoSaverDialogInternal(FragmentActivity activity, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final EditText fileNameET = new EditText(activity);
        fileNameET.setId(android.R.id.edit);
        fileNameET.setHint(R.string.enter_file_name);
        builder.setView(fileNameET);
        builder.setPositiveButton(android.R.string.ok, listener);
        builder.setNegativeButton(android.R.string.cancel, listener);

        builder.show();
    }

    private static void launchImagePickerInternal(FragmentActivity activity, Picker.PickListener listener, int imageCount) {
        Picker.Builder builder = new Picker.Builder(activity, listener, R.style.MIP_theme);
        builder.setVideosEnabled(false);
        builder.setPickMode(Picker.PickMode.MULTIPLE_IMAGES);
        builder.setLimit(imageCount);
        builder.setBackBtnInMainActivity(true);
        Picker picker = builder.build();
        picker.startActivity();
    }

    public static void launchAudioPicker(FragmentActivity activity, FileChooser.FileSelectedListener listener) {
        int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            launchAudioPickerInternal(activity, listener);
        } else {
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.REQ_CODE_READ_PERMISSION_AUDIO);
        }
//        Intent intent = new Intent();
//        intent.setType("audio/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        activity.startActivityForResult(intent, Constants.REQ_CODE_AUDIO_PICK);
    }

    private static void launchAudioPickerInternal(FragmentActivity activity, FileChooser.FileSelectedListener listener) {
        FileChooser fileChooser = new FileChooser(activity);
        fileChooser.setFileListener(listener);
        fileChooser.showDialog();
    }

    public static void launchAudioRecorder(FragmentActivity activity) {
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        activity.startActivityForResult(intent, Constants.REQ_CODE_AUDIO_RECORD);
    }

    public static void launchVideoSharer(FragmentActivity activity, Uri videoUri) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, videoUri);
        shareIntent.setType("video/mp4");
        activity.startActivity(Intent.createChooser(shareIntent, activity.getText(R.string.send_to)));
    }
}
