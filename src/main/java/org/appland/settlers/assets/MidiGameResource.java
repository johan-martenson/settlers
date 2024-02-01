package org.appland.settlers.assets;

import org.appland.settlers.assets.resources.MidiFile;

import static org.appland.settlers.assets.GameResourceType.MIDI_SOUND;

public class MidiGameResource extends GameResource {
    private final MidiFile midiFile;

    public MidiGameResource(MidiFile midiFile) {
        this.midiFile = midiFile;
    }

    @Override
    public GameResourceType getType() {
        return MIDI_SOUND;
    }
}
