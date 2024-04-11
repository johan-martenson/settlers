package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.CompassDirection;
import org.appland.settlers.assets.Nation;
import org.appland.settlers.assets.TextureFormat;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Bob;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.resources.PlayerBitmap;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.assets.utils.ImageTransformer;
import org.appland.settlers.assets.utils.NormalizedImageList;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.WorkerAction;
import org.appland.settlers.model.actors.Courier;

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

import static org.appland.settlers.model.actors.Courier.BodyType.FAT;

public class WorkerImageCollection {
    private final String name;
    private final Map<Nation, Map<CompassDirection, List<Bitmap>>> nationSpecificImages;
    private final Map<Nation, Map<CompassDirection, Map<PlayerColor, List<Bitmap>>>> nationSpecificImagesWithPlayerColor;
    private final Map<CompassDirection, List<Bitmap>> shadowImages;
    private final Map<Material, Map<CompassDirection, List<Bitmap>>> cargoImages;
    private final Map<CompassDirection, List<Bitmap>> imagesWithoutCargo;
    private final Map<CompassDirection, List<Bitmap>> bodyImages;
    private final Map<CompassDirection, Map<PlayerColor, List<Bitmap>>> imagesPerPlayer;
    private final Map<CompassDirection, Map<PlayerColor, List<Bitmap>>> imagesWithPlayerColor;
    private final Map<WorkerAction, Map<PlayerColor, List<Bitmap>>> actionsByPlayer;
    private final Map<WorkerAction, Map<CompassDirection, List<Bitmap>>> actionsWithDirection;
    private final Map<Nation, Map<WorkerAction, Map<CompassDirection, List<Bitmap>>>> nationSpecificActionsWithDirection;

    public WorkerImageCollection(String name) {
        this.name = name;

        nationSpecificImages = new EnumMap<>(Nation.class);
        imagesWithoutCargo = new EnumMap<>(CompassDirection.class);
        shadowImages = new EnumMap<>(CompassDirection.class);
        cargoImages = new EnumMap<>(Material.class);
        bodyImages = new EnumMap<>(CompassDirection.class);
        imagesPerPlayer = new EnumMap<>(CompassDirection.class);
        actionsByPlayer = new EnumMap<>(WorkerAction.class);
        actionsWithDirection = new EnumMap<>(WorkerAction.class);
        nationSpecificActionsWithDirection = new EnumMap<>(Nation.class);
        nationSpecificImagesWithPlayerColor = new EnumMap<>(Nation.class);
        imagesWithPlayerColor = new EnumMap<>(CompassDirection.class);
    }

    public void addNationSpecificImage(Nation nation, CompassDirection compassDirection, Bitmap workerImage) {
        if (!nationSpecificImages.containsKey(nation)) {
            nationSpecificImages.put(nation, new EnumMap<>(CompassDirection.class));
        }

        Map<CompassDirection, List<Bitmap>> directions = nationSpecificImages.get(nation);

        if (!directions.containsKey(compassDirection)) {
            directions.put(compassDirection, new ArrayList<>());
        }

        nationSpecificImages.get(nation).get(compassDirection).add(workerImage);
    }

