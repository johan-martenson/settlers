package org.appland.settlers.assets.utils;

import org.appland.settlers.assets.BitmapRLEResource;
import org.appland.settlers.assets.BitmapRawResource;
import org.appland.settlers.assets.BitmapResource;
import org.appland.settlers.assets.GameResource;
import org.appland.settlers.assets.PlayerBitmapResource;
import org.appland.settlers.assets.TextureFormat;
import org.appland.settlers.assets.resources.Bitmap;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    public static void markCenterPoint(Bitmap b) {
        for (int i = 0; i < b.getWidth(); i++) {
            b.setPixelValue(i, b.getNy(), (byte) 0, (byte) 0, (byte) 255, (byte) 255);
        }

        for (int i = 0; i < b.getHeight(); i++) {
            b.setPixelValue(b.getNx(), i, (byte) 0, (byte) 0, (byte) 255, (byte) 255);
        }
    }

    public static Bitmap mergeImages(Bitmap under, Bitmap over, int overOffsetX, int overOffsetY) {
        // TODO: handle player bitmaps
        // TODO: extend the merged image if the overOffsets push the overlay outside the image

        // Find the subset of each image that's non-transparent
        var underVisibleArea = under.getVisibleArea();
        var overVisibleArea = over.getVisibleArea();

        // Distances from the center point to the edges of the combined image
        var left = max(under.getOffsetsForVisibleImage().x, over.getOffsetsForVisibleImage().x);
        var right = max(underVisibleArea.width() - under.getOffsetsForVisibleImage().x, overVisibleArea.width() - over.getOffsetsForVisibleImage().x);
        var up = max(under.getOffsetsForVisibleImage().y, over.getOffsetsForVisibleImage().y);
        var down = max(underVisibleArea.height() - under.getOffsetsForVisibleImage().y, overVisibleArea.height() - over.getOffsetsForVisibleImage().y);

        // Create blank combined image
        var merged = new Bitmap(
                left + right,
                up + down,
                left,
                up,
                under.getPalette(),
                TextureFormat.BGRA
        );

        // Copy the images unto the combined image
        merged.copyNonTransparentPixels(
                under,
                new Point(
                        left - under.getOffsetsForVisibleImage().x,
                        up - under.getOffsetsForVisibleImage().y),
                underVisibleArea.getUpperLeftCoordinate(),
                underVisibleArea.getDimension());

        merged.copyNonTransparentPixels(
                over,
                new Point(
                        left - over.getOffsetsForVisibleImage().x + overOffsetX,
                        up - over.getOffsetsForVisibleImage().y + overOffsetY
                ),
                overVisibleArea.getUpperLeftCoordinate(),
                overVisibleArea.getDimension());

        return merged;
    }

    public static Bitmap mergeImages(Bitmap... images) {
        if (images == null || images.length == 0) {
            throw new IllegalArgumentException("At least one bitmap must be provided");
        }

        if (images.length == 1) {
            return images[0];
        }

        // TODO: handle player bitmaps

        // Find the subset of each image that's non-transparent
        var visibles = Arrays.stream(images)
                .map(bitmap -> Map.entry(bitmap, bitmap.getVisibleArea()))
                .toList();

        // Distances from the center point to the edges of the combined image
        var left = visibles.stream()
                .mapToInt(e -> e.getKey().getOffsetsForVisibleImage().x)
                .max()
                .orElseThrow();

        var right = visibles.stream()
                .mapToInt(e -> e.getValue().width() - e.getKey().getOffsetsForVisibleImage().x)
                .max()
                .orElseThrow();

        var up = visibles.stream()
                .mapToInt(e -> e.getKey().getOffsetsForVisibleImage().y)
                .max()
                .orElseThrow();

        var down = visibles.stream()
                .mapToInt(e -> e.getValue().height() - e.getKey().getOffsetsForVisibleImage().y)
                .max()
                .orElseThrow();

        // Create blank combined image
        var base = images[0];

        var merged = new Bitmap(
                left + right,
                up + down,
                left,
                up,
                base.getPalette(),
                TextureFormat.BGRA
        );

        // Copy the images unto the combined image
        for (var entry : visibles) {
            var bitmap = entry.getKey();
            var visible = entry.getValue();
            var offset = bitmap.getOffsetsForVisibleImage();

            merged.copyNonTransparentPixels(
                    bitmap,
                    new Point(left - offset.x, up - offset.y),
                    visible.getUpperLeftCoordinate(),
                    visible.getDimension()
            );
        }

        return merged;
    }

    public static Bitmap mergeImages(Bitmap under, Bitmap over) {
        // TODO: handle player bitmaps

        // Find the subset of each image that's non-transparent
        var underVisibleArea = under.getVisibleArea();
        var overVisibleArea = over.getVisibleArea();

        // Distances from the center point to the edges of the combined image
        var left = max(under.getOffsetsForVisibleImage().x, over.getOffsetsForVisibleImage().x);
        var right = max(underVisibleArea.width() - under.getOffsetsForVisibleImage().x, overVisibleArea.width() - over.getOffsetsForVisibleImage().x);
        var up = max(under.getOffsetsForVisibleImage().y, over.getOffsetsForVisibleImage().y);
        var down = max(underVisibleArea.height() - under.getOffsetsForVisibleImage().y, overVisibleArea.height() - over.getOffsetsForVisibleImage().y);

        // Create blank combined image
        var merged = new Bitmap(
                left + right,
                up + down,
                left,
                up,
                under.getPalette(),
                TextureFormat.BGRA
        );

        // Copy the images unto the combined image
        merged.copyNonTransparentPixels(
                under,
                new Point(
                        left - under.getOffsetsForVisibleImage().x,
                        up - under.getOffsetsForVisibleImage().y),
                underVisibleArea.getUpperLeftCoordinate(),
                underVisibleArea.getDimension());

        merged.copyNonTransparentPixels(
                over,
                new Point(
                        left - over.getOffsetsForVisibleImage().x,
                        up - over.getOffsetsForVisibleImage().y
                ),
                overVisibleArea.getUpperLeftCoordinate(),
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

    public static List<Bitmap> composeBuildingAnimation(List<GameResource> gameResources, int baseIndex, List<GameResource> overlayResources, int overlayIndex, int frames, int stride, int overlayOffsetX, int overlayOffsetY) {
        var animation = new ArrayList<Bitmap>();
        var staticImage = ImageUtils.getBitmapFromResource(gameResources.get(baseIndex));

        for (int i = 0; i < frames; i++) {
            var overlayAnimationFrame = ImageUtils.getBitmapFromResource(overlayResources.get(overlayIndex + stride * i));
            var merged = ImageUtils.mergeImages(staticImage, overlayAnimationFrame, overlayOffsetX, overlayOffsetY);
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

    public static Bitmap toBitmap(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage argbImage = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_4BYTE_ABGR
        );

        argbImage.getGraphics().drawImage(image, 0, 0, null);

        // Extract raw BGRA bytes (ABGR layout in memory)
        byte[] abgr = ((DataBufferByte) argbImage.getRaster().getDataBuffer()).getData();
        byte[] bgra = new byte[abgr.length];

        // Convert ABGR to BGRA
        for (int i = 0; i < abgr.length; i += 4) {
            byte a = abgr[i];
            byte b = abgr[i + 1];
            byte g = abgr[i + 2];
            byte r = abgr[i + 3];

            bgra[i]     = b;
            bgra[i + 1] = g;
            bgra[i + 2] = r;
            bgra[i + 3] = a;
        }

        var bitmap = new Bitmap(width, height, 0, 0, null, TextureFormat.BGRA);
        bitmap.setImageDataFromBuffer(bgra);

        return bitmap;
    }
}
