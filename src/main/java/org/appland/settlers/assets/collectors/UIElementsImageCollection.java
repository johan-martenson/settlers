package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.UiIcon;
import org.appland.settlers.assets.gamefiles.IoDat;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.assets.utils.ImageUtils;
import org.appland.settlers.model.Size;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class UIElementsImageCollection {
    private final Map<Size, Bitmap> hoverAvailableBuilding = new EnumMap<>(Size.class);
    private final Map<Size, Bitmap> availableBuilding = new EnumMap<>(Size.class);
    private final Map<UiIcon, Bitmap> uiElements = new EnumMap<>(UiIcon.class);
    private final Map<IoDat, Bitmap> ioImages = new EnumMap<>(IoDat.class);

    private Bitmap selectedPointImage;
    private Bitmap hoverPointImage;
    private Bitmap hoverAvailableFlag;
    private Bitmap hoverAvailableMine;
    private Bitmap hoverAvailableHarbor;
    private Bitmap availableFlag;
    private Bitmap availableMine;
    private Bitmap availableHarbor;

    /**
     * Adds the selected point image.
     *
     * @param image the bitmap image to add
     */
    public void addSelectedPointImage(Bitmap image) {
        this.selectedPointImage = image;
    }

    /**
     * Adds the hover point image.
     *
     * @param image the bitmap image to add
     */
    public void addHoverPoint(Bitmap image) {
        this.hoverPointImage = image;
    }

    /**
     * Adds the hover available flag image.
     *
     * @param image the bitmap image to add
     */
    public void addHoverAvailableFlag(Bitmap image) {
        this.hoverAvailableFlag = image;
    }

    /**
     * Adds the hover available mine image.
     *
     * @param image the bitmap image to add
     */
    public void addHoverAvailableMine(Bitmap image) {
        this.hoverAvailableMine = image;
    }

    /**
     * Adds the hover available building image for a specific size.
     *
     * @param size  the size of the building
     * @param image the bitmap image to add
     */
    public void addHoverAvailableBuilding(Size size, Bitmap image) {
        this.hoverAvailableBuilding.put(size, image);
    }

    /**
     * Adds the hover available harbor image.
     *
     * @param image the bitmap image to add
     */
    public void addHoverAvailableHarbor(Bitmap image) {
        this.hoverAvailableHarbor = image;
    }

    /**
     * Adds the available flag image.
     *
     * @param image the bitmap image to add
     */
    public void addAvailableFlag(Bitmap image) {
        this.availableFlag = image;
    }

    /**
     * Adds the available mine image.
     *
     * @param image the bitmap image to add
     */
    public void addAvailableMine(Bitmap image) {
        this.availableMine = image;
    }

    /**
     * Adds the available building image for a specific size.
     *
     * @param size  the size of the building
     * @param image the bitmap image to add
     */
    public void addAvailableBuilding(Size size, Bitmap image) {
        this.availableBuilding.put(size, image);
    }

    /**
     * Adds the available harbor image.
     *
     * @param image the bitmap image to add
     */
    public void addAvailableHarbor(Bitmap image) {
        this.availableHarbor = image;
    }

    /**
     * Writes the image atlas to the specified directory using the given palette.
     *
     * @param toDir   the directory to save the atlas
     * @param palette the palette to use for the images
     * @throws IOException if an I/O error occurs
     */
    public void writeImageAtlas(String toDir, Palette palette) throws IOException {
        var imageBoard = new ImageBoard();

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

        imageBoard.placeImagesAsRow(
                uiElements.entrySet().stream()
                        .map(entry -> ImageBoard.makeImagePathPair(
                                ImageUtils.scaleTo2x(entry.getValue()),
                                "icons",
                                entry.getKey().name().toUpperCase()))
                        .toList()
        );

        imageBoard.placeImagesAsRow(
                ioImages.entrySet().stream()
                        .map(entry -> ImageBoard.makeImagePathPair(
                                        ImageUtils.scaleTo2x(entry.getValue()),
                                "icons",
                                entry.getKey().name().toUpperCase()
                        ))
                        .toList()
        );

        imageBoard.writeBoard(toDir, "image-atlas-ui-elements", palette);
    }

    /**
     * Adds a UI element image associated with a specific icon.
     *
     * @param uiIcon the UI icon
     * @param image  the bitmap image to add
     */
    public void addUiElement(UiIcon uiIcon, Bitmap image) {
        uiElements.put(uiIcon, image);
    }

    public void addIcon(IoDat icon, Bitmap image) {
        ioImages.put(icon, image);
    }
}
