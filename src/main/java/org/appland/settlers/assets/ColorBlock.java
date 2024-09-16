package org.appland.settlers.assets;

import org.appland.settlers.utils.ByteReader;

import java.io.IOException;

import static java.lang.String.format;

/**
 * Represents a color block that contains an ID, size, and pixel data.
 */
public record ColorBlock(int id, int size, byte[] pixels) {
    private static final int COLOR_BLOCK_HEADER = 0x01F5;

    /**
     * Reads a ColorBlock from a ByteReader stream.
     *
     * @param streamReader the stream reader to read from
     * @return the ColorBlock read from the stream
     * @throws IOException             if an IO error occurs
     * @throws InvalidFormatException  if the color block header is invalid
     */
    public static ColorBlock readColorBlockFromStream(ByteReader streamReader) throws IOException, InvalidFormatException {
        var id = streamReader.getUint16();
        var size = streamReader.getUint16();

        if (id != COLOR_BLOCK_HEADER) {
            throw new InvalidFormatException(format("Header must match 0x01F5. Not %d", id));
        }

        var pixels = streamReader.getUint8ArrayAsBytes(size);

        return new ColorBlock(id, size, pixels);
    }
}
