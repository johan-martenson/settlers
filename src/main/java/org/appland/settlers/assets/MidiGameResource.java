package org.appland.settlers.assets;

import static org.appland.settlers.assets.GameResourceType.MIDI_SOUND;

public class MidiGameResource implements GameResource {
    private final MidiFile midiFile;

    public MidiGameResource(MidiFile midiFile) {
        this.midiFile = midiFile;
    }

    @Override
    public GameResourceType getType() {
        return MIDI_SOUND;
    }
}
