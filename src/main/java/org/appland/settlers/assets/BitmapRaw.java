package org.appland.settlers.assets;

public class BitmapRaw extends Bitmap {
    private final long length;

    public BitmapRaw(int width, int height, long length, Palette palette, TextureFormat format) {
        super(width, height, palette, format);

        this.length = length;
    }

    public long getLength() {
        return length;
    }
}
