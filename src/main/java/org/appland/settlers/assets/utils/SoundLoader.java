package org.appland.settlers.assets.utils;

import org.appland.settlers.assets.GameResource;
import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.MidiGameResource;
import org.appland.settlers.assets.SoundType;
import org.appland.settlers.assets.Utils;
import org.appland.settlers.assets.WaveGameResource;
import org.appland.settlers.assets.XMidiGameResource;
import org.appland.settlers.assets.decoders.MidiDecoder;
import org.appland.settlers.assets.decoders.WaveDecoder;
import org.appland.settlers.utils.ByteReader;

import java.io.IOException;

import static java.lang.String.format;
import static java.nio.ByteOrder.LITTLE_ENDIAN;
import static org.appland.settlers.assets.SoundType.*;

public class SoundLoader {

    private static boolean debug = false;

    private static void debugPrint(String debugString) {
        if (debug) {
            System.out.println(debugString);
        }
    }

    public static GameResource loadSoundFromStream(ByteReader streamReader) throws IOException, InvalidFormatException {
        streamReader.pushByteOrder(LITTLE_ENDIAN);

        long length = streamReader.getUint32();

        SoundType soundType = getSoundTypeFromByteReader(streamReader);

        debugPrint(format("Got sound type: %s", soundType.name()));


        if (soundType == MIDI) {
            return new MidiGameResource(MidiDecoder.loadSoundMidiFromStream(streamReader));
        } else if (soundType == WAVE) {
            return new WaveGameResource(WaveDecoder.loadWaveSoundFromStream(streamReader, length, true));
        } else if (soundType == XMIDI || soundType == XMID_DIR) {
            return new XMidiGameResource(MidiDecoder.loadXMidiSoundFromStream(streamReader, length, soundType == XMID_DIR));
        } else if (soundType == WAVE_WITHOUT_HEADER) {
            return new WaveGameResource(WaveDecoder.loadWaveSoundFromStream(streamReader, length, false));
        } else {
            throw new RuntimeException("Support for 'other sound' is not implemented yet");
        }
    }

    private static SoundType getSoundTypeFromByteReader(ByteReader streamReader) throws IOException {
        int position = streamReader.getPosition();

        String header = streamReader.getUint8ArrayAsString(4);

        debugPrint(Utils.convertBytesToHex(header.getBytes()));

        SoundType soundType = null;

        switch (header) {
            case "FORM":
            case "RIFF":

                long length = streamReader.getUint32();
                String subHeader = streamReader.getUint8ArrayAsString(4);

                soundType = switch (subHeader) {
                    case "XMID", "XDIR" -> XMIDI;
                    case "WAVE" -> WAVE;
                    default -> soundType;
                };

                break;

            case "MThd":
                soundType = MIDI;

                break;

            default:
                soundType = WAVE_WITHOUT_HEADER;
        }

        streamReader.setPosition(position);

        return soundType;
    }
}
