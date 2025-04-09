package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.GameResource;
import org.appland.settlers.assets.Nation;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.GameFiles;
import org.appland.settlers.assets.utils.ImageBoard;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.appland.settlers.assets.Utils.getImageAt;
import static org.appland.settlers.assets.utils.ImageTransformer.normalizeImageSeries;

public class BuildingsImageCollection {
    private final Map<Nation, Map<String, BuildingImages>> buildingMap = new EnumMap<>(Nation.class);
    private final Map<Nation, SpecialImages> specialImagesMap = new EnumMap<>(Nation.class);

    /**
     * Adds a ready building image for a specific nation and building.
     *
     * @param nation   the nation
     * @param building the building name
     * @param image    the bitmap image to add
     */
    public void addBuildingForNation(Nation nation, String building, Bitmap image) {
        buildingMap.computeIfAbsent(nation, k -> new HashMap<>())
                .computeIfAbsent(building, k -> new BuildingImages())
                .addReadyBuildingImage(image);
    }

    /**
     * Adds an under-construction building image for a specific nation and building.
     *
     * @param nation   the nation
     * @param building the building name
     * @param image    the bitmap image to add
     */
    public void addBuildingUnderConstructionForNation(Nation nation, String building, Bitmap image) {
        buildingMap.computeIfAbsent(nation, k -> new HashMap<>())
                .computeIfAbsent(building, k -> new BuildingImages())
                .addUnderConstructionBuildingImage(image);
    }

    /**
     * Adds a construction planned image for a specific nation.
     *
     * @param nation the nation
     * @param image  the bitmap image to add
     */
    public void addConstructionPlanned(Nation nation, Bitmap image) {
        specialImagesMap.computeIfAbsent(nation, k -> new SpecialImages())
                .addConstructionPlannedImage(image);
    }

    /**
     * Adds a construction just started image for a specific nation.
     *
     * @param nation the nation
     * @param image  the bitmap image to add
     */
    public void addConstructionJustStarted(Nation nation, Bitmap image) {
        specialImagesMap.computeIfAbsent(nation, k -> new SpecialImages())
                .addConstructionJustStartedImage(image);
    }

    /**
     * Writes the image atlas to the specified directory using the given palette.
     *
     * @param directory the directory to save the atlas
     * @param palette   the palette to use for the images
     * @throws IOException if an I/O error occurs
     */
    public void writeImageAtlas(String directory, Palette palette) throws IOException {
        ImageBoard imageBoard = new ImageBoard();

        buildingMap.forEach((nation, buildings) -> {
            int right = imageBoard.getCurrentWidth();

            buildings.forEach((building, buildingImages) -> {
                imageBoard.placeImagesAtBottomRightOf(
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
                                        ),
                                        ImageBoard.makeImagePathPair(
                                                buildingImages.openDoorImage,
                                                "buildings",
                                                nation.name().toUpperCase(),
                                                building,
                                                "openDoor"
                                        )
                                )
                                .filter(imagePathPair -> imagePathPair.image() != null)
                                .toList(),
                        right
                );

                if (buildingImages.workingAnimation != null) {
                    imageBoard.placeImageSeriesBottomRightOf(
                            right,
                            normalizeImageSeries(buildingImages.workingAnimation),
                            "buildings",
                            nation.name().toUpperCase(),
                            building,
                            "workingAnimation"
                    );
                }

                if (buildingImages.workingAnimationShadow != null) {
                    imageBoard.placeImageSeriesBottomRightOf(
                            right,
                            normalizeImageSeries(buildingImages.workingAnimationShadow),
                            "buildings",
                            nation.name().toUpperCase(),
                            building,
                            "workingAnimationShadow"
                    );
                }
            });

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
    }

    /**
     * Adds a shadow image for a ready building for a specific nation and building.
     *
     * @param nation   the nation
     * @param building the building name
     * @param image    the bitmap image to add
     */
    public void addBuildingShadowForNation(Nation nation, String building, Bitmap image) {
        buildingMap.computeIfAbsent(nation, k -> new HashMap<>())
                .computeIfAbsent(building, k -> new BuildingImages())
                .addReadyBuildingShadowImage(image);
    }

    /**
     * Adds a shadow image for an under-construction building for a specific nation and building.
     *
     * @param nation   the nation
     * @param building the building name
     * @param image    the bitmap image to add
     */
    public void addBuildingUnderConstructionShadowForNation(Nation nation, String building, Bitmap image) {
        buildingMap.computeIfAbsent(nation, k -> new HashMap<>())
                .computeIfAbsent(building, k -> new BuildingImages())
                .addUnderConstructionBuildingShadowImage(image);
    }

