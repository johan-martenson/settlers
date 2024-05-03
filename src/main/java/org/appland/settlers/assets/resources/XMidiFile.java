package org.appland.settlers.assets.resources;

import java.util.List;

public record XMidiFile(long headerSize, int numberTracks, List<XMidiTrack> trackList) {
}
