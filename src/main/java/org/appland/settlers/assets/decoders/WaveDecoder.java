package org.appland.settlers.assets.decoders;

import org.appland.settlers.assets.GameResource;
import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.resources.WaveFile;
import org.appland.settlers.assets.utils.SoundLoader;
import org.appland.settlers.utils.ByteArrayReader;
import org.appland.settlers.utils.ByteReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

public class WaveDecoder {
    private static final int WAVE_HEADER_SIZE = 304;

    private static boolean debug = false;

    private static void debugPrint(String debugString) {
        if (debug) {
            System.out.println(debugString);
        }
    }

    /**
     * Loads a wave sound from a stream.
     *
     * @param streamReader The byte reader stream to load data from
     * @param length       The length of the wave data
     * @param hasHeader    Flag indicating if the wave file has a header
     * @return A WaveFile object representing the wave data
     * @throws InvalidFormatException If the format of the wave file is invalid
     * @throws IOException            If an I/O error occurs
     */
    public static WaveFile loadWaveSoundFromStream(ByteReader streamReader, long length, boolean hasHeader) throws InvalidFormatException, IOException {
        debugPrint("   - Loading wave sound");
        debugPrint("      - Length: " + length);
        debugPrint("      - Has header: " + hasHeader);

        if (hasHeader && length < WAVE_HEADER_SIZE) { //
            throw new InvalidFormatException(String.format("Length must be larger than header size. Was %d", length));
        }

        WaveFile waveFile;

        // Read the header if it exists
        if (hasHeader) {
            String formatId = streamReader.getUint8ArrayAsString(4);
            long formatSize = streamReader.getUint32();
            int formatTag = streamReader.getUint16();
            int numberChannels = streamReader.getUint16();
            long samplesPerSec = streamReader.getUint32();
            long bytesPerSec = streamReader.getUint32();
            int frameSize = streamReader.getUint16();
            int bitsPerSample = streamReader.getUint16();
            String dataId = streamReader.getUint8ArrayAsString(4);
            long dataSize = streamReader.getUint32();

            debugPrint(String.format("""
                        Read wave header:
                           - Format id: %s
                           - Format size: %d
                           - Format tag: %d
                           - Number of channels: %d
                           - Samples per sec: %d
                           - Bytes per sec: %d
                           - Frame size: %d
                           - Bits per sample: %d
                           - Data id: %s
                           - Data size: %d""",
                    formatId, formatSize, formatTag, numberChannels, samplesPerSec, bytesPerSec, frameSize, bitsPerSample, dataId, dataSize));

            waveFile = new WaveFile(
                    formatId,
                    formatSize,
                    formatTag,
                    numberChannels,
                    samplesPerSec,
                    bytesPerSec,
                    frameSize,
                    bitsPerSample,
                    dataId,
                    dataSize
            );

            long dataLength = length - WAVE_HEADER_SIZE;

            byte[] waveData = streamReader.getUint8ArrayAsBytes((int) dataLength);

            waveFile.setData(waveData);

        // Create default header
        } else {
            String formatId = "fmt ";
            long formatSize = 16;
            int formatTag = 1;
            int numberChannels = 1;
            long samplesPerSec = 11_025;
            long bytesPerSec = 11_025;
            int frameSize = 1;
            int bitsPerSample = 8;
            String dataId = "data";

            waveFile = new WaveFile(
                    formatId,
                    formatSize,
                    formatTag,
                    numberChannels,
                    samplesPerSec,
                    bytesPerSec,
                    frameSize,
                    bitsPerSample,
                    dataId,
                    length
            );

            byte[] waveData = streamReader.getUint8ArrayAsBytes((int) length);

            waveFile.setData(waveData);
        }

        return waveFile;
    }

    public static GameResource loadSoundWaveFile(String filename) throws IOException, InvalidFormatException {
        InputStream fileInputStream = Files.newInputStream(Paths.get(filename));
        byte[] bytes = fileInputStream.readAllBytes();
        ByteArrayReader byteArrayReader = new ByteArrayReader(bytes, LITTLE_ENDIAN);

        debugPrint("Loading sound from file: " + filename);

        GameResource soundGameResource = SoundLoader.loadSoundFromStream(byteArrayReader);

        debugPrint("Loaded sound");

        fileInputStream.close();

        return soundGameResource;
    }
}
