package org.appland.settlers.assets.resources;

import org.appland.settlers.assets.Sound;
import org.appland.settlers.utils.DataOutputStreamLittleEndian;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class WaveFile implements Sound {
    private static final int BITS_PER_BYTE = 8;
    public static final int NUMBER_OF_BYTES_IN_RIFF_WAVE_AND_FORMAT_CHUNKS = 36;

    private final long dataSize;
    private final long bytesPerSec;
    private final String formatId;
    private final long formatSize;
    private final int formatTag;
    private final int numberChannels;
    private final long samplesPerSec;
    private final int frameSize;
    private final int bitsPerSample;
    private final String dataId;
    private byte[] waveData;

    public WaveFile(String formatId, long formatSize, int formatTag, int numberChannels, long samplesPerSec, long bytesPerSec, int frameSize, int bitsPerSample, String dataId, long dataSize) {
        this.formatId = formatId;
        this.formatSize = formatSize;
        this.formatTag = formatTag;
        this.numberChannels = numberChannels;
        this.samplesPerSec = samplesPerSec;
        this.bytesPerSec = bytesPerSec;
        this.frameSize = frameSize;
        this.bitsPerSample = bitsPerSample;
        this.dataId = dataId;
        this.dataSize = dataSize;
    }

    public void setData(byte[] waveData) {
        this.waveData = waveData;
    }

    public String getFormatId() {
        return formatId;
    }

    public int getFormatTag() {
        return formatTag;
    }

    public int getNumberChannels() {
        return numberChannels;
    }

    public long getSamplesPerSecond() {
        return samplesPerSec;
    }

    public long getBytesPerSecond() {
        return bytesPerSec;
    }

    public int getFrameSize() {
        return frameSize;
    }

    public int getBitsPerSample() {
        return bitsPerSample;
    }

    public String getDataId() {
        return dataId;
    }

    public long getDataSize() {
        return dataSize;
    }

    public long getFormatSize() {
        return formatSize;
    }

    public void writeToFile(String outSoundFile) {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(outSoundFile))
            );

            DataOutputStreamLittleEndian writer;
            writer = new DataOutputStreamLittleEndian(dataOutputStream);

            int numberOfBytesInSamples = waveData.length * numberChannels * bitsPerSample / BITS_PER_BYTE;

            writer.writeString("RIFF");
            writer.writeInt((numberOfBytesInSamples + NUMBER_OF_BYTES_IN_RIFF_WAVE_AND_FORMAT_CHUNKS));
            writer.writeString("WAVE");

            this.writeWaveHeader(writer);
            this.writeWaveData(writer);

            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void writeWaveHeader(DataOutputStreamLittleEndian writer) throws IOException {
        writer.writeString("fmt ");
        writer.writeInt((int)formatSize);
        writer.writeShort((short)formatTag);
        writer.writeShort((short)numberChannels);
        writer.writeInt((int)samplesPerSec);
        writer.writeInt((int) bytesPerSec);
        writer.writeShort((short) (numberChannels * bitsPerSample / BITS_PER_BYTE)); // frameSize
        writer.writeShort((short) bitsPerSample);
    }

    private void writeWaveData(DataOutputStreamLittleEndian writer) throws IOException {
        writer.writeString("data");

        int numberOfBytesInSamples = (waveData.length * numberChannels * bitsPerSample / BITS_PER_BYTE);

        writer.writeInt(numberOfBytesInSamples);

        writer.writeBytes(waveData);
    }

    public WaveFile getClip(int start, int duration) {
        int position = start * (int)bytesPerSec;
        byte[] clipData = new byte[duration * (int)bytesPerSec];

        System.out.println(Arrays.toString(waveData));
        System.out.println(waveData.length);
        System.out.println(waveData.length / samplesPerSec);

        for (int i = 0; i < duration * samplesPerSec; i++) {
            clipData[i] = waveData[position + i];
        }

        WaveFile waveClip = new WaveFile(
                formatId,
                formatSize,
                formatTag,
                numberChannels,
                samplesPerSec,
                bytesPerSec,
                frameSize,
                bitsPerSample,
                dataId,
                duration * bytesPerSec
        );

        waveClip.setData(clipData);

        return waveClip;
    }
}
