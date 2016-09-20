package com.pic2fro.pic2fro;

import android.app.Application;
import android.content.Intent;

import com.pic2fro.pic2fro.activities.CrashReporterActivity;

/**
 * Created by srikaram on 14-Sep-16.
 */
public class Pic2FroApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace(); // not all Android versions will print the stack trace automatically

                Intent intent = new Intent();
                intent.putExtra("Crash", e);
                intent.setAction("com.pic2fro.SEND_LOG");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                System.exit(1);
            }
        });
    }
}
