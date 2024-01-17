package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.Bitmap;
import org.appland.settlers.assets.Bob;
import org.appland.settlers.assets.BodyType;
import org.appland.settlers.assets.CompassDirection;
import org.appland.settlers.assets.ImageBoard;
import org.appland.settlers.assets.Nation;
import org.appland.settlers.assets.NormalizedImageList;
import org.appland.settlers.assets.Palette;
import org.appland.settlers.assets.PlayerBitmap;
import org.appland.settlers.assets.TextureFormat;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.WorkerAction;
import org.json.simple.JSONObject;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.appland.settlers.assets.BodyType.FAT;
import static org.appland.settlers.assets.ImageBoard.LayoutDirection.COLUMN;
import static org.appland.settlers.assets.ImageBoard.LayoutDirection.ROW;

public class WorkerImageCollection {
    private final String name;
    private final Map<Nation, Map<CompassDirection, List<Bitmap>>> nationSpecificBodyAndHeadImages;
    private final Map<CompassDirection, List<Bitmap>> commonShadowImages;
    private final Map<Material, Map<CompassDirection, List<Bitmap>>> commonCargoImages;
    private final Map<CompassDirection, List<Bitmap>> commonHeadImagesWithoutCargo;
    private final Map<CompassDirection, List<Bitmap>> commonBodyImages;
    private final Map<CompassDirection, List<Bitmap>> commonBodyAndHeadImages;
    private final Map<WorkerAction, List<Bitmap>> commonActions;
    private final Map<WorkerAction, Map<CompassDirection, List<Bitmap>>> commonActionsWithDirection;
    private final Map<Nation, Map<WorkerAction, Map<CompassDirection, List<Bitmap>>>> nationSpecificActionsWithDirection;

    public WorkerImageCollection(String name) {
        this.name = name;

        nationSpecificBodyAndHeadImages = new EnumMap<>(Nation.class);
        commonHeadImagesWithoutCargo = new EnumMap<>(CompassDirection.class);
        commonShadowImages = new EnumMap<>(CompassDirection.class);
        commonCargoImages = new EnumMap<>(Material.class);
        commonBodyImages = new EnumMap<>(CompassDirection.class);
        commonBodyAndHeadImages = new EnumMap<>(CompassDirection.class);
        commonActions = new EnumMap<>(WorkerAction.class);
        commonActionsWithDirection = new EnumMap<>(WorkerAction.class);
        nationSpecificActionsWithDirection = new EnumMap<>(Nation.class);
    }

    public void addNationSpecificFullImage(Nation nation, CompassDirection compassDirection, Bitmap workerImage) {
        if (!nationSpecificBodyAndHeadImages.containsKey(nation)) {
            nationSpecificBodyAndHeadImages.put(nation, new EnumMap<>(CompassDirection.class));
        }

        Map<CompassDirection, List<Bitmap>> directions = nationSpecificBodyAndHeadImages.get(nation);

        if (!directions.containsKey(compassDirection)) {
            directions.put(compassDirection, new ArrayList<>());
        }

        nationSpecificBodyAndHeadImages.get(nation).get(compassDirection).add(workerImage);
    }

