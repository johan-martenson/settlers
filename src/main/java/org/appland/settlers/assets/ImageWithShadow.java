package org.appland.settlers.assets;

import org.appland.settlers.assets.resources.Bitmap;

public class ImageWithShadow {
    public final Bitmap image;
    public final Bitmap shadowImage;

    public ImageWithShadow(Bitmap image, Bitmap shadowImage) {
        this.image = image;
        this.shadowImage = shadowImage;
    }
}