    public void writeImageAtlas(String directory, Palette palette) throws IOException {

        /*
         * Write the image atlas, one row per direction, and collect metadata to write as json
         *
         * JSON format:
         *   - common: walking images without cargo
         *        - fullImages
         *        - fullImagesByPlayer
         *              - BLUE
         *        - actions
         *        - bodyImages
         *        - cargoImages: walking images with cargo
         *        - shadowImages: shadow images, same regardless of nation or cargo
         *   - nationSpecific: walking images without cargo, nation-specific
         *        - fullImages
         *        - fullImagesByPlayer
         *        - cargoImages
         */
        ImageBoard imageBoard = new ImageBoard();

        // Write walking animations where the worker isn't carrying anything and that are not nation-specific
        if (!imagesPerPlayer.isEmpty()) {
            for (var entry : imagesPerPlayer.entrySet()) {
                var direction = entry.getKey();
                var playerMap = entry.getValue();

                for (var entry1 : playerMap.entrySet()) {
                    var playerColor = entry1.getKey();
                    var images = entry1.getValue();

                    imageBoard.placeImageSeriesBottom(
                            ImageTransformer.normalizeImageSeries(images),
                            "common",
                            "fullImagesByPlayer",
                            direction.name().toUpperCase(),
                            playerColor.name().toUpperCase()
                            );
                }
            }
        } else if (!bodyImages.isEmpty()) {
            for (Map.Entry<CompassDirection, List<Bitmap>> entry : bodyImages.entrySet()) {
                var direction = entry.getKey();
                var images = entry.getValue();

                for (var playerColor : PlayerColor.values()) {
                    imageBoard.placeImageSeriesBottom(
                            ImageTransformer.drawForPlayer(playerColor, images),
                            "common",
                            "bodyImagesByPlayer",
                            direction.name().toUpperCase(),
                            playerColor.name().toUpperCase()
                    );
                }
            }
        }

        // Write walking animations with the correct player color
        if (!imagesWithPlayerColor.isEmpty()) {
            for (Map.Entry<CompassDirection, Map<PlayerColor, List<Bitmap>>> entry : imagesWithPlayerColor.entrySet()) {
                var direction = entry.getKey();
                var playerMap = entry.getValue();

                for (Map.Entry<PlayerColor, List<Bitmap>> entry1 : playerMap.entrySet()) {
                    var playerColor = entry1.getKey();
                    var images = entry1.getValue();

                    imageBoard.placeImageSeriesBottom(
                            ImageTransformer.normalizeImageSeries(images),
            "common",
                    "fullImagesByPlayer",
                    direction.name().toUpperCase(),
                    playerColor.name().toUpperCase()
                    );
                }
            }
        }

        // Write walking animations, per nation and direction
        if (!nationSpecificImages.isEmpty()) {
            for (Nation nation : Nation.values()) {
                Map<CompassDirection, List<Bitmap>> directionToImageMap = nationSpecificImages.get(nation);

                for (CompassDirection direction : CompassDirection.values()) {
                    if (directionToImageMap.get(direction).isEmpty()) {
                        continue;
                    }

                    // Handle each image per nation x direction
                    List<Bitmap> workerImages = directionToImageMap.get(direction);

                    imageBoard.placeImageSeriesBottom(
                            ImageTransformer.normalizeImageSeries(workerImages),
                            "nationSpecific",
                            "fullImages",
                            nation.name().toUpperCase(),
                            direction.name().toUpperCase()
                            );
                }
            }
        }

        // Write shadows, per direction (shadows are not nation-specific or are the same regardless of if/what the courier is carrying)
        if (!shadowImages.isEmpty()) {
            for (Map.Entry<CompassDirection, List<Bitmap>> entry : shadowImages.entrySet()) {
                CompassDirection direction = entry.getKey();
                List<Bitmap> shadowImagesForDirection = entry.getValue();

                imageBoard.placeImageSeriesBottom(
                    ImageTransformer.normalizeImageSeries(shadowImagesForDirection),
                        "common",
                        "shadowImages",
                        direction.name().toUpperCase()
                );
            }
        }

        // Write direction-specific actions (if any)
        if (!actionsWithDirection.isEmpty()) {
            for (Map.Entry<WorkerAction, Map<CompassDirection, List<Bitmap>>> entry : actionsWithDirection.entrySet()) {
                String action = entry.getKey().name().toUpperCase();
                Map<CompassDirection, List<Bitmap>> actions = entry.getValue();

                for (Map.Entry<CompassDirection, List<Bitmap>> actionEntry : actions.entrySet()) {
                    CompassDirection direction = actionEntry.getKey();
                    List<Bitmap> images = actionEntry.getValue();

                    imageBoard.placeImageSeriesBottom(
                            ImageTransformer.normalizeImageSeries(images),
                    "actions",
                            action,
                            direction.name().toUpperCase());
                }
            }
        }

        // Write lists of cargo images (if any)
        if (!cargoImages.keySet().isEmpty()) {
            for (Map.Entry<Material, Map<CompassDirection, List<Bitmap>>> materialMapEntry : cargoImages.entrySet()) {
                Material material = materialMapEntry.getKey();
                Map<CompassDirection, List<Bitmap>> imagesForMaterial = materialMapEntry.getValue();

                if (material == null) {
                    continue;
                }

                for (Map.Entry<CompassDirection, List<Bitmap>> compassDirectionListEntry : imagesForMaterial.entrySet()) {
                    CompassDirection direction = compassDirectionListEntry.getKey();
                    List<Bitmap> cargoImagesForDirection = compassDirectionListEntry.getValue();

                    imageBoard.placeImageSeriesBottom(
                            ImageTransformer.normalizeImageSeries(cargoImagesForDirection),
                            "common",
                            "cargoImages",
                            material.name().toUpperCase(),
                            direction.name().toUpperCase());
                }
            }
        }

        // Write actions that apply to any direction (if any)
        if (!actionsByPlayer.isEmpty()) {
            for (var entry : actionsByPlayer.entrySet()) {
                var action = entry.getKey().name().toUpperCase();
                var playerMap = entry.getValue();

                for (var entry1 : playerMap.entrySet()) {
                    var playerColor = entry1.getKey();
                    var images = entry1.getValue();

                    imageBoard.placeImageSeriesBottom(
                            ImageTransformer.normalizeImageSeries(images),
                            "common",
                            "actions",
                            action,
                            "any",
                            playerColor.name().toUpperCase());
                }
            }
        }

        // Write actions that are specific per nation and per direction (if any)
        if (!nationSpecificActionsWithDirection.isEmpty()) {
            for (Map.Entry<Nation, Map<WorkerAction, Map<CompassDirection, List<Bitmap>>>> entry : nationSpecificActionsWithDirection.entrySet()) {
                var nation = entry.getKey();
                var actionToDirection = entry.getValue();

                for (Map.Entry<WorkerAction, Map<CompassDirection, List<Bitmap>>> actionEntry : actionToDirection.entrySet()) {
                    var action = actionEntry.getKey();
                    var directionToImages = actionEntry.getValue();

                    for (Map.Entry<CompassDirection, List<Bitmap>> directionEntry : directionToImages.entrySet()) {
                        var direction = directionEntry.getKey();
                        var images = directionEntry.getValue();

                        imageBoard.placeImageSeriesBottom(
                                ImageTransformer.normalizeImageSeries(images),
                                "common",
                                "actions",
                                nation.name().toUpperCase(),
                                action.name().toUpperCase(),
                                direction.name().toUpperCase());
                    }
                }
            }
        }

        // Write nation-specific images by player
        if (!nationSpecificImagesWithPlayerColor.isEmpty()) {
            for (var entry : nationSpecificImagesWithPlayerColor.entrySet()) {
                var nation = entry.getKey();
                var directionMap = entry.getValue();

                int width = imageBoard.getCurrentWidth();;

                for (var entry1 : directionMap.entrySet()) {
                    var direction = entry1.getKey();
                    var playerColorMap = entry1.getValue();

                    for (var entry2 : playerColorMap.entrySet()) {
                        var playerColor = entry2.getKey();
                        var images = entry2.getValue();

                        imageBoard.placeImageSeriesBottomRightOf(width, ImageTransformer.normalizeImageSeries(images),
                                "nationSpecific",
                                "fullImagesByPlayer",
                                nation.name().toUpperCase(),
                                direction.name().toUpperCase(),
                                playerColor.name().toUpperCase());
                    }
                }
            }
        }

        // Write the image atlas to disk
        imageBoard.writeBoardToBitmap(palette).writeToFile(directory + "/" + "image-atlas-" + name.toLowerCase() + ".png");

        Path filePath = Paths.get(directory, "image-atlas-" + name.toLowerCase() + ".json");

        //Files.writeString(filePath, jsonImageAtlas.toJSONString());
        Files.writeString(filePath, imageBoard.getMetadataAsJson().toJSONString());
    }

