package org.appland.settlers.assets;

import org.appland.settlers.assets.resources.Palette;

import static org.appland.settlers.assets.GameResourceType.PALETTE_RESOURCE;

public class PaletteResource extends GameResource {
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