    public void writeImageAtlas(String directory, Palette palette) throws IOException {

        /*
         * Write the image atlas, one row per direction, and collect metadata to write as json
         *
         * JSON format:
         *   - common: walking images without cargo, not nation-specific
         *        - fullImages
         *        - cargoImages: walking images with cargo, not nation-specific
         *        - shadowImages: shadow images, same regardless of nation or cargo
         *   - nationSpecific: walking images without cargo, nation-specific
         *        - fullImages
         *        - cargoImages
         */
        ImageBoard imageBoard = new ImageBoard();

        JSONObject jsonImageAtlas = new JSONObject();
        JSONObject jsonCommon = new JSONObject();
        JSONObject jsonNationSpecific = new JSONObject();
        JSONObject jsonActions = new JSONObject();

        jsonImageAtlas.put("common", jsonCommon);
        jsonImageAtlas.put("nationSpecific", jsonNationSpecific);

        if (!commonActionsWithDirection.isEmpty() || !commonActions.isEmpty()) {
            jsonCommon.put("actions", jsonActions);
        }

        Point cursor = new Point(0, 0);

        // Write walking animations where the worker isn't carrying anything and that are not nation-specific
        if (!commonBodyAndHeadImages.isEmpty()) {
            JSONObject jsonImages = new JSONObject();

            jsonCommon.put("fullImages", jsonImages);

            for (Map.Entry<CompassDirection, List<Bitmap>> entry : commonBodyAndHeadImages.entrySet()) {
                CompassDirection compassDirection = entry.getKey();
                List<Bitmap> images = entry.getValue();

                cursor.x = 0;

                NormalizedImageList normalizedImageList = new NormalizedImageList(images);
                List<Bitmap> normalizedImages = normalizedImageList.getNormalizedImages();

                imageBoard.placeImageSeries(normalizedImages, cursor, ROW);

                jsonImages.put(compassDirection.name().toUpperCase(), imageBoard.imageSeriesLocationToJson(normalizedImages));

                cursor.y = cursor.y + normalizedImageList.getImageHeight();
            }
        } else {
            JSONObject jsonBodyImages = new JSONObject();

            jsonCommon.put("bodyImages", jsonBodyImages);

            for (Map.Entry<CompassDirection, List<Bitmap>> entry : commonBodyImages.entrySet()) {
                CompassDirection compassDirection = entry.getKey();
                List<Bitmap> images = entry.getValue();

                cursor.x = 0;

                NormalizedImageList normalizedImageList = new NormalizedImageList(images);
                List<Bitmap> normalizedImages = normalizedImageList.getNormalizedImages();

                imageBoard.placeImageSeries(normalizedImages, cursor, ROW);

                jsonBodyImages.put(compassDirection.name().toUpperCase(), imageBoard.imageSeriesLocationToJson(normalizedImages));

                cursor.y = cursor.y + normalizedImageList.getImageHeight();
            }
        }

        // Write walking animations, per nation and direction
        if (!nationSpecificBodyAndHeadImages.isEmpty()) {
            JSONObject jsonFullImages = new JSONObject();

            jsonNationSpecific.put("fullImages", jsonFullImages);

            for (Nation nation : Nation.values()) {

                Map<CompassDirection, List<Bitmap>> directionToImageMap = nationSpecificBodyAndHeadImages.get(nation);

                JSONObject jsonNationInfo = new JSONObject();

                jsonFullImages.put(nation.name().toUpperCase(), jsonNationInfo);

                for (CompassDirection compassDirection : CompassDirection.values()) {

                    if (directionToImageMap.get(compassDirection).isEmpty()) {
                        continue;
                    }

                    cursor.x = 0;

                    // Handle each image per nation x direction
                    List<Bitmap> workerImages = directionToImageMap.get(compassDirection);
                    NormalizedImageList normalizedWorkerList = new NormalizedImageList(workerImages);
                    List<Bitmap> normalizedWorkerImages = normalizedWorkerList.getNormalizedImages();

                    imageBoard.placeImageSeries(normalizedWorkerImages, cursor, ROW);

                    jsonNationInfo.put(compassDirection.name().toUpperCase(), imageBoard.imageSeriesLocationToJson(normalizedWorkerImages));

                    cursor.y = cursor.y + normalizedWorkerList.getImageHeight();
                }
            }
        }

        // Write shadows, per direction (shadows are not nation-specific or are the same regardless of if/what the courier is carrying)
        if (!commonShadowImages.isEmpty()) {
            JSONObject jsonShadowImages = new JSONObject();

            jsonCommon.put("shadowImages", jsonShadowImages);

            for (Map.Entry<CompassDirection, List<Bitmap>> entry : commonShadowImages.entrySet()) {
                CompassDirection compassDirection = entry.getKey();
                List<Bitmap> shadowImagesForDirection = entry.getValue();

                cursor.x = 0;

                NormalizedImageList normalizedShadowListForDirection = new NormalizedImageList(shadowImagesForDirection);
                List<Bitmap> normalizedShadowImagesForDirection = normalizedShadowListForDirection.getNormalizedImages();

                imageBoard.placeImageSeries(normalizedShadowImagesForDirection, cursor, ROW);

                jsonShadowImages.put(compassDirection.name().toUpperCase(), imageBoard.imageSeriesLocationToJson(normalizedShadowImagesForDirection));

                cursor.y = cursor.y + normalizedShadowListForDirection.getImageHeight();
            }
        }

        // Write direction-specific actions (if any)
        if (!commonActionsWithDirection.isEmpty()) {
            for (Map.Entry<WorkerAction, Map<CompassDirection, List<Bitmap>>> entry : commonActionsWithDirection.entrySet()) {
                String action = entry.getKey().name().toUpperCase();
                Map<CompassDirection, List<Bitmap>> actions = entry.getValue();

                JSONObject jsonAction = new JSONObject();

                jsonActions.put(action, jsonAction);

                for (Map.Entry<CompassDirection, List<Bitmap>> actionEntry : actions.entrySet()) {
                    CompassDirection compassDirection = actionEntry.getKey();
                    List<Bitmap> images = actionEntry.getValue();

                    cursor.x = 0;

                    NormalizedImageList normalizedImageList = new NormalizedImageList(images);
                    List<Bitmap> normalizedImages = normalizedImageList.getNormalizedImages();

                    imageBoard.placeImageSeries(normalizedImages, cursor, ROW);

                    jsonAction.put(compassDirection.name().toUpperCase(), imageBoard.imageSeriesLocationToJson(normalizedImages));

                    cursor.y = cursor.y + normalizedImageList.getImageHeight();
                }
            }
        }

        // Write lists of cargo images (if any)
        if (!commonCargoImages.keySet().isEmpty()) {

            JSONObject jsonMultipleCargoImages = new JSONObject();

            jsonCommon.put("cargoImages", jsonMultipleCargoImages);

            for (Map.Entry<Material, Map<CompassDirection, List<Bitmap>>> materialMapEntry : commonCargoImages.entrySet()) {
                Material material = materialMapEntry.getKey();
                Map<CompassDirection, List<Bitmap>> imagesForMaterial = materialMapEntry.getValue();

                if (material == null) {
                    continue;
                }

                JSONObject jsonMaterialImages = new JSONObject();

                jsonMultipleCargoImages.put(material.name().toUpperCase(), jsonMaterialImages);

                for (Map.Entry<CompassDirection, List<Bitmap>> compassDirectionListEntry : imagesForMaterial.entrySet()) {

                    CompassDirection compassDirection = compassDirectionListEntry.getKey();
                    List<Bitmap> cargoImagesForDirection = compassDirectionListEntry.getValue();

                    cursor.x = 0;

                    NormalizedImageList normalizedCargoListForDirection = new NormalizedImageList(cargoImagesForDirection);
                    List<Bitmap> normalizedCargoImagesForDirection = normalizedCargoListForDirection.getNormalizedImages();

                    imageBoard.placeImageSeries(normalizedCargoImagesForDirection, cursor, ROW);

                    jsonMaterialImages.put(compassDirection.name().toUpperCase(), imageBoard.imageSeriesLocationToJson(normalizedCargoImagesForDirection));

                    cursor.y = cursor.y + normalizedCargoListForDirection.getImageHeight();
                }
            }
        }

        // Write actions that apply to any direction (if any)
        if (!commonActions.isEmpty()) {
            for (Map.Entry<WorkerAction, List<Bitmap>> entry : commonActions.entrySet()) {
                String action = entry.getKey().name().toUpperCase();
                List<Bitmap> images = entry.getValue();

                cursor.x = imageBoard.getCurrentWidth();
                cursor.y = 0;

                JSONObject jsonAction = new JSONObject();

                NormalizedImageList normalizedImageList = new NormalizedImageList(images);
                List<Bitmap> normalizedImages = normalizedImageList.getNormalizedImages();

                imageBoard.placeImageSeries(normalizedImages, cursor, COLUMN);

                jsonActions.put(action, jsonAction);
                jsonAction.put("any", imageBoard.imageSeriesLocationToJson(normalizedImages));

                cursor.y = cursor.y + normalizedImageList.getImageHeight();
            }
        }

        // Write actions that are specific per nation and per direction (if any)
        if (!nationSpecificActionsWithDirection.isEmpty()) {
            JSONObject jsonNationSpecificActions = new JSONObject();

            jsonNationSpecific.put("actions", jsonNationSpecificActions);

            for (Map.Entry<Nation, Map<WorkerAction, Map<CompassDirection, List<Bitmap>>>> entry : nationSpecificActionsWithDirection.entrySet()) {
                var nation = entry.getKey();
                var actionToDirection = entry.getValue();

                JSONObject jsonNation;

                if (jsonNationSpecific.containsKey(nation.name().toUpperCase())) {
                    jsonNation = (JSONObject) jsonNationSpecific.get(nation.name().toUpperCase());
                } else {
                    jsonNation = new JSONObject();

                    jsonNationSpecificActions.put(nation.name().toUpperCase(), jsonNation);
                }

                for (Map.Entry<WorkerAction, Map<CompassDirection, List<Bitmap>>> actionEntry : actionToDirection.entrySet()) {
                    var action = actionEntry.getKey();
                    var directionToImages = actionEntry.getValue();

                    JSONObject jsonAction = new JSONObject();

                    jsonNation.put(action.name().toUpperCase(), jsonAction);

                    for (Map.Entry<CompassDirection, List<Bitmap>> directionEntry : directionToImages.entrySet()) {
                        var direction = directionEntry.getKey();
                        var images = directionEntry.getValue();

                        NormalizedImageList normalizedImageList = new NormalizedImageList(images);
                        List<Bitmap> normalizedImages = normalizedImageList.getNormalizedImages();

                        JSONObject jsonImageSeries = imageBoard.placeImageSeriesBottom(normalizedImages);

                        jsonAction.put(direction.name().toUpperCase(), jsonImageSeries);
                    }
                }
            }
        }

        // Write the image atlas to disk
        imageBoard.writeBoardToBitmap(palette).writeToFile(directory + "/" + "image-atlas-" + name.toLowerCase() + ".png");

        Path filePath = Paths.get(directory, "image-atlas-" + name.toLowerCase() + ".json");

        Files.writeString(filePath, jsonImageAtlas.toJSONString());
    }

