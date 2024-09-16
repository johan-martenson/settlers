package org.appland.settlers.assets.resources;

import org.appland.settlers.assets.TextureFormat;

public class BitmapRaw extends Bitmap {
    private final long length;

    /**
     * Constructs a raw bitmap with the specified dimensions, origin, length, palette, and texture format.
     *
     * @param width   The width of the bitmap.
     * @param height  The height of the bitmap.
     * @param nx      The x-coordinate of the origin.
     * @param ny      The y-coordinate of the origin.
     * @param length  The length of the pixel data.
     * @param palette The color palette used in the bitmap.
     * @param format  The texture format of the bitmap.
     */
    public BitmapRaw(int width, int height, int nx, int ny, long length, Palette palette, TextureFormat format) {
        super(width, height, nx, ny, palette, format);

        this.length = length;
    }

    /**
     * Returns the length of the pixel data in the bitmap.
     *
     * @return The length of the pixel data.
     */
    public long getLength() {
        return length;
    }
}
