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

    public JSONObject placeImageSeries(List<Bitmap> images, Point position, LayoutDirection layoutDirection) {
        return placeImageSeries(images, position.x, position.y, layoutDirection);
    }

    public JSONObject placeImageSeries(List<Bitmap> images, int x, int y, LayoutDirection layoutDirection) {
        ImageSeries imageSeriesToPlace = new ImageSeries(images, x, y, layoutDirection);

        imageSeries.put(images, imageSeriesToPlace);

        JSONObject jsonImages = new JSONObject();

        jsonImages.put("startX", imageSeriesToPlace.x);
        jsonImages.put("startY", imageSeriesToPlace.y);
        jsonImages.put("width", imageSeriesToPlace.width);
        jsonImages.put("height", imageSeriesToPlace.height);
        jsonImages.put("nrImages", imageSeriesToPlace.images.size());
        jsonImages.put("offsetX", imageSeriesToPlace.offsetX);
        jsonImages.put("offsetY", imageSeriesToPlace.offsetY);

        return jsonImages;
    }

    public Bitmap writeBoardToBitmap(Palette palette) {

        // Calculate the needed size of the bitmap
        int width = 0;
        int height = 0;

        for (ImageOnBoard imageOnBoard : images.values()) {
            width = Math.max(width, imageOnBoard.x + imageOnBoard.image.width);
            height = Math.max(height, imageOnBoard.y + imageOnBoard.image.height);
        }

        for (ImageSeries oneImageSeries : imageSeries.values()) {

            if (oneImageSeries.layoutDirection == LayoutDirection.ROW) {
                width = Math.max(width, oneImageSeries.x + oneImageSeries.width * oneImageSeries.images.size());
                height = Math.max(height, oneImageSeries.y + oneImageSeries.height);
            } else {
                width = Math.max(width, oneImageSeries.x + oneImageSeries.width);
                height = Math.max(height, oneImageSeries.y + oneImageSeries.height * oneImageSeries.images.size());
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

        for (ImageSeries oneImageSeries : imageSeries.values()) {

            if (oneImageSeries.layoutDirection == LayoutDirection.ROW) {
                for (int i = 0; i < oneImageSeries.images.size(); i++) {
                    Bitmap image = oneImageSeries.images.get(i);

                    imageBoard.copyNonTransparentPixels(
                            image,
                            new Point(oneImageSeries.x + oneImageSeries.width * i, oneImageSeries.y),
                            new Point(0, 0),
                            image.getDimension()
                    );
                }
            } else {
                for (int i = 0; i < oneImageSeries.images.size(); i++) {
                    Bitmap image = oneImageSeries.images.get(i);

                    imageBoard.copyNonTransparentPixels(
                            image,
                            new Point(oneImageSeries.x, oneImageSeries.y + oneImageSeries.height * i),
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
        ImageSeries oneImageSeries = this.imageSeries.get(images);

        JSONObject jsonImages = new JSONObject();

        jsonImages.put("startX", oneImageSeries.x);
        jsonImages.put("startY", oneImageSeries.y);
        jsonImages.put("width", oneImageSeries.width);
        jsonImages.put("height", oneImageSeries.height);
        jsonImages.put("nrImages", oneImageSeries.images.size());
        jsonImages.put("offsetX", oneImageSeries.offsetX);
        jsonImages.put("offsetY", oneImageSeries.offsetY);

        return jsonImages;
    }

    public void placeImageBottom(Bitmap image) {
        int currentMaxY = this.getCurrentHeight();

        placeImage(image, 0, currentMaxY);
    }

    public JSONObject placeImageSeriesBottom(List<Bitmap> images) {
        int currentMaxY = this.getCurrentHeight();

        return placeImageSeries(images, new Point(0, currentMaxY), LayoutDirection.ROW);
    }

    private static class ImageOnBoard {
        private final int x;
        private final int y;
        private final Bitmap image;

        public ImageOnBoard(Bitmap image, int x, int y) {
            this.x = x;
            this.y = y;

            this.image = image;
        }
    }

    private static class ImageSeries {

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

        for (ImageSeries oneImageSeries : imageSeries.values()) {

            if (oneImageSeries.layoutDirection == LayoutDirection.ROW) {
                currentWidth = Math.max(currentWidth, oneImageSeries.width * oneImageSeries.images.size() + oneImageSeries.x);
            } else {
                currentWidth = Math.max(currentWidth, oneImageSeries.width + oneImageSeries.x);
            }
        }

        return currentWidth;
    }

    public int getCurrentHeight() {
        int currentHeight = 0;

        for (ImageOnBoard imageOnBoard : images.values()) {
            currentHeight = Math.max(currentHeight, imageOnBoard.image.height + imageOnBoard.y);
        }

        for (ImageSeries oneImageSeries : imageSeries.values()) {
            if (oneImageSeries.layoutDirection == LayoutDirection.ROW) {
                currentHeight = Math.max(currentHeight, oneImageSeries.height + oneImageSeries.y);
            } else {
                currentHeight = Math.max(currentHeight, oneImageSeries.height * oneImageSeries.images.size() + oneImageSeries.y);
            }
        }

        return currentHeight;
    }
}
