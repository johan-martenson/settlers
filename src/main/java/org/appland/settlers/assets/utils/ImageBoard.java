package org.appland.settlers.assets.utils;

import org.appland.settlers.assets.TextureFormat;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.json.simple.JSONObject;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageBoard {
    private final Map<Bitmap, ImageOnBoard> images = new HashMap<>();
    private final Map<List<Bitmap>, ImageSeries> imageSeries = new HashMap<>();

    /**
     * Places images in a column layout on the board.
     *
     * @param imagePathPairs the collection of image path pairs
     */
    public void placeImagesAsColumn(Collection<ImagePathPair> imagePathPairs) {
        int y = 0;

        for (var imagePathPair : imagePathPairs) {
            placeImage(imagePathPair.image(), 0, y, imagePathPair.path());

            y += imagePathPair.image().getHeight();
        }
    }

    /**
     * Places images in a row layout on the board.
     *
     * @param imagePathPairs the collection of image path pairs
     */
    public void placeImagesAsRow(Collection<ImagePathPair> imagePathPairs) {
        int x = 0;
        int y = getCurrentHeight();

        for (var imagePathPair : imagePathPairs) {
            placeImage(imagePathPair.image(), x, y, imagePathPair.path());

            x += imagePathPair.image().getWidth();
        }
    }

    /**
     * Writes the image board and its metadata to files.
     *
     * @param directory    the directory to save the files
     * @param baseFilename the base filename for the output files
     * @param palette      the palette to use for the images
     * @throws IOException if an I/O error occurs
     */
    public void writeBoard(String directory, String baseFilename, Palette palette) throws IOException {
        Path pathBitmapFile = Paths.get(directory, String.format("%s.png", baseFilename));
        Path pathJsonFile = Paths.get(directory, String.format("%s.json", baseFilename));

        writeBoardToBitmap(palette).writeToFile(pathBitmapFile);
        Files.writeString(pathJsonFile, getMetadataAsJson().toJSONString());
    }

    /**
     * Places an image to the right of the existing images on the board.
     *
     * @param image    the bitmap image to place
     * @param metadata the metadata associated with the image
     */
    public void placeImageRightOf(Bitmap image, String... metadata) {
        int x = getCurrentWidth();

        placeImage(image, x, 0, metadata);
    }

    /**
     * Places images at the bottom-right of the specified right edge on the board.
     *
     * @param imagePathPairs the collection of image path pairs
     * @param right          the right edge of the placement
     */
    public void placeImagesAtBottomRightOf(Collection<ImagePathPair> imagePathPairs, int right) {
        int x = right;
        int y = getCurrentHeightRightOf(right);

        for (var imagePathPair : imagePathPairs) {
            placeImage(imagePathPair.image, x, y, imagePathPair.path);

            x += imagePathPair.image.getWidth();
        }
    }

    /**
     * Places images in a row layout to the right of existing images on the board.
     *
     * @param imagePathPairs the collection of image path pairs
     */
    public void placeImagesAsRowRightOf(Collection<ImagePathPair> imagePathPairs) {
        int x = getCurrentWidth();
        int y = getCurrentHeightRightOf(x);

        for (var imagePathPair : imagePathPairs) {
            placeImage(imagePathPair.image, x, y, imagePathPair.path);

            x += imagePathPair.image.getWidth();
        }
    }

    public record ImagePathPair(Bitmap image, String[] path) { }

    /**
     * Creates a new ImagePathPair record.
     *
     * @param value the bitmap image
     * @param path  the metadata path
     * @return the ImagePathPair record
     */
    public static ImagePathPair makeImagePathPair(Bitmap value, String... path) {
        return new ImagePathPair(value, path);
    }

    /**
     * Places an image at the specified position on the board.
     *
     * @param image    the bitmap image to place
     * @param point    the position to place the image
     * @param metadata the metadata associated with the image
     * @return the JSON object representing the image location
     */
    public JSONObject placeImage(Bitmap image, Point point, String... metadata) {
        return placeImage(image, point.x, point.y, metadata);
    }

    /**
     * Places an image at the specified coordinates on the board.
     *
     * @param image    the bitmap image to place
     * @param x        the x-coordinate
     * @param y        the y-coordinate
     * @param metadata the metadata associated with the image
     * @return the JSON object representing the image location
     */
    public JSONObject placeImage(Bitmap image, int x, int y, String... metadata) {
        if (image == null) {
            throw new RuntimeException("Image is null for path: " + Arrays.asList(metadata));
        }

        if (images.containsKey(image)) {
            throw new RuntimeException(String.format("Image already exists! New metadata: %s, Prev metadata: %s",
                    Arrays.asList(metadata),
                    Arrays.asList(images.get(image).metadata())));
        }

        images.put(image, new ImageOnBoard(image, x, y, metadata));
        ImageOnBoard imageOnBoard = images.get(image);

        return new JSONObject(Map.of(
                "x", imageOnBoard.x,
                "y", imageOnBoard.y,
                "width", image.getWidth(),
                "height", image.getHeight(),
                "offsetX", image.getNx(),
                "offsetY", image.getNy()
        ));
    }

    /**
     * Places a series of images at the specified position on the board.
     *
     * @param images          the list of bitmap images to place
     * @param position        the starting position
     * @param layoutDirection the layout direction (ROW or COLUMN)
     * @param metadata        the metadata associated with the images
     * @return the JSON object representing the image series location
     */
    public JSONObject placeImageSeries(List<Bitmap> images, Point position, LayoutDirection layoutDirection, String... metadata) {
        return placeImageSeries(images, position.x, position.y, layoutDirection, metadata);
    }

    /**
     * Places a series of images at the specified coordinates on the board.
     *
     * @param images          the list of bitmap images to place
     * @param x               the x-coordinate
     * @param y               the y-coordinate
     * @param layoutDirection the layout direction (ROW or COLUMN)
     * @param metadata        the metadata associated with the images
     * @return the JSON object representing the image series location
     */
    public JSONObject placeImageSeries(List<Bitmap> images, int x, int y, LayoutDirection layoutDirection, String... metadata) {
        ImageSeries imageSeriesToPlace = new ImageSeries(images, x, y, layoutDirection, metadata);

        imageSeries.put(images, imageSeriesToPlace);

        return new JSONObject(Map.of(
                "startX", imageSeriesToPlace.x,
                "startY", imageSeriesToPlace.y,
                "width", imageSeriesToPlace.width,
                "height", imageSeriesToPlace.height,
                "nrImages", imageSeriesToPlace.images.size(),
                "offsetX", imageSeriesToPlace.offsetX,
                "offsetY", imageSeriesToPlace.offsetY
        ));
    }

    /**
     * Creates a bitmap representing the entire image board.
     *
     * @param palette the palette to use for the images
     * @return the resulting bitmap
     */
    public Bitmap writeBoardToBitmap(Palette palette) {
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
        images.values().forEach(imageOnBoard ->
                imageBoard.copyNonTransparentPixels(
                        imageOnBoard.image(),
                        new Point(imageOnBoard.x(), imageOnBoard.y()),
                        new Point(0, 0),
                        imageOnBoard.image().getDimension())
        );

        // Copy all image series onto the board
        imageSeries.values().forEach(series -> {
            for (int i = 0; i < series.images.size(); i++) {
                var image = series.images.get(i);
                int x = (series.layoutDirection == LayoutDirection.ROW)
                        ? series.x + series.width * i
                        : series.x;
                int y = (series.layoutDirection == LayoutDirection.ROW)
                        ? series.y
                        : series.y + series.height * i;

                imageBoard.copyNonTransparentPixels(
                        image,
                        new Point(x, y),
                        new Point(0, 0),
                        image.getDimension()
                );
            }
        });

        return imageBoard;
    }

    /**
     * Converts the location of an image on the board to a JSON object.
     *
     * @param image the bitmap image
     * @return the JSON object representing the image location
     */
    public JSONObject imageLocationToJson(Bitmap image) {
        ImageOnBoard imageOnBoard = images.get(image);

        return new JSONObject(Map.of(
                "x", imageOnBoard.x,
                "y", imageOnBoard.y,
                "width", image.getWidth(),
                "height", image.getHeight(),
                "offsetX", image.getNx(),
                "offsetY", image.getNy()
        ));
    }

    /**
     * Converts the location of an image series on the board to a JSON object.
     *
     * @param images the list of bitmap images in the series
     * @return the JSON object representing the image series location
     */
    public JSONObject imageSeriesLocationToJson(List<Bitmap> images) {
        ImageSeries oneImageSeries = this.imageSeries.get(images);

        return new JSONObject(Map.of(
                "startX", oneImageSeries.x,
                "startY", oneImageSeries.y,
                "width", oneImageSeries.width,
                "height", oneImageSeries.height,
                "nrImages", oneImageSeries.images.size(),
                "offsetX", oneImageSeries.offsetX,
                "offsetY", oneImageSeries.offsetY
        ));
    }

    /**
     * Places an image at the bottom of the board.
     *
     * @param image    the bitmap image to place
     * @param metadata the metadata associated with the image
     * @return the JSON object representing the image location
     */
    public JSONObject placeImageBottom(Bitmap image, String... metadata) {
        int currentMaxY = this.getCurrentHeight();

        return placeImage(image, 0, currentMaxY, metadata);
    }

    /**
     * Places a series of images at the bottom-right of the specified right edge on the board.
     *
     * @param right    the right edge of the placement
     * @param images   the list of bitmap images to place
     * @param metadata the metadata associated with the images
     * @return the JSON object representing the image series location
     */
    public JSONObject placeImageSeriesBottomRightOf(int right, List<Bitmap> images, String... metadata) {
        int currentMaxY = this.getCurrentHeightRightOf(right);

        return placeImageSeries(images, new Point(right + 1, currentMaxY), LayoutDirection.ROW, metadata);
    }

    /**
     * Places a series of images at the bottom of the board.
     *
     * @param images   the list of bitmap images to place
     * @param metadata the metadata associated with the images
     * @return the JSON object representing the image series location
     */
    public JSONObject placeImageSeriesBottom(List<Bitmap> images, String... metadata) {
        int currentMaxY = this.getCurrentHeight();

        return placeImageSeries(images, new Point(0, currentMaxY), LayoutDirection.ROW, metadata);
    }

    private record ImageOnBoard(Bitmap image, int x, int y, String... metadata) { }

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

    /**
     * Calculates the current width of the image board.
     *
     * @return the current width
     */
    public int getCurrentWidth() {
        return Math.max(
                images.values().stream()
                        .mapToInt(imageOnBoard -> imageOnBoard.image().getWidth() + imageOnBoard.x())
                        .max().orElse(0),
                imageSeries.values().stream()
                        .mapToInt(series -> (series.layoutDirection == LayoutDirection.ROW)
                                ? series.width * series.images.size() + series.x
                                : series.width + series.x)
                        .max().orElse(0)
        );
    }

    /**
     * Calculates the current height of the image board right of the specified right edge.
     *
     * @param right the right edge
     * @return the current height
     */
    public int getCurrentHeightRightOf(int right) {
        int currentHeight = images.values().stream()
                .filter(imageOnBoard -> imageOnBoard.x() + imageOnBoard.image().getWidth() > right)
                .mapToInt(imageOnBoard -> imageOnBoard.image().getHeight() + imageOnBoard.y())
                .max().orElse(0);

        int imageSeriesHeight = imageSeries.values().stream()
                .filter(series -> (series.layoutDirection == LayoutDirection.ROW)
                        ? series.x + series.width * series.images.size() > right
                        : series.x + series.width > right)
                .mapToInt(series -> (series.layoutDirection == LayoutDirection.ROW)
                        ? series.height + series.y
                        : series.height * series.images.size() + series.y)
                .max().orElse(0);

        return Math.max(currentHeight, imageSeriesHeight);
    }

    /**
     * Calculates the current height of the image board.
     *
     * @return the current height
     */
    public int getCurrentHeight() {
        int currentHeight = images.values().stream()
                .mapToInt(imageOnBoard -> imageOnBoard.image().getHeight() + imageOnBoard.y())
                .max().orElse(0);

        int imageSeriesHeight = imageSeries.values().stream()
                .mapToInt(series -> (series.layoutDirection == LayoutDirection.ROW)
                        ? series.height + series.y
                        : series.height * series.images.size() + series.y)
                .max().orElse(0);

        return Math.max(currentHeight, imageSeriesHeight);
    }

    /**
     * Adds metadata for an image to the JSON root.
     *
     * @param jsonRoot     the JSON root object
     * @param imageOnBoard the image on the board
     */
    private void addMetadataForImageToRoot(JSONObject jsonRoot, ImageOnBoard imageOnBoard) {
        JSONObject jsonCurrent = jsonRoot;

        for (var label : imageOnBoard.metadata()) {
            jsonCurrent = (JSONObject) jsonCurrent.computeIfAbsent(label, k -> new JSONObject());
        }

        jsonCurrent.put("x", imageOnBoard.x);
        jsonCurrent.put("y", imageOnBoard.y);
        jsonCurrent.put("width", imageOnBoard.image.getWidth());
        jsonCurrent.put("height", imageOnBoard.image.getHeight());
        jsonCurrent.put("offsetX", imageOnBoard.image.getNx());
        jsonCurrent.put("offsetY", imageOnBoard.image.getNy());
    }

    /**
     * Adds metadata for an image series to the JSON root.
     *
     * @param jsonRoot    the JSON root object
     * @param imageSeries the image series
     */
    private void addMetadataForImageSeriesToRoot(JSONObject jsonRoot, ImageSeries imageSeries) {
        JSONObject jsonCurrent = jsonRoot;

        for (var label : imageSeries.metadata) {
            jsonCurrent = (JSONObject) jsonCurrent.computeIfAbsent(label, k -> new JSONObject());
        }

        jsonCurrent.put("startX", imageSeries.x);
        jsonCurrent.put("startY", imageSeries.y);
        jsonCurrent.put("width", imageSeries.width);
        jsonCurrent.put("height", imageSeries.height);
        jsonCurrent.put("nrImages", imageSeries.images.size());
        jsonCurrent.put("offsetX", imageSeries.offsetX);
        jsonCurrent.put("offsetY", imageSeries.offsetY);
    }

    /**
     * Returns the metadata of the image board as a JSON object.
     *
     * @return the JSON object containing the metadata
     */
    public JSONObject getMetadataAsJson() {
        JSONObject jsonRoot = new JSONObject();

        images.values().forEach(imageOnBoard -> addMetadataForImageToRoot(jsonRoot, imageOnBoard));
        imageSeries.values().forEach(imageSeries -> addMetadataForImageSeriesToRoot(jsonRoot, imageSeries));

        return jsonRoot;
    }
}
