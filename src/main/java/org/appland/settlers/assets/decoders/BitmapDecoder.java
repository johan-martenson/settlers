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

    private static boolean debug = false;

    private static void debugPrint(String debugString) {
        if (debug) {
            System.out.println(debugString);
        }
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
    public static BitmapFile loadBitmapFile(String filename, Palette defaultPalette, Optional<TextureFormat> wantedTextureFormat) throws IOException, InvalidFormatException {
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

        debugPrint("WANTED TEXTURE FORMAT: " + TextureFormat.BGRA);

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
        bitmap.setColorUsed((int)numberColorsUsed);
        bitmap.setColorImp(numberImportantColors);

        int targetBytesPerPixel = 1;

        if (wantedTextureFormat.isPresent() && wantedTextureFormat.get() == TextureFormat.BGRA) {
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
}
