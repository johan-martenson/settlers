package org.appland.settlers.assets.resources;

import java.util.List;

public class XMidiFile {
    private final long headerSize;
    private final List<XMidiTrack> trackList;
    private final int numberTracks;

    public XMidiFile(long headerSize, int numberTracks, List<XMidiTrack> trackList) {
        this.headerSize = headerSize;
        this.numberTracks = numberTracks;
        this.trackList = trackList;
    }

    public long getHeaderSize() {
        return headerSize;
    }

    public int getNumberTracks() {
        return numberTracks;
    }

    public List<XMidiTrack> getTrackList() {
        return this.trackList;
    }
}
