package org.appland.settlers.assets;

import org.appland.settlers.assets.resources.Bitmap;

public class BitmapResource extends GameResource {
    private final Bitmap bitmap;

    public BitmapResource(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public GameResourceType getType() {
        return GameResourceType.BITMAP_RESOURCE;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
