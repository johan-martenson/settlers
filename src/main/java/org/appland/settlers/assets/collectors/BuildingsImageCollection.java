package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.assets.Utils;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageBoard;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
        ImageBoard imageBoard = new ImageBoard();

        buildingMap.forEach((nation, buildings) -> {
            int right = imageBoard.getCurrentWidth();

            buildings
                    .forEach((building, buildingImages) -> imageBoard.placeImagesAtBottomRightOf(
                            Stream.of(
                                    ImageBoard.makeImagePathPair(
                                            buildingImages.buildingReadyImage,
                                            "buildings",
                                            nation.name().toUpperCase(),
                                            building,
                                            "ready"
                                    ),
                                    ImageBoard.makeImagePathPair(
                                            buildingImages.buildingReadyShadowImage,
                                            "buildings",
                                            nation.name().toUpperCase(),
                                            building,
                                            "readyShadow"
                                    ),
                                    ImageBoard.makeImagePathPair(
                                            buildingImages.buildingUnderConstruction,
                                            "buildings",
                                            nation.name().toUpperCase(),
                                            building,
                                            "underConstruction"
                                    ),
                                    ImageBoard.makeImagePathPair(
                                            buildingImages.buildingUnderConstructionShadowImage,
                                            "buildings",
                                            nation.name().toUpperCase(),
                                            building,
                                            "underConstructionShadow"
                                    )
                            )
                            .filter(imagePathPair -> imagePathPair.image() != null)
                            .toList(),
                            right
                    ));

            imageBoard.placeImagesAtBottomRightOf(
                    List.of(
                            ImageBoard.makeImagePathPair(
                                    specialImagesMap.get(nation).constructionPlannedImage,
                                    "constructionPlanned",
                                    nation.name().toUpperCase(),
                                    "image"
                            ),
                            ImageBoard.makeImagePathPair(
                                    specialImagesMap.get(nation).constructionPlannedShadowImage,
                                    "constructionPlanned",
                                    nation.name().toUpperCase(),
                                    "shadowImage"
                            ),
                            ImageBoard.makeImagePathPair(
                                    specialImagesMap.get(nation).constructionJustStartedImage,
                                    "constructionJustStarted",
                                    nation.name().toUpperCase(),
                                    "image"
                            ),
                            ImageBoard.makeImagePathPair(
                                    specialImagesMap.get(nation).constructionJustStartedShadowImage,
                                    "constructionJustStarted",
                                    nation.name().toUpperCase(),
                                    "shadowImage"
                            )
                    ),
                    right
            );
        });

        imageBoard.writeBoard(directory, "image-atlas-buildings", palette);

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
