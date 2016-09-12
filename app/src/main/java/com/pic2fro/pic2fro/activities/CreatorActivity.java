package com.pic2fro.pic2fro.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pic2fro.pic2fro.R;
import com.pic2fro.pic2fro.controller.CreationAdapter;
import com.pic2fro.pic2fro.util.Constants;
import com.pic2fro.pic2fro.util.IntentCreator;
import com.pic2fro.pic2fro.views.FileChooser;

import java.io.File;
import java.util.List;

public class CreatorActivity extends AppCompatActivity implements View.OnClickListener, FileChooser.FileSelectedListener {

    private TextView addImageTV;
    private CardView audioPickCV;
    private CardView audioRecordCV;
    private GridLayout imagesGridLayout;
    private Spinner timeSpinner;
    private Spinner countSpinner;

    private CreationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new CreationAdapter(this);
        addImageTV = (TextView) findViewById(R.id.add_image_tv);
        audioPickCV = (CardView) findViewById(R.id.audio_pick_cv);
        audioRecordCV = (CardView) findViewById(R.id.audio_record_cv);
        imagesGridLayout = (GridLayout) findViewById(R.id.images_gd);
        countSpinner = (Spinner) findViewById(R.id.count_spinner);
        timeSpinner = (Spinner) findViewById(R.id.time_spinner);
        addImageTV.setOnClickListener(this);
        audioPickCV.setOnClickListener(this);
        audioRecordCV.setOnClickListener(this);
        adapter.refreshOnCreate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            Intent intent = new Intent(this, PlayActivity.class);
            Bundle args = new Bundle();
            args.putString(Constants.ARG_TIME, timeSpinner.getSelectedItem().toString());
            args.putString(Constants.ARG_COUNT, countSpinner.getSelectedItem().toString());
            intent.putExtras(args);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_image_tv:
                IntentCreator.launchImagePicker(this, adapter);
                break;
            case R.id.audio_pick_cv:
                IntentCreator.launchAudioPicker(this, this);
                break;
            case R.id.audio_record_cv:
                IntentCreator.launchAudioRecorder(this);
                break;
            default:
                break;
        }
    }

    @Override
    public void fileSelected(File file) {
        adapter.setAudioUri(Uri.fromFile(file));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.REQ_CODE_AUDIO_PICK:
                case Constants.REQ_CODE_AUDIO_RECORD:
                    adapter.setAudioUri(data.getData());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.REQ_CODE_READ_PERMISSION_IMAGES:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, getString(R.string.need_permission, "access images"), Toast.LENGTH_SHORT).show();
                } else {
                    IntentCreator.launchImagePicker(this, adapter);
                }
                break;
            case Constants.REQ_CODE_READ_PERMISSION_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, getString(R.string.need_permission, "access audios"), Toast.LENGTH_SHORT).show();
                } else {
                    IntentCreator.launchAudioPicker(this, this);
                }
                break;
            default:
                break;
        }
    }

    public void toggleAddImageTextView(boolean visible) {
        addImageTV.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void refreshImages(List<Bitmap> images) {
        if (images.isEmpty()) {
            toggleAddImageTextView(true);
        } else {
            toggleAddImageTextView(false);
            int childCount = 0;
            for (Bitmap image : images) {
                View childAt = imagesGridLayout.getChildAt(childCount++);
                if (childAt instanceof RelativeLayout && childAt.getVisibility() == View.GONE) {
                    childAt.setVisibility(View.VISIBLE);
                    ImageView imageView = (ImageView) ((RelativeLayout) childAt).getChildAt(0);
                    imageView.setImageBitmap(image);
                }
            }
            imagesGridLayout.requestLayout();
        }
    }

    public void removeChild(View v) {
        int index = imagesGridLayout.indexOfChild(((ViewGroup) v.getParent()));
        imagesGridLayout.getChildAt(index).setVisibility(View.GONE);
        adapter.removeImage(index);
    }
}
