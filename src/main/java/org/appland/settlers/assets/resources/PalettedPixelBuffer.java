package org.appland.settlers.assets.resources;

import org.appland.settlers.assets.TextureFormat;

public class PalettedPixelBuffer extends Bitmap {
    private final int transparentIndex;

    public PalettedPixelBuffer(int width, int height, int defaultTransparentIndex, Palette palette) {
        super(width, height, palette, TextureFormat.PALETTED);

        this.transparentIndex = defaultTransparentIndex;
    }

    public void set(int x, int y, short aByte) {
        setPixelByColorIndex(x, y, aByte);
    }
}
