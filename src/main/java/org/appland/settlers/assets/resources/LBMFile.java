package org.appland.settlers.assets.resources;

import org.appland.settlers.assets.TextureFormat;

import java.util.List;

public class LBMFile extends Bitmap {
    private List<PaletteAnim> paletteAnimList;
    private long length;

    public LBMFile(int width, int height, Palette defaultPalette, TextureFormat wantedTextureFormat) {
        super(width, height, defaultPalette, wantedTextureFormat);
    }

    public void setAnimPalettes(List<PaletteAnim> paletteAnimList) {
        this.paletteAnimList = paletteAnimList;
    }

    public List<PaletteAnim> getPaletteAnimList() {
        return paletteAnimList;
    }

    public void setLength(long length) {
        this.length = length;
    }
}
