package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.Utils;
import org.appland.settlers.model.Size;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.Map;

public class UIElementsImageCollection {
    private final Map<Size, Bitmap> hoverAvailableBuilding;
    private final Map<Size, Bitmap> availableBuilding;

    private Bitmap selectedPointImage;
    private Bitmap hoverPointImage;
    private Bitmap hoverAvailableFlag;
    private Bitmap hoverAvailableMine;
    private Bitmap hoverAvailableHarbor;
    private Bitmap availableFlag;
    private Bitmap availableMine;
    private Bitmap availableHarbor;

    public UIElementsImageCollection() {
        hoverAvailableBuilding = new EnumMap<>(Size.class);
        availableBuilding = new EnumMap<>(Size.class);
    }

    public void addSelectedPointImage(Bitmap image) {
        this.selectedPointImage = image;
    }

    public void addHoverPoint(Bitmap image) {
        this.hoverPointImage = image;
    }

    public void addHoverAvailableFlag(Bitmap image) {
        this.hoverAvailableFlag = image;
    }

    public void addHoverAvailableMine(Bitmap image) {
        this.hoverAvailableMine = image;
    }

    public void addHoverAvailableBuilding(Size size, Bitmap image) {
        this.hoverAvailableBuilding.put(size, image);
    }

    public void addHoverAvailableHarbor(Bitmap image) {
        this.hoverAvailableHarbor = image;
    }

    public void addAvailableFlag(Bitmap image) {
        this.availableFlag = image;
    }

    public void addAvailableMine(Bitmap image) {
        this.availableMine = image;
    }

    public void addAvailableBuilding(Size size, Bitmap image) {
        this.availableBuilding.put(size, image);
    }

    public void addAvailableHarbor(Bitmap image) {
        this.availableHarbor = image;
    }

