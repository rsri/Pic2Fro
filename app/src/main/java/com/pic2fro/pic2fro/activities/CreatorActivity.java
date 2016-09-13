package com.pic2fro.pic2fro.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.pic2fro.pic2fro.R;
import com.pic2fro.pic2fro.controller.CreationAdapter;
import com.pic2fro.pic2fro.util.Constants;
import com.pic2fro.pic2fro.util.IntentCreator;
import com.pic2fro.pic2fro.util.Util;
import com.pic2fro.pic2fro.views.FileChooser;

import java.io.File;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class CreatorActivity extends AppCompatActivity implements View.OnClickListener, FileChooser.FileSelectedListener {

    private TextView addImageTV;
    private CardView audioPickCV;
    private CardView audioRecordCV;
    private Spinner timeSpinner;
    private Spinner countSpinner;
    private ViewFlipper imagesFlipper;

    private CreationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new CreationAdapter(this);
        addImageTV = (TextView) findViewById(R.id.add_image_tv);
        audioPickCV = (CardView) findViewById(R.id.audio_pick_cv);
        audioRecordCV = (CardView) findViewById(R.id.audio_record_cv);
        imagesFlipper = (ViewFlipper) findViewById(R.id.images_flipper);
        countSpinner = (Spinner) findViewById(R.id.count_spinner);
        timeSpinner = (Spinner) findViewById(R.id.time_spinner);
        addImageTV.setOnClickListener(this);
        audioPickCV.setOnClickListener(this);
        audioRecordCV.setOnClickListener(this);
        adapter.refreshOnCreate();
    }

    public void restoreSpinnerInfo(int countSpinnerPos, int timeSpinnerPos) {
        countSpinner.setSelection(countSpinnerPos);
        timeSpinner.setSelection(timeSpinnerPos);
    }

    private void writeSpinnerInfo() {
        adapter.setSpinnerPos(countSpinner.getSelectedItemPosition(), timeSpinner.getSelectedItemPosition());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            if (imagesFlipper.getDisplayedChild() == 0) {
                Toast.makeText(this, R.string.select_image, Toast.LENGTH_SHORT).show();
                return true;
            }
            Intent intent = new Intent(this, PlayActivity.class);
            Bundle args = new Bundle();
            args.putString(Constants.ARG_TIME, timeSpinner.getSelectedItem().toString());
            args.putString(Constants.ARG_COUNT, countSpinner.getSelectedItem().toString());
            intent.putExtras(args);
            writeSpinnerInfo();
            startActivity(intent);
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            adapter.deleteAll();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        adapter.deleteAll();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_image_tv:
                IntentCreator.launchImagePicker(this, adapter, adapter.getAllowedCount());
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
        adapter.setAudioFile(file);
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

    public void refreshAudio(int source) {
        int cardBackground = Color.parseColor("#f9f9f9");
        switch (source) {
            case Constants.SOURCE_AUDIO_PICKER:
                audioPickCV.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light));
                audioRecordCV.setBackgroundColor(cardBackground);
                break;
            case Constants.SOURCE_AUDIO_RECORDER:
                audioRecordCV.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light));
                audioPickCV.setBackgroundColor(cardBackground);
                break;
            default:
                audioRecordCV.setBackgroundColor(cardBackground);
                audioPickCV.setBackgroundColor(cardBackground);
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
                    IntentCreator.launchImagePicker(this, adapter, adapter.getAllowedCount());
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
        if (visible) {
            imagesFlipper.setDisplayedChild(0);
        } else {
            imagesFlipper.setDisplayedChild(1);
        }
    }

    public void refreshImages(List<Bitmap> images) {
        if (images.isEmpty()) {
            toggleAddImageTextView(true);
        } else {
            toggleAddImageTextView(false);
            RelativeLayout container = (RelativeLayout) imagesFlipper.getCurrentView();
            insertChildren(container, images);
        }
    }

    private void insertChildren(RelativeLayout container, List<Bitmap> images) {
        container.removeAllViews();
        insertShims(container);
        int count = 2;
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Bitmap image : images) {
            View imageHolder = inflater.inflate(R.layout.image_thumbnail, container, false);
            imageHolder.setTag(count);
            ImageView imageView = (ImageView) imageHolder.findViewById(R.id.thumb_img);
            imageView.setImageBitmap(image);
            container.addView(imageHolder, count, buildLayoutParams(count++));
        }
        if (adapter.getAllowedCount() > 0) {
            ImageView addImageView = new ImageView(this);
            addImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            addImageView.setImageResource(R.drawable.ic_action_add);
            addImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentCreator.launchImagePicker(CreatorActivity.this, adapter, adapter.getAllowedCount());
                }
            });
            container.addView(addImageView, count, buildLayoutParams(count++));
        }
    }

    private void insertShims(RelativeLayout container) {
        View centerVerticalShim = new View(this);
        centerVerticalShim.setId(R.id.centerVerticalShim);
        centerVerticalShim.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(MATCH_PARENT, 0);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        container.addView(centerVerticalShim, 0, params);

        View centerHorizontalShim = new View(this);
        centerHorizontalShim.setId(R.id.centerHorizontalShim);
        centerHorizontalShim.setVisibility(View.INVISIBLE);
        params = new RelativeLayout.LayoutParams(0, MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        container.addView(centerHorizontalShim, 1, params);
    }

    private RelativeLayout.LayoutParams buildLayoutParams(int position) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        switch (position) {
            case Constants.POSITION_TOP_LEFT:
                params.addRule(RelativeLayout.ALIGN_PARENT_START);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                params.addRule(RelativeLayout.ABOVE, R.id.centerVerticalShim);
                params.addRule(RelativeLayout.START_OF, R.id.centerHorizontalShim);
                params.bottomMargin += Util.dpTopx(2.5f, this);
                params.rightMargin += Util.dpTopx(2.5f, this);
                break;
            case Constants.POSITION_TOP_RIGHT:
                params.addRule(RelativeLayout.ALIGN_PARENT_END);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                params.addRule(RelativeLayout.ABOVE, R.id.centerVerticalShim);
                params.addRule(RelativeLayout.END_OF, R.id.centerHorizontalShim);
                params.bottomMargin += Util.dpTopx(2.5f, this);
                params.leftMargin += Util.dpTopx(2.5f, this);
                break;
            case Constants.POSITION_BOTTOM_LEFT:
                params.addRule(RelativeLayout.ALIGN_PARENT_START);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.addRule(RelativeLayout.BELOW, R.id.centerVerticalShim);
                params.addRule(RelativeLayout.START_OF, R.id.centerHorizontalShim);
                params.topMargin += Util.dpTopx(2.5f, this);
                params.rightMargin += Util.dpTopx(2.5f, this);
                break;
            case Constants.POSITION_BOTTOM_RIGHT:
                params.addRule(RelativeLayout.ALIGN_PARENT_END);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.addRule(RelativeLayout.BELOW, R.id.centerVerticalShim);
                params.addRule(RelativeLayout.END_OF, R.id.centerHorizontalShim);
                params.topMargin += Util.dpTopx(2.5f, this);
                params.leftMargin += Util.dpTopx(2.5f, this);
                break;
        }
        return params;
    }

    public void removeChild(View v) {
        RelativeLayout container = (RelativeLayout) imagesFlipper.getCurrentView();
        ViewGroup parent = (ViewGroup) v.getParent();
        int position = (int) parent.getTag() - 2;
        adapter.removeImage(position);
    }
}
