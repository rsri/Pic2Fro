package com.pic2fro.pic2fro.creators;

import android.util.Pair;

import org.jcodec.codecs.h264.H264Encoder;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.codecs.h264.io.model.NALUnit;
import org.jcodec.codecs.h264.io.model.NALUnitType;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture8Bit;
import org.jcodec.common.model.Rational;
import org.jcodec.common.model.TapeTimecode;
import org.jcodec.containers.mp4.Brand;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.muxer.FramesMP4MuxerTrack;
import org.jcodec.containers.mp4.muxer.MP4Muxer;
import org.jcodec.scale.ColorUtil;
import org.jcodec.scale.Transform8Bit;

import java.io.File;
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
    private Transform8Bit transform;
    private H264Encoder encoder;
    private ArrayList<ByteBuffer> spsList;
    private ArrayList<ByteBuffer> ppsList;
    private FramesMP4MuxerTrack outTrack;
    private ByteBuffer _out;
    private int frameNo;
    private MP4Muxer muxer;
    private ByteBuffer sps;
    private ByteBuffer pps;
    private int timestamp;
    private Rational fps;
    private Pair<Integer, Integer> details;
    private Map<Picture8Bit, Picture8Bit> rgbToyuv420j = new HashMap<>();

    public SequenceEncoder(SeekableByteChannel ch, double time, int width, int height) throws IOException {
        this.ch = ch;
        this.muxer = MP4Muxer.createMP4Muxer(ch, Brand.MP4);
        this.details = timeToFPSMap.get(time);
        this.outTrack = this.muxer.addTrack(TrackType.VIDEO, details.first);
        this._out = ByteBuffer.allocate(width * height * 6);
        this.encoder = H264Encoder.createH264Encoder();
        this.transform = ColorUtil.getTransform8Bit(ColorSpace.RGB, this.encoder.getSupportedColorSpaces()[0]);
        this.spsList = new ArrayList<>();
        this.ppsList = new ArrayList<>();
    }

    public void encodeFrame(Picture8Bit pic) throws IOException {
        Picture8Bit yuv420jPic = rgbToyuv420j.get(pic);
        if (yuv420jPic == null) {
            yuv420jPic = Picture8Bit.create(pic.getWidth(), pic.getHeight(), this.encoder.getSupportedColorSpaces()[0]);
            this.transform.transform(pic, yuv420jPic);
            rgbToyuv420j.put(pic, yuv420jPic);
        }

        this._out.clear();
        ByteBuffer result = this.encoder.encodeFrame8Bit(yuv420jPic, this._out);
        this.spsList.clear();
        this.ppsList.clear();
        H264Utils.wipePSinplace(result, this.spsList, this.ppsList);
        NALUnit nu = NALUnit.read(NIOUtils.from(result.duplicate(), 4));
        H264Utils.encodeMOVPacket(result);
        if (this.sps == null && this.spsList.size() != 0) {
            this.sps = this.spsList.get(0);
        }

        if (this.pps == null && this.ppsList.size() != 0) {
            this.pps = this.ppsList.get(0);
        }

        timestamp = frameNo * details.second;
        this.outTrack.addFrame(MP4Packet.createMP4Packet(result, (long) this.timestamp, details.first, details.second, (long) this.frameNo, nu.type == NALUnitType.IDR_SLICE, (TapeTimecode) null, 0, (long) this.timestamp, 0));
        ++this.frameNo;
    }

    public void finish() throws IOException {
        if (this.sps != null && this.pps != null) {
            this.outTrack.addSampleEntry(H264Utils.createMOVSampleEntryFromBuffer(this.sps, this.pps, 4));
            this.muxer.writeHeader();
            NIOUtils.closeQuietly(this.ch);
        } else {
            throw new RuntimeException("Somehow the encoder didn\'t generate SPS/PPS pair, did you encode at least one frame?");
        }
    }
}
