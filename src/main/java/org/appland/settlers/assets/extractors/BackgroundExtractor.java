package org.appland.settlers.assets.extractors;

import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.Utils;
import org.appland.settlers.assets.decoders.LbmDecoder;
import org.appland.settlers.assets.gamefiles.Setup010Lbm;
import org.appland.settlers.assets.resources.Palette;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BackgroundExtractor {
    public static void extractBackgrounds(String fromDir, String toDir, Palette palette) throws IOException, InvalidFormatException {
        var backgroundsDir = toDir + "/backgrounds";

        Files.createDirectory(Paths.get(backgroundsDir));

        Utils.getBitmapFromGameResource(LbmDecoder.loadLBMFile(fromDir + "/" + Setup010Lbm.FILENAME, palette))
                .writeToFile(backgroundsDir + "/ship.png");
    }
}
