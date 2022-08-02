package org.appland.settlers.assets;

import java.util.List;

public class LBMFile {
    private final Bitmap bitmap;
    private List<PaletteAnim> paletteAnimList;
    private long length;

    public LBMFile(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setPalette(Palette palette) {
        this.bitmap.setPalette(palette);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Palette getPalette() {
        return bitmap.getPalette();
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
