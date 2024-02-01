package org.appland.settlers.assets.decoders;

import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.resources.MidiFile;
import org.appland.settlers.assets.resources.MidiTrack;
import org.appland.settlers.assets.resources.XMidiFile;
import org.appland.settlers.assets.resources.XMidiTrack;
import org.appland.settlers.utils.ByteReader;
import org.appland.settlers.utils.StreamReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.nio.ByteOrder.BIG_ENDIAN;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

public class MidiDecoder {
    private static final long SIZE_NUMBER_TRACKS = 2; // uint 16

    private static boolean debug = false;

    private static void debugPrint(String debugString) {
        if (debug) {
            System.out.println(debugString);
        }
    }

    public static XMidiFile loadSoundXMidiFile(String filename) throws IOException, InvalidFormatException {
        StreamReader streamReader = new StreamReader(new FileInputStream(filename), LITTLE_ENDIAN);

        // Read header
        String headerId = streamReader.getUint8ArrayAsString(4);

        if (!headerId.equals("FORM")) {
            throw new InvalidFormatException("Header must match 'FORM'. Not " + headerId);
        }

        debugPrint("FORM");

        // Read headerSize as big endian
        streamReader.pushByteOrder(ByteOrder.BIG_ENDIAN);

        long headerSize = streamReader.getUint32();

        streamReader.popByteOrder();

        if ((headerSize & 1) != 0) {
            headerSize = headerSize + 1;
        }

        // Manage XMID and XDIR separately
        int numberTracks;
        String chunkId = streamReader.getUint8ArrayAsString(4);

        debugPrint(chunkId);

        if (chunkId.equals("XMID")) {
            numberTracks = 1;
        } else if (chunkId.equals("XDIR")) {
            chunkId = streamReader.getUint8ArrayAsString(4);

            if (!chunkId.equals("INFO")) {
                throw new InvalidFormatException("Must match 'INFO'. Not " + chunkId);
            }

            debugPrint(chunkId);

            // Read chunk length as big endian! -- test
            streamReader.pushByteOrder(ByteOrder.BIG_ENDIAN);

            long chunkLength = streamReader.getUint32();

            streamReader.popByteOrder();

            if ((chunkLength & 1) != 0) {
                chunkLength = chunkLength + 1;
            }

            if (chunkLength != SIZE_NUMBER_TRACKS) {
                throw new InvalidFormatException("Must match size of number tracks (2). Not " + chunkLength);
            }

            // Read the next field as big endian!
            numberTracks = streamReader.getUint16();
        } else {
            throw new InvalidFormatException("Must match 'XMID' or 'XDIR'. Not " + chunkId);
        }

        chunkId = streamReader.getUint8ArrayAsString(4);

        if (!chunkId.equals("CAT ")) {
            throw new InvalidFormatException("Must match 'CAT '. Not " + chunkId);
        }

        debugPrint(chunkId);

        // Ignore the following 4 bytes
        streamReader.skip(4);

        chunkId = streamReader.getUint8ArrayAsString(4);

        if (!chunkId.equals("XMID")) {
            throw new InvalidFormatException("Must match 'XMID'. Not " + chunkId);
        }

        debugPrint(chunkId);

        debugPrint(" - Read header");
        debugPrint("    - Header id: " + headerId);
        debugPrint("    - Header size: " + headerSize);
        debugPrint("    - Number tracks: " + numberTracks);

        // Read tracks
        List<XMidiTrack> trackList = new ArrayList<>();

        for (long i = 0; i < numberTracks; i++) {

            debugPrint(" - Reading track: " + i);

            XMidiTrack xMidiTrack = new XMidiTrack();

            chunkId = streamReader.getUint8ArrayAsString(4);

            if (!chunkId.equals("FORM")) {
                throw new InvalidFormatException("Must match 'FORM'. Not " + chunkId);
            }

            debugPrint(" - " + chunkId);

            long chunkLength = streamReader.getUint32();

            if ((chunkLength & 1) != 0) {
                chunkLength = chunkLength + 1;
            }

            chunkId = streamReader.getUint8ArrayAsString(4);

            if (!chunkId.equals("XMID")) {
                throw new InvalidFormatException("Must match 'XMID'. Not " + chunkId);
            }

            debugPrint(" - " + chunkId);

            chunkId = streamReader.getUint8ArrayAsString(4);

            debugPrint(" - " + chunkId);

            // Read timbres, if any
            if (chunkId.equals("TIMB")) {

                // Read chunk length and number of timbres as big endian! (chunk length added because of spec)
                streamReader.pushByteOrder(ByteOrder.BIG_ENDIAN);

                chunkLength = streamReader.getUint32();

                if ((chunkLength & 1) != 0) {
                    chunkLength = chunkLength + 1;
                }

                streamReader.popByteOrder();

                int numberTimbres = streamReader.getUint16();

                debugPrint(" Number of timbres: " + numberTimbres);
                debugPrint(" Chunk length: " + chunkLength);

                if (numberTimbres * 2L + 2 != chunkLength) {
                    throw new InvalidFormatException("Chunk length must match number timbres (" + numberTimbres + ") * 2 + 2. Not " + chunkLength);
                }

                // Read timbres
                for (int j = 0; j < numberTimbres; j++) {

                    debugPrint("Read timbre pair!");

                    short patch = streamReader.getUint8();
                    short bank = streamReader.getUint8();

                    debugPrint(" Patch: " + patch);
                    debugPrint(" Bank: " + bank);

                    xMidiTrack.addTimbre(patch, bank);
                }

                chunkId = streamReader.getUint8ArrayAsString(4);

                debugPrint(" - Next section: " + chunkId);
            }

            // Read EVTN section
            if (!chunkId.equals("EVNT")) {
                throw new InvalidFormatException("Must match 'EVNT'. Not " + chunkId);
            }

            debugPrint(" - Read EVTN section");

            // Read the length as big endian!
            streamReader.pushByteOrder(ByteOrder.BIG_ENDIAN);

            chunkLength = streamReader.getUint32();

            streamReader.popByteOrder();

            if ((chunkLength & 1) != 0) {
                chunkLength = chunkLength + 1;
            }

            debugPrint("   - Length is: " + chunkLength);

            byte[] trackData = new byte[(int) chunkLength];

            streamReader.read(trackData, 0, (int) chunkLength);

            xMidiTrack.setData(trackData);

            trackList.add(xMidiTrack);
        }

        return new XMidiFile(headerSize, numberTracks, trackList);
    }

