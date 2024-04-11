package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.assets.Utils;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageBoard;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class BuildingsImageCollection {

    private final Map<Nation, Map<String, BuildingImages>> buildingMap;
    private final Map<Nation, SpecialImages> specialImagesMap;

    public BuildingsImageCollection() {
        this.buildingMap = new EnumMap<>(Nation.class);
        this.specialImagesMap = new EnumMap<>(Nation.class);

        for (Nation nation : Nation.values()) {
            this.buildingMap.put(nation, new HashMap<>());
            this.specialImagesMap.put(nation, new SpecialImages());
        }
    }

    public void addBuildingForNation(Nation nation, String building, Bitmap image) {
        Map<String, BuildingImages> buildingsForNation = this.buildingMap.get(nation);

        if (!buildingsForNation.containsKey(building)) {
            buildingsForNation.put(building, new BuildingImages());
        }

        BuildingImages buildingImages = buildingsForNation.get(building);

        buildingImages.addReadyBuildingImage(image);
    }

    public void addBuildingUnderConstructionForNation(Nation nation, String building, Bitmap image) {
        Map<String, BuildingImages> buildingsForNation = this.buildingMap.get(nation);

        if (!buildingsForNation.containsKey(building)) {
            buildingsForNation.put(building, new BuildingImages());
        }

        BuildingImages buildingImages = buildingsForNation.get(building);

        buildingImages.addUnderConstructionBuildingImage(image);
    }

    public void addConstructionPlanned(Nation nation, Bitmap image) {
        this.specialImagesMap.get(nation).addConstructionPlannedImage(image);
    }

    public void addConstructionJustStarted(Nation nation, Bitmap image) {
        this.specialImagesMap.get(nation).addConstructionJustStartedImage(image);
    }

    public void writeImageAtlas(String directory, Palette palette) throws IOException {

        // Create the image atlas
        ImageBoard imageBoard = new ImageBoard();

        // Fill in the image atlas and fill in the meta-data
        int startNextNationAtX = 0;
        int currentNationStartedAtX = 0;
        Point cursor = new Point(0, 0);

        for (Nation nation : Nation.values()) {
            currentNationStartedAtX = cursor.x;

            cursor.x = startNextNationAtX;
            cursor.y = 0;

            for (Map.Entry<String, BuildingImages> entry : this.buildingMap.get(nation).entrySet()) {
                String building = entry.getKey();
                BuildingImages images = entry.getValue();

                int currentRowHeight = 0;

                // Building ready image
                if (images.buildingReadyImage != null) {
                    imageBoard.placeImage(
                            images.buildingReadyImage,
                            cursor,
                            "buildings",
                            nation.name().toUpperCase(),
                            building,
                            "ready"
                            );

                    startNextNationAtX = Math.max(startNextNationAtX, cursor.x + images.buildingReadyImage.getWidth());

                    currentRowHeight = Math.max(currentRowHeight, images.buildingReadyImage.getHeight());

                    cursor.x = cursor.x + images.buildingReadyImage.getWidth();
                }

                // Building ready shadow image
                if (images.buildingReadyShadowImage != null) {
                    imageBoard.placeImage(
                            images.buildingReadyShadowImage,
                            cursor,
                            "buildings",
                            nation.name().toUpperCase(),
                            building,
                            "readyShadow"
                    );

                    startNextNationAtX = Math.max(startNextNationAtX, cursor.x + images.buildingReadyShadowImage.getWidth());

                    currentRowHeight = Math.max(currentRowHeight, images.buildingReadyShadowImage.getHeight());

                    cursor.x = cursor.x + images.buildingReadyShadowImage.getWidth();
                }

                // Under construction image
                if (images.buildingUnderConstruction != null) {
                    imageBoard.placeImage(
                            images.buildingUnderConstruction,
                            cursor,
                            "buildings",
                            nation.name().toUpperCase(),
                            building,
                            "underConstruction"
                    );

                    startNextNationAtX = Math.max(startNextNationAtX, cursor.x + images.buildingUnderConstruction.getWidth());

                    currentRowHeight = Math.max(currentRowHeight, images.buildingUnderConstruction.getHeight());

                    cursor.x = cursor.x + images.buildingUnderConstruction.getWidth();
                }

                // Under construction shadow image
                if (images.buildingUnderConstructionShadowImage != null) {
                    imageBoard.placeImage(
                            images.buildingUnderConstructionShadowImage,
                            cursor,
                            "buildings",
                            nation.name().toUpperCase(),
                            building,
                            "underConstructionShadow"
                    );

                    startNextNationAtX = Math.max(startNextNationAtX, cursor.x + images.buildingUnderConstructionShadowImage.getWidth());

                    currentRowHeight = Math.max(currentRowHeight, images.buildingUnderConstructionShadowImage.getHeight());

                    cursor.x = cursor.x + images.buildingUnderConstructionShadowImage.getWidth();
                }

                cursor.y = cursor.y + currentRowHeight;
                cursor.x = currentNationStartedAtX;
            }

            cursor.x = currentNationStartedAtX;

            // Fill in construction planned and construction just started
            SpecialImages specialImages = specialImagesMap.get(nation);

            Bitmap constructionPlannedImage = specialImages.constructionPlannedImage;
            Bitmap constructionPlannedShadowImage = specialImages.constructionPlannedShadowImage;
            Bitmap constructionJustStartedImage = specialImages.constructionJustStartedImage;
            Bitmap constructionJustStartedShadowImage = specialImages.constructionJustStartedShadowImage;

            // Construction planned image
            imageBoard.placeImage(
                    constructionPlannedImage,
                    cursor,
                    "constructionPlanned",
                    nation.name().toUpperCase(),
                    "image"
                    );

            cursor.x = cursor.x + constructionPlannedImage.getWidth();

            // Construction planned shadow image
            imageBoard.placeImage(
                    constructionPlannedShadowImage,
                    cursor,
                    "constructionPlanned",
                    nation.name().toUpperCase(),
                    "shadowImage"
            );

            cursor.x = cursor.x + constructionPlannedShadowImage.getWidth();

            // Under construction image
            imageBoard.placeImage(
                    constructionJustStartedImage,
                    cursor,
                    "constructionJustStarted",
                    nation.name().toUpperCase(),
                    "image"
            );

            cursor.x = cursor.x + constructionJustStartedImage.getWidth();

            // Under construction shadow image
            imageBoard.placeImage(
                    constructionJustStartedShadowImage,
                    cursor,
                    "constructionJustStarted",
                    nation.name().toUpperCase(),
                    "shadowImage"
            );

            // Start at the right place for the next column
            cursor.x = startNextNationAtX;
        }

        // Write the image and the meta-data to files
        imageBoard.writeBoardToBitmap(palette).writeToFile(directory + "/image-atlas-buildings.png");
        //Files.writeString(Paths.get(directory, "image-atlas-buildings.json"), jsonImageAtlas.toJSONString());
        Files.writeString(Paths.get(directory, "image-atlas-buildings.json"), imageBoard.getMetadataAsJson().toJSONString());

        // Write individual images for icons
        for (Nation nation : Nation.values()) {
            String buildingDir = directory + "/" + nation.name();

            // Create directory
            Utils.createDirectory(buildingDir);

            for (Map.Entry<String, BuildingImages> entry : this.buildingMap.get(nation).entrySet()) {

                String buildingFile = buildingDir + "/" + entry.getKey() + ".png";

                // Write each ready building as a separate image
                entry.getValue().buildingReadyImage.writeToFile(buildingFile);
            }
        }
    }

    public void addBuildingShadowForNation(Nation nation, String building, Bitmap image) {
        Map<String, BuildingImages> buildingsForNation = this.buildingMap.get(nation);

        if (!buildingsForNation.containsKey(building)) {
            buildingsForNation.put(building, new BuildingImages());
        }

        BuildingImages buildingImages = buildingsForNation.get(building);

        buildingImages.addReadyBuildingShadowImage(image);
    }

    public void addBuildingUnderConstructionShadowForNation(Nation nation, String building, Bitmap image) {
        Map<String, BuildingImages> buildingsForNation = this.buildingMap.get(nation);

        if (!buildingsForNation.containsKey(building)) {
            buildingsForNation.put(building, new BuildingImages());
        }

        BuildingImages buildingImages = buildingsForNation.get(building);

        buildingImages.addUnderConstructionBuildingShadowImage(image);
    }

    public void addConstructionPlannedShadow(Nation nation, Bitmap image) {
        this.specialImagesMap.get(nation).addConstructionPlannedShadowImage(image);
    }

    public void addConstructionJustStartedShadow(Nation nation, Bitmap image) {
        this.specialImagesMap.get(nation).addConstructionJustStartedShadowImage(image);
    }

    private static class BuildingImages {
        private Bitmap buildingReadyImage;
        private Bitmap buildingUnderConstruction;
        private Bitmap buildingReadyShadowImage;
        private Bitmap buildingUnderConstructionShadowImage;

        BuildingImages() {
            this.buildingReadyImage = null;
            this.buildingUnderConstruction = null;
        }

        public void addReadyBuildingImage(Bitmap image) {
            this.buildingReadyImage = image;
        }

        public void addUnderConstructionBuildingImage(Bitmap image) {
            this.buildingUnderConstruction = image;
        }

        public void addReadyBuildingShadowImage(Bitmap image) {
            buildingReadyShadowImage = image;
        }

        public void addUnderConstructionBuildingShadowImage(Bitmap image) {
            buildingUnderConstructionShadowImage = image;
        }
    }

    private static class SpecialImages {
        private Bitmap constructionPlannedImage;
        private Bitmap constructionJustStartedImage;
        private Bitmap constructionPlannedShadowImage;
        private Bitmap constructionJustStartedShadowImage;

        public void addConstructionPlannedImage(Bitmap image) {
            this.constructionPlannedImage = image;
        }

        public void addConstructionJustStartedImage(Bitmap image) {
            this.constructionJustStartedImage = image;
        }

        public void addConstructionPlannedShadowImage(Bitmap image) {
            this.constructionPlannedShadowImage = image;
        }

        public void addConstructionJustStartedShadowImage(Bitmap image) {
            this.constructionJustStartedShadowImage = image;
        }
    }
}