    public void addShadowImages(CompassDirection compassDirection, List<Bitmap> images) {
        commonShadowImages.put(compassDirection, images);
    }

    public void readCargoImagesFromBob(Material material, BodyType bodyType, int bobId, Bob jobsBob) {
        int fatOffset = 0;

        if (bodyType == FAT) {
            fatOffset = 1;
        }

        commonCargoImages.put(material, new EnumMap<>(CompassDirection.class));

        for (CompassDirection compassDirection : CompassDirection.values()) {

            List<Bitmap> cargoImagesForDirection = new ArrayList<>();

            for (int i = 0; i < 8; i++) {
                int link = ((bobId * 8 + i) * 2 + fatOffset) * 6 + compassDirection.ordinal();
                int index = jobsBob.getLinkForIndex(link);

                cargoImagesForDirection.add(jobsBob.getBitmapAtIndex(index));
            }

            commonCargoImages.get(material).put(compassDirection, cargoImagesForDirection);
        }
    }

    public void readHeadImagesWithoutCargoFromBob(BodyType bodyType, int bobId, Bob jobsBob) {
        int fatOffset = 0;

        if (bodyType == FAT) {
            fatOffset = 1;
        }

        for (CompassDirection compassDirection : CompassDirection.values()) {

            List<Bitmap> cargoImagesForDirection = new ArrayList<>();

            for (int i = 0; i < 8; i++) {
                int link = ((bobId * 8 + i) * 2 + fatOffset) * 6 + compassDirection.ordinal();
                int index = jobsBob.getLinkForIndex(link);

                cargoImagesForDirection.add(jobsBob.getBitmapAtIndex(index));
            }

            commonHeadImagesWithoutCargo.put(compassDirection, cargoImagesForDirection);
        }
    }

