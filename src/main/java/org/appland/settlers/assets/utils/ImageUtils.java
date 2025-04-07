package org.appland.settlers.assets.utils;

import org.appland.settlers.assets.BitmapRLEResource;
import org.appland.settlers.assets.BitmapResource;
import org.appland.settlers.assets.GameResource;
import org.appland.settlers.assets.PlayerBitmapResource;
import org.appland.settlers.assets.TextureFormat;
import org.appland.settlers.assets.resources.Bitmap;

import java.awt.Point;
import java.io.IOException;

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
}
