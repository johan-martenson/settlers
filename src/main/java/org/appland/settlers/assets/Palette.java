package org.appland.settlers.assets;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;

public class Palette {
    public static final int DEFAULT_TRANSPARENT_INDEX = 0;
    public static final RGBColor TRANSPARENT_COLOR = new RGBColor((byte)0xff, (byte)0, (byte)0x8f);

    private final byte[] colors;

    private int transparentIndex;
    private String name;

    public Palette(byte[] colors) { // uint 8 x 3
        this.colors = colors;
        this.transparentIndex = 0;
    }

    public static Palette load(StreamReader streamReader, boolean skip) throws IOException, InvalidFormatException {
        streamReader.pushByteOrder(ByteOrder.LITTLE_ENDIAN);

        if (skip) {
            int numberColors = streamReader.getUint16();

            if (numberColors != 256) {
                throw new InvalidFormatException("Must have 256 colors. Not " + numberColors);
            }
        }

        byte[] colors = streamReader.getUint8ArrayAsBytes(256 * 3);

        streamReader.popByteOrder();

        Palette palette = new Palette(colors);

        palette.setDefaultTransparentIdx();

        return palette;
    }

    public RGBColor getColorForIndex(int colorIndex) {
        int colorOffset = colorIndex * 3;

        return new RGBColor(
                colors[colorOffset],
                colors[colorOffset + 1],
                colors[colorOffset + 2]
        );
    }

    public boolean containsTransparentColor(int colorIndex) {
        return transparentIndex < colors.length;
    }

    @Override
    public String toString() {
        return "Palette{" +
                "colors=" + Arrays.toString(colors) +
                ", transparentIndex=" + transparentIndex +
                '}';
    }

    public int getTransparentIndex() {
        return transparentIndex;
    }

    public short getIndexForColor(int red, int green, int blue) {
        byte redByte = (byte)(red & 0XFF);
        byte greenByte = (byte)(green & 0xFF);
        byte blueByte = (byte)(blue & 0xFF);

        for (short i = 0; i < 256; i++) {
            int offset = i * 3;

            if (colors[offset] == redByte && colors[offset + 1] == greenByte && colors[offset + 2] == blueByte) {
                return i;
            }
        }

        return 0;
    }

    public boolean isTransparentColor(byte red, byte blue, byte green) {
        byte transparentRed = colors[transparentIndex];
        byte transparentGreen = colors[transparentIndex + 1];
        byte transparentBlue = colors[transparentIndex + 2];

        return transparentRed == red && transparentGreen == green && transparentBlue == blue;
    }

    public boolean isColorIndexTransparent(short colorIndex) {
        return colorIndex == transparentIndex;
    }

    public void setDefaultTransparentIdx() {
        transparentIndex = DEFAULT_TRANSPARENT_INDEX;

        for (int i = 0; i < colors.length; i++) {
            byte red = colors[transparentIndex];
            byte green = colors[transparentIndex + 1];
            byte blue = colors[transparentIndex + 2];

            if (TRANSPARENT_COLOR.matches(red, green, blue)) {
                transparentIndex = i;

                break;
            }
        }
    }

    public int getNumberColors() {
        return colors.length / 3;
    }

    public void setTransparentIndex(int transClr) {
        transparentIndex = transClr;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public byte getBlueAsByte(short imageDatum) {
        return colors[imageDatum * 3 + 2];
    }

    public byte getGreenAsByte(byte imageDatum) {
        return  colors[imageDatum * 3 + 1];
    }

    public byte getRedAsByte(byte imageDatum) {
        return  colors[imageDatum * 3];
    }
}
