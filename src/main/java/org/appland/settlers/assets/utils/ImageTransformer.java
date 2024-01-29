package org.appland.settlers.assets.utils;

import org.appland.settlers.assets.resources.Bitmap;

import java.util.List;

public class ImageTransformer {
    public static Bitmap makeSilhouette(Bitmap bitmap) {
        Bitmap silhouette = new Bitmap(
                bitmap.getWidth(),
                bitmap.getHeight(),
                bitmap.getNx(),
                bitmap.getNy(),
                bitmap.getPalette(),
                bitmap.getFormat()
        );

        bitmap.forEachPixel((x, y, red, green, blue) -> {
            if (!bitmap.isTransparent(x, y)) {
                silhouette.setPixelValue(x, y, (byte)0xF, (byte)0xF, (byte)0xF, (byte)0xF);
            } else {
                silhouette.setPixelValue(x, y, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0);
            }
        });

        return silhouette;
    }

    public static List<Bitmap> normalizeImageSeries(List<Bitmap> images) {
        NormalizedImageList normalizedImageList = new NormalizedImageList(images);

        return normalizedImageList.getNormalizedImages();
    }
}
