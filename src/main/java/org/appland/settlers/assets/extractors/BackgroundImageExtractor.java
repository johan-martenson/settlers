package org.appland.settlers.assets.extractors;

import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.Utils;
import org.appland.settlers.assets.decoders.LbmDecoder;
import org.appland.settlers.assets.gamefiles.Setup010Lbm;
import org.appland.settlers.assets.resources.Palette;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BackgroundImageExtractor {

    /**
     * Extracts background images from the specified directory and writes them to the target directory.
     *
     * @param fromDir The directory containing the source LBM file
     * @param toDir   The directory where the extracted backgrounds will be saved
     * @param palette The color palette to apply to the background images
     * @throws IOException            If an I/O error occurs during file operations
     * @throws InvalidFormatException If the format of the LBM file is invalid
     */
    public static void extractBackgroundImages(String fromDir, String toDir, Palette palette) throws IOException, InvalidFormatException {
        var backgroundsDir = String.format("%s/backgrounds", toDir);

        // Create the backgrounds directory if it doesn't exist
        Files.createDirectory(Paths.get(backgroundsDir));

        // Load and save the ship background image
        Utils.getBitmapFromGameResource(LbmDecoder.loadLBMFile(
                        String.format("%s/%s", fromDir, Setup010Lbm.FILENAME), palette))
                .writeToFile(String.format("%s/ship.png", backgroundsDir));
    }
}
