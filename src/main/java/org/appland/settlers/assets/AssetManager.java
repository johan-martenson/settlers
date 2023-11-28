package org.appland.settlers.assets;

import org.appland.settlers.utils.ByteArrayReader;
import org.appland.settlers.utils.ByteReader;
import org.appland.settlers.utils.StreamReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.nio.ByteOrder.BIG_ENDIAN;
import static java.nio.ByteOrder.LITTLE_ENDIAN;
import static org.appland.settlers.assets.BodyType.FAT;
import static org.appland.settlers.assets.GameResourceType.PLAYER_BITMAP_RESOURCE;
import static org.appland.settlers.assets.ResourceType.NONE;
import static org.appland.settlers.assets.ResourceType.SOUND;
import static org.appland.settlers.assets.SoundType.MIDI;
import static org.appland.settlers.assets.SoundType.WAVE;
import static org.appland.settlers.assets.SoundType.WAVE_WITHOUT_HEADER;
import static org.appland.settlers.assets.SoundType.XMIDI;
import static org.appland.settlers.assets.SoundType.XMID_DIR;
import static org.appland.settlers.utils.StreamReader.SIZE_OF_UINT32;

public class AssetManager {

    // char x 4 + uint 32 + char x 4 + char x 4 + uint 32 + uint 16 + uint 16 + uint 32 + uint 16 + char x 4 + uint 32
    private static final int WAVE_HEADER_SIZE = 304;

    private static final long SIZE_NUMBER_TRACKS = 2; // uint 16
    private static final long BMP_HEADER_SIZE = 40;
    private static final long NUM_BODY_IMAGES = 2 * 6 * 8;
    private static final int BOB_IMAGE_DATA_HEADER = 0x01F4;
    private static final int BOB_X_OFFSET = 16;
    private static final int BOB_SPRITE_WIDTH = 32;

    // int16 + uint 16 + uint 16 + uint 32
    private static final int TEXT_FILE_HEADER_SIZE = 2 + 2 + 2 + 4;

    private static final TextureFormat GLOBAL_TEXTURE_FORMAT = TextureFormat.BGRA;

    private TextureFormat wantedTextureFormat;
    boolean debug = false;

    public AssetManager() {
        wantedTextureFormat = TextureFormat.BGRA;
    }

    /**
     * File format (little endian)
     *  - header: unsigned 16bits
     *      - 0xFDE7 == Text file, German or English
     *      - 0x4E20 == Valid LST file
     *  - size of data: unsigned 32bits (integer) --- number of items?
     *  - unsigned 32 bits x size of data
     *      - is item used: signed 16 bits
     *          - 1: is used
     *      - resource type: signed 16bits
     *          - Sound: 1 (assume start at 1!)
     *          - Bitmap run length encoding: 2
     *          - Font: 3
     *          - Bitmap player: 4
     *          - Palette: 5
     *          - Bob: 6 ????
     *          - Bitmap shadow: 7
     *          - Map: 8
     *          - Text: 9
     *          - Raw: 10 ???
     *          - Map header: 11
     *          ... (extensions if any)
     *
     */
    public List<GameResource> loadLstFile(String filename, Palette defaultPalette) throws IOException, UnknownResourceTypeException, InvalidFormatException {
        List<GameResource> gameResources = new ArrayList<>();

        ByteArrayReader streamReader = new ByteArrayReader(
                Files.newInputStream(Paths.get(filename)).readAllBytes(),
                LITTLE_ENDIAN
        );

        int header = streamReader.getUint16();

        debugPrint(format("Header is: %s", Integer.toHexString(header)));

        switch (header) {

            /* Load text file */
            case 0xFDE7:
                List<String> strings = loadTextFromStream(streamReader);

                gameResources.add(new TextResource(strings));
            break;

            /* Load BOB file (?) */
            case 0x01F6:
                gameResources.add(loadBobFromStream(streamReader, defaultPalette));
            break;

            /* Load BOB file */
            case 0x4E20:

                debugPrint(" - Header is valid for LST bob file");

                long numberItems = streamReader.getUint32();

                debugPrint(" - Contains number of items: " + numberItems);

                /* Loop through and read each item */
                int hits = 0;

                for (long i = 0; i < numberItems; i++) {
                    short used = streamReader.getInt16();

                    /* Filter un-used items */
                    if (used != 1) {

                        debugPrint(" - Filter un-used item");

                        continue;
                    }

                    debugPrint(format("HIT: %d - %d", i, hits));
                    hits = hits + 1;

                    /* Find what type of resource it is */
                    int type = streamReader.getInt16();

                    ResourceType resourceType = ResourceType.fromInt(type);

                    debugPrint(" - Resource type number: " + type);
                    debugPrint(" - Resource type: " + resourceType);

                    /* Load the resource */
                    switch (resourceType) {

                        case SOUND:

                            debugPrint("Loading sound");

                            GameResource soundGameResource = loadSoundFromStream(streamReader);

                            debugPrint("Loaded sound");

                            gameResources.add(soundGameResource);

                            break;

                        case BITMAP_RLE:

                            debugPrint("Loading bitmap rle");

                            BitmapRLE bitmapRLE = loadBitmapRLEFromStream(streamReader, defaultPalette);

                            debugPrint("Loaded bitmap rle");

                            gameResources.add(new BitmapRLEResource(bitmapRLE));

                            break;

                        case FONT:

                            debugPrint("Loading font");

                            loadFontFromStream(streamReader);

                            debugPrint("Loaded font");

                            break;

                        case BITMAP_PLAYER:

                            debugPrint("Loading player bitmap");

                            PlayerBitmap playerBitmap = loadPlayerBitmapFromStream(streamReader, defaultPalette);

                            debugPrint("Loaded player bitmap");

                            gameResources.add(new PlayerBitmapResource(playerBitmap));

                            break;

                        case PALETTE:

                            debugPrint("Loading palette");

                            Palette palette = loadPaletteFromStream(streamReader, true);

                            debugPrint("Loaded palette");

                            gameResources.add(new PaletteResource(palette));

                            break;

                        case BOB:

                            debugPrint("Loading bob");

                            GameResource bobG = loadBobFromStream(streamReader, defaultPalette);

                            debugPrint("Loaded bob");

                            gameResources.add(bobG);

                            break;

                        case BITMAP_SHADOW:

                            debugPrint("Loading bitmap shadow");

                            Bitmap bitmap = loadBitmapShadowFromStream(streamReader, defaultPalette);

                            debugPrint("Loaded bitmap shadow");

                            gameResources.add(new BitmapResource(bitmap));

                            break;

                        case MAP:

                            debugPrint("Loading map");

                            loadMapFromStream(streamReader);

                            debugPrint("Loaded map");

                            break;

                        case RAW:

                            debugPrint("Loading raw bitmap");

                            loadRawBitmapFromStream(streamReader, defaultPalette);

                            debugPrint("Loaded raw bitmap");

                            break;

                        case PALETTE_ANIM:

                            debugPrint("Loading animated palette");

                            loadAnimatedPaletteFromStream(streamReader);

                            debugPrint("Loaded animated palette");

                            break;

                        case BITMAP:

                            debugPrint("Loading bitmap (not in original S2)");

                            BitmapRaw bitmapRaw = loadUncompressedBitmapFromStream(streamReader, defaultPalette);

                            debugPrint("Loaded bitmap");

                            gameResources.add(new BitmapRawResource(bitmapRaw));

                            break;

                        default:

                            System.out.println("UNKNOWN RESOURCE: " + type);
                            System.out.println(resourceType);

                            throw new UnknownResourceTypeException("Can't handle resource type " + resourceType);
                    }
                }
            break;

            default:
                throw new RuntimeException("Can't handle unknown header type: " + Integer.toHexString(header));
        }

        return gameResources;
    }

