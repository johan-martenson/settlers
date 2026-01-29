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
    private static final int PALETTE_LENGTH = 256 * 3;
    private static boolean debug = false;

    /**
     * Prints debug information if debugging is enabled.
     *
     * @param debugString the string to print if debug is enabled
     */
    private static void debugPrint(String debugString) {
        if (debug) {
            System.out.println(debugString);
        }
    }

    /**
     * Loads a BBM file and returns its content as a list of game resources.
     *
     * @param filename the name of the file to load
     * @return a list of game resources
     * @throws IOException            if an I/O error occurs
     * @throws InvalidFormatException if the file format is invalid
     */
    public static List<GameResource> loadBbmFile(String filename) throws IOException, InvalidFormatException {
        try (var streamReader = new StreamReader(new FileInputStream(filename), ByteOrder.BIG_ENDIAN)) {

            // Validate the header
            var header = streamReader.getUint8ArrayAsString(4);
            if (!header.equals("FORM")) {
                throw new InvalidFormatException(String.format("Must match 'FORM'. Not %s", header));
            }

            long length = streamReader.getUint32();

            var pbm = streamReader.getUint8ArrayAsString(4);
            if (!pbm.equals("PBM ")) {
                throw new InvalidFormatException(String.format("Must match 'PBM '. Not %s", pbm));
            }

            // Read the palettes
            var palettes = new ArrayList<GameResource>();
            long i = 0;

            // Read chunks until EOF
            while (!streamReader.isEof()) {
                var chunkId = streamReader.getUint8ArrayAsString(4);

                if (chunkId.equals("CMAP")) {
                    length = streamReader.getUint32();

                    // Adjust for odd length
                    if ((length & 1) == 1) {
                        length = length + 1;
                    }

                    if (length != PALETTE_LENGTH) {
                        throw new InvalidFormatException(String.format("Length must match %d. Not %d", PALETTE_LENGTH, length));
                    }

                    var palette = Palette.loadPalette(streamReader, false);
                    int lastSeparator = filename.lastIndexOf("/");

                    palette.setName(String.format("%s(%d)", filename.substring(lastSeparator + 1), i));
                    palettes.add(new PaletteResource(palette));

                    i++;
                }
            }

            return palettes;
        }
    }
}
