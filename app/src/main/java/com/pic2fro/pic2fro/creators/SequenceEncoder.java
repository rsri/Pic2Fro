package com.pic2fro.pic2fro.creators;

import android.graphics.Bitmap;
import android.util.Pair;

import org.jcodec.codecs.h264.H264Encoder;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.codecs.h264.io.model.NALUnit;
import org.jcodec.codecs.h264.io.model.NALUnitType;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.containers.mp4.Brand;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.muxer.FramesMP4MuxerTrack;
import org.jcodec.containers.mp4.muxer.MP4Muxer;
import org.jcodec.scale.BitmapUtil;
import org.jcodec.scale.ColorUtil;
import org.jcodec.scale.Transform;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by srikaram on 09-Sep-16.
 */
public class SequenceEncoder {
    private static Map<Double, Pair<Integer, Integer>> timeToFPSMap = new HashMap<>();

    static {
        timeToFPSMap.put(0.2D, Pair.create(5, 1));
        timeToFPSMap.put(0.4D, Pair.create(5, 2));
        timeToFPSMap.put(0.6D, Pair.create(5, 3));
        timeToFPSMap.put(0.8D, Pair.create(5, 4));
        timeToFPSMap.put(1.0D, Pair.create(1, 1));
        timeToFPSMap.put(1.2D, Pair.create(5, 6));
    }

    private SeekableByteChannel ch;
    private Picture toEncode;
    private Transform transform;
    private H264Encoder encoder;
    private ArrayList<ByteBuffer> spsList;
    private ArrayList<ByteBuffer> ppsList;
    private FramesMP4MuxerTrack outTrack;
    private ByteBuffer _out;
    private int frameNo;
    private MP4Muxer muxer;
    private Pair<Integer, Integer> details;

    public SequenceEncoder(SeekableByteChannel ch, double time) throws IOException {
        this.ch = ch;

        details = timeToFPSMap.get(time);

        // Muxer that will store the encoded frames
        muxer = new MP4Muxer(ch, Brand.MP4);

        // Add video track to muxer
        outTrack = muxer.addTrack(TrackType.VIDEO, details.first);

        // Allocate a buffer big enough to hold output frames
        _out = ByteBuffer.allocate(1920 * 1080 * 6);

        // Create an instance of encoder
        encoder = new H264Encoder();

        // Transform to convert between RGB and YUV
        transform = ColorUtil.getTransform(ColorSpace.RGB, encoder.getSupportedColorSpaces()[0]);

        // Encoder extra data ( SPS, PPS ) to be stored in a special place of
        // MP4
        spsList = new ArrayList<>();
        ppsList = new ArrayList<>();
    }

    public void encodeFrame(Bitmap pic) throws IOException {
        if (toEncode == null) {
            toEncode = Picture.create(pic.getWidth(), pic.getHeight(), encoder.getSupportedColorSpaces()[0]);
        }

        // Perform conversion
        transform.transform(BitmapUtil.fromBitmap(pic), toEncode);

        // Encode image into H.264 frame, the result is stored in '_out' buffer
        _out.clear();
        ByteBuffer result = encoder.encodeFrame(toEncode, _out);

        // Based on the frame above form correct MP4 packet
        spsList.clear();
        ppsList.clear();
        H264Utils.wipePS(result, spsList, ppsList);
        NALUnit nu = NALUnit.read(NIOUtils.from(result.duplicate(), 4));
        H264Utils.encodeMOVPacket(result);

        int timestamp = frameNo * details.second;
        // Add packet to video track
        outTrack.addFrame(new MP4Packet(result, timestamp, details.first, details.second, frameNo, nu.type == NALUnitType.IDR_SLICE, null, timestamp, frameNo));
        frameNo++;
    }

    public void finish() throws IOException {
        // Push saved SPS/PPS to a special storage in MP4
        outTrack.addSampleEntry(H264Utils.createMOVSampleEntry(spsList, ppsList, 4));

        // Write MP4 header and finalize recording
        muxer.writeHeader();
        NIOUtils.closeQuietly(ch);
    }
}
