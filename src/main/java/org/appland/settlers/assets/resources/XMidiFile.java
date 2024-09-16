package org.appland.settlers.assets.resources;

import java.util.List;

/**
 * Represents an XMidi file, containing a header size, number of tracks, and a list of XMidi tracks.
 */
public record XMidiFile(long headerSize, int numberTracks, List<XMidiTrack> trackList) {

    /**
     * Checks if the XMidiFile contains any tracks.
     *
     * @return true if the track list is not empty, false otherwise.
     */
    public boolean hasTracks() {
        return !trackList.isEmpty(); // Check if there are any tracks
    }

    /**
     * Returns the first track in the track list, if available.
     *
     * @return The first track or null if the list is empty.
     */
    public XMidiTrack getFirstTrack() {
        return trackList.isEmpty() ? null : trackList.getFirst(); // Return the first track if available
    }

    /**
     * Returns the last track in the track list, if available.
     *
     * @return The last track or null if the list is empty.
     */
    public XMidiTrack getLastTrack() {
        return trackList.isEmpty() ? null : trackList.getLast(); // Return the last track if available
    }
}
