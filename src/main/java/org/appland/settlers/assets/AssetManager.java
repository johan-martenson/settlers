package org.appland.settlers.assets;

import org.appland.settlers.utils.StreamReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.appland.settlers.assets.BodyType.FAT;
import static org.appland.settlers.assets.GameResourceType.PLAYER_BITMAP_RESOURCE;
import static org.appland.settlers.assets.ResourceType.NONE;
import static org.appland.settlers.assets.SoundType.MIDI;
import static org.appland.settlers.assets.SoundType.MP3;
import static org.appland.settlers.assets.SoundType.OGG;
import static org.appland.settlers.assets.SoundType.WAVE;
import static org.appland.settlers.assets.SoundType.WAVE_WITHOUT_HEADER;
import static org.appland.settlers.assets.SoundType.XMIDI;
import static org.appland.settlers.assets.SoundType.XMID_DIR;
import static org.appland.settlers.utils.StreamReader.SIZE_OF_UINT32;

public class AssetManager {

    // char * 4 + uint 32 + uint 16 + uint 16 + int 16
    private static final int MIDI_HEADER_LENGTH = 8 * 4 + 32 + 16 + 16 + 16;

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
    private static final Object JOB_FILE = "/media/johan/bb340575-f0b7-4908-abfe-a4a296dc355c1/Spel/BLUEBYTE/SETTLER2/DATA/BOBS/JOBS.BOB";

    private final TextureFormat globalTextureFormat = TextureFormat.BGRA;

    private TextureFormat wantedTextureFormat;
    private boolean debug = false;

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
    public List<GameResource> loadLstFile(String filename, Palette defaultPalette) throws IOException, InvalidHeaderException, UnknownResourceTypeException, InvalidFormatException {
        List<GameResource> gameResources = new ArrayList<>();

        FileInputStream fileInputStream = new FileInputStream(filename);
        StreamReader streamReader = new StreamReader(fileInputStream, ByteOrder.LITTLE_ENDIAN);

        byte[] header = streamReader.getUint8ArrayAsBytes(2);

        /* Load txt file if needed */
        if (header[1] == (byte)0xFD && header[0] == (byte)0xE7) {
            List<String> strings = loadTextFromStream(streamReader);

            gameResources.add(new TextResource(strings));
        }

        /* Try to load as bob file if needed */
        if (header[1] == 0x01 && Unsigned.getUnsignedByte(ByteBuffer.wrap(header)) == 0xF6) {
            gameResources.add(loadBobFromStream(fileInputStream, defaultPalette));

            return gameResources;
        }

        /* Verify that this is really a bob file */
        if (header[1] != 0x4e || header[0] != 0x20) {
            throw new InvalidHeaderException("Header must be 0x4E20. " + Utils.getHex(header) + " is not valid.");
        }

        if (debug) {
            System.out.println(" - Header is valid for LST bob file");
        }

        long numberItems = streamReader.getUint32();

        if (debug) {
            System.out.println(" - Contains number of items: " + numberItems);
        }

        /* Loop through and read each item */
        for (long i = 0; i < numberItems; i++) {
            short used = streamReader.getInt16();

            /* Filter un-used items */
            if (used != 1) {

                if (debug) {
                    System.out.println(" - Filter un-used item");
                }

                continue;
            }

            /* Find what type of resource it is */
            int type = streamReader.getInt16();

            ResourceType resourceType = ResourceType.fromInt(type);

            if (debug) {
                System.out.println(" - Resource type number: " + type);
                System.out.println(" - Resource type: " + resourceType);
            }

            /* Load the resource */
            switch (resourceType) {

                case SOUND:

                    if (debug) {
                        System.out.println("Loading sound");
                    }

                    GameResource soundGameResource = loadSoundFromStream(fileInputStream);

                    if (debug) {
                        System.out.println("Loaded sound");
                    }

                    gameResources.add(soundGameResource);

                    break;

                case BITMAP_RLE:

                    if (debug) {
                        System.out.println("Loading bitmap rle");
                    }

                    BitmapRLE bitmapRLE = loadBitmapRLEFromStream(fileInputStream, defaultPalette);

                    if (debug) {
                        System.out.println("Loaded bitmap rle");
                    }

                    gameResources.add(new BitmapRLEResource(bitmapRLE));

                    break;

                case FONT:

                    if (debug) {
                        System.out.println("Loading font");
                    }

                    loadFontFromStream(fileInputStream);

                    if (debug) {
                        System.out.println("Loaded font");
                    }

                    break;

                case BITMAP_PLAYER:

                    if (debug) {
                        System.out.println("Loading player bitmap");
                    }

                    PlayerBitmap playerBitmap = loadPlayerBitmapFromStream(fileInputStream, defaultPalette);

                    if (debug) {
                        System.out.println("Loaded player bitmap");
                    }

                    gameResources.add(new PlayerBitmapResource(playerBitmap));

                    break;

                case PALETTE:

                    if (debug) {
                        System.out.println("Loading palette");
                    }

                    Palette palette = loadPaletteFromStream(fileInputStream, true);

                    if (debug) {
                        System.out.println("Loaded palette");
                    }

                    gameResources.add(new PaletteResource(palette));

                    break;

                case BOB:

                    if (debug) {
                        System.out.println("Loading bob");
                    }

                    GameResource bobG = loadBobFromStream(fileInputStream, defaultPalette);

                    if (debug) {
                        System.out.println("Loaded bob");
                    }

                    gameResources.add(bobG);

                    break;

                case BITMAP_SHADOW:

                    if (debug) {
                        System.out.println("Loading bitmap shadow");
                    }

                    Bitmap bitmap = loadBitmapShadowFromStream(fileInputStream, defaultPalette);

                    if (debug) {
                        System.out.println("Loaded bitmap shadow");
                    }

                    gameResources.add(new BitmapResource(bitmap));

                    break;

                case MAP:

                    if (debug) {
                        System.out.println("Loading map");
                    }

                    loadMapFromStream(fileInputStream);

                    if (debug) {
                        System.out.println("Loaded map");
                    }

                    break;

                case RAW:

                    if (debug) {
                        System.out.println("Loading raw bitmap");
                    }

                    loadRawBitmapFromStream(fileInputStream, defaultPalette);

                    if (debug) {
                        System.out.println("Loaded raw bitmap");
                    }

                    break;

                case PALETTE_ANIM:

                    if (debug) {
                        System.out.println("Loading animated palette");
                    }

                    loadAnimatedPaletteFromStream(fileInputStream);

                    if (debug) {
                        System.out.println("Loaded animated palette");
                    }

                    break;

                case BITMAP:

                    if (debug) {
                        System.out.println("Loading bitmap (not in original S2)");
                    }

                    BitmapRaw bitmapRaw = loadUncompressedBitmapFromStream(fileInputStream, defaultPalette);

                    if (debug) {
                        System.out.println("Loaded bitmap");
                    }

                    gameResources.add(new BitmapRawResource(bitmapRaw));

                    break;

                default:

                    System.out.println("UNKNOWN RESOURCE: " + type);
                    System.out.println(resourceType);

                    throw new UnknownResourceTypeException("Can't handle resource type " + resourceType);
            }
        }

        return gameResources;
    }

