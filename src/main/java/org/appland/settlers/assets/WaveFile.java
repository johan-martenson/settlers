package org.appland.settlers.assets;

public class WaveFile implements Sound {
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
}
