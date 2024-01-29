package org.appland.settlers.assets.resources;

import java.util.List;

public class MidiTrack {
    private final byte[] data; // uint 8
    List<Timbre> timbres;

    public MidiTrack(byte[] midiTrackData) {
        this.data = midiTrackData;
    }
}