    public void writeImageAtlas(String toDir, Palette palette) throws IOException {

        /*
         * Layout in three rows:
         *   - Selected point, hover point
         *   - Hover over available - flag, mine, harbor, large, medium, small
         *   - Available - flag, mine, harbor, large, medium, small
         */

        // Calculate the dimension of the image atlas
        int row1Height = Utils.max(selectedPointImage.getHeight(), hoverPointImage.getHeight());
        int row2Height = Utils.max(hoverAvailableFlag.getHeight(), hoverAvailableMine.getHeight(), hoverAvailableHarbor.getHeight(),
                hoverAvailableBuilding.get(Size.LARGE).getHeight(), hoverAvailableBuilding.get(Size.MEDIUM).getHeight(),
                hoverAvailableBuilding.get(Size.SMALL).getHeight());

        // Create the image atlas
        ImageBoard imageBoard = new ImageBoard();

        JSONObject jsonImageAtlas = new JSONObject();

        // Fill in the image atlas

        // Row 1 - selected point and hover point
        int nextStartAtX = 0;

        // Selected point
        imageBoard.placeImage(selectedPointImage, nextStartAtX, 0);

        JSONObject jsonSelectedPoint = imageBoard.imageLocationToJson(selectedPointImage);

        jsonImageAtlas.put("selectedPoint", jsonSelectedPoint);

        nextStartAtX = nextStartAtX + selectedPointImage.getWidth();

        // Hover point
        imageBoard.placeImage(hoverPointImage, nextStartAtX, 0);

        JSONObject jsonHoverPoint = imageBoard.imageLocationToJson(hoverPointImage);

        jsonImageAtlas.put("hoverPoint", jsonHoverPoint);

        // Row 2 - Hover over available: flag, mine, harbor, large, medium, small
        Bitmap hoverAvailableBuildingLarge = hoverAvailableBuilding.get(Size.LARGE);
        Bitmap hoverAvailableBuildingMedium = hoverAvailableBuilding.get(Size.MEDIUM);
        Bitmap hoverAvailableBuildingSmall = hoverAvailableBuilding.get(Size.SMALL);

        nextStartAtX = 0;

        // Hover over available flag
        imageBoard.placeImage(hoverAvailableFlag, nextStartAtX, row1Height);

        JSONObject jsonHoverAvailableFlag = imageBoard.imageLocationToJson(hoverAvailableFlag);

        jsonImageAtlas.put("hoverAvailableFlag", jsonHoverAvailableFlag);

        nextStartAtX = nextStartAtX + hoverAvailableFlag.getWidth();

        // Hover over available mine
        imageBoard.placeImage(hoverAvailableMine, nextStartAtX, row1Height);

        JSONObject jsonHoverAvailableMine = imageBoard.imageLocationToJson(hoverAvailableMine);

        jsonImageAtlas.put("hoverAvailableMine", jsonHoverAvailableMine);

        nextStartAtX = nextStartAtX + hoverAvailableMine.getWidth();

        // Hover over available harbor
        imageBoard.placeImage(hoverAvailableHarbor, nextStartAtX, row1Height);

        JSONObject jsonHoverAvailableHarbor = imageBoard.imageLocationToJson(hoverAvailableHarbor);

        jsonImageAtlas.put("hoverAvailableHarbor", jsonHoverAvailableHarbor);

        nextStartAtX = nextStartAtX + hoverAvailableHarbor.getWidth();

        // Hover over available large building
        imageBoard.placeImage(hoverAvailableBuildingLarge, nextStartAtX, row1Height);

        JSONObject jsonHoverAvailableBuildingLarge = imageBoard.imageLocationToJson(hoverAvailableBuildingLarge);

        jsonImageAtlas.put("hoverAvailableBuildingLarge", jsonHoverAvailableBuildingLarge);

        nextStartAtX = nextStartAtX + hoverAvailableBuildingLarge.getWidth();

        // Hover over available medium building
        imageBoard.placeImage(hoverAvailableBuildingMedium, nextStartAtX, row1Height);

        JSONObject jsonHoverAvailableBuildingMedium = imageBoard.imageLocationToJson(hoverAvailableBuildingMedium);

        jsonImageAtlas.put("hoverAvailableBuildingMedium", jsonHoverAvailableBuildingMedium);

        nextStartAtX = nextStartAtX + hoverAvailableBuildingMedium.getWidth();

        // Hover over available small building
        imageBoard.placeImage(hoverAvailableBuildingSmall, nextStartAtX, row1Height);

        JSONObject jsonHoverAvailableBuildingSmall = imageBoard.imageLocationToJson(hoverAvailableBuildingSmall);

        jsonImageAtlas.put("hoverAvailableBuildingSmall", jsonHoverAvailableBuildingSmall);

        // Row 3 - Available - flag, mine, harbor, large, medium, small
        Bitmap availableBuildingLarge = availableBuilding.get(Size.LARGE);
        Bitmap availableBuildingMedium = availableBuilding.get(Size.MEDIUM);
        Bitmap availableBuildingSmall = availableBuilding.get(Size.SMALL);

        nextStartAtX = 0;
        int nextStartAtY = row1Height + row2Height;

        // Available flag
        imageBoard.placeImage(availableFlag, nextStartAtX, nextStartAtY);

        JSONObject jsonAvailableFlag = imageBoard.imageLocationToJson(availableFlag);

        jsonImageAtlas.put("availableFlag", jsonAvailableFlag);

        nextStartAtX = nextStartAtX + availableFlag.getWidth();

        // Available mine
        imageBoard.placeImage(availableMine, nextStartAtX, nextStartAtY);

        JSONObject jsonAvailableMine = imageBoard.imageLocationToJson(availableMine);

        jsonImageAtlas.put("availableMine", jsonAvailableMine);

        nextStartAtX = nextStartAtX + availableMine.getWidth();

        // Available harbor
        imageBoard.placeImage(availableHarbor, nextStartAtX, nextStartAtY);

        JSONObject jsonAvailableHarbor = imageBoard.imageLocationToJson(availableHarbor);

        jsonImageAtlas.put("availableHarbor", jsonAvailableHarbor);

        nextStartAtX = nextStartAtX + availableHarbor.getWidth();

        // Large available building
        imageBoard.placeImage(availableBuildingLarge, nextStartAtX, nextStartAtY);

        JSONObject jsonAvailableBuildingLarge = imageBoard.imageLocationToJson(availableBuildingLarge);

        jsonImageAtlas.put("availableBuildingLarge", jsonAvailableBuildingLarge);

        nextStartAtX = nextStartAtX + availableBuildingLarge.getWidth();

        // Medium available building
        imageBoard.placeImage(availableBuildingMedium, nextStartAtX, nextStartAtY);

        JSONObject jsonAvailableBuildingMedium = imageBoard.imageLocationToJson(availableBuildingMedium);

        jsonImageAtlas.put("availableBuildingMedium", jsonAvailableBuildingMedium);

        nextStartAtX = nextStartAtX + availableBuildingMedium.getWidth();

        // Small available building
        imageBoard.placeImage(availableBuildingSmall, nextStartAtX, nextStartAtY);

        JSONObject jsonAvailableBuildingSmall = imageBoard.imageLocationToJson(availableBuildingSmall);

        jsonImageAtlas.put("availableBuildingSmall", jsonAvailableBuildingSmall);

        // Write the image atlas to file
        imageBoard.writeBoardToBitmap(palette).writeToFile(toDir + "/image-atlas-ui-elements.png");

        Files.writeString(Paths.get(toDir, "image-atlas-ui-elements.json"), jsonImageAtlas.toJSONString());
    }
}
