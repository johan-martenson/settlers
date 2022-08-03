package org.appland.settlers.assets;

import java.util.ArrayList;
import java.util.List;

public class MidiFile implements Sound {
    private final long headerSize; // uint 32
    private final int format; // uint 16
    private final int numberTracks; // uint 16
    private final short ppqs; // int 16
    private final List<MidiTrack> tracks;

    public MidiFile(long headerSize, int format, int numTracks, short ppqs) {
        this.headerSize = headerSize;
        this.format = format;
        this.numberTracks = numTracks;
        this.ppqs = ppqs;

        tracks = new ArrayList<>();
    }

    public void addTrack(MidiTrack midiTrack) {
        this.tracks.add(midiTrack);
    }

    public long getHeaderSize() {
        return headerSize;
    }

    public int getFormat() {
        return format;
    }

    public int getNumberTracks() {
        return numberTracks;
    }

    public short getPpqs() {
        return ppqs;
    }

    public List<MidiTrack> getTracks() {
        return tracks;
    }
}
