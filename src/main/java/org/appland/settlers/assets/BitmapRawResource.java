package org.appland.settlers.assets;

import org.appland.settlers.assets.resources.BitmapRaw;

/**
 * Represents a game resource containing raw bitmap data.
 */
public class BitmapRawResource extends GameResource {
    private final BitmapRaw bitmap;

    public BitmapRawResource(BitmapRaw bitmap) {
        this.bitmap = bitmap;
    }

    /**
     * Retrieves the type of the game resource.
     *
     * @return The resource type, which is {@link GameResourceType#BITMAP_RAW}.
     */
    @Override
    public GameResourceType getType() {
        return GameResourceType.BITMAP_RAW;
    }

    /**
     * Retrieves the raw bitmap data.
     *
     * @return The {@link BitmapRaw} object representing the raw bitmap.
     */
    public BitmapRaw getBitmap() {
        return this.bitmap;
    }
}
