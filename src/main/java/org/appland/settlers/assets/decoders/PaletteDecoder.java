package org.appland.settlers.assets.decoders;

import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.utils.ByteReader;
import org.appland.settlers.utils.StreamReader;

import java.io.FileInputStream;
import java.io.IOException;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

public class PaletteDecoder {

    /**
     * Loads a palette from a stream and validates the number of colors.
     *
     * @param streamReader ByteReader to read the palette data
     * @param skip boolean indicating whether to skip the number of colors check
     * @return Palette object representing the loaded colors
     * @throws IOException when an I/O error occurs
     * @throws InvalidFormatException when the format is invalid
     */
    public static Palette loadPaletteFromStream(ByteReader streamReader, boolean skip) throws IOException, InvalidFormatException {
        if (skip) {
            int numberColors = streamReader.getUint16();

            if (numberColors != 256) {
                throw new InvalidFormatException(String.format("Invalid number of colors (%d). Must be 256.", numberColors));
            }
        }

        byte[] colors = streamReader.getUint8ArrayAsBytes(256 * 3);
        int transparentIndex = 270; // no transparency for now

        return new Palette(colors);
    }

    /**
     * Loads a palette from a file by reading its color information.
     *
     * @param filename the file path for the palette
     * @return Palette object with the loaded colors
     * @throws IOException when an I/O error occurs during reading
     */
    public static Palette loadPaletteFromFile(String filename) throws IOException {
        StreamReader streamReader = new StreamReader(new FileInputStream(filename), LITTLE_ENDIAN);

        byte[] colors = streamReader.getUint8ArrayAsBytes(256 * 3); // uint 8 x 3, rgb

        Palette palette =  new Palette(colors);
        palette.setDefaultTransparentIdx();

        int lastSeparator = filename.lastIndexOf("/");
        palette.setName(String.format("%s(%d)", filename.substring(lastSeparator + 1), 0));

        return palette;
    }

}
