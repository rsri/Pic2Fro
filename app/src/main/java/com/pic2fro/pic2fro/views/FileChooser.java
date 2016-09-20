package com.pic2fro.pic2fro.views;

/**
 * Created by srikaram on 11-Sep-16.
 */

import android.app.Dialog;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.pic2fro.pic2fro.R;
import com.pic2fro.pic2fro.creators.SoundFile;

import org.apache.commons.io.FilenameUtils;
import org.jcodec.common.io.IOUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileChooser {
    private static final String PARENT_DIR = "..";

    private final FragmentActivity activity;
    private ListView list;
    private Dialog dialog;
    private File currentPath;
    private Map<String, File> defaultAudios = new HashMap<>();

    // file selection event handling
    public interface FileSelectedListener {
        void onFilePicked(File file);
    }

    public FileChooser setFileListener(FileSelectedListener fileListener) {
        this.fileListener = fileListener;
        return this;
    }

    private FileSelectedListener fileListener;

    public FileChooser(FragmentActivity activity) {
        this.activity = activity;
        dialog = new Dialog(activity);
        list = new ListView(activity);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int which, long id) {
                String fileChosen = (String) list.getItemAtPosition(which);
                File chosenFile = getChosenFile(fileChosen);
                if (chosenFile == null) {
                    init();
                } else {
                    if (chosenFile.isDirectory()) {
                        refresh(chosenFile);
                    } else {
                        if (fileListener != null) {
                            fileListener.onFilePicked(chosenFile);
                        }
                        dialog.dismiss();
                    }
                }
            }
        });
        dialog.setContentView(list);
        dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        init();
    }

    private void init() {
        currentPath = null;
        File file = new File(activity.getFilesDir().getAbsolutePath() + "/audio");
        file.mkdir();
        FileOutputStream fos = null;
        try {
            String[] audioFiles = activity.getResources().getAssets().list("");
            for (String audioFile : audioFiles) {
                String name = FilenameUtils.getName(audioFile);
                if (!SoundFile.isFilenameSupported(name)) {
                    continue;
                }
                File audio = new File(file, name);
                defaultAudios.put(name, audio);
                if (!audio.createNewFile()) {
                    continue;
                }
                InputStream is = activity.getAssets().open(name);
                byte[] data = new byte[1024];
                int read;
                fos = new FileOutputStream(audio);
                while ((read = is.read(data)) > -1) {
                    fos.write(data, 0, read);
                }
            }
            List<String> initialPaths = new ArrayList<>(defaultAudios.size() + 1);
            initialPaths.add(Environment.getExternalStorageDirectory().getPath());
            initialPaths.addAll(defaultAudios.keySet());
            String[] initialPathStrs = initialPaths.toArray(new String[initialPaths.size()]);
            dialog.setTitle(R.string.pick_audio);
            setAdapter(initialPathStrs);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    public void showDialog() {
        dialog.show();
    }


    /**
     * Sort, filter and display the files for the given path.
     */
    private void refresh(File path) {
        this.currentPath = path;
        if (path.exists()) {
            File[] dirs = path.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return (file.isDirectory() && file.canRead());
                }
            });
            File[] files = path.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    if (!file.isDirectory()) {
                        if (!file.canRead()) {
                            return false;
                        } else {
                            return SoundFile.isFilenameSupported(file.getName());
                        }
                    } else {
                        return false;
                    }
                }
            });

            // convert to an array
            int i = 0;
            String[] fileList;
            if (path.getParentFile() == null) {
                fileList = new String[dirs.length + files.length];
            } else {
                fileList = new String[dirs.length + files.length + 1];
                fileList[i++] = PARENT_DIR;
            }
            Arrays.sort(dirs);
            Arrays.sort(files);
            for (File dir : dirs) {
                fileList[i++] = dir.getName();
            }
            for (File file : files) {
                fileList[i++] = file.getName();
            }

            // refresh the user interface
            dialog.setTitle(currentPath.getPath());
            setAdapter(fileList);
        }
    }

    private void setAdapter(final String[] fileList) {
        list.setAdapter(new ArrayAdapter<String>(activity,
                android.R.layout.simple_list_item_1, fileList) {
            @Override
            public View getView(int pos, View view, ViewGroup parent) {
                view = super.getView(pos, view, parent);
                TextView tv = (TextView) view;
                int color = SoundFile.isFilenameSupported(fileList[pos]) ?
                        android.R.color.primary_text_light :
                        android.R.color.holo_green_light;
                tv.setTextColor(ContextCompat.getColor(activity, color));
                tv.setSingleLine(true);
                return view;
            }
        });
    }

    /**
     * Convert a relative filename into an actual File object.
     */
    private File getChosenFile(String fileChosen) {
        File externalStorage = Environment.getExternalStorageDirectory();
        if (fileChosen.equals(PARENT_DIR)) {
            if (currentPath.equals(externalStorage)) {
                return null;
            } else {
                return currentPath.getParentFile();
            }
        } else if (currentPath == null) {
            if (fileChosen.equals(externalStorage.getPath())) {
                return externalStorage;
            } else {
                return defaultAudios.get(fileChosen);
            }
        } else {
            return new File(currentPath, fileChosen);
        }
    }
}