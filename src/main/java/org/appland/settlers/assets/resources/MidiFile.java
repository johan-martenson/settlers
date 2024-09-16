package org.appland.settlers.assets.resources;

import org.appland.settlers.assets.Sound;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a MIDI file with multiple tracks, format information, and a pulse-per-quarter note (PPQS) setting.
 */
public class MidiFile implements Sound {
    private final long headerSize;       // uint 32
    private final int format;            // uint 16
    private final int numberTracks;      // uint 16
    private final short pulsePerQuarter; // int 16
    private final List<MidiTrack> tracks = new ArrayList<>();

    /**
     * Constructs a MidiFile object with the specified header size, format, number of tracks, and PPQS.
     *
     * @param headerSize      The size of the header.
     * @param format          The MIDI format.
     * @param numberTracks    The number of tracks in the MIDI file.
     * @param pulsePerQuarter The pulse-per-quarter note (PPQS) value.
     */
    public MidiFile(long headerSize, int format, int numberTracks, short pulsePerQuarter) {
        this.headerSize = headerSize;
        this.format = format;
        this.numberTracks = numberTracks;
        this.pulsePerQuarter = pulsePerQuarter;
    }

    /**
     * Adds a MIDI track to the list of tracks in this MIDI file.
     *
     * @param midiTrack The track to add.
     */
    public void addTrack(MidiTrack midiTrack) {
        this.tracks.add(midiTrack);
    }

    /**
     * Returns the header size of the MIDI file.
     *
     * @return The header size.
     */
    public long getHeaderSize() {
        return headerSize;
    }

    /**
     * Returns the format of the MIDI file.
     *
     * @return The MIDI format.
     */
    public int getFormat() {
        return format;
    }

    /**
     * Returns the number of tracks in the MIDI file.
     *
     * @return The number of tracks.
     */
    public int getNumberTracks() {
        return numberTracks;
    }

    /**
     * Returns the pulse-per-quarter note (PPQS) setting of the MIDI file.
     *
     * @return The PPQS value.
     */
    public short getPulsePerQuarter() {
        return pulsePerQuarter;
    }

    /**
     * Returns the list of tracks in the MIDI file.
     *
     * @return The list of MIDI tracks.
     */
    public List<MidiTrack> getTracks() {
        return tracks;
    }
}
