package org.appland.settlers.assets;

public class PalettedPixelBuffer {
    private final int transparentIndex;
    private final int width;
    private final int height;
    private final byte[] data;

    public PalettedPixelBuffer(int width, int height, int defaultTransparentIndex) {
        this.width = width;
        this.height = height;
        this.transparentIndex = defaultTransparentIndex;

        data = new byte[width * height];
    }

    public void set(int x, int y, byte aByte) {
        data[y * width + x] = aByte;
    }

    public Bitmap toBitmapUsingPalette(Palette palette) {
        Bitmap bitmap = new Bitmap(width, height, palette, TextureFormat.BGRA);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                bitmap.setPixelByColorIndex(x, y, data[y * width + x]);
            }
        }

        return bitmap;
    }
}
