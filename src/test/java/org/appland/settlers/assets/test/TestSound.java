package org.appland.settlers.assets.test;

import junit.framework.TestCase;
import org.appland.settlers.assets.GameResource;
import org.appland.settlers.assets.GameResourceType;
import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.WaveGameResource;
import org.appland.settlers.assets.decoders.MidiDecoder;
import org.appland.settlers.assets.decoders.WaveDecoder;
import org.appland.settlers.assets.resources.MidiFile;
import org.appland.settlers.assets.resources.MidiTrack;
import org.appland.settlers.assets.resources.WaveFile;
import org.appland.settlers.assets.resources.XMidiFile;
import org.appland.settlers.assets.resources.XMidiTrack;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class TestSound {

    private static final String TEST_STEREO_WAVE_FILE = "src/test/resources/testStereo.wav";
    private static final String TEST_MONO_WAVE_FILE = "src/test/resources/testMono.wav";
    private static final String TEST_MIDI_FILE = "src/test/resources/testMidi.mid";
    private static final String TEST_XMIDI_FILE = "src/test/resources/testXMidi.xmi";


    @Test
    public void testStereoWave() throws InvalidFormatException, IOException {
        GameResource gameResource = WaveDecoder.loadSoundWaveFile(TEST_STEREO_WAVE_FILE);

        TestCase.assertEquals(gameResource.getType(), GameResourceType.WAVE_SOUND);

        WaveGameResource waveGameResource = (WaveGameResource) gameResource;

        assertNotNull(waveGameResource.getWaveFile());

        WaveFile waveFile = waveGameResource.getWaveFile();

        assertEquals(waveFile.getFormatId(), "fmt ");
        assertEquals(waveFile.getFormatSize(), 16);
        assertEquals(waveFile.getFormatTag(), 1);
        assertEquals(waveFile.getNumberChannels(), 2);
        assertEquals(waveFile.getSamplesPerSecond(), 44100);
        assertEquals(waveFile.getBytesPerSecond(), 176400);
        assertEquals(waveFile.getFrameSize(), 4);
        assertEquals(waveFile.getBitsPerSample(), 16);
        assertEquals(waveFile.getDataId(), "data");
        assertEquals(waveFile.getDataSize(), 111284);
    }

    @Test
    public void testMonoWave() throws IOException, InvalidFormatException {
        GameResource gameResource = WaveDecoder.loadSoundWaveFile(TEST_MONO_WAVE_FILE);

        assertEquals(gameResource.getType(), GameResourceType.WAVE_SOUND);

        WaveGameResource waveGameResource = (WaveGameResource) gameResource;

        assertNotNull(waveGameResource.getWaveFile());

        WaveFile waveFile = waveGameResource.getWaveFile();

        assertEquals(waveFile.getFormatId(), "fmt ");
        assertEquals(waveFile.getFormatSize(), 16);
        assertEquals(waveFile.getFormatTag(), 1);
        assertEquals(waveFile.getNumberChannels(), 1);
        assertEquals(waveFile.getSamplesPerSecond(), 44100);
        assertEquals(waveFile.getBytesPerSecond(), 88200);
        assertEquals(waveFile.getFrameSize(), 2);
        assertEquals(waveFile.getBitsPerSample(), 16);
        assertEquals(waveFile.getDataId(), "data");
        assertEquals(waveFile.getDataSize(), 135900);
    }

    @Test
    public void testMidi() throws IOException, InvalidFormatException {
        MidiFile midiFile = MidiDecoder.loadSoundMidiFile(TEST_MIDI_FILE);

        assertNotNull(midiFile);
        assertEquals(midiFile.getHeaderSize(), 6);
        assertEquals(midiFile.getFormat(), 1);
        assertEquals(midiFile.getNumberTracks(), 6);
        assertEquals(midiFile.getPulsePerQuarter(), 480);

        List<MidiTrack> trackList = midiFile.getTracks();

        assertNotNull(trackList);
        assertEquals(trackList.size(), 6);
    }

    @Test
    public void testXMidi() throws IOException, InvalidFormatException {
        XMidiFile xMidiFile = MidiDecoder.loadSoundXMidiFile(TEST_XMIDI_FILE);

        assertNotNull(xMidiFile);
        assertEquals(xMidiFile.headerSize(), 14);
        assertEquals(xMidiFile.numberTracks(), 1);

        List<XMidiTrack> trackList = xMidiFile.trackList();

        assertNotNull(trackList);
        assertEquals(trackList.size(), 1);
    }
}
