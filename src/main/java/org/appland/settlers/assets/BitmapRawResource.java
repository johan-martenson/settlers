package org.appland.settlers.assets;

import org.appland.settlers.assets.resources.BitmapRaw;

public class BitmapRawResource implements GameResource {
    private final BitmapRaw bitmap;

    public BitmapRawResource(BitmapRaw bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public GameResourceType getType() {
        return GameResourceType.BITMAP_RAW;
    }

    public BitmapRaw getBitmap() {
        return this.bitmap;
    }
}
