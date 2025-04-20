package org.appland.settlers.assets.utils;

import org.appland.settlers.assets.BitmapRLEResource;
import org.appland.settlers.assets.BitmapRawResource;
import org.appland.settlers.assets.BitmapResource;
import org.appland.settlers.assets.GameResource;
import org.appland.settlers.assets.PlayerBitmapResource;
import org.appland.settlers.assets.TextureFormat;
import org.appland.settlers.assets.resources.Bitmap;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static hqx.Hqx_2x.hq2x_32_rb;
import static hqx.Hqx_4x.hq4x_32_rb;
import static java.lang.Math.max;

public class ImageUtils {

    public static void writeImageResourceToFile(GameResource gameResource, String outFilename) throws IOException {
        switch (gameResource.getType()) {
            case BITMAP_RLE:
                BitmapRLEResource headquarterRLEBitmapResource = (BitmapRLEResource) gameResource;
                headquarterRLEBitmapResource.getBitmap().writeToFile(outFilename);
                break;

            case PLAYER_BITMAP_RESOURCE:
                PlayerBitmapResource playerBitmapResource = (PlayerBitmapResource) gameResource;
                playerBitmapResource.getBitmap().writeToFile(outFilename);
                break;

            case BITMAP_RESOURCE:
                BitmapResource bitmapResource = (BitmapResource) gameResource;
                bitmapResource.getBitmap().writeToFile(outFilename);
                break;

            default:
                throw new RuntimeException("CANNOT HANDLE " + gameResource.getClass());
        }
    }

    public static Bitmap mergeImages(Bitmap under, Bitmap over) {
        // TODO: handle player bitmaps

        var underVisibleArea = under.getVisibleArea();
        var overVisibleArea = over.getVisibleArea();

        var underVisibilityStartX = underVisibleArea.x();
        var underVisibilityStartY = underVisibleArea.y();
        var underVisibilityEndX = underVisibleArea.width() + underVisibilityStartX;
        var underVisibilityEndY = underVisibleArea.height() + underVisibilityStartY;

        var overVisibilityStartX = overVisibleArea.x();
        var overVisibilityStartY = overVisibleArea.y();
        var overVisibilityEndX = overVisibleArea.width() + overVisibilityStartX;
        var overVisibilityEndY = overVisibleArea.height() + overVisibilityStartY;

        var left = max(under.getOrigin().x - underVisibilityStartX, over.getOrigin().x - overVisibilityStartX);
        var right = max(underVisibilityEndX - under.getOrigin().x, overVisibilityEndX - over.getOrigin().x);
        var up = max(under.getOrigin().y - underVisibilityStartY, over.getOrigin().y - overVisibilityStartY);
        var down = max(underVisibilityEndY - under.getOrigin().y, overVisibilityEndY - over.getOrigin().y);

        var merged = new Bitmap(
                left + right,
                up + down,
                left,
                up,
                under.getPalette(),
                TextureFormat.BGRA
        );

        merged.copyNonTransparentPixels(
                under,
                new Point(
                        left - (under.getOrigin().x - underVisibilityStartX),
                        up - (under.getOrigin().y - underVisibilityStartY)),
                new Point(
                        underVisibilityStartX,
                        underVisibilityStartY),
                underVisibleArea.getDimension());

        merged.copyNonTransparentPixels(
                over,
                new Point(
                        left - (over.getOrigin().x - overVisibilityStartX),
                        up - (over.getOrigin().y - overVisibilityStartY)
                ),
                new Point(
                        overVisibilityStartX,
                        overVisibilityStartY
                ),
                overVisibleArea.getDimension());

        return merged;
    }

    public static Bitmap getBitmapFromResource(GameResource imageResource) {
        return switch (imageResource) {
            case BitmapResource bitmapResource -> bitmapResource.getBitmap();
            case BitmapRLEResource bitmapRLEResource -> bitmapRLEResource.getBitmap();
            case BitmapRawResource bitmapRawResource -> bitmapRawResource.getBitmap();
            case PlayerBitmapResource playerBitmapResource -> playerBitmapResource.getBitmap();
            default -> throw new RuntimeException("CANNOT HANDLE " + imageResource.getClass());
        };
    }

    public static List<Bitmap> readAnimationFromResourceList(List<GameResource> resources, int index, int count) {
        return resources.subList(index, index + count).stream().map(ImageUtils::getBitmapFromResource).toList();
    }

    public static List<Bitmap> readAnimationFromResourceList(List<GameResource> resources, int index, int count, int stride) {
        var animation = new ArrayList<Bitmap>();

        for (int i = index; i < index + count; i += stride) {
            animation.add(getBitmapFromResource(resources.get(i)));
        }

        return animation;
    }

    public static List<Bitmap> composeBuildingAnimation(List<GameResource> gameResources, int baseIndex, int overlayIndex, int frames, int stride) {
        var animation = new ArrayList<Bitmap>();
        var staticImage = ImageUtils.getBitmapFromResource(gameResources.get(baseIndex));

        for (int i = 0; i < frames; i++) {
            var overlayAnimation = ImageUtils.getBitmapFromResource(gameResources.get(overlayIndex + stride * i));
            var merged = ImageUtils.mergeImages(staticImage, overlayAnimation);

            animation.add(merged);
        }

        return animation;
    }

    public static Bitmap scaleTo2x(Bitmap bitmap) {
        var scaledArgb = new int[bitmap.getWidth() * bitmap.getHeight() * 4];

        hq2x_32_rb(bgraToArgb(bitmap.getImageData()), scaledArgb, bitmap.getWidth(), bitmap.getHeight());

        var out = new Bitmap(bitmap.getWidth() * 2, bitmap.getHeight() * 2, 0, 0, bitmap.getPalette(), TextureFormat.BGRA);

        out.setImageDataFromBuffer(argbToBgra(scaledArgb));

        return out;
    }

    public static Bitmap scaleTo4x(Bitmap bitmap) {
        var scaledArgb = new int[bitmap.getWidth() * bitmap.getHeight() * 4];

        hq4x_32_rb(bgraToArgb(bitmap.getImageData()), scaledArgb, bitmap.getWidth(), bitmap.getHeight());

        var out = new Bitmap(bitmap.getWidth() * 2, bitmap.getHeight() * 2, 0, 0, bitmap.getPalette(), TextureFormat.BGRA);

        out.setImageDataFromBuffer(argbToBgra(scaledArgb));

        return out;
    }

    public static int[] bgraToArgb(byte[] bgra) {
        int pixelCount = bgra.length / 4;
        int[] argb = new int[pixelCount];

        for (int i = 0, j = 0; i < pixelCount; i++) {
            int b = bgra[j++] & 0xFF;
            int g = bgra[j++] & 0xFF;
            int r = bgra[j++] & 0xFF;
            int a = bgra[j++] & 0xFF;

            argb[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }

        return argb;
    }

    public static byte[] argbToBgra(int[] argb) {
        byte[] bgra = new byte[argb.length * 4];

        for (int i = 0, j = 0; i < argb.length; i++) {
            int pixel = argb[i];
            bgra[j++] = (byte)(pixel & 0xFF);        // B
            bgra[j++] = (byte)((pixel >> 8) & 0xFF);  // G
            bgra[j++] = (byte)((pixel >> 16) & 0xFF); // R
            bgra[j++] = (byte)((pixel >> 24) & 0xFF); // A
        }

        return bgra;
    }
}
