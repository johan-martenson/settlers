package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.*;
import org.json.simple.JSONObject;

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

        JSONObject jsonImageAtlas = new JSONObject();

        // Fill in the image atlas and fill in the meta-data
        int startNextNationAtX = 0;
        int currentNationStartedAtX = 0;
        Point cursor = new Point(0, 0);

        JSONObject jsonRegularBuildings = new JSONObject();

        jsonImageAtlas.put("buildings", jsonRegularBuildings);

        JSONObject jsonConstructionPlannedImages = new JSONObject();

        jsonImageAtlas.put("constructionPlanned", jsonConstructionPlannedImages);

        JSONObject jsonConstructionJustStartedImages = new JSONObject();

        jsonImageAtlas.put("constructionJustStarted", jsonConstructionJustStartedImages);

        for (Nation nation : Nation.values()) {

            JSONObject jsonBuildings = new JSONObject();

            jsonRegularBuildings.put(nation.name().toUpperCase(), jsonBuildings);

            currentNationStartedAtX = cursor.x;

            cursor.x = startNextNationAtX;
            cursor.y = 0;

            for (Map.Entry<String, BuildingImages> entry : this.buildingMap.get(nation).entrySet()) {
                String building = entry.getKey();
                BuildingImages images = entry.getValue();

                JSONObject jsonBuilding = new JSONObject();

                jsonBuildings.put(building, jsonBuilding);

                int currentRowHeight = 0;

                // Building ready image
                if (images.buildingReadyImage != null) {
                    imageBoard.placeImage(images.buildingReadyImage, cursor);

                    JSONObject jsonBuildingReadyImage = imageBoard.imageLocationToJson(images.buildingReadyImage);

                    jsonBuilding.put("ready", jsonBuildingReadyImage);

                    startNextNationAtX = Math.max(startNextNationAtX, cursor.x + images.buildingReadyImage.getWidth());

                    currentRowHeight = Math.max(currentRowHeight, images.buildingReadyImage.getHeight());

                    cursor.x = cursor.x + images.buildingReadyImage.getWidth();
                }

                // Building ready shadow image
                if (images.buildingReadyShadowImage != null) {
                    imageBoard.placeImage(images.buildingReadyShadowImage, cursor);

                    JSONObject jsonBuildingReadyShadowImage = imageBoard.imageLocationToJson(images.buildingReadyShadowImage);

                    jsonBuilding.put("readyShadow", jsonBuildingReadyShadowImage);

                    startNextNationAtX = Math.max(startNextNationAtX, cursor.x + images.buildingReadyShadowImage.getWidth());

                    currentRowHeight = Math.max(currentRowHeight, images.buildingReadyShadowImage.getHeight());

                    cursor.x = cursor.x + images.buildingReadyShadowImage.getWidth();
                }

                // Under construction image
                if (images.buildingUnderConstruction != null) {
                    imageBoard.placeImage(images.buildingUnderConstruction, cursor);

                    JSONObject jsonBuildingUnderConstructionImage = imageBoard.imageLocationToJson(images.buildingUnderConstruction);

                    jsonBuilding.put("underConstruction", jsonBuildingUnderConstructionImage);

                    startNextNationAtX = Math.max(startNextNationAtX, cursor.x + images.buildingUnderConstruction.getWidth());

                    currentRowHeight = Math.max(currentRowHeight, images.buildingUnderConstruction.getHeight());

                    cursor.x = cursor.x + images.buildingUnderConstruction.getWidth();
                }

                // Under construction shadow image
                if (images.buildingUnderConstructionShadowImage != null) {
                    imageBoard.placeImage(images.buildingUnderConstructionShadowImage, cursor);

                    JSONObject jsonBuildingUnderConstructionShadowImage = imageBoard.imageLocationToJson(images.buildingUnderConstruction);

                    jsonBuilding.put("underConstructionShadow", jsonBuildingUnderConstructionShadowImage);

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

            JSONObject jsonConstructionPlanned = new JSONObject();
            JSONObject jsonUnderConstruction = new JSONObject();

            jsonConstructionPlannedImages.put(nation.name().toUpperCase(), jsonConstructionPlanned);
            jsonConstructionJustStartedImages.put(nation.name().toUpperCase(), jsonUnderConstruction);

            // Construction planned image
            imageBoard.placeImage(constructionPlannedImage, cursor);

            JSONObject jsonConstructionPlannedImage = imageBoard.imageLocationToJson(constructionPlannedImage);

            jsonConstructionPlanned.put("image", jsonConstructionPlannedImage);

            cursor.x = cursor.x + constructionPlannedImage.getWidth();

            // Construction planned shadow image
            imageBoard.placeImage(constructionPlannedShadowImage, cursor);

            JSONObject jsonConstructionPlannedShadowImage = imageBoard.imageLocationToJson(constructionPlannedShadowImage);

            jsonConstructionPlanned.put("shadowImage", jsonConstructionPlannedShadowImage);

            cursor.x = cursor.x + constructionPlannedShadowImage.getWidth();

            // Under construction image
            imageBoard.placeImage(constructionJustStartedImage, cursor);

            JSONObject jsonConstructionJustStartedImage = imageBoard.imageLocationToJson(constructionJustStartedImage);

            jsonUnderConstruction.put("image", jsonConstructionJustStartedImage);

            cursor.x = cursor.x + constructionJustStartedImage.getWidth();

            // Under construction shadow image
            imageBoard.placeImage(constructionJustStartedShadowImage, cursor);

            JSONObject jsonConstructionJustStartedShadowImage = imageBoard.imageLocationToJson(constructionJustStartedShadowImage);

            jsonUnderConstruction.put("shadowImage", jsonConstructionJustStartedShadowImage);

            // Start at the right place for the next column
            cursor.x = startNextNationAtX;
        }

        // Write the image and the meta-data to files
        imageBoard.writeBoardToBitmap(palette).writeToFile(directory + "/image-atlas-buildings.png");
        Files.writeString(Paths.get(directory, "image-atlas-buildings.json"), jsonImageAtlas.toJSONString());

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
