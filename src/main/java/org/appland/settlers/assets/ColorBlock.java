package org.appland.settlers.assets;

import org.appland.settlers.maps.StreamReader;

import java.io.IOException;

public class ColorBlock {
    private static final int COLOR_BLOCK_HEADER = 0x01F5;

    public int id;
    public int size;
    public byte[] pixels;

    static ColorBlock readColorBlockFromStream(StreamReader streamReader) throws IOException, InvalidFormatException {
        ColorBlock colorBlock = new ColorBlock();

        colorBlock.id = streamReader.getUint16();
        colorBlock.size = streamReader.getUint16();

        if (colorBlock.id != COLOR_BLOCK_HEADER) {
            throw new InvalidFormatException("Header must match 0x01F5. Not " + colorBlock.id);
        }

        colorBlock.pixels = streamReader.getUint8ArrayAsBytes(colorBlock.size);

        return colorBlock;
    }
}