    public void addShadowImages(CompassDirection compassDirection, List<Bitmap> images) {
        shadowImages.put(compassDirection, images);
    }

    public void readCargoImagesFromBob(Material material, Courier.BodyType bodyType, int bobId, Bob jobsBob) {
        int fatOffset = 0;

        if (bodyType == FAT) {
            fatOffset = 1;
        }

        cargoImages.put(material, new EnumMap<>(CompassDirection.class));

        for (CompassDirection compassDirection : CompassDirection.values()) {
            List<Bitmap> cargoImagesForDirection = new ArrayList<>();

            for (int i = 0; i < 8; i++) {
                int link = ((bobId * 8 + i) * 2 + fatOffset) * 6 + compassDirection.ordinal();
                int index = jobsBob.getLinkForIndex(link);

                cargoImagesForDirection.add(jobsBob.getBitmapAtIndex(index));
            }

            cargoImages.get(material).put(compassDirection, cargoImagesForDirection);
        }
    }

    public void readHeadImagesWithoutCargoFromBob(Courier.BodyType bodyType, int bobId, Bob jobsBob) {
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

            imagesWithoutCargo.put(compassDirection, cargoImagesForDirection);
        }
    }

    public void mergeBodyAndHeadImages(Palette palette) {
        for (Map.Entry<CompassDirection, List<Bitmap>> entry : bodyImages.entrySet()) {
            CompassDirection direction = entry.getKey();
            List<Bitmap> bodyImagesForDirection = entry.getValue();
            List<Bitmap> headImagesForDirection = imagesWithoutCargo.get(direction);

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

                Bitmap combinedImage;

                if (normalizedBodyImage instanceof PlayerBitmap normalizedBodyImagePlayer) {
                    PlayerBitmap playerCombinedImage = new PlayerBitmap(
                            normalizedImageList.getImageWidth(),
                            normalizedImageList.getImageHeight(),
                            palette,
                            TextureFormat.BGRA);

                    playerCombinedImage.getTextureBitmap().copyNonTransparentPixels(
                            normalizedBodyImagePlayer.getTextureBitmap(),
                            new Point(0, 0),
                            new Point(0, 0),
                            normalizedBodyImage.getDimension()
                    );

                    combinedImage = playerCombinedImage;
                } else {
                    combinedImage = new Bitmap(
                            normalizedImageList.getImageWidth(),
                            normalizedImageList.getImageHeight(),
                            palette,
                            TextureFormat.BGRA);
                }

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

            imagesPerPlayer.put(direction, new EnumMap<>(PlayerColor.class));
            for (var playerColor : PlayerColor.values()) {
                imagesPerPlayer.get(direction).put(playerColor, ImageTransformer.drawForPlayer(playerColor, mergedBodyAndHeadImagesForDirection));
            }
        }
    }

    public void readBodyImagesFromBob(Courier.BodyType bodyType, Bob carrierBob) {
        for (CompassDirection direction : CompassDirection.values()) {
            List<Bitmap> bodyImagesForDirection = new ArrayList<>();

            for (int animationIndex = 0; animationIndex < 8; animationIndex++) {
                PlayerBitmap body = carrierBob.getBody(bodyType == FAT, direction.ordinal(), animationIndex);

                bodyImagesForDirection.add(body);
            }

            bodyImages.put(direction, bodyImagesForDirection);
        }
    }

    public void addAnimation(WorkerAction action, List<Bitmap> images) {
        actionsByPlayer.put(action, new EnumMap<>(PlayerColor.class));

        for (var playerColor : PlayerColor.values()) {
            actionsByPlayer.get(action).put(playerColor, ImageTransformer.drawForPlayer(playerColor, images));
        }
    }

    public void addWorkAnimationInDirection(WorkerAction action, CompassDirection direction, List<Bitmap> images) {
        if (!actionsWithDirection.containsKey(action)) {
            actionsWithDirection.put(action, new EnumMap<>(CompassDirection.class));
        }

        actionsWithDirection.get(action).put(direction, images);
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

    public void addImage(CompassDirection direction, Bitmap image) {
        if (!imagesPerPlayer.containsKey(direction)) {
            imagesPerPlayer.put(direction, new EnumMap<>(PlayerColor.class));
        }

        for (var playerColor : PlayerColor.values()) {
            if (!imagesPerPlayer.get(direction).containsKey(playerColor)) {
                imagesPerPlayer.get(direction).put(playerColor, new ArrayList<>());
            }

            imagesPerPlayer.get(direction).get(playerColor).add(((PlayerBitmap)image).getBitmapForPlayer(playerColor));
        }
    }

    public void addNationSpecificImageWithPlayerColor(Nation nation, PlayerColor playerColor, CompassDirection direction, Bitmap image) {
        if (!nationSpecificImagesWithPlayerColor.containsKey(nation)) {
            nationSpecificImagesWithPlayerColor.put(nation, new EnumMap<>(CompassDirection.class));
        }

        if (!nationSpecificImagesWithPlayerColor.get(nation).containsKey(direction)) {
            nationSpecificImagesWithPlayerColor.get(nation).put(direction, new EnumMap<>(PlayerColor.class));
        }

        if (!nationSpecificImagesWithPlayerColor.get(nation).get(direction).containsKey(playerColor)) {
            nationSpecificImagesWithPlayerColor.get(nation).get(direction).put(playerColor, new ArrayList<>());
        }

        nationSpecificImagesWithPlayerColor.get(nation).get(direction).get(playerColor).add(image);
    }

    public void addImageWithPlayerColor(PlayerColor playerColor, CompassDirection direction, Bitmap image) {
        if (!imagesWithPlayerColor.containsKey(direction)) {
            imagesWithPlayerColor.put(direction, new EnumMap<>(PlayerColor.class));
        }

        if (!imagesWithPlayerColor.get(direction).containsKey(playerColor)) {
            imagesWithPlayerColor.get(direction).put(playerColor, new ArrayList<>());
        }

        imagesWithPlayerColor.get(direction).get(playerColor).add(image);
    }
}
