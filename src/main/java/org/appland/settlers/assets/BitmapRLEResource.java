package org.appland.settlers.assets;

import org.appland.settlers.assets.resources.BitmapRLE;

public class BitmapRLEResource extends GameResource {
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
