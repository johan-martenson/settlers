package org.appland.settlers.assets.resources;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a track in an XMidi file, containing a list of timbres and track data.
 */
public class XMidiTrack {

    private final List<Timbre> timbres = new ArrayList<>(); // List of timbres for the track
    private byte[] data; // Track data in byte array

    /**
     * Adds a timbre (patch and bank) to the track.
     *
     * @param patch The patch number.
     * @param bank  The bank number.
     */
    public void addTimbre(short patch, short bank) {
        timbres.add(new Timbre(patch, bank)); // Add a new timbre to the list
    }

    /**
     * Sets the data for the track.
     *
     * @param trackData The track data as a byte array.
     */
    public void setData(byte[] trackData) {
        this.data = trackData; // Set the track data
    }

    /**
     * Retrieves the first timbre in the track, if available.
     *
     * @return The first timbre or null if none exist.
     */
    public Timbre getFirstTimbre() {
        return timbres.isEmpty() ? null : timbres.get(0); // Get the first timbre
    }

    /**
     * Retrieves the last timbre in the track, if available.
     *
     * @return The last timbre or null if none exist.
     */
    public Timbre getLastTimbre() {
        return timbres.isEmpty() ? null : timbres.get(timbres.size() - 1); // Get the last timbre
    }

    /**
     * Retrieves the number of timbres in the track.
     *
     * @return The number of timbres in the list.
     */
    public int getNumberOfTimbres() {
        return timbres.size(); // Get the count of timbres
    }

    /**
     * Returns a string representation of the XMidiTrack, including its timbres and data size.
     *
     * @return A formatted string representation of the track.
     */
    @Override
    public String toString() {
        return String.format("""
            XMidiTrack {
                timbres=%s,
                dataSize=%d bytes
            }
            """, timbres, data != null ? data.length : 0); // Use String.format for clarity
    }
}