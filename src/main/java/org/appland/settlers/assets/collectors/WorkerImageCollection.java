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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
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
    private final Map<WorkerAction, Map<CompassDirection, Map<PlayerColor, List<Bitmap>>>> actionsWithDirectionByPlayer;
    private final Map<Nation, Map<WorkerAction, Map<CompassDirection, Map<PlayerColor, List<Bitmap>>>>> nationSpecificActionsWithDirectionByPlayer;

    public WorkerImageCollection(String name) {
        this.name = name;

        nationSpecificImages = new EnumMap<>(Nation.class);
        imagesWithoutCargo = new EnumMap<>(CompassDirection.class);
        shadowImages = new EnumMap<>(CompassDirection.class);
        cargoImages = new EnumMap<>(Material.class);
        bodyImages = new EnumMap<>(CompassDirection.class);
        imagesPerPlayer = new EnumMap<>(CompassDirection.class);
        actionsByPlayer = new EnumMap<>(WorkerAction.class);
        actionsWithDirectionByPlayer = new EnumMap<>(WorkerAction.class);
        nationSpecificActionsWithDirectionByPlayer = new EnumMap<>(Nation.class);
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
        ImageBoard imageBoard = new ImageBoard();

        // TODO: resolve obvious bug where two sets of images are written to the same path:
        //   - common / fullImagesByPlayer / <direction> / <player color>

        // Write walking animations where the worker isn't carrying anything and that are not nation-specific
        imagesPerPlayer.forEach((direction, playerMap) -> playerMap
                .forEach((playerColor, images) -> imageBoard.placeImageSeriesBottom(
                        ImageTransformer.normalizeImageSeries(images),
                        "common",
                        "fullImagesByPlayer",
                        direction.name().toUpperCase(),
                        playerColor.name().toUpperCase())));

        if (imagesPerPlayer.isEmpty() && !bodyImages.isEmpty()) {
            bodyImages.forEach((direction, images) -> Arrays.stream(PlayerColor.values())
                    .forEach((playerColor -> imageBoard.placeImageSeriesBottom(
                            ImageTransformer.drawForPlayer(playerColor, images),
                            "common",
                            "bodyImagesByPlayer",
                            direction.name().toUpperCase(),
                            playerColor.name().toUpperCase()))));
        }

        // Write walking animations with the correct player color
        imagesWithPlayerColor.forEach((direction, playerColorMap) -> playerColorMap
                .forEach((playerColor, images) -> imageBoard.placeImageSeriesBottom(
                        ImageTransformer.normalizeImageSeries(images),
                        "common",
                        "fullImagesByPlayer",
                        direction.name().toUpperCase(),
                        playerColor.name().toUpperCase())));

        // Write walking animations, per nation and direction
        nationSpecificImages.forEach((nation, directionMap) -> directionMap
                .forEach((direction, images) -> imageBoard.placeImageSeriesBottom(
                        ImageTransformer.normalizeImageSeries(images),
                        "nationSpecific",
                        "fullImages",
                        nation.name().toUpperCase(),
                        direction.name().toUpperCase())));

        // Write shadows, per direction (shadows are not nation-specific or are the same regardless of if/what the courier is carrying)
        shadowImages.forEach((direction, images) -> imageBoard.placeImageSeriesBottom(
                ImageTransformer.normalizeImageSeries(images),
                "common",
                "shadowImages",
                direction.name().toUpperCase()));

        // Write direction-specific actions (if any)
        actionsWithDirectionByPlayer.forEach((action, directionMap) -> directionMap
                .forEach((direction, playerMap) -> playerMap
                        .forEach((playerColor, images) -> imageBoard.placeImageSeriesBottom(
                                ImageTransformer.normalizeImageSeries(images),
                                "common",
                                "actionsByPlayer",
                                action.name().toUpperCase(),
                                direction.name().toUpperCase(),
                                playerColor.name().toUpperCase()))));

        // Write lists of cargo images (if any)
        cargoImages.forEach((material, directionMap) -> directionMap
                .forEach((direction, images) -> imageBoard.placeImageSeriesBottom(
                        ImageTransformer.normalizeImageSeries(images),
                        "common",
                        "cargoImages",
                        material.name().toUpperCase(),
                        direction.name().toUpperCase())));

        // Write actions that apply to any direction (if any)
        actionsByPlayer.forEach((action, playerColorMap) -> playerColorMap
                .forEach((playerColor, images) -> imageBoard.placeImageSeriesBottom(
                        ImageTransformer.normalizeImageSeries(images),
                        "common",
                        "actionsByPlayer",
                        action.name().toUpperCase(),
                        "any",
                        playerColor.name().toUpperCase())));

        // Write actions that are specific per nation and per direction (if any)
        nationSpecificActionsWithDirectionByPlayer.forEach((nation, actionMap) -> actionMap
                .forEach((action, directionMap) -> directionMap
                        .forEach((direction, playerColorMap) -> playerColorMap
                                .forEach((playerColor, images) -> imageBoard.placeImageSeriesBottom(
                                        ImageTransformer.normalizeImageSeries(images),
                                        "common",
                                        "actionsByPlayer",
                                        nation.name().toUpperCase(),
                                        action.name().toUpperCase(),
                                        direction.name().toUpperCase(),
                                        playerColor.name().toUpperCase())))));

        // Write nation-specific images by player
        nationSpecificImagesWithPlayerColor.forEach((nation, directionMap) -> {
                    int width = imageBoard.getCurrentWidth();

                    directionMap.forEach(
                            (direction, playerColorMap) -> playerColorMap.forEach(
                                    (playerColor, images) -> imageBoard.placeImageSeriesBottomRightOf(width, ImageTransformer.normalizeImageSeries(images),
                                            "nationSpecific",
                                            "fullImagesByPlayer",
                                            nation.name().toUpperCase(),
                                            direction.name().toUpperCase(),
                                            playerColor.name().toUpperCase())));
                }
        );

        imageBoard.writeBoard(directory, "image-atlas-" + name.toLowerCase(), palette);
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

    public void addAnimation(WorkerAction action, List<PlayerBitmap> images) {
        actionsByPlayer.put(action, new EnumMap<>(PlayerColor.class));

        for (var playerColor : PlayerColor.values()) {
            actionsByPlayer.get(action).put(playerColor, ImageTransformer.drawForPlayer(playerColor, images));
        }
    }

    public void addWorkAnimationInDirection(WorkerAction action, CompassDirection direction, List<Bitmap> images) {
        if (!actionsWithDirectionByPlayer.containsKey(action)) {
            actionsWithDirectionByPlayer.put(action, new EnumMap<>(CompassDirection.class));
        }

        if (!actionsWithDirectionByPlayer.get(action).containsKey(direction)) {
            actionsWithDirectionByPlayer.get(action).put(direction, new EnumMap<>(PlayerColor.class));
        }

        Arrays.stream(PlayerColor.values()).forEach(
            playerColor -> actionsWithDirectionByPlayer.get(action).get(direction).put(
                    playerColor,
                    ImageTransformer.drawForPlayer(playerColor, images)
            ));
    }

    public void addNationSpecificAnimationInDirection(Nation nation, CompassDirection direction, WorkerAction action, List<PlayerBitmap> images) {
        if (!nationSpecificActionsWithDirectionByPlayer.containsKey(nation)) {
            nationSpecificActionsWithDirectionByPlayer.put(nation, new EnumMap<>(WorkerAction.class));
        }

        if (!nationSpecificActionsWithDirectionByPlayer.get(nation).containsKey(action)) {
            nationSpecificActionsWithDirectionByPlayer.get(nation).put(action, new EnumMap<>(CompassDirection.class));
        }

        if (!nationSpecificActionsWithDirectionByPlayer.get(nation).get(action).containsKey(direction)) {
            nationSpecificActionsWithDirectionByPlayer.get(nation).get(action).put(direction, new EnumMap<>(PlayerColor.class));
        }

        Arrays.stream(PlayerColor.values()).forEach(
                playerColor -> nationSpecificActionsWithDirectionByPlayer.get(nation).get(action).get(direction).put(
                        playerColor,
                        ImageTransformer.drawForPlayer(playerColor, images))
        );
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

    public void addNationSpecificAnimationInDirectionWithPlayerColor(Nation nation, CompassDirection direction, WorkerAction action, List<Bitmap> images) {

    }
}
