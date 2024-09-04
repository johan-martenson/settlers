package org.appland.settlers.assets.resources;

import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.TextureFormat;
import org.appland.settlers.assets.Unsigned;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BitmapRLE extends Bitmap {
    private final Palette palette;

    private boolean debug = false;

    /**
     * Constructor to create a BitmapRLE object.
     *
     * @param width   the width of the bitmap
     * @param height  the height of the bitmap
     * @param data    the byte data containing image information
     * @param palette the color palette
     * @param length  the length of the image data
     * @param format  the texture format (PALETTED or BGRA)
     * @throws InvalidFormatException if the format or data size is invalid
     */
    public BitmapRLE(int width, int height, byte[] data, Palette palette, long length, TextureFormat format) throws InvalidFormatException {
        super(width, height, palette, format);

        this.palette = palette;
        ByteBuffer byteBufferData = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        // Determine bits per pixel and the transparent index
        short bitsPerPixel = (format == TextureFormat.PALETTED) ? (short) 1 : (short) 3;
        int clear = (format == TextureFormat.PALETTED) ? palette.getTransparentIndex() : 0;

        // Read and decode image data
        if (length != 0) {
            int position = height * 2;

            for (int y = 0; y < height; ++y) {
                int x = 0;

                if (debug) {
                    System.out.println("   + x: " + x);
                    System.out.println("   + y: " + y);
                    System.out.println("   + Position: " + position);
                    System.out.println("   + Data length: " + data.length);
                }

                while (x < width) {
                    short count = Unsigned.getUnsignedByte(byteBufferData, position++);

                    if (debug) {
                        System.out.println("      + Count: " + count);
                    }

                    if (position + count + 1 >= data.length) {
                        throw new InvalidFormatException("Exceeded data size!");
                    }

                    for (short i = 0; i < count; i++, ++x) {
                        setPixelByColorIndex(x, y, Unsigned.getUnsignedByte(byteBufferData, position++));
                    }

                    count = Unsigned.getUnsignedByte(byteBufferData, position++);
                    x += count;
                }

                if (position >= data.length) {
                    throw new InvalidFormatException("Exceeded data size!");
                }

                position += 1; // Skip to the next row
            }

            if (position >= data.length) {
                throw new InvalidFormatException("Exceeded data size!");
            }

            position = position + 1;

            if (position != length) {
                throw new InvalidFormatException("Should have reached the end!");
            }
        }
    }

    @Override
    public String toString() {
        return "BitmapRLE{" +
                ", height=" + height +
                ", width=" + width +
                ", palette=" + palette +
                '}';
    }
}
