package org.appland.settlers.assets;

import java.util.List;

import static org.appland.settlers.assets.GameResourceType.XMIDI_SOUND;

public class XMidiGameResource implements GameResource {
    private final List<XMidiTrack> xMidiTracks;

    public XMidiGameResource(List<XMidiTrack> xMidiTracks) {
        this.xMidiTracks = xMidiTracks;
    }

    @Override
    public GameResourceType getType() {
        return XMIDI_SOUND;
    }
}
