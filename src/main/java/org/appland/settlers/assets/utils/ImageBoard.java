package org.appland.settlers.assets.utils;

import org.appland.settlers.assets.TextureFormat;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
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

    public JSONObject placeImage(Bitmap image, Point point, String... metadata) {
        return placeImage(image, point.x, point.y, metadata);
    }

    public JSONObject placeImage(Bitmap image, int x, int y, String... metadata) {
        images.put(image, new ImageOnBoard(image, x, y, metadata));

        ImageOnBoard imageOnBoard = images.get(image);

        JSONObject jsonImageLocation = new JSONObject();

        jsonImageLocation.put("x", imageOnBoard.x);
        jsonImageLocation.put("y", imageOnBoard.y);
        jsonImageLocation.put("width", image.getWidth());
        jsonImageLocation.put("height", image.getHeight());
        jsonImageLocation.put("offsetX", image.getNx());
        jsonImageLocation.put("offsetY", image.getNy());

        return jsonImageLocation;
    }

    public JSONObject placeImageSeries(List<Bitmap> images, Point position, LayoutDirection layoutDirection, String... metadata) {
        return placeImageSeries(images, position.x, position.y, layoutDirection, metadata);
    }

    public JSONObject placeImageSeries(List<Bitmap> images, int x, int y, LayoutDirection layoutDirection, String... metadata) {
        ImageSeries imageSeriesToPlace = new ImageSeries(images, x, y, layoutDirection, metadata);

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
            width = Math.max(width, imageOnBoard.x + imageOnBoard.image.getWidth());
            height = Math.max(height, imageOnBoard.y + imageOnBoard.image.getHeight());
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
        jsonImageLocation.put("width", image.getWidth());
        jsonImageLocation.put("height", image.getHeight());
        jsonImageLocation.put("offsetX", image.getNx());
        jsonImageLocation.put("offsetY", image.getNy());

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

    public JSONObject placeImageBottom(Bitmap image, String... metadata) {
        int currentMaxY = this.getCurrentHeight();

        return placeImage(image, 0, currentMaxY, metadata);
    }

    public JSONObject placeImageSeriesBottomRightOf(int right, List<Bitmap> images, String... metadata) {
        int currentMaxY = this.getCurrentHeightRightOf(right);

        return placeImageSeries(images, new Point(right + 1, currentMaxY), LayoutDirection.ROW, metadata);
    }

    public JSONObject placeImageSeriesBottom(List<Bitmap> images, String... metadata) {
        int currentMaxY = this.getCurrentHeight();

        return placeImageSeries(images, new Point(0, currentMaxY), LayoutDirection.ROW, metadata);
    }

    private static class ImageOnBoard {
        private final int x;
        private final int y;
        private final Bitmap image;
        private final String[] metadata;

        public ImageOnBoard(Bitmap image, int x, int y, String... metadata) {
            this.x = x;
            this.y = y;

            this.image = image;
            this.metadata = metadata;
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
        private final String[] metadata;

        public ImageSeries(List<Bitmap> images, int x, int y, LayoutDirection layoutDirection, String... metadata) {
            this.images = images;
            this.metadata = metadata;

            this.layoutDirection = layoutDirection;

            this.x = x;
            this.y = y;

            Bitmap image = images.getFirst();

            this.width = image.getWidth();
            this.height = image.getHeight();

            this.offsetX = image.getNx();
            this.offsetY = image.getNy();
        }
    }

    public enum LayoutDirection {
        ROW,
        COLUMN
    }

    public int getCurrentWidth() {
        int currentWidth = 0;

        for (ImageOnBoard imageOnBoard : images.values()) {
            currentWidth = Math.max(currentWidth, imageOnBoard.image.getWidth() + imageOnBoard.x);
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

    public int getCurrentHeightRightOf(int right) {
        int currentHeight = 0;

        for (ImageOnBoard imageOnBoard : images.values()) {
            if (imageOnBoard.x + imageOnBoard.image.getWidth() <= right) {
                continue;
            }

            currentHeight = Math.max(currentHeight, imageOnBoard.image.getHeight() + imageOnBoard.y);
        }

        for (ImageSeries oneImageSeries : imageSeries.values()) {
            if (oneImageSeries.layoutDirection == LayoutDirection.ROW) {
                if (oneImageSeries.x + oneImageSeries.width * oneImageSeries.images.size() <= right) {
                    continue;
                }

                currentHeight = Math.max(currentHeight, oneImageSeries.height + oneImageSeries.y);
            } else {
                if (oneImageSeries.x + oneImageSeries.width <= right) {
                    continue;
                }

                currentHeight = Math.max(currentHeight, oneImageSeries.height * oneImageSeries.images.size() + oneImageSeries.y);
            }
        }

        return currentHeight;
    }

    public int getCurrentHeight() {
        int currentHeight = 0;

        for (ImageOnBoard imageOnBoard : images.values()) {
            currentHeight = Math.max(currentHeight, imageOnBoard.image.getHeight() + imageOnBoard.y);
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

    private void addMetadataForImageToRoot(JSONObject jsonRoot, ImageOnBoard imageOnBoard) {
        JSONObject jsonCurrent = jsonRoot;

        for (var label : imageOnBoard.metadata) {
            if (!jsonCurrent.containsKey(label)) {
                jsonCurrent.put(label, new JSONObject());
            }

            jsonCurrent = (JSONObject) jsonCurrent.get(label);
        }

        jsonCurrent.put("x", imageOnBoard.x);
        jsonCurrent.put("y", imageOnBoard.y);
        jsonCurrent.put("width", imageOnBoard.image.getWidth());
        jsonCurrent.put("height", imageOnBoard.image.getHeight());
        jsonCurrent.put("offsetX", imageOnBoard.image.getNx());
        jsonCurrent.put("offsetY", imageOnBoard.image.getNy());
    }

    private void addMetadataForImageSeriesToRoot(JSONObject jsonRoot, ImageSeries imageSeries) {
        JSONObject jsonCurrent = jsonRoot;

        for (var label : imageSeries.metadata) {
            if (!jsonCurrent.containsKey(label)) {
                jsonCurrent.put(label, new JSONObject());
            }

            jsonCurrent = (JSONObject) jsonCurrent.get(label);
        }

        jsonCurrent.put("startX", imageSeries.x);
        jsonCurrent.put("startY", imageSeries.y);
        jsonCurrent.put("width", imageSeries.width);
        jsonCurrent.put("height", imageSeries.height);
        jsonCurrent.put("nrImages", imageSeries.images.size());
        jsonCurrent.put("offsetX", imageSeries.offsetX);
        jsonCurrent.put("offsetY", imageSeries.offsetY);
    }

    public JSONObject getMetadataAsJson() {
        JSONObject jsonRoot = new JSONObject();

        for (ImageOnBoard imageOnBoard : images.values()) {
            addMetadataForImageToRoot(jsonRoot, imageOnBoard);
        }

        for (ImageSeries oneImageSeries : imageSeries.values()) {
            addMetadataForImageSeriesToRoot(jsonRoot, oneImageSeries);
        }

        return jsonRoot;
    }
}
