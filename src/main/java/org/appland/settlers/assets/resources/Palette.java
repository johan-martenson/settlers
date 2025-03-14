package org.appland.settlers.assets.resources;

import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.RGBColor;
import org.appland.settlers.utils.StreamReader;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.stream.IntStream;

import static java.lang.String.format;

/**
 * Represents a color palette with up to 256 colors, including a transparent color index.
 */
public class Palette {
    public static final int DEFAULT_TRANSPARENT_INDEX = 0;
    public static final RGBColor TRANSPARENT_COLOR = new RGBColor((byte)0xff, (byte)0, (byte)0x8f);

    private static final int PALETTE_MAX_SIZE = 256;
    private static final boolean debug = false;

    private final byte[] colors;

    private int transparentIndex;
    private String name;

    /**
     * Prints debug information if debugging is enabled.
     *
     * @param debugString the debug message to print
     */
    private static void debugPrint(String debugString) {
        if (debug) {
            System.out.println(debugString);
        }
    }

    /**
     * Constructs a Palette object with the provided color data.
     *
     * @param colors The byte array containing the color data (RGB).
     */
    public Palette(byte[] colors) {
        this.colors = colors;
        this.transparentIndex = DEFAULT_TRANSPARENT_INDEX;
    }

    /**
     * Loads a palette from the provided stream reader.
     *
     * @param streamReader The stream reader to read from.
     * @param skip         Whether to skip certain validations.
     * @return The loaded Palette object.
     * @throws IOException            If an I/O error occurs.
     * @throws InvalidFormatException If the palette format is invalid.
     */
    public static Palette loadPalette(StreamReader streamReader, boolean skip) throws IOException, InvalidFormatException {
        streamReader.pushByteOrder(ByteOrder.LITTLE_ENDIAN);

        if (skip) {
            int numberColors = streamReader.getUint16();

            if (numberColors != 256) {
                throw new InvalidFormatException(format("Must have 256 colors. Not %d", numberColors));
            }
        }

        byte[] colors = streamReader.getUint8ArrayAsBytes(256 * 3);
        streamReader.popByteOrder();

        Palette palette = new Palette(colors);
        palette.setDefaultTransparentIdx();

        return palette;
    }

    /**
     * Loads the palette from a stream encoded as BGRA, ignoring the alpha channel.
     *
     * @param streamReader      The stream reader to read from.
     * @param numberColorsUsed  The number of colors used.
     * @return The loaded Palette object.
     * @throws IOException If an I/O error occurs.
     */
    public static Palette loadFromBgra(StreamReader streamReader, long numberColorsUsed) throws IOException {
        debugPrint("    - Loading palette");

        long adjustedColors = Math.min(numberColorsUsed, PALETTE_MAX_SIZE);
        byte[] paletteColors = new byte[PALETTE_MAX_SIZE * 3];

        for (int i = 0; i < adjustedColors; i++) {
            paletteColors[i * 3] = streamReader.getInt8();     // red
            paletteColors[i * 3 + 1] = streamReader.getInt8(); // green
            paletteColors[i * 3 + 2] = streamReader.getInt8(); // blue
            streamReader.getInt8();                            // alpha, not used
        }

        Palette palette = new Palette(paletteColors);
        palette.setDefaultTransparentIdx();
        return palette;
    }

    /**
     * Retrieves the RGB color at a specified index.
     *
     * @param colorIndex The index of the color.
     * @return The RGBColor object at the given index.
     */
    public RGBColor getColorForIndex(int colorIndex) {
        int colorOffset = colorIndex * 3;

        return new RGBColor(
                colors[colorOffset],
                colors[colorOffset + 1],
                colors[colorOffset + 2]
        );
    }

    /**
     * Checks whether the palette contains a transparent color.
     *
     * @param colorIndex The index to check.
     * @return True if the palette contains a transparent color, otherwise false.
     */
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

    /**
     * Returns the transparent color index.
     *
     * @return The transparent color index.
     */
    public int getTransparentIndex() {
        return transparentIndex;
    }

    /**
     * Finds the index of a color matching the provided red, green, and blue values.
     *
     * @param red   The red component.
     * @param green The green component.
     * @param blue  The blue component.
     * @return The index of the matching color, or 0 if not found.
     */
    public int getIndexForColor(int red, int green, int blue) {
        byte redByte = (byte)(red & 0XFF);
        byte greenByte = (byte)(green & 0xFF);
        byte blueByte = (byte)(blue & 0xFF);

        return IntStream.range(0, 256)
                .filter(i -> {
                    int offset = i * 3;
                    return colors[offset] == redByte && colors[offset + 1] == greenByte && colors[offset + 2] == blueByte;
                })
                .findFirst()
                .orElse((short) 0);  // Return 0 if not found
    }

    /**
     * Checks if the specified color is transparent.
     *
     * @param red   The red component.
     * @param blue  The blue component.
     * @param green The green component.
     * @return True if the color is transparent, otherwise false.
     */
    public boolean isColorTransparent(byte red, byte blue, byte green) {
        return colors[transparentIndex] == red &&
                colors[transparentIndex + 1] == green &&
                colors[transparentIndex + 2] == blue;
    }

    /**
     * Checks if the color index is transparent.
     *
     * @param colorIndex The color index to check.
     * @return True if the index points to a transparent color, otherwise false.
     */
    public boolean isColorIndexTransparent(short colorIndex) {
        return colorIndex == transparentIndex;
    }

    /**
     * Sets the default transparent index based on the defined transparent color.
     */
    public void setDefaultTransparentIdx() {
        transparentIndex = DEFAULT_TRANSPARENT_INDEX;

        IntStream.range(0, colors.length / 3)
                .filter(i -> TRANSPARENT_COLOR.matches(colors[i * 3], colors[i * 3 + 1], colors[i * 3 + 2]))
                .findFirst()
                .ifPresent(i -> transparentIndex = i);
    }

    /**
     * Returns the number of colors in the palette.
     *
     * @return The number of colors.
     */
    public int getNumberColors() {
        return colors.length / 3;
    }

    /**
     * Sets the transparent color index.
     *
     * @param transparentColorIndex The new transparent color index.
     */
    public void setTransparentIndex(int transparentColorIndex) {
        this.transparentIndex = transparentColorIndex;
    }

    /**
     * Sets the name of the palette.
     *
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the palette.
     *
     * @return The palette's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the blue component of a color at a specific index.
     *
     * @param imageDatum The index of the color.
     * @return The blue component.
     */
    public byte getBlueAsByte(short imageDatum) {
        return colors[imageDatum * 3 + 2];
    }

    /**
     * Gets the green component of a color at a specific index.
     *
     * @param imageDatum The index of the color.
     * @return The green component.
     */
    public byte getGreenAsByte(byte imageDatum) {
        return  colors[imageDatum * 3 + 1];
    }

    /**
     * Gets the red component of a color at a specific index.
     *
     * @param imageDatum The index of the color.
     * @return The red component.
     */
    public byte getRedAsByte(byte imageDatum) {
        return  colors[imageDatum * 3];
    }

    /**
     * Gets the transparent index of the palette as a byte. I.e., with the upper bits truncated
     * @return The transparent index
     */
    public byte getTransparentIndexAsByte() {
        return (byte)(transparentIndex & 0xFF);
    }
}
