package com.pic2fro.pic2fro.creators;

import android.content.DialogInterface;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pic2fro.pic2fro.R;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by srikaram on 18-Sep-16.
 */
public class SoundRecorder {

    private final FragmentActivity activity;
    private final RecordListener recordListener;
    private boolean mRecordingKeepGoing;
    private boolean mCancelRecording;
    private AlertDialog mAlertDialog;
    private TextView mTimerTextView;
    private long mRecordingLastUpdateTime;
    private double mRecordingTime;
    private Thread mRecordAudioThread;
    private SoundFile mSoundFile;
    private Handler mHandler;
    private boolean mStarted;

    public SoundRecorder(FragmentActivity activity, RecordListener listener) {
        this.activity = activity;
        this.recordListener = listener;
        mHandler = new Handler();
    }

    public void startRecording() {
        showDialog();
    }

    private void doRecord() {
        final SoundFile.ProgressListener listener =
                new SoundFile.ProgressListener() {
                    public boolean reportProgress(double elapsedTime) {
                        long now = getCurrentTime();
                        if (now - mRecordingLastUpdateTime > 20) {
                            mRecordingTime = elapsedTime;
                            // Only UI thread can update Views such as TextViews.
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    int min = (int) (mRecordingTime / 60);
                                    float sec = (float) (mRecordingTime - 60 * min);
                                    String time = String.format(Locale.getDefault(), "%d:%05.2f", min, sec);
                                    System.out.println("Textview " + time);
                                    mTimerTextView.setText(time);
                                }
                            });
                            mRecordingLastUpdateTime = now;
                        }
                        return mRecordingKeepGoing;
                    }
                };

        mRecordAudioThread = new Thread() {
            public void run() {
                try {
                    mSoundFile = SoundFile.record(listener);
                    if (mSoundFile == null) {
                        mAlertDialog.dismiss();
                        Runnable runnable = new Runnable() {
                            public void run() {
                                Toast.makeText(activity, activity.getString(R.string.error_audio), Toast.LENGTH_SHORT).show();
                            }
                        };
                        mHandler.post(runnable);
                        return;
                    }
                } catch (final Exception e) {
                    mAlertDialog.dismiss();
                    e.printStackTrace();

                    Runnable runnable = new Runnable() {
                        public void run() {
                            Toast.makeText(activity, activity.getString(R.string.error_audio), Toast.LENGTH_SHORT).show();
                        }
                    };
                    mHandler.post(runnable);
                    return;
                }
                mAlertDialog.dismiss();
                if (mCancelRecording) {
                    recordListener.onRecordCancelled();
                } else {
                    Runnable runnable = new Runnable() {
                        public void run() {
                            finishRecording();
                        }
                    };
                    mHandler.post(runnable);
                }
            }
        };
        mRecordAudioThread.start();
    }

    private void finishRecording() {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            File audioTempFile = File.createTempFile(timestamp, ".m4a", activity.getExternalCacheDir());
            mSoundFile.writeFile(audioTempFile, 0, (float) mRecordingTime);
            recordListener.onRecordFileSaved(audioTempFile);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(activity, activity.getString(R.string.error_audio), Toast.LENGTH_SHORT).show();
        }
    }

    private void showDialog() {
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(activity);
        adBuilder.setTitle(activity.getString(R.string.progress_dialog_recording));
        adBuilder.setCancelable(true);
        adBuilder.setNegativeButton(
                activity.getString(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        adBuilder.setPositiveButton(
                activity.getString(R.string.record_audio_start),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        adBuilder.setView(View.inflate(activity, R.layout.record_audio, null));
        adBuilder.setCancelable(false);
        mAlertDialog = adBuilder.show();
        mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mStarted) {
                    mStarted = true;
                    ((Button) v).setText(R.string.record_audio_stop);
                    mRecordingKeepGoing = true;
                    doRecord();
                } else {
                    mRecordingKeepGoing = false;
                    mAlertDialog.dismiss();
                }
            }
        });
        mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecordingKeepGoing = false;
                mCancelRecording = true;
                mAlertDialog.dismiss();
            }
        });
        mTimerTextView = (TextView) mAlertDialog.findViewById(R.id.record_audio_timer);
    }

    private long getCurrentTime() {
        return System.nanoTime() / 1000000;
    }

    public interface RecordListener {

        void onRecordFileSaved(File file);

        void onRecordCancelled();
    }

}