    public List<String> loadTextFile(String filename) throws IOException, InvalidFormatException {
        return loadTextFromStream(new StreamReader(new FileInputStream(filename), ByteOrder.LITTLE_ENDIAN));
    }

    private List<String> loadTextFromStream(StreamReader streamReader) throws IOException, InvalidFormatException {

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

            starts.add((size + TEXT_FILE_HEADER_SIZE));

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

                    streamReader.setPosition(itemPosition);

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

    private BitmapRLE loadBitmapRLEFromStream(InputStream fileInputStream, Palette palette) throws IOException, InvalidFormatException {
        StreamReader streamReader = new StreamReader(fileInputStream, ByteOrder.LITTLE_ENDIAN);

        /* Read header */
        short nx = streamReader.getInt16();
        short ny = streamReader.getInt16();
        long unknown1 = streamReader.getUint32();
        int width = streamReader.getUint16();
        int height = streamReader.getUint16();
        int unknown2 = streamReader.getUint16();
        long length = streamReader.getUint32();

        if (debug) {
            System.out.println(" - nx: " + nx);
            System.out.println(" - ny: " + ny);
            System.out.println(" - Unknown 1: " + unknown1);
            System.out.println(" - Width: " + width);
            System.out.println(" - Height: " + height);
            System.out.println(" - Unknown 2: " + unknown2);
            System.out.println(" - Length: " + length);
        }

        // Load the image data
        byte[] data = streamReader.getUint8ArrayAsBytes((int)length);

        BitmapRLE bitmap = new BitmapRLE(width, height, data, palette, length, TextureFormat.BGRA);

        bitmap.setNx(nx);
        bitmap.setNy(ny);

        return bitmap;
    }

    private BitmapRaw loadUncompressedBitmapFromStream(InputStream fileInputStream, Palette palette) throws IOException, InvalidFormatException {
        StreamReader streamReader = new StreamReader(fileInputStream, ByteOrder.LITTLE_ENDIAN);

        /* Read header */
        int unknown1 = streamReader.getUint16();
        long length = streamReader.getUint32();

        if (unknown1 != 1) {
            throw new InvalidFormatException("Must match '1'. Not " + unknown1);
        }

        byte[] data = streamReader.getUint8ArrayAsBytes((int)length);

        short nx = streamReader.getInt16();
        short ny = streamReader.getInt16();

        int width = streamReader.getUint16();
        int height = streamReader.getUint16();

        /* Verify that the length is correct */
        if (length != (long) width * height) {
            throw new InvalidFormatException("Length (" + length + ") must equal width (" + width + ") * height (" + height + ")");
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
        if (globalTextureFormat == TextureFormat.ORIGINAL) {
            wantedFormat = TextureFormat.PALETTED;
        }

        if (debug) {
            System.out.println(" - Loading format: " + wantedFormat);
        }

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

        if (debug) {
            System.out.println(" - Height: " + height);
            System.out.println(" - Width: " + width);
            System.out.println(" - Row size: " + rowSize);
            System.out.println(" - Length: " + length);
            System.out.println(" - Width x height: " + width * height);
            System.out.println(" - Width x height x 4: " + width * height * 4);
            System.out.println(" - Data size: " + data.length);
        }

        if (debug) {
            System.out.println(" - Source format: " + sourceFormat);
            System.out.println(" - Wanted format: " + wantedFormat);
        }

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
        FileInputStream fileInputStream = new FileInputStream(filename);

        if (debug) {
            System.out.println("Loading sound from file: " + filename);
        }

        GameResource soundGameResource = loadSoundFromStream(fileInputStream);

        if (debug) {
            System.out.println("Loaded sound");
        }

        return soundGameResource;
    }

    private void loadFontFromStream(FileInputStream fileInputStream) {
        throw new RuntimeException("Support for load font is not implemented yet");
    }

    private void loadAnimatedPaletteFromStream(FileInputStream fileInputStream) {
        throw new RuntimeException("Support for load animated palette is not implemented yet");
    }

    private BitmapRaw loadRawBitmapFromStream(InputStream fileInputStream, Palette defaultPalette) throws IOException, InvalidFormatException {
        BitmapRaw bitmapRaw = loadUncompressedBitmapFromStream(fileInputStream, defaultPalette);

        return bitmapRaw;
    }

    private void loadMapFromStream(FileInputStream fileInputStream) {
        throw new RuntimeException("Support for load map is not implemented yet");
    }

    private Bitmap loadBitmapShadowFromStream(InputStream inputStream, Palette palette) throws IOException {

        StreamReader streamReader = new StreamReader(inputStream, ByteOrder.LITTLE_ENDIAN);

        /* Read header */
        short nx = streamReader.getInt16();
        short ny = streamReader.getInt16();
        long unknown1 = streamReader.getUint32();
        int width = streamReader.getUint16();
        int height = streamReader.getUint16();
        int unknown2 = streamReader.getUint16();
        long length = streamReader.getUint32();

        if (unknown1 != 0 || unknown2 != 1) {
            throw new RuntimeException("Invalid format. Unknown 1 must be 0, was " + unknown1 + ". Unknown 2 must be 1, was " + unknown2);
        }

        byte[] data = streamReader.getUint8ArrayAsBytes((int)length);

        short grayIndex = palette.getIndexForColor(255, 255, 255);

        if (debug) {
            System.out.println(" - Width: " + width);
            System.out.println(" - Height: " + height);
            System.out.println(" - Length: " + length);
        }

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
        FileInputStream fileInputStream = new FileInputStream(filename);
        StreamReader streamReader = new StreamReader(fileInputStream, ByteOrder.LITTLE_ENDIAN);

        byte[] header = streamReader.getUint8ArrayAsBytes(2);

        if (header[1] == 0x01 && Unsigned.getUnsignedByte(ByteBuffer.wrap(header)) == 0xF6) {
            Bob bob = loadBobFromStream(fileInputStream, defaultPalette).getBob();

            return bob;
        }

        return null;
    }

    private BobGameResource loadBobFromStream(FileInputStream fileInputStream, Palette palette) throws IOException, InvalidFormatException {
        StreamReader streamReader = new StreamReader(fileInputStream, ByteOrder.LITTLE_ENDIAN);

        PlayerBitmap[] playerBitmaps = new PlayerBitmap[(int)NUM_BODY_IMAGES];

        /* Read the color block for the body */
        ColorBlock colorBlock = ColorBlock.readColorBlockFromStream(streamReader);

        /* Read body images */
        for (long i = 0; i < NUM_BODY_IMAGES; i++) {
            int bodyImageId = streamReader.getUint16();
            short bodyImageHeight = streamReader.getUint8();

            if (bodyImageId != BOB_IMAGE_DATA_HEADER) {
                throw new InvalidFormatException("Body image id must match '0x01F4'. Not " + bodyImageId);
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
                throw new InvalidFormatException("Must match '0x01F4'. Not " + overlayImageId);
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
                throw new InvalidFormatException("Number of overlay images is: " + numberOverlayImages + ". Cannot have more than: " + links[(int)i]);
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

    private GameResource loadSoundFromStream(FileInputStream fileInputStream) throws IOException, InvalidFormatException {
        StreamReader streamReader = new StreamReader(fileInputStream, ByteOrder.LITTLE_ENDIAN);

        SoundType soundType;

        long length = 0;

        String headerAsString = streamReader.getUint8ArrayAsString(4);

        if (debug) {
            System.out.println(" - Length is: " + length);
            System.out.println(" - File header is: " + headerAsString);
        }

        if (headerAsString.equals("FORM") || headerAsString.equals("RIFF")) {
            long subLength = streamReader.getUint32(); // uint 32

            if (debug) {
                System.out.println("   - Sub length is: " + subLength);
            }

            String subHeaderAsString = streamReader.getUint8ArrayAsString(4); // 4 x uint 8

            switch (subHeaderAsString) {
                case "XMID":
                    soundType = XMIDI;
                    break;
                case "XDIR":
                    soundType = XMID_DIR;
                    break;
                case "WAVE":
                    soundType = WAVE;
                    break;
                default:
                    throw new InvalidFormatException("Failed to locate sound type.");
            }

            if (debug) {
                System.out.println("   - Sub type is: " + soundType);
            }
        } else if (headerAsString.equals("MThd")) {
            soundType = MIDI;
        } else if (headerAsString.equals("OggS")) {
            soundType = OGG;
        } else if (headerAsString.equals("ID3")) { // TODO: handle \xFF\xFB which should also mean mp3
            soundType = MP3;
        } else {
            soundType = WAVE_WITHOUT_HEADER;
        }

        if (debug) {
            System.out.println(" - Sound type is: " + soundType);
        }

        if (soundType == MIDI) {
            return new MidiGameResource(loadSoundMidiFromStream(streamReader));
        } else if (soundType == WAVE) {
            return new WaveGameResource(loadWaveSoundFromStream(streamReader, length, true));
        } else if (soundType == XMIDI) {
            return new XMidiGameResource(loadXMidiSoundFromStream(streamReader, length, false));
        } else if (soundType == XMID_DIR) {
            return new XMidiGameResource(loadXMidiSoundFromStream(streamReader, length, true));
        } else if (soundType == WAVE_WITHOUT_HEADER) {
            return new WaveGameResource(loadWaveSoundFromStream(streamReader, length, false));
        } else {
            throw new RuntimeException("Support for 'other sound' is not implemented yet");
        }
    }

    public XMidiFile loadSoundXMidiFile(String filename) throws IOException, InvalidFormatException {
        StreamReader streamReader = new StreamReader(new FileInputStream(filename), ByteOrder.LITTLE_ENDIAN);

        // Read header
        String headerId = streamReader.getUint8ArrayAsString(4);

        if (!headerId.equals("FORM")) {
            throw new InvalidFormatException("Header must match 'FORM'. Not " + headerId);
        }

        if (debug) {
            System.out.println("FORM");
        }

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

        if (debug) {
            System.out.println(chunkId);
        }

        if (chunkId.equals("XMID")) {
            numberTracks = 1;
        } else if (chunkId.equals("XDIR")) {
            chunkId = streamReader.getUint8ArrayAsString(4);

            if (!chunkId.equals("INFO")) {
                throw new InvalidFormatException("Must match 'INFO'. Not " + chunkId);
            }

            if (debug) {
                System.out.println(chunkId);
            }

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

        if (debug) {
            System.out.println(chunkId);
        }

        // Ignore the following 4 bytes
        streamReader.skip(4);

        chunkId = streamReader.getUint8ArrayAsString(4);

        if (!chunkId.equals("XMID")) {
            throw new InvalidFormatException("Must match 'XMID'. Not " + chunkId);
        }

        if (debug) {
            System.out.println(chunkId);

            System.out.println(" - Read header");
            System.out.println("    - Header id: " + headerId);
            System.out.println("    - Header size: " + headerSize);
            System.out.println("    - Number tracks: " + numberTracks);
        }

        // Read tracks
        List<XMidiTrack> trackList = new ArrayList<>();

        for (long i = 0; i < numberTracks; i++) {

            if (debug) {
                System.out.println(" - Reading track: " + i);
            }

            XMidiTrack xMidiTrack = new XMidiTrack();

            chunkId = streamReader.getUint8ArrayAsString(4);

            if (!chunkId.equals("FORM")) {
                throw new InvalidFormatException("Must match 'FORM'. Not " + chunkId);
            }

            if (debug) {
                System.out.println(" - " + chunkId);
            }

            long chunkLength = streamReader.getUint32();

            if ((chunkLength & 1) != 0) {
                chunkLength = chunkLength + 1;
            }

            chunkId = streamReader.getUint8ArrayAsString(4);

            if (!chunkId.equals("XMID")) {
                throw new InvalidFormatException("Must match 'XMID'. Not " + chunkId);
            }

            if (debug) {
                System.out.println(" - " + chunkId);
            }

            chunkId = streamReader.getUint8ArrayAsString(4);

            if (debug) {
                System.out.println(" - " + chunkId);
            }

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

                if (debug) {
                    System.out.println(" Number of timbres: " + numberTimbres);
                    System.out.println(" Chunk length: " + chunkLength);
                }

                if (numberTimbres * 2L + 2 != chunkLength) {
                    throw new InvalidFormatException("Chunk length must match number timbres (" + numberTimbres + ") * 2 + 2. Not " + chunkLength);
                }

                // Read timbres
                for (int j = 0; j < numberTimbres; j++) {

                    if (debug) {
                        System.out.println("Read timbre pair!");
                    }

                    short patch = streamReader.getUint8();
                    short bank = streamReader.getUint8();

                    if (debug) {
                        System.out.println(" Patch: " + patch);
                        System.out.println(" Bank: " + bank);
                    }

                    xMidiTrack.addTimbre(patch, bank);
                }

                chunkId = streamReader.getUint8ArrayAsString(4);

                if (debug) {
                    System.out.println(" - Next section: " + chunkId);
                }
            }

            Utils.getHex(chunkId.getBytes());

            // Read EVTN section
            if (!chunkId.equals("EVNT")) {
                throw new InvalidFormatException("Must match 'EVNT'. Not " + chunkId);
            }

            if (debug) {
                System.out.println(" - Read EVTN section");
            }

            // Read the length as big endian!
            streamReader.pushByteOrder(ByteOrder.BIG_ENDIAN);

            chunkLength = streamReader.getUint32();

            streamReader.popByteOrder();

            if ((chunkLength & 1) != 0) {
                chunkLength = chunkLength + 1;
            }

            if (debug) {
                System.out.println("   - Length is: " + chunkLength);
            }

            byte[] trackData = new byte[(int) chunkLength];

            streamReader.read(trackData, 0, (int) chunkLength);

            xMidiTrack.setData(trackData);

            trackList.add(xMidiTrack);
        }

        XMidiFile xMidiFile = new XMidiFile(headerSize, numberTracks, trackList);

        return xMidiFile;
    }

    private List<XMidiTrack> loadXMidiSoundFromStream(StreamReader streamReader, long length, boolean isDir) throws IOException, InvalidFormatException {
        StreamReader streamReaderBigEndian = new StreamReader(streamReader.getInputStream(), ByteOrder.BIG_ENDIAN);

        long formHeaderLength = streamReader.getUint32();

        if ((formHeaderLength & 1) != 0) {
            formHeaderLength = formHeaderLength + 1;
        }

        List<XMidiTrack> tracks = new ArrayList<>();
        long numberTracks = 1;

        if (isDir) {
            char[] chunk = streamReader.getUint8ArrayAsChar(4);

            if (!String.copyValueOf(chunk).equals("INFO")) {
                throw new InvalidFormatException("Must have INFO chunk id. Not " + String.copyValueOf(chunk));
            }

            long chunkLength = streamReader.getUint32();

            if ((chunkLength & 1) != 0) {
                chunkLength = chunkLength + 1;
            }

            if (chunkLength != SIZE_NUMBER_TRACKS) {
                throw new InvalidFormatException("Remaining chunk length must match size of number_tracks. Not " + chunkLength);
            }

            numberTracks = streamReaderBigEndian.getUint32();

            if (debug) {
                System.out.println("Number of tracks: " + numberTracks);
            }

            chunk = streamReader.getUint8ArrayAsChar(4);

            if (!String.copyValueOf(chunk).equals("CAT ")) {
                throw new InvalidFormatException("Chunk must match 'CAT '. Not " + String.copyValueOf(chunk));
            }

            // Skip 4
            streamReader.skip(4);

            chunk = streamReader.getUint8ArrayAsChar(4);

            if (!String.copyValueOf(chunk).equals("XMID")) {
                throw new InvalidFormatException("Must match 'XMID'. Not " + String.copyValueOf(chunk));
            }
        }

        // Read tracks
        for (long i = 0; i < numberTracks; i++) {
            XMidiTrack xMidiTrack = new XMidiTrack();

            char[] chunk = streamReader.getUint8ArrayAsChar(4);

            if (!String.copyValueOf(chunk).equals("FORM")) {
                throw new InvalidFormatException("Must match 'FORM'. Not " + String.copyValueOf(chunk));
            }

            long chunkLength = streamReader.getUint32(); // FIXME: is it correct to read two lengths like this?

            formHeaderLength = streamReader.getUint32();

            if ((formHeaderLength & 1) != 0) {
                formHeaderLength = formHeaderLength + 1;
            }

            chunk = streamReader.getUint8ArrayAsChar(4);

            if (!String.copyValueOf(chunk).equals("XMID")) {
                throw new InvalidFormatException("Must match 'XMID'. Not " + String.copyValueOf(chunk));
            }

            chunk = streamReader.getUint8ArrayAsChar(4);

            // Read timbres, if any
            if (String.copyValueOf(chunk).equals("TIMB")) {
                chunkLength = streamReader.getUint32();

                if ((chunkLength & 1) != 0) {
                    chunkLength = chunkLength + 1;
                }

                int numberTimbres = streamReaderBigEndian.getUint16();

                if (numberTimbres * 2L + 2 != chunkLength) {
                    throw new InvalidFormatException("Chunk length must match number timbres (" + numberTimbres + ") * 2 + 2. Not " + chunkLength);
                }

                // Read timbres
                for (int j = 0; j < numberTimbres; j++) {
                    short patch = streamReader.getUint8();
                    short bank = streamReader.getUint8();

                    xMidiTrack.addTimbre(patch, bank);
                }
            }

            chunk = streamReader.getUint8ArrayAsChar(4);

            if (!String.copyValueOf(chunk).equals("EVNT")) {
                throw new InvalidFormatException("Must match 'EVNT'. Not " + String.copyValueOf(chunk));
            }

            chunkLength = streamReader.getUint32();

            if ((chunkLength & 1) != 0) {
                chunkLength = chunkLength + 1;
            }

            byte[] trackData = new byte[(int) chunkLength];

            streamReader.read(trackData, 0, (int) chunkLength);

            xMidiTrack.setData(trackData);

            tracks.add(xMidiTrack);
        }

        return tracks;
    }

    private WaveFile loadWaveSoundFromStream(StreamReader streamReader, long length, boolean hasHeader) throws InvalidFormatException, IOException {

        if (debug) {
            System.out.println("   - Loading wave sound");
            System.out.println("      - Length: " + length);
            System.out.println("      - Has header: " + hasHeader);
        }

        if (length > WAVE_HEADER_SIZE) { //
            throw new InvalidFormatException("Length must be larger than header size. Was " + length);
        }

        // Read the remaining parts of the header
        WaveFile waveFile;

        if (hasHeader) {

            /* Read header */
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

            if (debug) {
                System.out.println("      - Read wave header");
                System.out.println("         - Format id: " + formatId);
                System.out.println("         - Format size: " + formatSize);
                System.out.println("         - Format tag: " + formatTag);
                System.out.println("         - Number of channels: " + numberChannels);
                System.out.println("         - Samples per sec: " + samplesPerSec);
                System.out.println("         - Bytes per sec: " + bytesPerSec);
                System.out.println("         - Frame size: " + frameSize);
                System.out.println("         - Bits per sample: " + bitsPerSample);
                System.out.println("         - Data id: " + dataId);
                System.out.println("         - Data size: " + dataSize);
            }

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

            /* Set a default header */
            String formatId = "fmt ";
            long formatSize = 16;
            int formatTag = 1;
            int numberChannels = 1;
            long samplesPerSec = 11025;
            long bytesPerSec = 11025;
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

        byte[] waveData = streamReader.getRemainingBytes();

        waveFile.setData(waveData);

        return waveFile;
    }

    public MidiFile loadSoundMidiFile(String filename) throws IOException, InvalidFormatException {
        StreamReader streamReader = new StreamReader(new FileInputStream(filename), ByteOrder.LITTLE_ENDIAN);

        MidiFile midiFile = loadSoundMidiFromStream(streamReader);

        return midiFile;
    }

    public MidiFile loadSoundMidiFromStream(StreamReader streamReader) throws InvalidFormatException, IOException {

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

        if (debug) {
            System.out.println(" - Read header");
            System.out.println("    - Header id: " + headerId);
            System.out.println("    - Header size: " + headerSize);
            System.out.println("    - Format: " + format);
            System.out.println("    - Number tracks: " + numTracks);
            System.out.println("    - ppqs: " + ppqs);
        }

        MidiFile midiFile = new MidiFile(headerSize, format, numTracks, ppqs);

        if (numTracks == 0 || numTracks > 256) {
            streamReader.popByteOrder();

            throw new InvalidFormatException("Invalid number of tracks(" + numTracks + "), must be between 0-256.");
        }

        // Read the tracks
        for (int i = 0; i < numTracks; i++) {

            if (debug) {
                System.out.println(" - Reading track: " + i);
            }

            String chunkId = streamReader.getUint8ArrayAsString(4);

            long chunkLen = streamReader.getUint32(); // uint 32

            if (chunkId.equals("MTrk")) {

                byte[] midiTrackData = new byte[(int)chunkLen];

                streamReader.read(midiTrackData, 0, (int)chunkLen);

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

    private static Palette loadPaletteFromStream(FileInputStream fileInputStream, boolean skip) throws IOException, InvalidFormatException {

        StreamReader streamReader = new StreamReader(fileInputStream, ByteOrder.LITTLE_ENDIAN);

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

    private PlayerBitmap loadPlayerBitmapFromStream(InputStream fileInputStream, Palette palette) throws IOException, InvalidFormatException {
        StreamReader streamReader = new StreamReader(fileInputStream, ByteOrder.LITTLE_ENDIAN);

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

        if (debug) {
            System.out.println("    - Width: " + width);
            System.out.println("    - Height: " + height);
            System.out.println("    - Length: " + length);
            System.out.println("    - Height * 2: " + height * 2);
            System.out.println("    - Length - height * 2: " + (int) (length - height * 2));
        }

        int[] starts = streamReader.getUint16ArrayAsInts(height);
        byte[] imageData = streamReader.getUint8ArrayAsBytes((int)(length - height * 2));

        if (debug) {
            System.out.println("    - Number starts: " + starts.length);
            System.out.println("    - Size of image data: " + imageData.length);
            System.out.println("    - Image dimensions are: " + width + "x" + height);
            System.out.println("    - Multiplied: " + width * height);
        }

        PlayerBitmap playerBitmap = new PlayerBitmap(width, height, palette, TextureFormat.BGRA);

        playerBitmap.setNx(nx);
        playerBitmap.setNy(ny);
        playerBitmap.setLength(length);

        if (debug) {
            System.out.println(" Loading from image data");
        }

        playerBitmap.loadImageFromData(imageData, starts, false);

        if (debug) {
            System.out.println(" Loaded from image data");
        }

        return playerBitmap;
    }

    public Palette loadPaletteFromFile(String filename) throws IOException {
        StreamReader streamReader = new StreamReader(new FileInputStream(filename), ByteOrder.LITTLE_ENDIAN);

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
        StreamReader streamReader = new StreamReader(new FileInputStream(filename), ByteOrder.LITTLE_ENDIAN);

        String headerId = streamReader.getUint8ArrayAsString(2);
        long fileSize = streamReader.getUint32();
        long reserved = streamReader.getUint32();
        long pixelOffset = streamReader.getUint32();

        if (debug) {
            System.out.println(" - BMP header:");
            System.out.println("    - File size: " + fileSize);
            System.out.println("    - Reserved: " + reserved);
            System.out.println("    - Pixel offset: " + pixelOffset);
        }

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

        if (debug) {
            System.out.println(" - More header info");
            System.out.println("    - Header size: " + headerSize);
            System.out.println("    - Width: " + width);
            System.out.println("    - Height: " + height);
            System.out.println("    - Planes: " + planes);
            System.out.println("    - Bits per pixel: " + bitsPerPixel);
            System.out.println("    - Compression: " + compression);
            System.out.println("    - Size: " + size);
            System.out.println("    - X Pixels per M: " + xPixelsPerMeter);
            System.out.println("    - Y Pixels per M: " + yPixelsPerMeter);
            System.out.println("    - Color used: " + numberColorsUsed);
            System.out.println("    - Color imp: " + numberImportantColors);
        }

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

            if (debug) {
                System.out.println(" ---- 2^" + bitsPerPixel + " = " + numberColorsUsed);
            }
        }

        if (debug) {
            System.out.println("COLORS USED: " + numberColorsUsed);
        }

        Palette palette = defaultPalette;

        if (bitsPerPixel == 8) {

            if (debug) {
                System.out.println("    - Loading palette");
            }

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

        if (debug) {
            System.out.println(" ---- Calculated row size: " + rowSize);
            System.out.println(" ---- Width * bytes per pixel: " + width * sourceBytesPerPixel);

            System.out.println("    - Bytes per pixel: " + sourceBytesPerPixel);
        }

        TextureFormat sourceFormat = TextureFormat.PALETTED;

        if (sourceBytesPerPixel == 4) {
            sourceFormat = TextureFormat.BGRA;
        } else if (sourceBytesPerPixel == 3) {
            sourceFormat = TextureFormat.BGR;
        }

        if (debug) {
            System.out.println("WANTED TEXTURE FORMAT: " + wantedTextureFormat);
        }

        BitmapFile bitmap = new BitmapFile(width, height, palette, wantedTextureFormat);

        bitmap.setFileSize(fileSize)
            .setReserved(reserved)
            .setPixelOffset(pixelOffset)
            .setHeaderSize(headerSize)
            .setPlanes(planes)
            .setBitsPerPixel(bitsPerPixel)
            .setCompression(compression)
            .setSize(size)
            .setXPixelsPerM(xPixelsPerMeter)
            .setYPixelsPerM(yPixelsPerMeter)
            .setColorUsed((int)numberColorsUsed)
            .setColorImp(numberImportantColors);

        int targetBytesPerPixel = 1;

        if (wantedTextureFormat == TextureFormat.BGRA) {
            targetBytesPerPixel = 4;
        }

        byte[] tmpBuffer;

        streamReader.setPosition(pixelOffset);

        if (debug) {
            System.out.println("    - Read bottom up: " + bottomUp);
        }

        if (bottomUp) {
            for (int y = height - 1; y >= 0; y--) {

                /* Read the source as paletted */
                if (sourceBytesPerPixel == 1) {

                    /* Copy one row */
                    for (int i = 0; i < width; i++) {
                        bitmap.setPixelByColorIndex(i, y, streamReader.getUint8());
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

        if (debug) {
            System.out.println(baseFile);
        }

        String datFilename = baseFile + ".DAT";
        String idxFilename = baseFile + ".IDX";

        if (!Files.exists(Paths.get(datFilename)) || !Files.exists(Paths.get(idxFilename))) {
            throw new InvalidFormatException("Both DAT and IDX file must exist. Not only " + filename);
        }

        StreamReader datReader = new StreamReader(new FileInputStream(datFilename), ByteOrder.LITTLE_ENDIAN);
        StreamReader idxReader = new StreamReader(new FileInputStream(idxFilename), ByteOrder.LITTLE_ENDIAN);

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

                if (debug) {
                    System.out.println("Seems like an invalid item??");
                    System.out.println(idxFilename);
                    System.out.println(datFilename);
                    System.out.println(i);
                }

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

                datReader.pushByteOrder(ByteOrder.LITTLE_ENDIAN);

                short dx = datReader.getUint8();
                short dy = datReader.getUint8();

                boolean isUnicode = (dx == 255 && dy == 255);

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

                PlayerBitmap playerBitmap = loadPlayerBitmapFromStream(datReader.getInputStream(), defaultPalette);

                if (debug) {
                    System.out.println("Loaded player bitmap");
                }

                return new PlayerBitmapResource(playerBitmap);

            case BITMAP:

                BitmapRaw bitmapRaw = loadRawBitmapFromStream(datReader.getInputStream(), defaultPalette);

                return new BitmapRawResource(bitmapRaw);

            case BITMAP_RLE:

                BitmapRLE bitmapRLE = loadBitmapRLEFromStream(datReader.getInputStream(), defaultPalette);

                return new BitmapRLEResource(bitmapRLE);

            default:
                throw new RuntimeException("Not implemented yet. " + resourceType);
        }
    }

    /**
     * JOBS.BOB
     * SLIM GUY (no head)
     * 0-7   - Walk east
     * 8-15  - Walk north-east
     * 16-23 - Walk south-west
     * 24-31 - Walk west
     * 32-39 - Walk north-west
     * 40-47 - Walk east (south-east?)
     *
     * FAT GUY (no head)
     * 48-55 - Walk east
     * 56-63 - Walk north-east (?)
     * 64-71 - Walk south-west
     * 72-79 - Walk west
     * 80-87 - Walk north-west
     * 88-95 - Walk south-east
     *
     * HEAD 1
     * 96 - East
     * 97 - South-east (?)
     * 98 - South-west
     * 99 - West
     * 100 - North-west
     * 101 - North-east
     *
     * HEAD 2
     * 102 - East
     * 103 - South-east
     * 104 - South-west
     * 105 - West
     * 106 - North-west
     * 107 - North-east
     *
     * HEAD 3
     * 108 - East
     * 109 - South-east
     * 110 - South-west
     * 111 - West
     * 112 - North-west
     * 113 - North-east
     *
     * HEAD 4
     * 114 - East
     * 115 - South-east
     * 116 - South-west
     * 117 - West
     * 118 - North-west
     * 119 - North-east
     *
     * HEAD 5
     * 120 - East
     * 121 - South-east
     * 122 - South-west
     * 123 - West
     * 124 - North-west
     * 125 - North-east
     *
     * HEAD 6
     * 126 - East
     * 127 - South-east
     * 128 - South-west
     * 129 - West
     * 130 - North-west
     * 131 - North-east
     *
     * HEAD 7
     * 132-137 - E, SE, SW, W, NW, NE
     *
     * HEAD 8
     * 138-142 - E, SE, SW, (W missing) NW, NE
     *
     * HEAD 9
     * 143-148 - E, SE, SW, W, NW, NE
     *
     * HEAD 10
     * 149-152 - E, SE, W, NE
     *
     * HEAD 11
     * 153-156 - E, SE, W, NE
     *
     * HEAD 12
     * 157-158 - E, NE
     *
     * HEAD 13
     * 159-162 - E, SE, NW, NE
     *
     * HEAD 14
     * 163-167 - E, SE, SW, NW, NE
     *
     * HEAD 15
     * 168-172 - E, SE, W, NW, NE
     *
     * HEAD 16
     * 173-178 - E, SE, SW, W, NW, NE
     *
     * HEAD 17
     * 179-184 - E, SE, SW, W, NW, NE
     *
     * VERY MINOR DETAIL (overlay?)
     * 185-189
     *
     * HEAD 18
     * 190-195 - E, SE, SW, W, NW, NE
     *
     * HEAD 19
     * 196-201 - E, SE, SW, W, NW, NE
     *
     * HEAD 20
     * 202-207 - E, SE, SW, W, NW, NE
     *
     * HEAD 21
     * 208-213 - E, SE, SW, W, NW, NE
     *
     * ... more heads ...
     *
     * HEAD WITH AXE OR HAMMER
     * 278-283 - E, SE, SW, W, NW, NE
     *
     * ...
     *
     * WOODCUTTER HEAD
     * 310-315 - E, SE, SW, W, NW, NE
     * 316-17 - W - animation(?)
     *
     * ... more heads and sometimes a bit of body ...
     *
     * MILITARY
     * 859-906 - Roman private (?)
     * 907-1098 - Other roman soldiers
     * 1099-1338 - Viking soldiers
     * 1339-1626 - Japanese soldiers
     * 1627-1962 - African soldiers (?)
     *
     *
     * @param jobsBob
     * @param workerDetailsMap
     * @return
     */
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
