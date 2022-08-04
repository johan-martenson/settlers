package org.appland.settlers.assets;

import org.appland.settlers.maps.StreamReader;

import java.io.IOException;
import java.nio.ByteOrder;

public class PaletteAnim {
    private final int padding;
    private final int rate;
    private final short firstColor;
    private final short lastColor;
    private final boolean isActive;
    private final boolean moveUp;
    private final int flags;

    public PaletteAnim(int padding, int rate, int flags, short firstColor, short lastColor, boolean isActive, boolean moveUp) {
        this.padding = padding;
        this.rate = rate;
        this.flags = flags;
        this.firstColor = firstColor;
        this.lastColor = lastColor;
        this.isActive = isActive;
        this.moveUp = moveUp;
    }

    public static PaletteAnim load(StreamReader streamReader) throws IOException {
        streamReader.pushByteOrder(ByteOrder.BIG_ENDIAN);

        int padding = streamReader.getUint16();
        int rate = streamReader.getUint16();
        int flags = streamReader.getUint16();
        short firstColor = streamReader.getUint8();
        short lastColor = streamReader.getUint8();

        boolean isActive = (flags & 1) != 0;
        boolean moveUp = (flags & 2) != 0;

        if (rate != 0) {
            isActive = true;
            moveUp = true;
        }

        return new PaletteAnim(padding, rate, flags, firstColor, lastColor, isActive, moveUp);
    }
}
