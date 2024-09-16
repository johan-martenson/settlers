package org.appland.settlers.assets.resources;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a Bob object containing player bitmaps and associated links.
 */
public class Bob {
    private static final int NUM_BODY_IMAGES = 2 * 6 * 8;

    private final int numberBodyImages;
    private final int numberOverlayImages;
    private final int[] links;
    private final PlayerBitmap[] playerBitmaps;

    /**
     * Constructs a Bob object with the specified overlay images, links, and player bitmaps.
     *
     * @param numberOverlayImages The number of overlay images.
     * @param links               The array of links.
     * @param playerBitmaps       The array of player bitmaps.
     */
    public Bob(int numberOverlayImages, int[] links, PlayerBitmap[] playerBitmaps) {
        this.numberOverlayImages = numberOverlayImages;
        this.links = links;
        this.playerBitmaps = playerBitmaps;
        this.numberBodyImages = playerBitmaps.length - numberOverlayImages;
    }

    /**
     * Returns an array of body bitmaps.
     *
     * @return An array of body bitmaps.
     */
    public PlayerBitmap[] getBodyBitmaps() {
        return Arrays.copyOfRange(playerBitmaps, 0, NUM_BODY_IMAGES); // Use copyOfRange for conciseness
    }

    /**
     * Returns the number of body images.
     *
     * @return The number of body images.
     */
    public int getNumberBodyImages() {
        return numberBodyImages;
    }

    /**
     * Returns the number of overlay images.
     *
     * @return The number of overlay images.
     */
    public int getNumberOverlayImages() {
        return numberOverlayImages;
    }

    /**
     * Returns the number of links.
     *
     * @return The number of links.
     */
    public int getNumberLinks() {
        return links.length;
    }

    /**
     * Returns a list of all player bitmaps.
     *
     * @return A list of all player bitmaps.
     */
    public List<PlayerBitmap> getAllBitmaps() {
        return Arrays.asList(this.playerBitmaps);
    }

    /**
     * Returns the body bitmap for the specified parameters.
     *
     * @param fat             Indicates if the body is fat.
     * @param direction       The direction index.
     * @param animationStep   The animation index.
     * @return The corresponding body bitmap.
     */
    // TODO: change from directionIndex to just direction
    public PlayerBitmap getBody(boolean fat, int direction, int animationStep) {
        int bodyIndex = fat
                ? (6 + direction) * 8 + animationStep
                : direction * 8 + animationStep;

        return playerBitmaps[bodyIndex];
    }

    /**
     * Returns the overlay bitmap for the specified parameters.
     *
     * @param overlayId       The ID of the overlay.
     * @param fat             Indicates if the body is fat.
     * @param direction       The direction index.
     * @param animationStep   The animation step.
     * @return The corresponding overlay bitmap.
     */
    public PlayerBitmap getOverlay(int overlayId, boolean fat, int direction, int animationStep) {
        int overlayIndex = getOverlayIndex(overlayId, fat, direction, animationStep);

        return playerBitmaps[overlayIndex];
    }

    /**
     * Calculates the index for an overlay based on the provided parameters.
     *
     * @param overlayIndex    The index of the overlay.
     * @param fat             Indicates if the body is fat.
     * @param direction       The direction index.
     * @param animationStep   The animation step.
     * @return The overlay index.
     */
    private int getOverlayIndex(int overlayIndex, boolean fat, int direction, int animationStep) {
        return fat
                ? links[((overlayIndex * 8 + animationStep) * 2 + 1) * 6 + direction]
                : links[(overlayIndex * 8 + animationStep) * 2 * 6 + direction];
    }

    /**
     * Returns the array of links.
     *
     * @return The array of links.
     */
    public int[] getLinks() {
        return links;
    }

    /**
     * Returns the bitmap at the specified index.
     *
     * @param index The index of the bitmap.
     * @return The corresponding player bitmap.
     */
    public PlayerBitmap getBitmapAtIndex(int index) {
        return playerBitmaps[index];
    }

    /**
     * Returns the link at the specified index.
     *
     * @param link The index of the link.
     * @return The link value at the specified index.
     */
    public int getLinkForIndex(int link) {
        return links[link];
    }
}