    private void debugPrint(String debugString) {
        if (debug) {
            System.out.println(debugString);
        }
    }

    public List<String> loadTextFile(String filename) throws IOException, InvalidFormatException {
        return loadTextFromStream(new StreamReader(new FileInputStream(filename), LITTLE_ENDIAN));
    }

    private List<String> loadTextFromStream(ByteReader streamReader) throws IOException, InvalidFormatException {

        List<String> textsLoaded = new ArrayList<>();

        byte[] header = streamReader.getUint8ArrayAsBytes(2);

        /* Handle straight text data */
        if (header[1] != (byte)0xE7 && header[0] != (byte)0xFD) {

            /* Get the remaining text data */
            byte[] remainingBytes = streamReader.getRemainingBytes();

            /* In this case, the first two bytes were part of the text and not a special header */
            byte[] fullTextAsBytes = new byte[remainingBytes.length + 2];

            fullTextAsBytes[0] = header[0];
            fullTextAsBytes[1] = header[1];

            System.arraycopy(remainingBytes, 0, fullTextAsBytes, 2, remainingBytes.length);

            String text = Utils.nullTerminatedByteArrayToString(fullTextAsBytes);

            textsLoaded.add(text);

        /* Load "archived" text */
        } else {
            int count = streamReader.getUint16();
            int unknown = streamReader.getUint16();
            long size = streamReader.getUint32();

            if (size == 0) {
                // size = fileSize - headerSize
            }

            if (size < (long) count * SIZE_OF_UINT32) {
                throw new InvalidFormatException("Size must be less that count * 4 (" + count * SIZE_OF_UINT32 + "). Not " + size);
            }

            List<Long> starts = streamReader.getUint32ArrayAsList(count);

            long lastStart = 0;

            /* Verify that each start offset is correct */
            for (int x = 0; x < count; x++) {
                if (starts.get(x) != 0) {

                    if (starts.get(x) < lastStart || starts.get(x) < count * SIZE_OF_UINT32) {
                        throw new InvalidFormatException("Start value is wrong. Cannot be " + starts.get(x));
                    }

                    lastStart = starts.get(x);
                    starts.set(x, starts.get(x) + TEXT_FILE_HEADER_SIZE);
                }
            }

            starts.add(size + TEXT_FILE_HEADER_SIZE);

            /* Read each text item */
            for (int x = 0; x < count; x++) {
                long itemPosition = starts.get(x);

                if (itemPosition != 0) {
                    long itemSize = 0;

                    for (int j = x + 1; j <= count; j++) {

                        if (starts.get(j) != 0) {
                            itemSize = starts.get(j) - itemPosition;

                            break;
                        }
                    }

                    streamReader.setPosition((int) itemPosition);

                    if (itemSize > 0) {
                        String textItem = streamReader.getUint8ArrayAsString((int) itemSize);

                        textsLoaded.add(textItem);
                    } else {
                        String textItem = streamReader.getRemainingBytesAsString();

                        textsLoaded.add(textItem);
                    }
                }
            }
        }

        return textsLoaded;
    }

    private BitmapRLE loadBitmapRLEFromStream(ByteReader streamReader, Palette palette) throws IOException, InvalidFormatException {

        /* Read header */
        short nx = streamReader.getInt16();
        short ny = streamReader.getInt16();
        long unknown1 = streamReader.getUint32();
        int width = streamReader.getUint16();
        int height = streamReader.getUint16();
        int unknown2 = streamReader.getUint16();
        long length = streamReader.getUint32();

        debugPrint(" - nx: " + nx);
        debugPrint(" - ny: " + ny);
        debugPrint(" - Unknown 1: " + unknown1);
        debugPrint(" - Width: " + width);
        debugPrint(" - Height: " + height);
        debugPrint(" - Unknown 2: " + unknown2);
        debugPrint(" - Length: " + length);

        // Load the image data
        byte[] data = streamReader.getUint8ArrayAsBytes((int)length);

        BitmapRLE bitmap = new BitmapRLE(width, height, data, palette, length, TextureFormat.BGRA);

        bitmap.setNx(nx);
        bitmap.setNy(ny);

        return bitmap;
    }