    public void mergeBodyAndHeadImages(Palette palette) {
        for (Map.Entry<CompassDirection, List<Bitmap>> entry : commonBodyImages.entrySet()) {
            CompassDirection compassDirection = entry.getKey();
            List<Bitmap> bodyImagesForDirection = entry.getValue();
            List<Bitmap> headImagesForDirection = commonHeadImagesWithoutCargo.get(compassDirection);

            List<Bitmap> mergedBodyAndHeadImagesForDirection = new ArrayList<>();

            // Merge the head images with the body images
            for (int i = 0; i < 8; i++) {
                Bitmap bodyImage = bodyImagesForDirection.get(i);
                Bitmap headImage = headImagesForDirection.get(i);

                List<Bitmap> imageList = new ArrayList<>();
                imageList.add(bodyImage);
                imageList.add(headImage);

                NormalizedImageList normalizedImageList = new NormalizedImageList(imageList);
                List<Bitmap> normalizedImages = normalizedImageList.getNormalizedImages();

                Bitmap normalizedBodyImage = normalizedImages.get(0);
                Bitmap normalizedHeadImage = normalizedImages.get(1);

                Bitmap combinedImage = new Bitmap(
                        normalizedImageList.getImageWidth(),
                        normalizedImageList.getImageHeight(),
                        palette,
                        TextureFormat.BGRA);

                combinedImage.copyNonTransparentPixels(
                        normalizedBodyImage,
                        new Point(0, 0),
                        new Point(0, 0),
                        normalizedBodyImage.getDimension()
                );

                combinedImage.copyNonTransparentPixels(
                        normalizedHeadImage,
                        new Point(0, 0),
                        new Point(0, 0),
                        normalizedHeadImage.getDimension()
                );

                combinedImage.setNx(normalizedImageList.nx);
                combinedImage.setNy(normalizedImageList.ny);

                mergedBodyAndHeadImagesForDirection.add(combinedImage);
            }

            commonBodyAndHeadImages.put(compassDirection, mergedBodyAndHeadImagesForDirection);
        }
    }

