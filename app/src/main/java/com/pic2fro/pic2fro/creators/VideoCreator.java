package com.pic2fro.pic2fro.creators;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.pic2fro.pic2fro.util.Util;

import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Picture8Bit;
import org.jcodec.scale.BitmapUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by srikaram on 06-Sep-16.
 */
public class VideoCreator {

    public static String constructVoicelessVideo(Context context, String timestamp, List<Bitmap> bitmaps, double time, int count) throws IOException {
        File tempFile = File.createTempFile(timestamp, ".mp4", context.getExternalCacheDir());
        List<Picture8Bit> pictures = new ArrayList<>(bitmaps.size());
        int maxWidth = 0, maxHeight = 0;
        for (Bitmap image : bitmaps) {
            if (maxWidth < image.getWidth()) {
                maxWidth = image.getWidth();
            }
            if (maxHeight < image.getHeight()) {
                maxHeight = image.getHeight();
            }
        }
        maxWidth = maxWidth % 2 == 0? maxWidth : maxWidth+1;
        maxHeight = maxHeight % 2 == 0? maxHeight : maxHeight+1;
        SequenceEncoder encoder = new SequenceEncoder(NIOUtils.rwChannel(tempFile), time, maxWidth, maxHeight);
        Bitmap modImage = null;
        for (Bitmap image : bitmaps) {
            Bitmap bmToUse;
            if (image.getHeight() == maxHeight && image.getWidth() == maxWidth) {
                bmToUse = image;
            } else {
                modImage = Util.getResizedBitmap(image, maxWidth, maxHeight, modImage);
                bmToUse = modImage;
            }
            int width = bmToUse.getWidth() % 2 == 0? bmToUse.getWidth() : bmToUse.getWidth() + 1;
            int height = bmToUse.getHeight() % 2 == 0? bmToUse.getHeight() : bmToUse.getHeight() + 1;
            Picture8Bit picture = Picture8Bit.create(width, height, ColorSpace.RGB);
            BitmapUtil.fromBitmap8Bit(bmToUse, picture);
            pictures.add(picture);
        }
        if (modImage != null) {
            modImage.recycle();
        }
        for (int i = 0; i < count; i++) {
            for (Picture8Bit image : pictures) {
                encoder.encodeFrame(image);
            }
        }
        encoder.finish();
        return tempFile.getAbsolutePath();
    }

    public static long getMediaLength(Context context, String mediaPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, Uri.fromFile(new File(mediaPath)));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Long.parseLong(time);
    }

    public static void createFullVideo(String finalPath, Context context, String timestamp, String audioPath, String videoTempPath) throws IOException, SoundFile.InvalidInputException {
        File finalFile = new File(finalPath);
        finalFile.createNewFile();
        List<File> audioFiles = new LinkedList<>();
        if (audioPath != null) {
            long time = VideoCreator.getMediaLength(context, videoTempPath);
            time = (long) Math.ceil(time / 1000.0);
            long audioLength = (long) Math.ceil(getMediaLength(context, audioPath) / 1000.0);
            long startTime = 0, endTime = time;
            if (audioLength >= time) {
                SoundFile soundFile = SoundFile.create(audioPath, null);
                File audioTempFile = File.createTempFile(timestamp, ".m4a", context.getExternalCacheDir());
                soundFile.writeFile(audioTempFile, startTime, endTime);
                audioFiles.add(audioTempFile);
            } else {
                SoundFile soundFile = SoundFile.create(audioPath, null);

                long fullLength = (long) Math.floor(time / audioLength);
                long partLength = time % audioLength;
                for (int i = 0; i < fullLength; i++) {
                    File audioTempFile = File.createTempFile(timestamp + "" + i, ".m4a", context.getExternalCacheDir());
                    soundFile.writeFile(audioTempFile, startTime, audioLength);
                    audioFiles.add(audioTempFile);
                }
                if (partLength > 0) {
                    File audioTempFile = File.createTempFile(timestamp + "" + fullLength, ".m4a", context.getExternalCacheDir());
                    soundFile.writeFile(audioTempFile, startTime, partLength);
                    audioFiles.add(audioTempFile);
                }
            }
        }

        Movie movie = new Movie();
        Movie video = MovieCreator.build(videoTempPath);
        List<Movie> audios = new LinkedList<>();
        for (File audioFile : audioFiles) {
            audios.add(MovieCreator.build(new FileDataSourceImpl(audioFile)));
        }

        for (Track movieTrack : video.getTracks()) {
            movie.addTrack(movieTrack);
        }
        List<Track> audioTracks = new LinkedList<>();
        for (Movie audio : audios) {
            audioTracks.addAll(audio.getTracks());
        }
        if (!audioTracks.isEmpty()) {
            movie.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
        }
        Container out = new DefaultMp4Builder().build(movie);
        FileChannel fc = new RandomAccessFile(finalFile, "rw").getChannel();
        out.writeContainer(fc);
        fc.close();

    }
}
