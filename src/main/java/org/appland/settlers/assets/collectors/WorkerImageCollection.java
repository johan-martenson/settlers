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
    private final Map<Nation, Map<CompassDirection, List<Bitmap>>> nationSpecificImages = new EnumMap<>(Nation.class);
    private final Map<Nation, Map<CompassDirection, Map<PlayerColor, List<Bitmap>>>> nationSpecificImagesWithPlayerColor = new EnumMap<>(Nation.class);
    private final Map<CompassDirection, List<Bitmap>> shadowImages = new EnumMap<>(CompassDirection.class);
    private final Map<Material, Map<CompassDirection, List<Bitmap>>> cargoImages = new EnumMap<>(Material.class);
    private final Map<CompassDirection, List<Bitmap>> imagesWithoutCargo = new EnumMap<>(CompassDirection.class);
    private final Map<CompassDirection, List<Bitmap>> bodyImages = new EnumMap<>(CompassDirection.class);
    private final Map<CompassDirection, Map<PlayerColor, List<Bitmap>>> imagesPerPlayer = new EnumMap<>(CompassDirection.class);
    private final Map<CompassDirection, Map<PlayerColor, List<Bitmap>>> imagesWithPlayerColor = new EnumMap<>(CompassDirection.class);
    private final Map<WorkerAction, Map<PlayerColor, List<Bitmap>>> actionsByPlayer = new EnumMap<>(WorkerAction.class);
    private final Map<WorkerAction, Map<CompassDirection, Map<PlayerColor, List<Bitmap>>>> actionsWithDirectionByPlayer = new EnumMap<>(WorkerAction.class);
    private final Map<Nation, Map<WorkerAction, Map<CompassDirection, Map<PlayerColor, List<Bitmap>>>>> nationSpecificActionsWithDirectionByPlayer = new EnumMap<>(Nation.class);

    /**
     * Constructs a WorkerImageCollection with a specific name.
     *
     * @param name the name of the collection
     */
    public WorkerImageCollection(String name) {
        this.name = name;
    }

    /**
     * Adds a nation-specific image for a particular direction.
     *
     * @param nation            the nation
     * @param compassDirection  the direction
     * @param workerImage       the image to add
     */
    public void addNationSpecificImage(Nation nation, CompassDirection compassDirection, Bitmap workerImage) {
        nationSpecificImages
                .computeIfAbsent(nation, k -> new EnumMap<>(CompassDirection.class))
                .computeIfAbsent(compassDirection, k -> new ArrayList<>())
                .add(workerImage);
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

        // TODO: resolve obvious bug where two sets of images are written to the same path:
        //   - common / fullImagesByPlayer / <direction> / <player color>

        // Write walking animations where the worker isn't carrying anything and that are not nation-specific
        imagesPerPlayer.forEach((direction, playerMap) ->
                playerMap.forEach((playerColor, images) -> imageBoard.placeImageSeriesBottom(
                        ImageTransformer.normalizeImageSeries(images),
                        "common",
                        "fullImagesByPlayer",
                        direction.name().toUpperCase(),
                        playerColor.name().toUpperCase())));

        if (imagesPerPlayer.isEmpty() && !bodyImages.isEmpty()) {
            bodyImages.forEach((direction, images) ->
                    Arrays.stream(PlayerColor.values()).forEach((playerColor -> imageBoard.placeImageSeriesBottom(
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
                                        "nationSpecific",
                                        "actionsByPlayer",
                                        nation.name().toUpperCase(),
                                        action.name().toUpperCase(),
                                        direction.name().toUpperCase(),
                                        playerColor.name().toUpperCase())))));

        // Write nation-specific images by player
        nationSpecificImagesWithPlayerColor.forEach((nation, directionMap) -> {
                    int width = imageBoard.getCurrentWidth();

                    directionMap.forEach((direction, playerColorMap) -> playerColorMap.forEach(
                            (playerColor, images) -> imageBoard.placeImageSeriesBottomRightOf(width, ImageTransformer.normalizeImageSeries(images),
                                    "nationSpecific",
                                    "fullImagesByPlayer",
                                    nation.name().toUpperCase(),
                                    direction.name().toUpperCase(),
                                    playerColor.name().toUpperCase())));
                }
        );

        imageBoard.writeBoard(directory, String.format("image-atlas-%s", name.toLowerCase()), palette);
    }

    /**
     * Adds shadow images for a specific direction.
     *
     * @param compassDirection the direction
     * @param images           the list of shadow images
     */
    public void addShadowImages(CompassDirection compassDirection, List<Bitmap> images) {
        shadowImages.put(compassDirection, images);
    }

    /**
     * Reads cargo images from a BOB resource for a specific material and body type.
     *
     * @param material the material
     * @param bodyType the body type
     * @param bobId    the BOB ID
     * @param jobsBob  the BOB resource
     */
    public void readCargoImagesFromBob(Material material, Courier.BodyType bodyType, int bobId, Bob jobsBob) {
        int fatOffset = (bodyType == FAT) ? 1 : 0;

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

    /**
     * Reads head images without cargo from a BOB resource for a specific body type.
     *
     * @param bodyType the body type
     * @param bobId    the BOB ID
     * @param jobsBob  the BOB resource
     */
    public void readHeadImagesWithoutCargoFromBob(Courier.BodyType bodyType, int bobId, Bob jobsBob) {
        int fatOffset = (bodyType == FAT) ? 1 : 0;

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

    /**
     * Merges body and head images into a single image for each direction and player color.
     *
     * @param palette the palette to use for the images
     */
    public void mergeBodyAndHeadImages(Palette palette) {
        bodyImages.forEach((direction, bodyImagesForDirection) -> {
            List<Bitmap> headImagesForDirection = imagesWithoutCargo.get(direction);
            List<Bitmap> mergedBodyAndHeadImagesForDirection = new ArrayList<>();

            // Merge the head images with the body images
            for (int i = 0; i < 8; i++) {
                Bitmap bodyImage = bodyImagesForDirection.get(i);
                Bitmap headImage = headImagesForDirection.get(i);

                var imageList = List.of(bodyImage, headImage);

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
        });
    }

    /**
     * Reads body images from a BOB resource for a specific body type.
     *
     * @param bodyType  the body type
     * @param carrierBob the BOB resource
     */
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

    /**
     * Adds an animation for a specific action and player color.
     *
     * @param action the worker action
     * @param images the list of player bitmap images
     */
    public void addAnimation(WorkerAction action, List<PlayerBitmap> images) {
        actionsByPlayer.put(action, new EnumMap<>(PlayerColor.class));

        for (var playerColor : PlayerColor.values()) {
            actionsByPlayer.get(action).put(playerColor, ImageTransformer.drawForPlayer(playerColor, images));
        }
    }

    /**
     * Adds a work animation in a specific direction for a specific action.
     *
     * @param action    the worker action
     * @param direction the compass direction
     * @param images    the list of bitmap images
     */
    public void addWorkAnimationInDirection(WorkerAction action, CompassDirection direction, List<Bitmap> images) {
        actionsWithDirectionByPlayer
                .computeIfAbsent(action, k -> new EnumMap<>(CompassDirection.class))
                .computeIfAbsent(direction, k -> new EnumMap<>(PlayerColor.class));

        Arrays.stream(PlayerColor.values()).forEach(
            playerColor -> actionsWithDirectionByPlayer.get(action).get(direction).put(
                    playerColor,
                    ImageTransformer.drawForPlayer(playerColor, images)
            ));
    }

    /**
     * Adds a nation-specific animation in a specific direction for a specific action and player color.
     *
     * @param nation    the nation
     * @param direction the compass direction
     * @param action    the worker action
     * @param images    the list of player bitmap images
     */
    public void addNationSpecificAnimationInDirection(Nation nation, CompassDirection direction, WorkerAction action, List<PlayerBitmap> images) {
        nationSpecificActionsWithDirectionByPlayer
                .computeIfAbsent(nation, k -> new EnumMap<>(WorkerAction.class))
                .computeIfAbsent(action, k -> new EnumMap<>(CompassDirection.class))
                .computeIfAbsent(direction, k -> new EnumMap<>(PlayerColor.class));

        Arrays.stream(PlayerColor.values()).forEach(
                playerColor -> nationSpecificActionsWithDirectionByPlayer.get(nation).get(action).get(direction).put(
                        playerColor,
                        ImageTransformer.drawForPlayer(playerColor, images))
        );
    }

    /**
     * Adds an image for a specific direction and player color.
     *
     * @param direction the compass direction
     * @param image     the bitmap image
     */
    public void addImage(CompassDirection direction, Bitmap image) {
        imagesPerPlayer
                .computeIfAbsent(direction, k -> new EnumMap<>(PlayerColor.class));

        for (var playerColor : PlayerColor.values()) {
            if (!imagesPerPlayer.get(direction).containsKey(playerColor)) {
                imagesPerPlayer.get(direction).put(playerColor, new ArrayList<>());
            }

            imagesPerPlayer.get(direction).get(playerColor).add(((PlayerBitmap)image).getBitmapForPlayer(playerColor));
        }
    }

    /**
     * Adds a nation-specific image with player color for a specific direction.
     *
     * @param nation       the nation
     * @param playerColor  the player color
     * @param direction    the compass direction
     * @param image        the bitmap image
     */
    public void addNationSpecificImageWithPlayerColor(Nation nation, PlayerColor playerColor, CompassDirection direction, Bitmap image) {
        nationSpecificImagesWithPlayerColor
                .computeIfAbsent(nation, k -> new EnumMap<>(CompassDirection.class))
                .computeIfAbsent(direction, k -> new EnumMap<>(PlayerColor.class))
                .computeIfAbsent(playerColor, k -> new ArrayList<>())
                .add(image);
    }

    /**
     * Adds an image with player color for a specific direction.
     *
     * @param playerColor  the player color
     * @param direction    the compass direction
     * @param image        the bitmap image
     */
    public void addImageWithPlayerColor(PlayerColor playerColor, CompassDirection direction, Bitmap image) {
        imagesWithPlayerColor
                .computeIfAbsent(direction, k -> new EnumMap<>(PlayerColor.class))
                .computeIfAbsent(playerColor, k -> new ArrayList<>())
                .add(image);
    }
}
