package org.appland.settlers.assets.utils;

import org.appland.settlers.assets.Area;
import org.appland.settlers.assets.TextureFormat;
import org.appland.settlers.assets.resources.Bitmap;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class NormalizedImageList {
    private final List<Bitmap> originalImages;
    private final List<Bitmap> normalizedImages;
    public final int width;
    public final int height;
    public final int nx;
    public final int ny;

    public NormalizedImageList(List<Bitmap> images) {

        int maxWidthBeforeNx = 0;
        int maxWidthAfterNx = 0;
        int maxHeightBelowNy = 0;
        int maxHeightAboveNy = 0;

        // Calculate the normalized width, height, nx, and ny
        for (Bitmap image : images) {
            Area visibleArea = image.getVisibleArea();

            maxWidthBeforeNx = Math.max(maxWidthBeforeNx, image.getNx() - visibleArea.x);
            maxWidthAfterNx = Math.max(maxWidthAfterNx, visibleArea.width - image.getNx() + visibleArea.x);

            maxHeightBelowNy = Math.max(maxHeightBelowNy, image.getNy() - visibleArea.y);
            maxHeightAboveNy = Math.max(maxHeightAboveNy, visibleArea.height - image.getNy() + visibleArea.y);
        }

        this.width = maxWidthBeforeNx + maxWidthAfterNx;
        this.height = maxHeightBelowNy + maxHeightAboveNy;
        this.nx = maxWidthBeforeNx;
        this.ny = maxHeightBelowNy;

        // Store the originals
        this.originalImages = images;

        // Create a list of adjusted images where they all share the same width, height, and offsets
        this.normalizedImages = new ArrayList<>();

        for (Bitmap originalImage : originalImages) {
            Bitmap normalizedImage = new Bitmap(width, height, originalImages.get(0).getPalette(), TextureFormat.BGRA);

            Point copyTo = new Point(0, 0);
            Point copyFrom = new Point(0, 0);

            if (originalImage.getNx() < nx) {
                copyTo.x = nx - originalImage.getNx();
                copyFrom.x = 0;
            } else {
                copyFrom.x = originalImage.getNx() - nx;
            }

            if (originalImage.getNy() < ny) {
                copyTo.y = ny - originalImage.getNy();
                copyFrom.y = 0;
            } else {
                copyFrom.y = originalImage.getNy() - ny;
            }

            normalizedImage.copyNonTransparentPixels(
                    originalImage,
                    copyTo,
                    copyFrom,
                    new Dimension(
                            Math.min(nx, originalImage.getNx()) + Math.min(width - nx, originalImage.getWidth() - originalImage.getNx()),
                            Math.min(ny, originalImage.getNy()) + Math.min(height - ny, originalImage.getHeight() - originalImage.getNy())
                    )
            );

            normalizedImage.setNx(nx);
            normalizedImage.setNy(ny);

            this.normalizedImages.add(normalizedImage);
        }
    }

    public List<Bitmap> getNormalizedImages() {
        return this.normalizedImages;
    }

    public int getImageHeight() {
        return height;
    }

    public int getImageWidth() {
        return width;
    }

    public int size() {
        return normalizedImages.size();
    }
}
