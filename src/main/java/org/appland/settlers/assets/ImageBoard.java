package org.appland.settlers.assets;

import org.json.simple.JSONObject;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageBoard {

    private final Map<Bitmap, ImageOnBoard> images;
    private final Map<List<Bitmap>, ImageSeries> imageSeries;

    public ImageBoard() {
        images = new HashMap<>();
        imageSeries = new HashMap<>();
    }

    public void placeImage(Bitmap image, Point point) {
        images.put(image, new ImageOnBoard(image, point.x, point.y));
    }

    public void placeImage(Bitmap image, int x, int y) {
        images.put(image, new ImageOnBoard(image, x, y));
    }

    public void placeImageSeries(List<Bitmap> images, Point position, LayoutDirection layoutDirection) {
        placeImageSeries(images, position.x, position.y, layoutDirection);
    }

    public void placeImageSeries(List<Bitmap> images, int x, int y, LayoutDirection layoutDirection) {
        imageSeries.put(images, new ImageSeries(images, x, y, layoutDirection));
    }

    public Bitmap writeBoardToBitmap(Palette palette) {

        // Calculate the needed size of the bitmap
        int width = 0;
        int height = 0;

        for (ImageOnBoard imageOnBoard : images.values()) {
            width = Math.max(width, imageOnBoard.x + imageOnBoard.image.width);
            height = Math.max(height, imageOnBoard.y + imageOnBoard.image.height);
        }

        for (ImageSeries imageSeries : imageSeries.values()) {

            if (imageSeries.layoutDirection == LayoutDirection.ROW) {
                width = Math.max(width, imageSeries.x + imageSeries.width * imageSeries.images.size());
                height = Math.max(height, imageSeries.y + imageSeries.height);
            } else {
                width = Math.max(width, imageSeries.x + imageSeries.width);
                height = Math.max(height, imageSeries.y + imageSeries.height * imageSeries.images.size());
            }
        }

        // Create the bitmap
        Bitmap imageBoard = new Bitmap(width, height, palette, TextureFormat.BGRA);

        // Copy all images onto the board
        for (ImageOnBoard imageOnBoard : images.values()) {
            imageBoard.copyNonTransparentPixels(
                    imageOnBoard.image,
                    new Point(imageOnBoard.x, imageOnBoard.y),
                    new Point(0, 0),
                    imageOnBoard.image.getDimension());
        }

        for (ImageSeries imageSeries : imageSeries.values()) {

            if (imageSeries.layoutDirection == LayoutDirection.ROW) {
                for (int i = 0; i < imageSeries.images.size(); i++) {
                    Bitmap image = imageSeries.images.get(i);

                    imageBoard.copyNonTransparentPixels(
                            image,
                            new Point(imageSeries.x + imageSeries.width * i, imageSeries.y),
                            new Point(0, 0),
                            image.getDimension()
                    );
                }
            } else {
                for (int i = 0; i < imageSeries.images.size(); i++) {
                    Bitmap image = imageSeries.images.get(i);

                    imageBoard.copyNonTransparentPixels(
                            image,
                            new Point(imageSeries.x, imageSeries.y + imageSeries.height * i),
                            new Point(0, 0),
                            image.getDimension()
                    );
                }
            }
        }

        return imageBoard;
    }

    public JSONObject imageLocationToJson(Bitmap image) {
        ImageOnBoard imageOnBoard = images.get(image);

        JSONObject jsonImageLocation = new JSONObject();

        jsonImageLocation.put("x", imageOnBoard.x);
        jsonImageLocation.put("y", imageOnBoard.y);
        jsonImageLocation.put("width", image.width);
        jsonImageLocation.put("height", image.height);
        jsonImageLocation.put("offsetX", image.nx);
        jsonImageLocation.put("offsetY", image.ny);

        return jsonImageLocation;
    }

    public JSONObject imageSeriesLocationToJson(List<Bitmap> images) {
        ImageSeries imageSeries = this.imageSeries.get(images);

        JSONObject jsonImages = new JSONObject();

        jsonImages.put("startX", imageSeries.x);
        jsonImages.put("startY", imageSeries.y);
        jsonImages.put("width", imageSeries.width);
        jsonImages.put("height", imageSeries.height);
        jsonImages.put("nrImages", imageSeries.images.size());
        jsonImages.put("offsetX", imageSeries.offsetX);
        jsonImages.put("offsetY", imageSeries.offsetY);

        return jsonImages;
    }

    private class ImageOnBoard {
        private final int x;
        private final int y;
        private final Bitmap image;

        public ImageOnBoard(Bitmap image, int x, int y) {
            this.x = x;
            this.y = y;

            this.image = image;
        }
    }

    private class ImageSeries {

        private final List<Bitmap> images;
        private final int x;
        private final int y;
        public final int width;
        private final int height;
        private final int offsetX;
        private final int offsetY;
        private final LayoutDirection layoutDirection;

        public ImageSeries(List<Bitmap> images, int x, int y, LayoutDirection layoutDirection) {
            this.images = images;

            this.layoutDirection = layoutDirection;

            this.x = x;
            this.y = y;

            Bitmap image = images.get(0);

            this.width = image.width;
            this.height = image.height;

            this.offsetX = image.nx;
            this.offsetY = image.ny;
        }
    }

    public enum LayoutDirection {
        ROW,
        COLUMN
    }

    public int getCurrentWidth() {
        int currentWidth = 0;

        for (ImageOnBoard imageOnBoard : images.values()) {
            currentWidth = Math.max(currentWidth, imageOnBoard.image.width + imageOnBoard.x);
        }

        for (ImageSeries imageSeries : imageSeries.values()) {

            if (imageSeries.layoutDirection == LayoutDirection.ROW) {
                currentWidth = Math.max(currentWidth, imageSeries.width * imageSeries.images.size() + imageSeries.x);
            } else {
                currentWidth = Math.max(currentWidth, imageSeries.width + imageSeries.x);
            }
        }

        return currentWidth;
    }
}
