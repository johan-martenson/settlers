package org.appland.settlers.assets.decoders;

import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.TextureFormat;
import org.appland.settlers.assets.resources.BitmapFile;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.utils.StreamReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

public class BitmapDecoder {
    private static final long BMP_HEADER_SIZE = 40;
    private static final byte TRANSPARENT_BYTE = (byte) 0xFF;

    private static boolean debug = false;

    /**
     * Prints debug information if debugging is enabled.
     *
     * @param debugString the debug message to print
     */
    private static void debugPrint(String debugString) {
        if (debug) {
            System.out.println(debugString);
        }
    }

    /**
     * Reads a bitmap file
     * <p>
     * File specification:
     * Name             Type       Times
     * - Header id        uint 8     4
     * - File size        uint 32    1
     * - Reserved         uint 32    1
     * - Pixel offset     uint 32    1
     * - Header size      uint 32    1
     * - Width            int 32     1
     * - Height           int 32     1
     * - Planes           int 16     1
     * - Bits per pixel   int 16     1
     * - Compression      uint 32    1
     * - Size             uint 32    1
     * - x pixels per m   int 32     1
     * - y pixels per m   int 32     1
     * - Color used       int 32     1
     * - Color imp        int 32     1
     * <p>
     * Palette section (only if bits per pixel is 8)
     * - Palette data     uint 8     colors used x 4
     * <p>
     * Image data section
     * Alt 1 - paletted data
     * - Image data       uint 8     width x height
     * <p>
     * Alt 2 - BGR format
     * - Image data       uint 8     width x height x 3
     *
     * @param filename            the path of the bitmap file
     * @param defaultPalette      the default palette used if the file does not contain one
     * @param wantedTextureFormat an optional texture format
     * @return the decoded bitmap file
     * @throws IOException            if an I/O error occurs
     * @throws InvalidFormatException if the file format is invalid
     */
    public static BitmapFile loadBitmapFile(String filename, Palette defaultPalette, Optional<TextureFormat> wantedTextureFormat) throws IOException, InvalidFormatException {
        try (StreamReader streamReader = new StreamReader(new FileInputStream(filename), LITTLE_ENDIAN)) {

            // Read the BMP header
            String headerId = streamReader.getUint8ArrayAsString(2);
            long fileSize = streamReader.getUint32();
            long reserved = streamReader.getUint32();
            long pixelOffset = streamReader.getUint32();

            debugPrint(" - BMP header:");
            debugPrint("    - File size: " + fileSize);
            debugPrint("    - Reserved: " + reserved);
            debugPrint("    - Pixel offset: " + pixelOffset);

            if (!headerId.equals("BM")) {
                throw new InvalidFormatException(String.format("Must match 'BM'. Not '%s'", headerId));
            }

            // Read the BMP image header
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
                throw new InvalidFormatException(String.format("Header size must match %d. Not %d", BMP_HEADER_SIZE, headerSize));
            }

            boolean bottomUp = height >= 0;
            height = Math.abs(height);

            if (planes != 1) {
                throw new InvalidFormatException(String.format("Can only handle BMP with 1 plane. Not %d", planes));
            }

            if (compression != 0) {
                throw new InvalidFormatException(String.format("Compression parameter must be 0. Not %d", compression));
            }

            if (numberColorsUsed == 0) {
                numberColorsUsed = 1L << bitsPerPixel;

                debugPrint(String.format(" ---- 2^%d = %d", bitsPerPixel, numberColorsUsed));
            }

            debugPrint(String.format("COLORS USED: %d", numberColorsUsed));

            Palette palette = bitsPerPixel == 8
                    ? Palette.loadFromBgra(streamReader, numberColorsUsed)
                    : defaultPalette;

            int sourceBytesPerPixel = bitsPerPixel / 8;
            long rowSize = (int) Math.ceil((bitsPerPixel * width) / 32.0) * 4L;

            debugPrint(String.format(" ---- Calculated row size: %d", rowSize));
            debugPrint(String.format(" ---- Width * bytes per pixel: %d", width * sourceBytesPerPixel));
            debugPrint(String.format("    - Bytes per pixel: %d", sourceBytesPerPixel));

            TextureFormat sourceFormat = TextureFormat.PALETTED;

            if (sourceBytesPerPixel == 4) {
                sourceFormat = TextureFormat.BGRA;
            } else if (sourceBytesPerPixel == 3) {
                sourceFormat = TextureFormat.BGR;
            }

            debugPrint(String.format("Wanted texture output format: %s", TextureFormat.BGRA));

            BitmapFile bitmap = new BitmapFile(width, height, palette, TextureFormat.BGRA);

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
            bitmap.setColorsUsed((int) numberColorsUsed);
            bitmap.setImportantColors(numberImportantColors);

            int targetBytesPerPixel = 1;

            if (wantedTextureFormat.isPresent() && wantedTextureFormat.get() == TextureFormat.BGRA) {
                targetBytesPerPixel = 4;
            }

            byte[] tmpBuffer;

            streamReader.setPosition(pixelOffset);

            debugPrint("    - Read bottom up: " + bottomUp);

            if (bottomUp) {
                for (int y = height - 1; y >= 0; y--) {

                    // Paletted data
                    if (sourceBytesPerPixel == 1) {
                        for (int x = 0; x < width; x++) {
                            bitmap.setPixelByColorIndex(x, y, streamReader.getUint8());
                        }

                    // BGR data
                    } else if (sourceBytesPerPixel == 3) {
                        tmpBuffer = streamReader.getUint8ArrayAsBytes(width * sourceBytesPerPixel);

                        for (int x = 0; x < width; x++) {
                            byte blue = tmpBuffer[x * 3];
                            byte green = tmpBuffer[x * 3 + 1];
                            byte red = tmpBuffer[x * 3 + 2];

                            byte transparency = palette.isColorTransparent(red, blue, green) ? 0 : TRANSPARENT_BYTE;
                            bitmap.setPixelValue(x, y, red, green, blue, transparency);
                        }
                    } else {
                        throw new RuntimeException(String.format("Can't read bitmap data with %d bytes per pixel", sourceBytesPerPixel));
                    }

                    // Ignore extra spacing at end of each line to match up with 4 byte blocks
                    if (width * sourceBytesPerPixel % 4 > 0) {
                        streamReader.skip(4 - (width * sourceBytesPerPixel % 4));
                    }
                }
            } else {
                for (int y = 0; y < height; y++) {

                    // Paletted data
                    if (sourceBytesPerPixel == 1) {
                        for (int x = 0; x < width; x++) {
                            bitmap.setPixelByColorIndex(x, y, streamReader.getUint8());
                        }

                    // BGR data
                    } else {
                        tmpBuffer = streamReader.getUint8ArrayAsBytes(width * sourceBytesPerPixel);

                        for (int x = 0; x < width; x++) {
                            byte blue = tmpBuffer[x * 3];
                            byte green = tmpBuffer[x * 3 + 1];
                            byte red = tmpBuffer[x * 3 + 2];

                            byte transparency = palette.isColorTransparent(red, blue, green) ? 0 : TRANSPARENT_BYTE;
                            bitmap.setPixelValue(x, y, red, green, blue, transparency);
                        }
                    }

                    // Ignore extra spacing at end of each line to match up with 4 byte blocks
                    if (width * sourceBytesPerPixel % 4 > 0) {
                        streamReader.skip(4 - (width * sourceBytesPerPixel % 4));
                    }
                }
            }

            return bitmap;
        }
    }
}
