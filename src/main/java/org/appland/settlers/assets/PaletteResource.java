package org.appland.settlers.assets;

import static org.appland.settlers.assets.GameResourceType.PALETTE_RESOURCE;

public class PaletteResource implements GameResource {
    private final Palette palette;

    public PaletteResource(Palette palette) {
        this.palette = palette;
    }

    @Override
    public GameResourceType getType() {
        return PALETTE_RESOURCE;
    }

    public Palette getPalette() {
        return palette;
    }
}
