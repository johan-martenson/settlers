package org.appland.settlers.assets.decoders;

import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.TextureFormat;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.resources.PlayerBitmap;
import org.appland.settlers.utils.ByteReader;

import java.io.IOException;

public class PlayerBitmapDecoder {

    private static boolean debug = false;

    /**
     * Prints debug information if debug mode is enabled.
     *
     * @param debugString The debug information to print
     */
    private static void debugPrint(String debugString) {
        if (debug) {
            System.out.println(debugString);
        }
    }

    /**
     * Loads a PlayerBitmap from a stream.
     *
     * @param streamReader The stream reader used to load the bitmap
     * @param palette      The palette to apply to the bitmap
     * @return A PlayerBitmap object representing the loaded bitmap
     * @throws IOException            If an I/O error occurs
     * @throws InvalidFormatException If the format is invalid
     */
    public static PlayerBitmap loadPlayerBitmapFromStream(ByteReader streamReader, Palette palette) throws IOException, InvalidFormatException {

        // Read header data
        int nx = streamReader.getInt16();
        int ny = streamReader.getInt16();
        long unknown1 = streamReader.getUint32(); // Unknown - always 0

        int width = streamReader.getUint16();
        int height = streamReader.getUint16();
        int paletteId = streamReader.getUint16(); // Palette id - always 1

        long length = streamReader.getUint32();

        // Validate length
        if (length <= (height * 2L)) {
            throw new InvalidFormatException(String.format("Length (%d) must be larger than height (%d) * 2", length, height));
        }

        debugPrint(String.format("    - Width: %d", width));
        debugPrint(String.format("    - Height: %d", height));
        debugPrint(String.format("    - Length: %d", length));
        debugPrint(String.format("    - Height * 2: %d", height * 2));
        debugPrint(String.format("    - Length - height * 2: %d", (length - height * 2)));

        // Read starts and image data
        int[] starts = streamReader.getUint16ArrayAsInts(height);
        byte[] imageData = streamReader.getUint8ArrayAsBytes((int)(length - height * 2));

        debugPrint(String.format("    - Number of starts: %d", starts.length));
        debugPrint(String.format("    - Size of image data: %d", imageData.length));
        debugPrint(String.format("    - Image dimensions: %dx%d", width, height));
        debugPrint(String.format("    - Multiplied: %d", width * height));

        PlayerBitmap playerBitmap = new PlayerBitmap(width, height, nx, ny, palette, TextureFormat.BGRA);
        playerBitmap.setLength(length);

        debugPrint(" Loading from image data");

        playerBitmap.loadImageFromData(imageData, starts, false);

        debugPrint(" Loaded from image data");

        return playerBitmap;
    }
}
