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
    public static void extractCursors(String fromDir, String toDir, Palette palette) throws UnknownResourceTypeException, IOException, InvalidFormatException {
        var editresDat = DatDecoder.loadDatFile(fromDir + "/" + EditResDat.FILENAME, palette);

        var cursorDir = toDir + "/cursors";

        Files.createDirectory(Paths.get(cursorDir));

        Utils.getBitmapFromGameResource(editresDat.get(EditResDat.MOVE)).writeToFile(cursorDir + "/cursor-move.png");
        Utils.getBitmapFromGameResource(editresDat.get(EditResDat.BUILD_ROAD)).writeToFile(cursorDir + "/cursor-build-road.png");
        Utils.getBitmapFromGameResource(editresDat.get(EditResDat.BUILD_ROAD_PRESSED)).writeToFile(cursorDir + "/cursor-build-road-pressed.png");
        Utils.getBitmapFromGameResource(editresDat.get(EditResDat.PAUSE)).writeToFile(cursorDir + "/cursor-pause.png");
    }
}
