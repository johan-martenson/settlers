package org.appland.settlers.assets;

import org.appland.settlers.assets.resources.Bitmap;

/**
 * Represents a game resource containing a bitmap.
 */
public class BitmapResource extends GameResource {
    private final Bitmap bitmap;

    public BitmapResource(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    /**
     * Retrieves the type of the game resource.
     *
     * @return The resource type, which is {@link GameResourceType#BITMAP_RESOURCE}.
     */
    @Override
    public GameResourceType getType() {
        return GameResourceType.BITMAP_RESOURCE;
    }

    /**
     * Retrieves the bitmap data.
     *
     * @return The {@link Bitmap} object representing the bitmap resource.
     */
    public Bitmap getBitmap() {
        return bitmap;
    }
}
