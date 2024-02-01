package org.appland.settlers.assets.decoders;

import org.appland.settlers.assets.GameResource;
import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.PaletteResource;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.utils.StreamReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class BbmDecoder {
    private static boolean debug = false;

    private static void debugPrint(String debugString) {
        if (debug) {
            System.out.println(debugString);
        }
    }

    public static List<GameResource> loadBbmFile(String filename) throws IOException, InvalidFormatException {
        StreamReader streamReader = new StreamReader(new FileInputStream(filename), ByteOrder.BIG_ENDIAN);

        String header = streamReader.getUint8ArrayAsString(4);

        if (!header.equals("FORM")) {
            throw new InvalidFormatException("Must match 'FORM'. Not " + header);
        }

        long length = streamReader.getUint32();

        String pbm = streamReader.getUint8ArrayAsString(4);

        if (!pbm.equals("PBM ")) {
            throw new InvalidFormatException("Must match 'PBM '. Not " + pbm);
        }

        List<GameResource> palettes = new ArrayList<>();
        long i = 0;

        while (!streamReader.isEof()) {
            String chunkId = streamReader.getUint8ArrayAsString(4);

            if (chunkId.equals("CMAP")) {
                length = streamReader.getUint32();

                if ((length & 1)  == 1) {
                    length = length + 1;
                }

                if (length != 256 * 3) {
                    throw new InvalidFormatException("Length must match 256 x 3. Not " + length);
                }

                Palette palette = Palette.load(streamReader, false);

                int lastSeparator = filename.lastIndexOf("/");

                palette.setName(filename.substring(lastSeparator + 1) + "(" + i + ")");

                palettes.add(new PaletteResource(palette));

                i = i + 1;
            }
        }

        return palettes;
    }
}
