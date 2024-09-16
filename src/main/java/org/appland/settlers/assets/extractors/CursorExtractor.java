package org.appland.settlers.assets.extractors;

import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.UnknownResourceTypeException;
import org.appland.settlers.assets.Utils;
import org.appland.settlers.assets.decoders.DatDecoder;
import org.appland.settlers.assets.gamefiles.EditResDat;
import org.appland.settlers.assets.resources.Palette;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CursorExtractor {

    /**
     * Extracts cursor images from the specified game resource file and writes them to the output directory.
     *
     * @param fromDir The directory containing the source DAT file
     * @param toDir The directory where the extracted cursors will be saved
     * @param palette The color palette to apply to the cursors
     * @throws UnknownResourceTypeException If an unknown resource type is encountered during extraction
     * @throws IOException If an I/O error occurs during file reading or writing
     * @throws InvalidFormatException If the format of the DAT file is invalid
     */
    public static void extractCursors(String fromDir, String toDir, Palette palette) throws UnknownResourceTypeException, IOException, InvalidFormatException {
        var editresDat = DatDecoder.loadDatFile(String.format("%s/%s", fromDir, EditResDat.FILENAME), palette);

        var cursorDir = String.format("%s/cursors", toDir);

        Files.createDirectory(Paths.get(cursorDir));

        Utils.getBitmapFromGameResource(editresDat.get(EditResDat.MOVE))
                .writeToFile(String.format("%s/cursor-move.png", cursorDir));

        Utils.getBitmapFromGameResource(editresDat.get(EditResDat.BUILD_ROAD))
                .writeToFile(String.format("%s/cursor-build-road.png", cursorDir));

        Utils.getBitmapFromGameResource(editresDat.get(EditResDat.BUILD_ROAD_PRESSED))
                .writeToFile(String.format("%s/cursor-build-road-pressed.png", cursorDir));

        Utils.getBitmapFromGameResource(editresDat.get(EditResDat.PAUSE))
                .writeToFile(String.format("%s/cursor-pause.png", cursorDir));
    }
}
