package org.appland.settlers.assets.decoders;

import org.appland.settlers.assets.ColorBlock;
import org.appland.settlers.assets.CompassDirection;
import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.JobType;
import org.appland.settlers.assets.Nation;
import org.appland.settlers.assets.RenderedWorker;
import org.appland.settlers.assets.StackedBitmaps;
import org.appland.settlers.assets.TextureFormat;
import org.appland.settlers.assets.WorkerDetails;
import org.appland.settlers.assets.resources.Bob;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.resources.PlayerBitmap;
import org.appland.settlers.utils.ByteReader;
import org.appland.settlers.utils.StreamReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.Map;

import static java.nio.ByteOrder.LITTLE_ENDIAN;
import static org.appland.settlers.model.actors.Courier.BodyType.FAT;

public class BobDecoder {
    private static final long NUM_BODY_IMAGES = 2 * 6 * 8;
    private static final int BOB_IMAGE_DATA_HEADER = 0x01F4;
    private static final int BOB_X_OFFSET = 16;
    private static final int BOB_SPRITE_WIDTH = 32;

    /**
     * Load Bob from stream and return a Bob object.
     *
     * @param streamReader Stream reader for reading the file.
     * @param palette      Palette for rendering.
     * @return Bob object containing the images and overlay.
     * @throws IOException             In case of file access issues.
     * @throws InvalidFormatException  In case the format is invalid.
     */
    public static Bob loadBobFromStream(ByteReader streamReader, Palette palette) throws IOException, InvalidFormatException {
        PlayerBitmap[] playerBitmaps = new PlayerBitmap[(int)NUM_BODY_IMAGES];

        // Read the color block for the body
        ColorBlock colorBlock = ColorBlock.readColorBlockFromStream(streamReader);

        // Read body images
        for (long i = 0; i < NUM_BODY_IMAGES; i++) {
            int bodyImageId = streamReader.getUint16();
            short bodyImageHeight = streamReader.getUint8();

            if (bodyImageId != BOB_IMAGE_DATA_HEADER) {
                throw new InvalidFormatException(String.format("Body image id must match '0x01F4'. Not: %s", Integer.toHexString(bodyImageId)));
            }

            int[] starts = streamReader.getUint16ArrayAsInts(bodyImageHeight);
            short ny = streamReader.getUint8();

            playerBitmaps[(int)i] = PlayerBitmap.loadFrom(BOB_X_OFFSET, ny, BOB_SPRITE_WIDTH, colorBlock, starts, true, palette, TextureFormat.BGRA);
        }

        // Read color blocks for each direction
        ColorBlock[] colorBlocks = new ColorBlock[6];
        for (int i = 0; i < 6; i++) {
            colorBlocks[i] = ColorBlock.readColorBlockFromStream(streamReader);
        }

        // Read overlay images
        int numberOverlayImages = streamReader.getUint16();
        int[][] overlayImageStarts = new int[numberOverlayImages][];
        short[] overlayImageNy = new short[numberOverlayImages];

        PlayerBitmap[] allPlayerBitmaps = new PlayerBitmap[(int)(NUM_BODY_IMAGES + numberOverlayImages)];
        System.arraycopy(playerBitmaps, 0, allPlayerBitmaps, 0, playerBitmaps.length);

        for (int i = 0; i < numberOverlayImages; i++) {
            int overlayImageId = streamReader.getUint16();
            short overlayImageHeight = streamReader.getUint8();

            if (overlayImageId != BOB_IMAGE_DATA_HEADER) {
                throw new InvalidFormatException(String.format("Must match '0x01F4'. Not: %s ", Integer.toHexString(overlayImageId)));
            }

            overlayImageStarts[i] = streamReader.getUint16ArrayAsInts(overlayImageHeight);
            overlayImageNy[i] = streamReader.getUint8();
        }

        // Follow links to create complete pictures
        int numberLinks = streamReader.getUint16();
        int[] links = new int[numberLinks];
        boolean[] loaded = new boolean[numberOverlayImages];

        for (long i = 0; i < numberLinks; i++) {
            links[(int)i] = streamReader.getUint16();
            int unknown = streamReader.getUint16();

            if (links[(int)i] >= numberOverlayImages) {
                throw new InvalidFormatException(String.format("Number of overlay images is: %d. Cannot have more than: %d", numberOverlayImages, links[(int)i]));
            }

            // Skip the image if it's already loaded
            if (loaded[links[(int)i]]) {
                continue;
            }

            PlayerBitmap playerBitmap = PlayerBitmap.loadFrom(
                    BOB_X_OFFSET,
                    overlayImageNy[links[(int)i]],
                    BOB_SPRITE_WIDTH,
                    colorBlocks[(int)i % 6], // raw
                    overlayImageStarts[links[(int)i]],
                    true,
                    palette,
                    TextureFormat.BGRA);

            allPlayerBitmaps[(int)NUM_BODY_IMAGES + links[(int)i]] = playerBitmap;

            loaded[links[(int)i]] = true;
        }

        for (int i = 0; i < links.length; i++) {
            links[i] = links[i] + (int)NUM_BODY_IMAGES;
        }

        return new Bob(numberOverlayImages, links, allPlayerBitmaps);
    }

    /**
     * Load a Bob file from disk and return the Bob object.
     *
     * @param filename        Name of the file to load.
     * @param defaultPalette  Default palette to use.
     * @return Bob object or null if not loaded correctly.
     * @throws IOException             In case of file access issues.
     * @throws InvalidFormatException  In case the format is invalid.
     */
    public static Bob loadBobFile(String filename, Palette defaultPalette) throws IOException, InvalidFormatException {
        try (InputStream fileInputStream = Files.newInputStream(Paths.get(filename));
             StreamReader streamReader = new StreamReader(fileInputStream, LITTLE_ENDIAN)) {

            short header = streamReader.getInt16();

            if (header == 0x01F6) {
                return BobDecoder.loadBobFromStream(streamReader, defaultPalette);
            }
        }

        return null;
    }

    /**
     * Render worker images based on jobs and worker details.
     *
     * @param jobsBob            Bob containing jobs.
     * @param workerDetailsMap   Map of job type to worker details.
     * @return Map of job type to rendered worker images.
     */
    public static Map<JobType, RenderedWorker> renderWorkerImages(Bob jobsBob, Map<JobType, WorkerDetails> workerDetailsMap) {
        Map<JobType, RenderedWorker> workerImages = new EnumMap<>(JobType.class);

        // Go through each job type
        for (JobType job : JobType.values()) {
            RenderedWorker worker = new RenderedWorker(job);

            for (Nation nation : Nation.values()) {
                for (CompassDirection compassDirection : CompassDirection.values()) {
                    for (int animationStep = 0; animationStep < 8; animationStep++) {
                        WorkerDetails workerDetails = workerDetailsMap.get(job);
                        int id = workerDetails.getBobId(nation);
                        boolean fat = workerDetails.getBodyType() == FAT;

                        StackedBitmaps bitmaps = new StackedBitmaps();
                        bitmaps.add(jobsBob.getBody(fat, compassDirection.ordinal(), animationStep));
                        bitmaps.add(jobsBob.getOverlay(id, fat, compassDirection.ordinal(), animationStep));

                        worker.addAnimationStep(nation, compassDirection, bitmaps, animationStep);
                        workerImages.put(job, worker);
                    }
                }
            }
        }

        return workerImages;
    }
}
