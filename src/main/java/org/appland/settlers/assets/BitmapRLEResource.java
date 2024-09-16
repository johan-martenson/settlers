package org.appland.settlers.assets;

import org.appland.settlers.assets.resources.BitmapRLE;

/**
 * Represents a game resource containing a run-length encoded (RLE) bitmap.
 */
public class BitmapRLEResource extends GameResource {
    private final BitmapRLE bitmap;

    public BitmapRLEResource(BitmapRLE bitmapRLE) {
        this.bitmap = bitmapRLE;
    }

    /**
     * Retrieves the type of the game resource.
     *
     * @return The resource type, which is {@link GameResourceType#BITMAP_RLE}.
     */
    @Override
    public GameResourceType getType() {
        return GameResourceType.BITMAP_RLE;
    }

    /**
     * Retrieves the run-length encoded bitmap data.
     *
     * @return The {@link BitmapRLE} object representing the RLE bitmap resource.
     */
    public BitmapRLE getBitmap() {
        return bitmap;
    }
}
