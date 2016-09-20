package com.pic2fro.pic2fro.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import com.pic2fro.pic2fro.util.Util;

import org.jcodec.common.io.IOUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

public class CrashReporterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Reached crash reporter");
        sendLogFile();
//        File crashFile = new File(Util.getOurFolder() + "/" + System.currentTimeMillis() + ".txt");
//        try {
//            crashFile.createNewFile();
//            FileWriter writer = new FileWriter(crashFile);
//            e.printStackTrace(new PrintWriter(writer));
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
    }

    private void sendLogFile ()
    {
        String fullName = extractLogToFile();
        if (fullName == null)
            return;

        Intent intent = new Intent (Intent.ACTION_SEND);
        intent.setType ("plain/text");
        intent.putExtra (Intent.EXTRA_EMAIL, new String[] {"srikanthr.0402@gmail.com"});
        intent.putExtra (Intent.EXTRA_SUBJECT, "Pic2Fro log file");
        intent.putExtra (Intent.EXTRA_STREAM, Uri.parse ("file://" + fullName));
        intent.putExtra (Intent.EXTRA_TEXT, "Log file attached."); // do this so some email clients don't complain about empty body.
        startActivity (intent);
        finish();
    }
    private String extractLogToFile()
    {
        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo (this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e2) {
        }
        String model = Build.MODEL;
        if (!model.startsWith(Build.MANUFACTURER))
            model = Build.MANUFACTURER + " " + model;

        File crashFile = new File(Util.getOurFolder() + "/" + System.currentTimeMillis() + ".txt");
        InputStreamReader reader = null;
        FileWriter writer = null;
        try
        {
            // For Android 4.0 and earlier, you will get all app's log output, so filter it to
            // mostly limit it to your app's output.  In later versions, the filtering isn't needed.
            String cmd = "logcat -d -v time";


            // get input stream
            Process process = Runtime.getRuntime().exec(cmd);
            reader = new InputStreamReader (process.getInputStream());

            // write output stream
            writer = new FileWriter (crashFile);
            writer.write ("Android version: " +  Build.VERSION.SDK_INT + "\n");
            writer.write ("Device: " + model + "\n");
            writer.write ("App version: " + (info == null ? "(null)" : info.versionCode) + "\n");

            writer.write("\n\n\n" + getStackTrace((Throwable) getIntent().getSerializableExtra("Crash")));
            writer.write("\n\n\n");
            char[] buffer = new char[10000];
            do
            {
                int n = reader.read (buffer, 0, buffer.length);
                if (n == -1)
                    break;
                writer.write (buffer, 0, n);
            } while (true);

            reader.close();
            writer.close();
        }
        catch (IOException e)
        {
            IOUtils.closeQuietly(writer);
            IOUtils.closeQuietly(reader);
            // You might want to write a failure message to the log here.
            return null;
        }

        return crashFile.getAbsolutePath();
    }

    private String getStackTrace(Throwable e) {
        e.printStackTrace();
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
