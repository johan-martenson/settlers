package org.appland.settlers.assets.resources;

import org.appland.settlers.assets.TextureFormat;

import java.util.List;

/**
 * Represents an LBM file that extends a Bitmap with support for palette animations.
 */
public class AnimatedLBMFile extends Bitmap {
    private List<AnimatedPalette> animatedPaletteList;
    private long length = 0;

    /**
     * Constructs an LBMFile with the specified dimensions, default palette, and desired texture format.
     *
     * @param width               The width of the bitmap.
     * @param height              The height of the bitmap.
     * @param defaultPalette       The default palette for the bitmap.
     * @param wantedTextureFormat  The texture format desired for the bitmap.
     */
    public AnimatedLBMFile(int width, int height, Palette defaultPalette, TextureFormat wantedTextureFormat) {
        super(width, height, defaultPalette, wantedTextureFormat);
    }

    /**
     * Sets the list of palette animations for the LBM file.
     *
     * @param animatedPaletteList The list of palette animations to set.
     */
    public void setPaletteAnimations(List<AnimatedPalette> animatedPaletteList) {
        this.animatedPaletteList = animatedPaletteList;
    }

    /**
     * Returns the list of palette animations.
     *
     * @return The list of palette animations.
     */
    public List<AnimatedPalette> getPaletteAnimations() {
        return animatedPaletteList;
    }

    /**
     * Sets the length of the LBM file.
     *
     * @param length The length to set.
     */
    public void setLength(long length) {
        this.length = length;
    }
}
