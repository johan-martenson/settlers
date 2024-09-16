package org.appland.settlers.assets.resources;

import java.util.List;

/**
 * Represents a MIDI track, containing the raw MIDI data and associated timbres.
 */
public class MidiTrack {
    private final byte[] data; // uint 8
    List<Timbre> timbres;

    /**
     * Constructs a MidiTrack with the provided MIDI track data.
     *
     * @param midiTrackData The raw MIDI data for the track.
     */
    public MidiTrack(byte[] midiTrackData) {
        this.data = midiTrackData;
    }

    /**
     * Returns the raw MIDI track data.
     *
     * @return The raw MIDI data as a byte array.
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Sets the list of timbres used in this MIDI track.
     *
     * @param timbres The list of timbres to set.
     */
    public void setTimbres(List<Timbre> timbres) {
        this.timbres = timbres;
    }

    /**
     * Returns the list of timbres used in this MIDI track.
     *
     * @return The list of timbres.
     */
    public List<Timbre> getTimbres() {
        return timbres;
    }}
