package org.appland.settlers.assets.utils;

import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.PlayerBitmap;
import org.appland.settlers.model.PlayerColor;

import java.util.ArrayList;
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

        bitmap.forEachPixel((x, y, red, green, blue, alpha) -> {
            if (!bitmap.isTransparent(x, y)) {
                silhouette.setPixelValue(x, y, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF);
            }
        });

        return silhouette;
    }

    public static List<Bitmap> normalizeImageSeries(List<Bitmap> images) {
        NormalizedImageList normalizedImageList = new NormalizedImageList(images);

        return normalizedImageList.getNormalizedImages();
    }

    public static List<Bitmap> makeGetHitAnimation(Bitmap image) {
        var whiteSilhouette = makeSilhouette(image);

        List<Bitmap> getHitAnimation = new ArrayList<>();

        getHitAnimation.add(image);
        getHitAnimation.add(image);
        getHitAnimation.add(image);
        getHitAnimation.add(whiteSilhouette);
        getHitAnimation.add(whiteSilhouette);
        getHitAnimation.add(whiteSilhouette);
        getHitAnimation.add(image);
        getHitAnimation.add(image);

        return getHitAnimation;
    }

    public static List<Bitmap> drawForPlayer(PlayerColor playerColor, List<? extends Bitmap> images) {
        return images.stream()
                .map(image -> {
                    if (image instanceof PlayerBitmap playerBitmap) {
                        return playerBitmap.getBitmapForPlayer(playerColor);
                    } else {
                        return image;
                    }
                }).toList();
    }
}