    private BitmapRaw loadUncompressedBitmapFromStream(ByteReader streamReader, Palette palette) throws IOException, InvalidFormatException {

        /* Read header */
        int unknown1 = streamReader.getUint16();
        long length = streamReader.getUint32();

        if (unknown1 != 1) {
            throw new InvalidFormatException(format("Must match '1'. Not: %d", unknown1));
        }

        byte[] data = streamReader.getUint8ArrayAsBytes((int)length);

        short nx = streamReader.getInt16();
        short ny = streamReader.getInt16();

        int width = streamReader.getUint16();
        int height = streamReader.getUint16();

        /* Verify that the length is correct */
        if (length != (long) width * height) {
            throw new InvalidFormatException(format("Length (%d) must equal width (%d) * height (%d)", length, width, height));
        }

        // Guess at source format
        TextureFormat sourceFormat;

        if (length == (long) width * height * 4) {
            sourceFormat = TextureFormat.BGRA;
        } else if (length == (long) width * height) {
            sourceFormat = TextureFormat.PALETTED;
        } else {
            throw new RuntimeException("Unknown source format!");
        }

        // Get wanted format
        TextureFormat wantedFormat = wantedTextureFormat;
        if (GLOBAL_TEXTURE_FORMAT == TextureFormat.ORIGINAL) {
            wantedFormat = TextureFormat.PALETTED;
        }

        debugPrint(" - Loading format: " + wantedFormat);

        if (length == 0) {
            throw new RuntimeException("No implementation for empty raw bitmap image");
        }

        // Ensure a palette is available if paletted format is requested
        if (palette == null && wantedFormat == TextureFormat.PALETTED) {
            throw new InvalidFormatException("Palette requested but palette is null.");
        }

        // Decide on bits-per-pixel - 1 for paletted or 4 for BGRA
        short bpp = 1;

        if (wantedFormat == TextureFormat.BGRA) {
            bpp = 4;
        }

        int rowSize = width * bpp;

        debugPrint(" - Height: " + height);
        debugPrint(" - Width: " + width);
        debugPrint(" - Row size: " + rowSize);
        debugPrint(" - Length: " + length);
        debugPrint(" - Width x height: " + width * height);
        debugPrint(" - Width x height x 4: " + width * height * 4);
        debugPrint(" - Data size: " + data.length);

        debugPrint(" - Source format: " + sourceFormat);
        debugPrint(" - Wanted format: " + wantedFormat);

        BitmapRaw bitmapRaw = new BitmapRaw(width, height, length, palette, wantedFormat);

        bitmapRaw.setNx(nx);
        bitmapRaw.setNy(ny);

        /* Return the file directly if no conversion is required */
        if (wantedFormat == TextureFormat.PALETTED) {
            bitmapRaw.setImageDataFromBuffer(data);
        }

        /* Store as BGRA if required */
        if (wantedFormat == TextureFormat.BGRA) {

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    bitmapRaw.setPixelByColorIndex(x, y, (short)(data[y * width + x] & 0xFF));
                }
            }
        }

        return bitmapRaw;
    }

    public GameResource loadSoundWaveFile(String filename) throws IOException, InvalidFormatException {
        InputStream fileInputStream = Files.newInputStream(Paths.get(filename));
        byte[] bytes = fileInputStream.readAllBytes();
        ByteArrayReader byteArrayReader = new ByteArrayReader(bytes, LITTLE_ENDIAN);

        debugPrint("Loading sound from file: " + filename);

        GameResource soundGameResource = loadSoundFromStream(byteArrayReader);

        debugPrint("Loaded sound");

        fileInputStream.close();

        return soundGameResource;
    }

    private void loadFontFromStream(ByteReader byteReader) {
        throw new RuntimeException("Support for load font is not implemented yet");
    }

    private void loadAnimatedPaletteFromStream(ByteReader byteReader) {
        throw new RuntimeException("Support for load animated palette is not implemented yet");
    }

    private BitmapRaw loadRawBitmapFromStream(ByteReader streamReader, Palette defaultPalette) throws IOException, InvalidFormatException {
        BitmapRaw bitmapRaw = loadUncompressedBitmapFromStream(streamReader, defaultPalette);

        return bitmapRaw;
    }

    private void loadMapFromStream(ByteReader byteReader) {
        throw new RuntimeException("Support for load map is not implemented yet");
    }

    private Bitmap loadBitmapShadowFromStream(ByteReader streamReader, Palette palette) throws IOException {

        /* Read header */
        short nx = streamReader.getInt16();
        short ny = streamReader.getInt16();
        long unknown1 = streamReader.getUint32();
        int width = streamReader.getUint16();
        int height = streamReader.getUint16();
        int unknown2 = streamReader.getUint16();
        long length = streamReader.getUint32();

        if (unknown1 != 0 || unknown2 != 1) {
            throw new RuntimeException(format("Invalid format. Unknown 1 must be 0, was: %d. Unknown 2 must be 1, was: %d.", unknown1, unknown2));
        }

        byte[] data = streamReader.getUint8ArrayAsBytes((int)length);

        short grayIndex = palette.getIndexForColor(255, 255, 255);

        debugPrint(" - Width: " + width);
        debugPrint(" - Height: " + height);
        debugPrint(" - Length: " + length);

        if (length == 0) {
            throw new RuntimeException("Not implemented support for empty images");
        }

        long position = height * 2L;
        Bitmap bitmap = new Bitmap(width, height, palette, TextureFormat.BGRA);

        bitmap.setNx(nx);
        bitmap.setNy(ny);

        for (int y = 0; y < height; y++) {
            int x = 0;

            while (x < width && position + 2 < data.length) {
                int count = data[(int)position++];

                for (int i = 0; i < count; i++, x++) {
                    bitmap.setPixelByColorIndex(x, y, grayIndex);
                }

                count = data[(int)position++];

                x = x + count;
            }

            if (position >= data.length) {
                throw new RuntimeException("Exceeded data size");
            }

            position = position + 1;
        }

        return bitmap;
    }

    public Bob loadBobFile(String filename, Palette defaultPalette) throws IOException, InvalidFormatException {
        InputStream fileInputStream = Files.newInputStream(Paths.get(filename));
        StreamReader streamReader = new StreamReader(fileInputStream, LITTLE_ENDIAN);

        short header = streamReader.getInt16();

        if (header == 0x01F6) {
            Bob bob = loadBobFromStream(streamReader, defaultPalette).getBob();

            return bob;
        }

        streamReader.close();

        return null;
    }

    private BobGameResource loadBobFromStream(ByteReader streamReader, Palette palette) throws IOException, InvalidFormatException {

        PlayerBitmap[] playerBitmaps = new PlayerBitmap[(int)NUM_BODY_IMAGES];

        /* Read the color block for the body */
        ColorBlock colorBlock = ColorBlock.readColorBlockFromStream(streamReader);

        /* Read body images */
        for (long i = 0; i < NUM_BODY_IMAGES; i++) {
            int bodyImageId = streamReader.getUint16();
            short bodyImageHeight = streamReader.getUint8();

            if (bodyImageId != BOB_IMAGE_DATA_HEADER) {
                throw new InvalidFormatException(format("Body image id must match '0x01F4'. Not: %s", Integer.toHexString(bodyImageId)));
            }

            int[] starts = streamReader.getUint16ArrayAsInts(bodyImageHeight);
            short ny = streamReader.getUint8();

            PlayerBitmap playerBitmap = PlayerBitmap.loadFrom(BOB_X_OFFSET, ny, BOB_SPRITE_WIDTH, colorBlock, starts, true, palette, TextureFormat.BGRA);

            playerBitmaps[(int)i] = playerBitmap;
        }

        /* Read color block for each direction */
        ColorBlock[] colorBlocks = new ColorBlock[6];
        for (int i = 0; i < 6; i++) {
            colorBlocks[i] = ColorBlock.readColorBlockFromStream(streamReader);
        }

        /* Read the overlay images */
        int numberOverlayImages = streamReader.getUint16();
        int[][] overlayImageStarts = new int[numberOverlayImages][];
        short[] overlayImageNy = new short[numberOverlayImages];

        PlayerBitmap[] allPlayerBitmaps = new PlayerBitmap[(int)(NUM_BODY_IMAGES + numberOverlayImages)];

        System.arraycopy(playerBitmaps, 0, allPlayerBitmaps, 0, playerBitmaps.length);

        for (int i = 0; i < numberOverlayImages; i++) {
            int overlayImageId = streamReader.getUint16();
            short overlayImageHeight = streamReader.getUint8();

            if (overlayImageId != BOB_IMAGE_DATA_HEADER) {
                throw new InvalidFormatException(format("Must match '0x01F4'. Not: %s ", Integer.toHexString(overlayImageId)));
            }

            overlayImageStarts[i] = streamReader.getUint16ArrayAsInts(overlayImageHeight);

            overlayImageNy[i] = streamReader.getUint8();
        }

        /* Follow links to create complete pictures */
        int numberLinks = streamReader.getUint16();
        int[] links = new int[numberLinks];

        // Default value for boolean array is false
        boolean[] loaded = new boolean[numberOverlayImages];

        for (long i = 0; i < numberLinks; i++) {
            links[(int)i] = streamReader.getUint16();
            int unknown = streamReader.getUint16();

            if (links[(int)i] >= numberOverlayImages) {
                throw new InvalidFormatException(format(
                        "Number of overlay images is: %d. Cannot have more than: %d",
                        numberOverlayImages,
                        links[(int)i]));
            }

            /* Skip the image if it's already loaded */
            if (loaded[links[(int)i]]) {
                continue;
            }

            PlayerBitmap playerBitmap = PlayerBitmap.loadFrom(
                    BOB_X_OFFSET,
                    overlayImageNy[links[(int)i]],
                    BOB_SPRITE_WIDTH,
                    colorBlocks[(int)i % 6], // raw
                    overlayImageStarts[links[(int)i]],
                    true,
                    palette,
                    TextureFormat.BGRA);

            allPlayerBitmaps[(int)NUM_BODY_IMAGES + links[(int)i]] = playerBitmap;

            loaded[links[(int)i]] = true;
        }

        for (int i = 0; i < links.length; i++) {
            links[i] = links[i] + (int)NUM_BODY_IMAGES;
        }

        Bob bob = new Bob(numberOverlayImages, links, allPlayerBitmaps);

        return new BobGameResource(bob);
    }

    GameResource loadSoundFromStream(ByteReader streamReader) throws IOException, InvalidFormatException {
        streamReader.pushByteOrder(LITTLE_ENDIAN);

        long length = streamReader.getUint32();

        SoundType soundType = getSoundTypeFromByteReader(streamReader);

        debugPrint(format("Got sound type: %s", soundType.name()));


        if (soundType == MIDI) {
            return new MidiGameResource(loadSoundMidiFromStream(streamReader));
        } else if (soundType == WAVE) {
            return new WaveGameResource(loadWaveSoundFromStream(streamReader, length, true));
        } else if (soundType == XMIDI || soundType == XMID_DIR) {
            return new XMidiGameResource(loadXMidiSoundFromStream(streamReader, length, soundType == XMID_DIR));
        } else if (soundType == WAVE_WITHOUT_HEADER) {
            return new WaveGameResource(loadWaveSoundFromStream(streamReader, length, false));
        } else {
            throw new RuntimeException("Support for 'other sound' is not implemented yet");
        }
    }

    private SoundType getSoundTypeFromByteReader(ByteReader streamReader) throws IOException {
        int position = streamReader.getPosition();

        String header = streamReader.getUint8ArrayAsString(4);

        debugPrint(Utils.bytesToHex(header.getBytes()));

        SoundType soundType = null;

        switch (header) {
            case "FORM":
            case "RIFF":

                long length = streamReader.getUint32();
                String subHeader = streamReader.getUint8ArrayAsString(4);

                switch (subHeader) {
                    case "XMID":
                    case "XDIR":
                        soundType = XMIDI;

                    break;

                    case "WAVE":
                        soundType = WAVE;

                    break;
                }

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

    public XMidiFile loadSoundXMidiFile(String filename) throws IOException, InvalidFormatException {
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

        XMidiFile xMidiFile = new XMidiFile(headerSize, numberTracks, trackList);

        return xMidiFile;
    }

    private List<XMidiTrack> loadXMidiSoundFromStream(ByteReader streamReader, long length, boolean isDir) throws IOException, InvalidFormatException {

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

    private WaveFile loadWaveSoundFromStream(ByteReader streamReader, long length, boolean hasHeader) throws InvalidFormatException, IOException {

        debugPrint("   - Loading wave sound");
        debugPrint("      - Length: " + length);
        debugPrint("      - Has header: " + hasHeader);

        if (hasHeader && length < WAVE_HEADER_SIZE) { //
            throw new InvalidFormatException("Length must be larger than header size. Was " + length);
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

            debugPrint("      - Read wave header");
            debugPrint("         - Format id: " + formatId);
            debugPrint("         - Format size: " + formatSize);
            debugPrint("         - Format tag: " + formatTag);
            debugPrint("         - Number of channels: " + numberChannels);
            debugPrint("         - Samples per sec: " + samplesPerSec);
            debugPrint("         - Bytes per sec: " + bytesPerSec);
            debugPrint("         - Frame size: " + frameSize);
            debugPrint("         - Bits per sample: " + bitsPerSample);
            debugPrint("         - Data id: " + dataId);
            debugPrint("         - Data size: " + dataSize);

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

            length = length - WAVE_HEADER_SIZE;

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
        }

        byte[] waveData = streamReader.getUint8ArrayAsBytes((int) length);

        waveFile.setData(waveData);

        return waveFile;
    }

    public MidiFile loadSoundMidiFile(String filename) throws IOException, InvalidFormatException {
        StreamReader streamReader = new StreamReader(new FileInputStream(filename), LITTLE_ENDIAN);

        MidiFile midiFile = loadSoundMidiFromStream(streamReader);

        return midiFile;
    }

    public MidiFile loadSoundMidiFromStream(ByteReader streamReader) throws InvalidFormatException, IOException {

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

    private static Palette loadPaletteFromStream(ByteReader streamReader, boolean skip) throws IOException, InvalidFormatException {

        if (skip) {
            int numberColors = streamReader.getUint16();

            if (numberColors != 256) {
                throw new InvalidFormatException("Invalid number of colors (" + numberColors + "). Must be 256.");
            }
        }

        byte[] colors = streamReader.getUint8ArrayAsBytes(256 * 3); // uint 8 x 3 - rgb

        int transparentIndex = 270; // no transparency for now

        return new Palette(colors);
    }

    private PlayerBitmap loadPlayerBitmapFromStream(ByteReader streamReader, Palette palette) throws IOException, InvalidFormatException {

        /* Read header */
        int nx = streamReader.getInt16();
        int ny = streamReader.getInt16();

        long unknown1 = streamReader.getUint32();

        int width = streamReader.getUint16();
        int height = streamReader.getUint16();

        int unknown2 = streamReader.getUint16();

        long length = streamReader.getUint32();

        /* Verify that length is not too short */
        if (length <= (height * 2L)) {
            throw new InvalidFormatException("Length (" + length + ") must be larger than height (" + height + ") * 2");
        }

        debugPrint("    - Width: " + width);
        debugPrint("    - Height: " + height);
        debugPrint("    - Length: " + length);
        debugPrint("    - Height * 2: " + height * 2);
        debugPrint("    - Length - height * 2: " + (int) (length - height * 2));

        int[] starts = streamReader.getUint16ArrayAsInts(height);
        byte[] imageData = streamReader.getUint8ArrayAsBytes((int)(length - height * 2));

        debugPrint("    - Number starts: " + starts.length);
        debugPrint("    - Size of image data: " + imageData.length);
        debugPrint("    - Image dimensions are: " + width + "x" + height);
        debugPrint("    - Multiplied: " + width * height);

        PlayerBitmap playerBitmap = new PlayerBitmap(width, height, palette, TextureFormat.BGRA);

        playerBitmap.setNx(nx);
        playerBitmap.setNy(ny);
        playerBitmap.setLength(length);

        debugPrint(" Loading from image data");

        playerBitmap.loadImageFromData(imageData, starts, false);

        debugPrint(" Loaded from image data");

        return playerBitmap;
    }

    public Palette loadPaletteFromFile(String filename) throws IOException {
        StreamReader streamReader = new StreamReader(new FileInputStream(filename), LITTLE_ENDIAN);

        byte[] colors = streamReader.getUint8ArrayAsBytes(256 * 3); // uint 8 x 3, rgb

        Palette palette =  new Palette(colors);

        palette.setDefaultTransparentIdx();

        int lastSeparator = filename.lastIndexOf("/");

        palette.setName(filename.substring(lastSeparator + 1) + "(" + 0 + ")");

        return palette;
    }

    /**
     * Reads a bitmap file
     *
     * File specification:
     *    Name             Type       Times
     *  - Header id        uint 8     4
     *  - File size        uint 32    1
     *  - Reserved         uint 32    1
     *  - Pixel offset     uint 32    1
     *  - Header size      uint 32    1
     *  - Width            int 32     1
     *  - Height           int 32     1
     *  - Planes           int 16     1
     *  - Bits per pixel   int 16     1
     *  - Compression      uint 32    1
     *  - Size             uint 32    1
     *  - x pixels per m   int 32     1
     *  - y pixels per m   int 32     1
     *  - Color used       int 32     1
     *  - Color imp        int 32     1
     *
     *  Palette section (only if bits per pixel is 8)
     *  - Palette data     uint 8     colors used x 4
     *
     *  Image data section
     *  Alt 1 - paletted data
     *  - Image data       uint 8     width x height
     *
     *  Alt 2 - BGR format
     *  - Image data       uint 8     width x height x 3
     *
     *
     * @param filename
     * @param defaultPalette
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    public BitmapFile loadBitmapFile(String filename, Palette defaultPalette) throws IOException, InvalidFormatException {
        StreamReader streamReader = new StreamReader(new FileInputStream(filename), LITTLE_ENDIAN);

        String headerId = streamReader.getUint8ArrayAsString(2);
        long fileSize = streamReader.getUint32();
        long reserved = streamReader.getUint32();
        long pixelOffset = streamReader.getUint32();

        debugPrint(" - BMP header:");
        debugPrint("    - File size: " + fileSize);
        debugPrint("    - Reserved: " + reserved);
        debugPrint("    - Pixel offset: " + pixelOffset);

        if (!headerId.equals("BM")) {
            throw new InvalidFormatException("Must match 'BM'. Not " + headerId);
        }

        long headerSize = streamReader.getUint32();
        int width = streamReader.getInt32();
        int height = streamReader.getInt32();
        short planes = streamReader.getInt16();
        short bitsPerPixel = streamReader.getInt16();
        long compression = streamReader.getUint32();
        long size = streamReader.getUint32();
        int xPixelsPerMeter = streamReader.getInt32();
        int yPixelsPerMeter = streamReader.getInt32();
        long numberColorsUsed = streamReader.getUint32(); // int 32 in file
        int numberImportantColors = streamReader.getInt32();

        debugPrint(" - More header info");
        debugPrint("    - Header size: " + headerSize);
        debugPrint("    - Width: " + width);
        debugPrint("    - Height: " + height);
        debugPrint("    - Planes: " + planes);
        debugPrint("    - Bits per pixel: " + bitsPerPixel);
        debugPrint("    - Compression: " + compression);
        debugPrint("    - Size: " + size);
        debugPrint("    - X Pixels per M: " + xPixelsPerMeter);
        debugPrint("    - Y Pixels per M: " + yPixelsPerMeter);
        debugPrint("    - Color used: " + numberColorsUsed);
        debugPrint("    - Color imp: " + numberImportantColors);

        if (headerSize != BMP_HEADER_SIZE) {
            throw new InvalidFormatException("Header size must match " + BMP_HEADER_SIZE + ". Not " + headerSize);
        }

        boolean bottomUp = false;

        if (height >= 0) {
            bottomUp = true;
        } else {
            height = -height;
        }

        if (planes != 1) {
            throw new InvalidFormatException("Can only handle BMP with 1 plane. Not " + planes);
        }

        if (compression != 0) {
            throw new InvalidFormatException("Compression parameter must be 0. Not " + compression);
        }

        if (numberColorsUsed == 0) {
            numberColorsUsed = 1L << bitsPerPixel; // bmih.clrused = 1u << uint32_t(bmih.bpp); // 2^n

            debugPrint(" ---- 2^" + bitsPerPixel + " = " + numberColorsUsed);
        }

        debugPrint("COLORS USED: " + numberColorsUsed);

        Palette palette = defaultPalette;

        if (bitsPerPixel == 8) {

            debugPrint("    - Loading palette");


            long numberColorsUsedAdjusted = Math.min(numberColorsUsed, 256);

            /* Read the palette */
            byte[] myPaletteColors = new byte[256 * 3];

            /* Read color by color in BGRA mode */
            for (int i = 0; i < numberColorsUsedAdjusted; i++) {
                byte blue = streamReader.getInt8();
                byte green = streamReader.getInt8();
                byte red = streamReader.getInt8();
                byte alphaNotUsed = streamReader.getInt8();

                myPaletteColors[i * 3] = red;
                myPaletteColors[i * 3 + 1] = green;
                myPaletteColors[i * 3 + 2] = blue;
            }

            palette = new Palette(myPaletteColors);

            palette.setDefaultTransparentIdx();
        }

        int sourceBytesPerPixel = bitsPerPixel / 8;

        long rowSize = (int)Math.ceil((bitsPerPixel * width) / 32.0) * 4L;

        debugPrint(" ---- Calculated row size: " + rowSize);
        debugPrint(" ---- Width * bytes per pixel: " + width * sourceBytesPerPixel);
        debugPrint("    - Bytes per pixel: " + sourceBytesPerPixel);

        TextureFormat sourceFormat = TextureFormat.PALETTED;

        if (sourceBytesPerPixel == 4) {
            sourceFormat = TextureFormat.BGRA;
        } else if (sourceBytesPerPixel == 3) {
            sourceFormat = TextureFormat.BGR;
        }

        debugPrint("WANTED TEXTURE FORMAT: " + wantedTextureFormat);

        BitmapFile bitmap = new BitmapFile(width, height, palette, wantedTextureFormat);

        bitmap.setFileSize(fileSize);
        bitmap.setReserved(reserved);
        bitmap.setPixelOffset(pixelOffset);
        bitmap.setHeaderSize(headerSize);
        bitmap.setPlanes(planes);
        bitmap.setSourceBitsPerPixel(bitsPerPixel);
        bitmap.setCompression(compression);
        bitmap.setSize(size);
        bitmap.setXPixelsPerM(xPixelsPerMeter);
        bitmap.setYPixelsPerM(yPixelsPerMeter);
        bitmap.setColorUsed((int)numberColorsUsed);
        bitmap.setColorImp(numberImportantColors);

        int targetBytesPerPixel = 1;

        if (wantedTextureFormat == TextureFormat.BGRA) {
            targetBytesPerPixel = 4;
        }

        byte[] tmpBuffer;

        streamReader.setPosition(pixelOffset);

        debugPrint("    - Read bottom up: " + bottomUp);

        if (bottomUp) {
            for (int y = height - 1; y >= 0; y--) {

                /* Read the source as paletted */
                if (sourceBytesPerPixel == 1) {

                    /* Copy one row */
                    for (int x = 0; x < width; x++) {
                        bitmap.setPixelByColorIndex(x, y, streamReader.getUint8());
                    }

                /* Read the source as BGR */
                } else {

                    tmpBuffer = streamReader.getUint8ArrayAsBytes(width * sourceBytesPerPixel);

                    for (int x = 0; x < width; x++) { // ++x
                        byte blue = tmpBuffer[x * 3];
                        byte green = tmpBuffer[x * 3 + 1];
                        byte red = tmpBuffer[x * 3 + 2];

                        byte transparency = (byte)0xFF;

                        if (palette.isTransparentColor(red, blue, green)) {
                            transparency = 0;
                        }

                        bitmap.setPixelValue(x, y, red, green, blue, transparency);
                    }
                }

                /* Ignore extra spacing at end of each line to match up with 4 byte blocks */
                if (width * sourceBytesPerPixel % 4 > 0) {
                    streamReader.skip(4 - (width * sourceBytesPerPixel % 4));
                }
            }
        } else {

            for (int y = 0; y < height; y++) {

                /* Read source paletted */
                if (sourceBytesPerPixel == 1) {
                    for (int x = 0; x < width; x++) {
                        bitmap.setPixelByColorIndex(x, y, streamReader.getUint8());
                    }

                /* Read source as BGR */
                } else {

                    tmpBuffer = streamReader.getUint8ArrayAsBytes(width * sourceBytesPerPixel);

                    for (int x = 0; x < width; x++) {
                        byte blue = tmpBuffer[x * 3];
                        byte green = tmpBuffer[x * 3 + 1];
                        byte red = tmpBuffer[x * 3 + 2];

                        byte transparency = (byte)0xFF;

                        if (palette.isTransparentColor(red, blue, green)) {
                            transparency = 0;
                        }

                        bitmap.setPixelValue(x, y, red, green, blue, transparency);
                    }
                }

                /* Ignore extra spacing at end of each line to match up with 4 byte blocks */
                if (width * sourceBytesPerPixel % 4 > 0) {
                    streamReader.skip(4 - (width * sourceBytesPerPixel % 4));
                }
            }
        }

        return bitmap;
    }

    public void setWantedTextureFormat(TextureFormat format) {
        wantedTextureFormat = format;
    }

    public GameResource loadLBMFile(String filename, Palette defaultPalette) throws IOException, InvalidFormatException {
        StreamReader streamReader = new StreamReader(new FileInputStream(filename), ByteOrder.BIG_ENDIAN);

        String header = streamReader.getUint8ArrayAsString(4);
        long length = streamReader.getUint32();
        String pbm = streamReader.getUint8ArrayAsString(4);

        /* Check that the header is valid */
        if (!header.equals("FORM") || !pbm.equals("PBM ")) {
            throw new InvalidFormatException("Must match 'FORM' or 'PBM'. Not " + header);
        }

        int width = 0;
        int height = 0;
        int transClr = 0;
        short compression = 0;
        short mask = 0;
        boolean headerRead = false;

        Bitmap bitmap = null;
        Palette palette = null;

        /* Read sections of the file until it's done */
        List<PaletteAnim> paletteAnimList = new ArrayList<>();

        while (!streamReader.isEof()) {

            /* Read next chunk */
            String chunkId = streamReader.getUint8ArrayAsString(4);
            long chunkLength = streamReader.getUint32();

            if ((chunkLength & 1) == 1) {
                chunkLength = chunkLength + 1;
            }

            /* Read bitmap header */
            if (chunkId.equals("BMHD")) {

                if (headerRead) { // || !bitmap
                    throw new InvalidFormatException("Must read header");
                }

                width = streamReader.getUint16();
                height = streamReader.getUint16();
                int xOrig = streamReader.getUint16();
                int yOrig = streamReader.getUint16();
                short numPlanes = streamReader.getUint8();
                mask = streamReader.getUint8();
                compression = streamReader.getUint8();
                short pad = streamReader.getUint8();
                transClr = streamReader.getUint16();
                short xAspect = streamReader.getUint8();
                short yAspect = streamReader.getUint8();
                int pageW = streamReader.getUint16();
                int pageH = streamReader.getUint16();

                if (numPlanes != 8 || (mask != 0 && mask != 2)) {
                    throw new InvalidFormatException("Planes and maks combination is not valid. Number planes: " + numPlanes + ", mask: " + mask);
                }

                if (compression > 1) {
                    throw new InvalidFormatException("Compression must not be greater than 1. Is " + compression);
                }

                bitmap = new Bitmap(width, height, defaultPalette, wantedTextureFormat);

                headerRead = true;
            } else if (chunkId.equals("CRNG")) {

                PaletteAnim paletteAnim = PaletteAnim.load(streamReader);

                paletteAnimList.add(paletteAnim);

            } else if (chunkId.equals("CMAP")) {

                if (chunkLength != 256 * 3) {
                    throw new InvalidFormatException("Chunk length must be 256 * 3. Not " + chunkLength);
                }

                palette = Palette.load(streamReader, false);

                if (mask == 2 && transClr < 256) {
                    palette.setTransparentIndex(transClr);
                } else {
                    palette.setTransparentIndex(0);
                }
            } else if (chunkId.equals("BODY")) {

                if(!headerRead) {
                    throw new InvalidFormatException("Header is not read.");
                }

                // if bitmap == null, invalid format palette missing

                if (compression == 0) {
                    if (chunkLength != (long) width * height) {
                        throw new InvalidFormatException("Length must match width x height (" + width + "x" + height + ". Not " + chunkLength);
                    }

                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            short color = streamReader.getUint8();

                            bitmap.setPixelByColorIndex(x, y, color);
                        }
                    }
                } else {
                    int x = 0;
                    int y = 0;

                    while (chunkLength > 0) {
                        byte compressionType = streamReader.getInt8();

                        chunkLength = chunkLength - 1;

                        if (chunkLength == 0) {
                            continue;
                        }

                        if (compressionType > 0) {
                            int count = 1 + compressionType;

                            for (short j = 0; j < count; j++) {
                                short color = streamReader.getUint8();

                                chunkLength = chunkLength - 1;

                                bitmap.setPixelByColorIndex(x++, y, color);

                                if (x >= width) {
                                    y = y + 1;
                                    x = 0;
                                }
                            }
                        } else {
                            int count = 1 - compressionType;

                            short color = streamReader.getUint8();

                            chunkLength = chunkLength - 1;

                            for (int j = 0; j < count; j++) {
                                bitmap.setPixelByColorIndex(x++, y, color);

                                if (x >= width) {
                                    y = y + 1;
                                    x = 0;
                                }
                            }
                        }
                    }
                }
            } else {
                streamReader.skip((int)chunkLength);
            }
        }

        LBMFile lbmFile = new LBMFile(bitmap);

        lbmFile.setAnimPalettes(paletteAnimList);
        lbmFile.setLength(length);

        if (palette != null) {
            lbmFile.setPalette(palette);
        }

        return new LBMGameResource(lbmFile);
    }

    public List<GameResource> loadBbmFile(String filename) throws IOException, InvalidFormatException {
        StreamReader streamReader = new StreamReader(new FileInputStream(filename), ByteOrder.BIG_ENDIAN);

        String header = streamReader.getUint8ArrayAsString(4);
        
        if (!header.equals("FORM")) {
            throw new InvalidFormatException("Must match 'FORM'. Not " + header);
        }

        long length = streamReader.getUint32();
        
        String pbm = streamReader.getUint8ArrayAsString(4);
        
        if (!pbm.equals("PBM ")) {
            throw new InvalidFormatException("Must match 'PBM '. Not " + pbm);
        }
        
        List<GameResource> paletteGameResources = new ArrayList<>();
        long i = 0;
        
        while (!streamReader.isEof()) {
            String chunkId = streamReader.getUint8ArrayAsString(4);
            
            if (chunkId.equals("CMAP")) {
                length = streamReader.getUint32();
                
                if ((length & 1)  == 1) {
                    length = length + 1;
                }

                if (length != 256 * 3) {
                    throw new InvalidFormatException("Length must match 256 x 3. Not " + length);
                }

                Palette palette = Palette.load(streamReader, false);

                int lastSeparator = filename.lastIndexOf("/");

                palette.setName(filename.substring(lastSeparator + 1) + "(" + i + ")");
                
                paletteGameResources.add(new PaletteResource(palette));

                i = i + 1;
            }
        }

        return paletteGameResources;
    }

    public List<GameResource> loadDatFile(String filename, Palette defaultPalette) throws IOException, UnknownResourceTypeException, InvalidFormatException {

        String baseFile = filename.substring(0, filename.length() - 4);

        debugPrint(baseFile);

        String datFilename = baseFile + ".DAT";
        String idxFilename = baseFile + ".IDX";

        if (!Files.exists(Paths.get(datFilename)) || !Files.exists(Paths.get(idxFilename))) {
            //throw new InvalidFormatException("Both DAT and IDX file must exist. Not only " + filename);
            StreamReader datReader = new StreamReader(new FileInputStream(datFilename), LITTLE_ENDIAN);
            short datBobtype = datReader.getInt16();

            //ResourceType resourceType = ResourceType.fromInt(datBobtype);
            ResourceType resourceType = SOUND;

            GameResource gameResource = loadType(resourceType, datReader, defaultPalette);

            List<GameResource> result = new ArrayList<>();

            result.add(gameResource);

            return result;
        }

        StreamReader datReader = new StreamReader(new FileInputStream(datFilename), LITTLE_ENDIAN);
        StreamReader idxReader = new StreamReader(new FileInputStream(idxFilename), LITTLE_ENDIAN);

        long count = idxReader.getUint32();

        List<GameResource> gameResourceList = new ArrayList<>();

        for (long i = 0; i < count; i++) {
            String name = idxReader.getUint8ArrayAsString(16);
            long offset = idxReader.getUint32();
            String unknown = idxReader.getUint8ArrayAsString(6);
            int idxBobType = idxReader.getInt16();

            datReader.setPosition(offset);

            short datBobtype = datReader.getInt16();

            if (idxBobType != datBobtype) {

                debugPrint("Seems like an invalid item??");
                debugPrint(idxFilename);
                debugPrint(datFilename);
                debugPrint("" + i);

                continue;
            }

            ResourceType resourceType = ResourceType.fromInt(datBobtype);

            GameResource gameResource = loadType(resourceType, datReader, defaultPalette);

            gameResourceList.add(gameResource);
        }

        return gameResourceList;
    }

    private GameResource loadType(ResourceType resourceType, StreamReader datReader, Palette defaultPalette) throws IOException, UnknownResourceTypeException, InvalidFormatException {

        switch (resourceType) {
            case FONT:

                datReader.pushByteOrder(LITTLE_ENDIAN);

                short dx = datReader.getUint8();
                short dy = datReader.getUint8();

                boolean isUnicode = dx == 255 && dy == 255;

                long numberChars;

                if (isUnicode) {
                    numberChars = datReader.getUint32();
                    dx = datReader.getUint8();
                    dy = datReader.getUint8();
                } else {
                    numberChars = 256;
                }

                /* Read the letters */
                Map<String, PlayerBitmap> letterMap = new HashMap<>();
                for (long i = 32; i < numberChars; ++i) {
                    short bobType = datReader.getInt16();

                    ResourceType resourceType1 = ResourceType.fromInt(bobType);

                    if (resourceType1 == NONE) {
                        continue;
                    }

                    GameResource loadedGameResource = loadType(resourceType1, datReader, defaultPalette);

                    if (loadedGameResource.getType() != PLAYER_BITMAP_RESOURCE) {
                        throw new InvalidFormatException("Can only read player bitmap for fonts. Not " + loadedGameResource.getClass());
                    }

                    PlayerBitmapResource playerBitmapResource = (PlayerBitmapResource) loadedGameResource;

                    letterMap.put("U+" + Integer.toHexString((int)i), playerBitmapResource.getBitmap());
                }

                datReader.popByteOrder();

                return new FontGameResource(letterMap);

            case BITMAP_PLAYER:

                PlayerBitmap playerBitmap = loadPlayerBitmapFromStream(datReader, defaultPalette);

                debugPrint("Loaded player bitmap");

                return new PlayerBitmapResource(playerBitmap);

            case BITMAP:

                BitmapRaw bitmapRaw = loadRawBitmapFromStream(datReader, defaultPalette);

                return new BitmapRawResource(bitmapRaw);

            case BITMAP_RLE:

                BitmapRLE bitmapRLE = loadBitmapRLEFromStream(datReader, defaultPalette);

                return new BitmapRLEResource(bitmapRLE);

            case SOUND:
                WaveFile waveFile = loadWaveSoundFromStream(datReader, 5, false);

                return new WaveGameResource(waveFile);

            default:
                throw new RuntimeException("Not implemented yet. " + resourceType);
        }
    }

    public Map<JobType, RenderedWorker> renderWorkerImages(Bob jobsBob, Map<JobType, WorkerDetails> workerDetailsMap) {
        Map<JobType, RenderedWorker> workerImages = new EnumMap<>(JobType.class);

        /* Go through each job type */
        for (JobType job : JobType.values()) {
            RenderedWorker worker = new RenderedWorker(job);

            for (Nation nation : Nation.values()) {
                for (CompassDirection compassDirection : CompassDirection.values()) {

                    for (int animationStep = 0; animationStep < 8; animationStep++) {
                        boolean fat;
                        int id;

                        WorkerDetails workerDetails = workerDetailsMap.get(job);

                        id = workerDetails.getBobId(nation);
                        fat = workerDetails.getBodyType() == FAT;

                        StackedBitmaps bitmaps = new StackedBitmaps();

                        bitmaps.add(jobsBob.getBody(fat, compassDirection.ordinal(), animationStep));
                        bitmaps.add(jobsBob.getOverlay(id, fat, compassDirection.ordinal(), animationStep));

                        worker.setAnimationStep(nation, compassDirection, bitmaps, animationStep);

                        workerImages.put(job, worker);
                    }
                }
            }
        }

        /* Also handle fat carrier */
//        for (int direction = 0; direction < Direction.values().length; direction++) {
//            for (int animationIndex = 0; animationIndex < 8; animationIndex++) {
//                boolean fat = false;
//                long id;
//
//                fat = true;
//                id = 0;
//
//                Sprite jobSprite = new Sprite();
//
//                jobSprite.add(bobJobs.getBody(fat, imgDir, animationIndex));
//                jobSprite.add(bobJobs.getOverlay(id, fat, imgDir, animationIndex));
//            }
//        }

        return workerImages;
    }
}