    public void readBodyImagesFromBob(BodyType bodyType, Bob carrierBob) {
        for (CompassDirection compassDirection : CompassDirection.values()) {

            List<Bitmap> bodyImagesForDirection = new ArrayList<>();

            for (int animationIndex = 0; animationIndex < 8; animationIndex++) {
                PlayerBitmap body = carrierBob.getBody(bodyType == FAT, compassDirection.ordinal(), animationIndex);

                bodyImagesForDirection.add(body);
            }

            commonBodyImages.put(compassDirection, bodyImagesForDirection);
        }
    }

    public void addAnimation(WorkerAction action, List<Bitmap> images) {
        commonActions.put(action, images);
    }

    public void addWorkAnimationInDirection(WorkerAction action, CompassDirection direction, List<Bitmap> images) {
        if (!commonActionsWithDirection.containsKey(action)) {
            commonActionsWithDirection.put(action, new EnumMap<>(CompassDirection.class));
        }

        commonActionsWithDirection.get(action).put(direction, images);
    }

    public void addNationSpecificAnimationInDirection(Nation nation, CompassDirection direction, WorkerAction workerAction, List<Bitmap> images) {
        if (!nationSpecificActionsWithDirection.containsKey(nation)) {
            nationSpecificActionsWithDirection.put(nation, new HashMap<>());
        }

        var actionToDirection = nationSpecificActionsWithDirection.get(nation);

        if (!actionToDirection.containsKey(workerAction)) {
            actionToDirection.put(workerAction, new HashMap<>());
        }

        var directionToImages = actionToDirection.get(workerAction);

        directionToImages.put(direction, images);
    }
}