    public static List<XMidiTrack> loadXMidiSoundFromStream(ByteReader streamReader, long length, boolean isDir) throws IOException, InvalidFormatException {

        String chunk0Header = streamReader.getUint8ArrayAsString(4);

        // The first chunk must be a form
        if (!chunk0Header.equals("FORM")) {
            throw new RuntimeException(format("Can't handle unknown header: %s", chunk0Header));
        }

        // Read the length of the first chunk
        long chunk0Length = streamReader.getUint32(BIG_ENDIAN);

        List<XMidiTrack> tracks = new ArrayList<>();
        long numberTracks = 1;

        String chunk1Header = streamReader.getUint8ArrayAsString(4);

        switch (chunk1Header) {
            case "XMID":
                numberTracks = 1;
                break;

            case "XDIR":
                String chunk2Header = streamReader.getUint8ArrayAsString(4);

                if (!chunk2Header.equals("INFO")) {
                    throw new RuntimeException(format("Must be INFO, %s is not allowed.", chunk2Header));
                }

                long chunk2Length = streamReader.getUint32(BIG_ENDIAN);

                if ((chunk2Length & 1) != 0) {
                    chunk2Length = chunk2Length + 1;
                }

                if (chunk2Length != SIZE_NUMBER_TRACKS) {
                    throw new RuntimeException(format("Chunk size is wrong! %d != %d", chunk2Length, SIZE_NUMBER_TRACKS));
                }

                numberTracks = streamReader.getUint16();

                String chunk3Header = streamReader.getUint8ArrayAsString(4);

                if (!chunk3Header.equals("CAT ")) {
                    throw new RuntimeException(format("Must be CAT, %s is not allowed.", chunk3Header));
                }

                streamReader.skip(4);

                String chunk4Header = streamReader.getUint8ArrayAsString(4);

                if (!chunk4Header.equals("XMID")) {
                    throw new RuntimeException(format("Must be CAT, %s is not allowed.", chunk4Header));
                }

                break;

            default:
                throw new RuntimeException(format("Can't handle header type %s at this place.", chunk1Header));
        }

        debugPrint(format("Number of tracks: %d", numberTracks));

        // Read tracks
        for (long i = 0; i < numberTracks; i++) {
            XMidiTrack xMidiTrack = new XMidiTrack();

            String trackChunkHeader0 = streamReader.getUint8ArrayAsString(4);

            if (!trackChunkHeader0.equals("FORM")) {
                throw new InvalidFormatException("Must match 'FORM'. Not " + trackChunkHeader0);
            }

            long trackChunkLength0 = streamReader.getUint32();

            if ((trackChunkLength0 & 1) != 0) {
                trackChunkLength0 = trackChunkLength0 + 1;
            }

            String trackChunkHeader1 = streamReader.getUint8ArrayAsString(4);

            if (!trackChunkHeader1.equals("XMID")) {
                throw new InvalidFormatException("Must match 'XMID'. Not " + trackChunkHeader1);
            }

            // XMID has no content, skip reading length and data

            String trackChunkHeader2 = streamReader.getUint8ArrayAsString(4);

            // Read timbres, if any
            if (trackChunkHeader2.equals("TIMB")) {
                long trackChunkLength2 = streamReader.getUint32(BIG_ENDIAN);

                if ((trackChunkLength2 & 1) != 0) {
                    trackChunkLength2 = trackChunkLength2 + 1;
                }

                int numberTimbres = streamReader.getUint16(LITTLE_ENDIAN);

                if (numberTimbres * 2L + 2 != trackChunkLength2) {
                    throw new InvalidFormatException("Chunk length must match number timbres (" + numberTimbres + ") * 2 + 2. Not " + trackChunkLength0);
                }

                // Read timbres
                for (int j = 0; j < numberTimbres; j++) {
                    short patch = streamReader.getUint8();
                    short bank = streamReader.getUint8();

                    xMidiTrack.addTimbre(patch, bank);
                }
            }

            String trackChunkHeader3 = streamReader.getUint8ArrayAsString(4);

            if (!trackChunkHeader3.equals("EVNT")) {
                throw new InvalidFormatException("Must match 'EVNT'. Not " + trackChunkHeader3);
            }

            long trackChunkLength3 = streamReader.getUint32(BIG_ENDIAN);

            if ((trackChunkLength3 & 1) != 0) {
                trackChunkLength3 = trackChunkLength3 + 1;
            }

            debugPrint(format("Reading EVNT data, number bytes: %d", trackChunkLength3));

            byte[] trackData = streamReader.getUint8ArrayAsBytes((int) trackChunkLength3);

            xMidiTrack.setData(trackData);

            tracks.add(xMidiTrack);
        }

        return tracks;
    }

