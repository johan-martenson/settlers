package org.appland.settlers.assets;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class NormalizedImageList {
    private final List<Bitmap> originalImages;
    private final List<Bitmap> normalizedImages;
    public int width;
    public int height;
    public int nx;
    public int ny;

    public NormalizedImageList(List<Bitmap> images) {

        int maxWidthBeforeNx = 0;
        int maxWidthAfterNx = 0;
        int maxHeightBelowNy = 0;
        int maxHeightAboveNy = 0;

        // Calculate the normalized width, height, nx, and ny
        for (Bitmap image : images) {
            Area visibleArea = image.getVisibleArea();

            maxWidthBeforeNx = Math.max(maxWidthBeforeNx, image.nx - visibleArea.x);
            maxWidthAfterNx = Math.max(maxWidthAfterNx, visibleArea.width - image.nx + visibleArea.x);

            maxHeightBelowNy = Math.max(maxHeightBelowNy, image.ny - visibleArea.y);
            maxHeightAboveNy = Math.max(maxHeightAboveNy, visibleArea.height - image.ny + visibleArea.y);
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

            if (originalImage.nx < nx) {
                copyTo.x = nx - originalImage.nx;
                copyFrom.x = 0;
            } else {
                copyFrom.x = originalImage.nx - nx;
            }

            if (originalImage.ny < ny) {
                copyTo.y = ny - originalImage.ny;
                copyFrom.y = 0;
            } else {
                copyFrom.y = originalImage.ny - ny;
            }

            normalizedImage.copyNonTransparentPixels(
                    originalImage,
                    copyTo,
                    copyFrom,
                    new Dimension(
                            Math.min(nx, originalImage.nx) + Math.min(width - nx, originalImage.getWidth() - originalImage.nx),
                            Math.min(ny, originalImage.ny) + Math.min(height - ny, originalImage.getHeight() - originalImage.ny)
                    )
            );

            normalizedImage.nx = nx;
            normalizedImage.ny = ny;

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
