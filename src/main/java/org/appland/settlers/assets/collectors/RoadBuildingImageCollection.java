package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.RoadConnectionDifference;
import org.json.simple.JSONObject;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.Map;

public class RoadBuildingImageCollection {
    private Bitmap startPointImage;
    private Bitmap sameLevelConnectionImage;
    private final Map<RoadConnectionDifference, Bitmap> upwardsConnectionImages;
    private final Map<RoadConnectionDifference, Bitmap> downwardsConnectionImages;

    public RoadBuildingImageCollection() {
        downwardsConnectionImages = new EnumMap<>(RoadConnectionDifference.class);
        upwardsConnectionImages = new EnumMap<>(RoadConnectionDifference.class);
    }

    public void addStartPointImage(Bitmap image) {
        this.startPointImage = image;
    }

    public void addSameLevelConnectionImage(Bitmap image) {
        this.sameLevelConnectionImage = image;
    }

    public void addUpwardsConnectionImage(RoadConnectionDifference difference, Bitmap image) {
        this.upwardsConnectionImages.put(difference, image);
    }

    public void addDownwardsConnectionImage(RoadConnectionDifference difference, Bitmap image) {
        this.downwardsConnectionImages.put(difference, image);
    }

    public void writeImageAtlas(String toDir, Palette palette) throws IOException {

        // Create the image atlas
        // Layout:
        //     - row 1: start point, same level connection
        //     - row 2: upwards connection small, medium, large
        //     - row 3: downwards connection small, medium, large
        ImageBoard imageBoard = new ImageBoard();

        JSONObject jsonImageAtlas = new JSONObject();

        // Fill in the image atlas

        Point cursor = new Point(0, 0);

        // Start point
        imageBoard.placeImage(startPointImage, cursor);

        JSONObject jsonStartPoint = imageBoard.imageLocationToJson(startPointImage);

        jsonImageAtlas.put("startPoint", jsonStartPoint);

        cursor.x = cursor.x + startPointImage.getWidth();

        // Connection on same level
        imageBoard.placeImage(sameLevelConnectionImage, cursor);

        JSONObject jsonSameLevelConnection = imageBoard.imageLocationToJson(sameLevelConnectionImage);

        jsonImageAtlas.put("sameLevelConnection", jsonSameLevelConnection);

        // Upwards connections
        JSONObject jsonUpwardsConnections = new JSONObject();

        jsonImageAtlas.put("upwardsConnections", jsonUpwardsConnections);

        cursor.x = 0;
        cursor.y = Math.max(startPointImage.getHeight(), sameLevelConnectionImage.getHeight());

        int rowHeight = 0;

        for (Map.Entry<RoadConnectionDifference, Bitmap> entry : this.upwardsConnectionImages.entrySet()) {

            RoadConnectionDifference difference = entry.getKey();
            Bitmap image = entry.getValue();

            imageBoard.placeImage(image, cursor);

            JSONObject jsonUpwardsConnection = imageBoard.imageLocationToJson(image);

            jsonUpwardsConnections.put(difference.name().toUpperCase(), jsonUpwardsConnection);

            rowHeight = Math.max(rowHeight, image.getHeight());

            cursor.x = cursor.x + image.getWidth();
        }

        // Downwards connections
        JSONObject jsonDownwardsConnections = new JSONObject();

        jsonImageAtlas.put("downwardsConnections", jsonDownwardsConnections);

        cursor.y = cursor.y + rowHeight;
        cursor.x = 0;

        for (Map.Entry<RoadConnectionDifference, Bitmap> entry : this.downwardsConnectionImages.entrySet()) {

            RoadConnectionDifference difference = entry.getKey();
            Bitmap image = entry.getValue();

            imageBoard.placeImage(image, cursor);

            JSONObject jsonDownwardsConnection = imageBoard.imageLocationToJson(image);

            jsonDownwardsConnections.put(difference.name().toUpperCase(), jsonDownwardsConnection);

            cursor.x = cursor.x + image.getWidth();
        }

        // Write the image atlas to file
        imageBoard.writeBoardToBitmap(palette).writeToFile(toDir + "/image-atlas-road-building.png");

        Files.writeString(Paths.get(toDir, "image-atlas-road-building.json"), jsonImageAtlas.toJSONString());
    }
}
