package org.appland.settlers.assets;

import java.util.ArrayList;
import java.util.List;

public class XMidiTrack {
    private final List<Timbre> timbres;
    private byte[] data;

    public XMidiTrack() {
        timbres = new ArrayList<>();
    }

    public void addTimbre(short patch, short bank) {
        timbres.add(new Timbre(patch, bank));
    }

    public void setData(byte[] trackData) {
        this.data = trackData;
    }
}