    /**
     * Adds a shadow image for a construction planned image for a specific nation.
     *
     * @param nation the nation
     * @param image  the bitmap image to add
     */
    public void addConstructionPlannedShadow(Nation nation, Bitmap image) {
        specialImagesMap.computeIfAbsent(nation, k -> new SpecialImages())
                .addConstructionPlannedShadowImage(image);
    }

    /**
     * Adds a shadow image for a construction just started image for a specific nation.
     *
     * @param nation the nation
     * @param image  the bitmap image to add
     */
    public void addConstructionJustStartedShadow(Nation nation, Bitmap image) {
        specialImagesMap.computeIfAbsent(nation, k -> new SpecialImages())
                .addConstructionJustStartedShadowImage(image);
    }

    /**
     * Adds an open door image for a specific nation and building.
     *
     * @param nation   the nation
     * @param building the building name
     * @param image    the bitmap image to add
     */
    public void addOpenDoorForBuilding(Nation nation, String building, Bitmap image) {
        buildingMap.computeIfAbsent(nation, k -> new HashMap<>())
                .computeIfAbsent(building, k -> new BuildingImages())
                .addOpenDoorImage(image);
    }

    /**
     * Adds images for a specific building for a nation, including shadows and under-construction states.
     *
     * @param lstFile the list of game resources
     * @param nation  the nation
     * @param house   the house (building) resource
     */
    public void addImagesForBuilding(List<GameResource> lstFile, Nation nation, GameFiles.House house) {
        addBuildingForNation(nation, house.name(), getImageAt(lstFile, house.index()));
        addBuildingShadowForNation(nation, house.name(), getImageAt(lstFile, house.index() + 1));

        if (house.underConstruction()) {
            addBuildingUnderConstructionForNation(nation, house.name(), getImageAt(lstFile, house.index() + 2));

            if (house.underConstructionShadow()) {
                addBuildingUnderConstructionShadowForNation(nation, house.name(), getImageAt(lstFile, house.index() + 3));
            }
        } else {
            System.out.println("No under construction for " + house.name());
        }

        if (house.openDoor()) {
            int offset = house.underConstruction() ? 3 : 2;
            if (house.underConstructionShadow()) {
                offset += 1;
            }

            addOpenDoorForBuilding(nation, house.name(), getImageAt(lstFile, house.index() + offset));
        }

        if (house.name().equals("Headquarter")) {
            System.out.println(this.buildingMap.get(nation).get(house.name()));
        }
    }

    public void addBuildingWorkingAnimationWithShadow(Nation nation, String building, List<Bitmap> animation, List<Bitmap> animationShadow) {
        buildingMap.computeIfAbsent(nation, k -> new HashMap<>())
                .computeIfAbsent(building, k -> new BuildingImages())
                .addWorkingAnimationWithShadow(animation, animationShadow);
    }

    public void addBuildingWorkingAnimation(Nation nation, String name, List<Bitmap> animation) {
        buildingMap.computeIfAbsent(nation, k -> new HashMap<>())
                .computeIfAbsent(name, k -> new BuildingImages())
                .addWorkingAnimation(animation);
    }

    private static class BuildingImages {
        private Bitmap buildingReadyImage = null;
        private Bitmap buildingUnderConstruction = null;
        private Bitmap buildingReadyShadowImage;
        private Bitmap buildingUnderConstructionShadowImage;
        private Bitmap openDoorImage;
        private List<Bitmap> workingAnimation;
        private List<Bitmap> workingAnimationShadow;

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

        public void addOpenDoorImage(Bitmap image) {
            openDoorImage = image;
        }

        public void addWorkingAnimationWithShadow(List<Bitmap> animation, List<Bitmap> animationShadow) {
            workingAnimation = animation;
            workingAnimationShadow = animationShadow;
        }

        @Override
        public String toString() {
            return "BuildingImages{" +
                    "buildingReadyImage=" + buildingReadyImage +
                    ", buildingUnderConstruction=" + buildingUnderConstruction +
                    ", buildingReadyShadowImage=" + buildingReadyShadowImage +
                    ", buildingUnderConstructionShadowImage=" + buildingUnderConstructionShadowImage +
                    ", openDoorImage=" + openDoorImage +
                    ", workingAnimation" + workingAnimation +
                    ", workingAnimationShadow" + workingAnimationShadow +
                    '}';
        }

        public void addWorkingAnimation(List<Bitmap> animation) {
            workingAnimation = animation;
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
