package org.appland.settlers.assets;

import java.util.List;

public class MidiTrack {
    byte[] data; // uint 8
    List<Timbre> timbres;

    public MidiTrack(byte[] midiTrackData) {
        this.data = midiTrackData;
    }
}
