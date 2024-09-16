package org.appland.settlers.assets.resources;

import org.appland.settlers.utils.StreamReader;

import java.io.IOException;
import java.nio.ByteOrder;

/**
 * Represents a palette animation with parameters for color ranges and animation behavior.
 */
public record AnimatedPalette(int padding, int rate, int flags, short firstColor, short lastColor, boolean isActive, boolean moveUp) {

    /**
     * Loads a PaletteAnim from the provided stream reader.
     *
     * @param streamReader The stream reader to read from.
     * @return The loaded PaletteAnim object.
     * @throws IOException If an I/O error occurs while reading.
     */
    public static AnimatedPalette load(StreamReader streamReader) throws IOException {
        streamReader.pushByteOrder(ByteOrder.BIG_ENDIAN);

        int padding = streamReader.getUint16();
        int rate = streamReader.getUint16();
        int flags = streamReader.getUint16();
        short firstColor = streamReader.getUint8();
        short lastColor = streamReader.getUint8();

        boolean isActive = (flags & 1) != 0 || rate != 0;
        boolean moveUp = (flags & 2) != 0 || rate != 0;

        return new AnimatedPalette(padding, rate, flags, firstColor, lastColor, isActive, moveUp);
    }
}
