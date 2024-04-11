package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.model.Size;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
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
        ImageBoard imageBoard = new ImageBoard();

        imageBoard.placeImageRightOf(selectedPointImage, "selectedPoint");

        imageBoard.placeImageRightOf(hoverPointImage, "hoverPoint");

        imageBoard.placeImagesAsRow(
                List.of(
                        ImageBoard.makeImagePathPair(hoverAvailableFlag, "hoverAvailableFlag"),
                        ImageBoard.makeImagePathPair(hoverAvailableMine, "hoverAvailableMine"),
                        ImageBoard.makeImagePathPair(hoverAvailableHarbor, "hoverAvailableHarbor"),
                        ImageBoard.makeImagePathPair(hoverAvailableBuilding.get(Size.LARGE), "hoverAvailableBuildingLarge"),
                        ImageBoard.makeImagePathPair(hoverAvailableBuilding.get(Size.MEDIUM), "hoverAvailableBuildingMedium"),
                        ImageBoard.makeImagePathPair(hoverAvailableBuilding.get(Size.SMALL), "hoverAvailableBuildingSmall")
                )
        );

        imageBoard.placeImagesAsRow(
                List.of(
                        ImageBoard.makeImagePathPair(availableFlag, "availableFlag"),
                        ImageBoard.makeImagePathPair(availableMine, "availableMine"),
                        ImageBoard.makeImagePathPair(availableHarbor, "availableHarbor"),
                        ImageBoard.makeImagePathPair(availableBuilding.get(Size.LARGE), "availableBuildingLarge"),
                        ImageBoard.makeImagePathPair(availableBuilding.get(Size.MEDIUM), "availableBuildingMedium"),
                        ImageBoard.makeImagePathPair(availableBuilding.get(Size.SMALL), "availableBuildingSmall")
                )
        );

        imageBoard.writeBoard(toDir, "image-atlas-ui-elements", palette);
    }
}
