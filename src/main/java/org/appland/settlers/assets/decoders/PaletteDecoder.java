package org.appland.settlers.assets.decoders;

import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.utils.ByteReader;
import org.appland.settlers.utils.StreamReader;

import java.io.FileInputStream;
import java.io.IOException;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

public class PaletteDecoder {

    public static Palette loadPaletteFromStream(ByteReader streamReader, boolean skip) throws IOException, InvalidFormatException {

        if (skip) {
            int numberColors = streamReader.getUint16();

            if (numberColors != 256) {
                throw new InvalidFormatException("Invalid number of colors (" + numberColors + "). Must be 256.");
            }
        }

        byte[] colors = streamReader.getUint8ArrayAsBytes(256 * 3); // uint 8 x 3 - rgb

        int transparentIndex = 270; // no transparency for now

        return new Palette(colors);
    }

    public static Palette loadPaletteFromFile(String filename) throws IOException {
        StreamReader streamReader = new StreamReader(new FileInputStream(filename), LITTLE_ENDIAN);

        byte[] colors = streamReader.getUint8ArrayAsBytes(256 * 3); // uint 8 x 3, rgb

        Palette palette =  new Palette(colors);

        palette.setDefaultTransparentIdx();

        int lastSeparator = filename.lastIndexOf("/");

        palette.setName(filename.substring(lastSeparator + 1) + "(" + 0 + ")");

        return palette;
    }

}