    public static MidiFile loadSoundMidiFile(String filename) throws IOException, InvalidFormatException {
        StreamReader streamReader = new StreamReader(new FileInputStream(filename), LITTLE_ENDIAN);

        return loadSoundMidiFromStream(streamReader);
    }

    public static MidiFile loadSoundMidiFromStream(ByteReader streamReader) throws InvalidFormatException, IOException {

        // Read the file in big endian format
        streamReader.pushByteOrder(ByteOrder.BIG_ENDIAN);

        // Read the header
        String headerId = streamReader.getUint8ArrayAsString(4);
        long headerSize = streamReader.getUint32();
        int format = streamReader.getUint16();
        int numTracks = streamReader.getUint16(); // uint 16 -- read in big endian
        short ppqs = streamReader.getInt16();

        if (!headerId.equals("MThd")) {
            throw new InvalidFormatException("Header id must match 'MThd'. Not " + headerId);
        }

        debugPrint(" - Read header");
        debugPrint("    - Header id: " + headerId);
        debugPrint("    - Header size: " + headerSize);
        debugPrint("    - Format: " + format);
        debugPrint("    - Number tracks: " + numTracks);
        debugPrint("    - ppqs: " + ppqs);

        MidiFile midiFile = new MidiFile(headerSize, format, numTracks, ppqs);

        if (numTracks == 0 || numTracks > 256) {
            streamReader.popByteOrder();

            throw new InvalidFormatException("Invalid number of tracks(" + numTracks + "), must be between 0-256.");
        }

        // Read the tracks
        for (int i = 0; i < numTracks; i++) {

            debugPrint(" - Reading track: " + i);

            String chunkId = streamReader.getUint8ArrayAsString(4);

            long chunkLen = streamReader.getUint32(); // uint 32

            if (chunkId.equals("MTrk")) {

                byte[] midiTrackData = streamReader.getUint8ArrayAsBytes((int) chunkLen);

                midiFile.addTrack(new MidiTrack(midiTrackData));
            } else {
                streamReader.popByteOrder();

                throw new InvalidFormatException("Must start with MTrk. Not " + chunkId);
            }
        }

        // Reset to previous byte order
        streamReader.popByteOrder();

        return midiFile;
    }
}
