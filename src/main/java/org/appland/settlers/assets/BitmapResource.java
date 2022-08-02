package org.appland.settlers.assets;

public class BitmapResource implements GameResource {
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
