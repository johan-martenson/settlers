package org.appland.settlers.assets;

public class BitmapRLEResource implements GameResource {
    private final BitmapRLE bitmap;

    public BitmapRLEResource(BitmapRLE bitmapRLE) {
        this.bitmap = bitmapRLE;
    }

    @Override
    public GameResourceType getType() {
        return GameResourceType.BITMAP_RLE;
    }

    public BitmapRLE getBitmap() {
        return bitmap;
    }
}
