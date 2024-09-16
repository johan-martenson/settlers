package org.appland.settlers.assets.decoders;

import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.TextureFormat;
import org.appland.settlers.assets.resources.BitmapRaw;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.utils.ByteReader;

import java.io.IOException;
import java.util.Optional;

import static java.lang.String.format;

public class RawBitmapDecoder {
    private static boolean debug = false;

    private static void debugPrint(String debugString) {
        if (debug) {
            System.out.println(debugString);
        }
    }

    /**
     * Loads a raw bitmap from the provided stream, applying the specified palette and texture format if necessary.
     *
     * @param streamReader        The stream reader to read the bitmap from
     * @param palette             The color palette to apply (if necessary)
     * @param wantedTextureFormat The optional texture format to be applied
     * @return The decoded BitmapRaw
     * @throws IOException              If an I/O error occurs
     * @throws InvalidFormatException   If the format of the bitmap is invalid
     */
    public static BitmapRaw loadRawBitmapFromStream(ByteReader streamReader, Palette palette, Optional<TextureFormat> wantedTextureFormat) throws IOException, InvalidFormatException {

        // Read header
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

        // Verify that the length is correct
        if (length != (long) width * height) {
            throw new InvalidFormatException(format("Length (%d) must equal width (%d) * height (%d)", length, width, height));
        }

        // Guess source format
        TextureFormat sourceFormat;

        if (length == (long) width * height * 4) {
            sourceFormat = TextureFormat.BGRA;
        } else if (length == (long) width * height) {
            sourceFormat = TextureFormat.PALETTED;
        } else {
            throw new RuntimeException("Unknown source format!");
        }

        // Get wanted format
        TextureFormat wantedFormat = wantedTextureFormat.orElse(TextureFormat.BGRA);

        debugPrint(" - Loading format: " + wantedFormat);

        if (length == 0) {
            throw new RuntimeException("No implementation for empty raw bitmap image");
        }

        // Ensure a palette is available if paletted format is requested
        if (palette == null && wantedFormat == TextureFormat.PALETTED) {
            throw new InvalidFormatException("Palette requested but palette is null.");
        }

        // Decide on bits-per-pixel - 1 for paletted or 4 for BGRA
        short bpp = (short) (wantedFormat == TextureFormat.BGRA ? 4 : 1);
        int rowSize = width * bpp;

        debugPrint(String.format(" - Height: %d", height));
        debugPrint(String.format(" - Width: %d", width));
        debugPrint(String.format(" - Row size: %d", rowSize));
        debugPrint(String.format(" - Length: %d", length));
        debugPrint(String.format(" - Width x height: %d", width * height));
        debugPrint(String.format(" - Width x height x 4: %d", width * height * 4));
        debugPrint(String.format(" - Data size: %d", data.length));
        debugPrint(String.format(" - Source format: %s", sourceFormat));
        debugPrint(String.format(" - Wanted format: %s", wantedFormat));

        BitmapRaw bitmapRaw = new BitmapRaw(width, height, nx, ny, length, palette, wantedFormat);

        // Return the file directly if no conversion is required
        if (wantedFormat == TextureFormat.PALETTED) {
            bitmapRaw.setImageDataFromBuffer(data);
        }

        // Store as BGRA if required
        if (wantedFormat == TextureFormat.BGRA) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    bitmapRaw.setPixelByColorIndex(x, y, (short)(data[y * width + x] & 0xFF));
                }
            }
        }

        return bitmapRaw;
    }
}
