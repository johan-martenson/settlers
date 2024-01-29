package org.appland.settlers.assets;

import org.appland.settlers.assets.resources.WaveFile;

import static java.lang.String.format;
import static org.appland.settlers.assets.GameResourceType.WAVE_SOUND;

public class WaveGameResource implements GameResource {
    private final WaveFile waveFile;

    public WaveGameResource(WaveFile waveFile) {
        this.waveFile = waveFile;
    }

    @Override
    public GameResourceType getType() {
        return WAVE_SOUND;
    }

    public WaveFile getWaveFile() {
        return waveFile;
    }

    @Override
    public String toString() {
        return "Wave game resource";
    }
}
