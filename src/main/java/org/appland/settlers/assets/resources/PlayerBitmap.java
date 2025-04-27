package org.appland.settlers.assets.resources;

import org.appland.settlers.assets.ColorBlock;
import org.appland.settlers.assets.TextureFormat;
import org.appland.settlers.assets.Unsigned;
import org.appland.settlers.model.PlayerColor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.appland.settlers.assets.resources.PlayerBitmap.SegmentType.*;

public class PlayerBitmap extends Bitmap {
    private final Bitmap playerTexture;

    private boolean debug = false;
    private long length;

    /**
     * Creates a PlayerBitmap with the specified dimensions, origin, palette, and format.
     *
     * @param width  The width of the bitmap.
     * @param height The height of the bitmap.
     * @param nx     The x-coordinate of the origin.
     * @param ny     The y-coordinate of the origin.
     * @param palette The palette to use for colors.
     * @param format The texture format of the bitmap.
     */
    public PlayerBitmap(int width, int height, int nx, int ny, Palette palette, TextureFormat format) {
        super(width, height, nx, ny, palette, format);

        playerTexture = new Bitmap(width, height, palette, TextureFormat.PALETTED);

        // Ensure the overlay is transparent by default
        playerTexture.makeTransparent();
    }

    /**
     * Creates a PlayerBitmap with the specified dimensions, palette, and format.
     *
     * @param width  The width of the bitmap.
     * @param height The height of the bitmap.
     * @param palette The palette to use for colors.
     * @param format The texture format of the bitmap.
     */
    public PlayerBitmap(int width, int height, Palette palette, TextureFormat format) {
        this(width, height, 0, 0, palette, format);
    }

    /**
     * Loads a PlayerBitmap from the provided data.
     *
     * @param nx            The x-coordinate of the origin.
     * @param ny            The y-coordinate of the origin.
     * @param width         The width of the bitmap.
     * @param colorBlock    The color block data.
     * @param starts        The starting positions of each row.
     * @param absoluteStarts Whether the start positions are absolute or relative.
     * @param palette       The palette to use for colors.
     * @param format        The texture format of the bitmap.
     * @return A loaded PlayerBitmap.
     */
    public static PlayerBitmap loadFrom(int nx, short ny, int width, ColorBlock colorBlock, int[] starts, boolean absoluteStarts, Palette palette, TextureFormat format) {
        int height = starts.length;

        PlayerBitmap playerBitmap = new PlayerBitmap(width, height, nx, ny, palette, format);

        playerBitmap.loadImageFromData(colorBlock.pixels(), starts, absoluteStarts);

        return playerBitmap;
    }

    /**
     * Loads image data into the PlayerBitmap.
     *
     * @param sourceData     The source pixel data.
     * @param starts         The start positions for each row.
     * @param absoluteStarts Whether the start positions are absolute or relative.
     */
    public void loadImageFromData(byte[] sourceData, int[] starts, boolean absoluteStarts) {
        if (debug) {
            System.out.println("       - Height based on start list size: " + height);
        }

        ByteBuffer sourceByteBuffer = ByteBuffer.wrap(sourceData).order(ByteOrder.LITTLE_ENDIAN);

        // Build the image, line by line
        for (int y = 0; y < height; y++) {
            int x = 0;

            // Look up where the first non-transparent pixel is on the line
            int position = absoluteStarts ? starts[y] : starts[y] - height * 2;

            if (debug) {
                System.out.printf("""
        - Reading row of pixels
        - Updated: position before row: %d
        - Position before update: %d
        - X start at: %d
""",
                        position, position, x);
            }

            // Read the line
            while (x < width) {
                short typeAndLength = Unsigned.getUnsignedByte(sourceByteBuffer, position);
                var segment = Segment.uint16ToSegment(typeAndLength);
                position++;

                switch (segment.type) {
                    case TRANSPARENT -> x += segment.length;
                    case COLOR -> {
                        for (int i = 0; i < segment.length; i++, x++) {
                            setPixelByColorIndex(x, y, Unsigned.getUnsignedByte(sourceByteBuffer, position));
                            position++;
                        }
                    }
                    case PLAYER_COLOR -> {
                        for (int i = 0; i < segment.length; i++, x++) {
                            playerTexture.setPixelByColorIndex(x, y, Unsigned.getUnsignedByte(sourceByteBuffer, position));
                            setPixelByColorIndex(x, y, (short)(Unsigned.getUnsignedByte(sourceByteBuffer, position) + 128));
                        }

                        position++;
                    }
                    case COMPRESSED -> {
                        for (int i = 0; i < segment.length; i++, x++) {
                            setPixelByColorIndex(x, y, Unsigned.getUnsignedByte(sourceByteBuffer, position));
                        }

                        position++;
                    }
                }
            }
        }
    }

    /**
     * Generates a bitmap customized for the specified player color.
     *
     * @param playerColor The player color to apply.
     * @return A new Bitmap customized for the player.
     */
    public Bitmap getBitmapForPlayer(PlayerColor playerColor) {
        Bitmap bitmap = new Bitmap(width, height, nx, ny, getPalette(), TextureFormat.BGRA);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (playerTexture.isTransparent(x, y)) {
                    bitmap.copyPixelFrom(x, y, this);
                } else {
                    bitmap.setPixelByColorIndex(x, y, (short)(playerTexture.getColorIndex(x, y) + 128 + playerColor.index * 4));
                }
            }
        }

        return bitmap;
    }

    /**
     * Returns the player-specific texture bitmap.
     *
     * @return The player texture bitmap.
     */
    public Bitmap getTextureBitmap() {
        return this.playerTexture;
    }

    /**
     * Sets the length of the bitmap.
     *
     * @param length The length to set.
     * @return The current PlayerBitmap instance.
     */
    public PlayerBitmap setLength(long length) {
        this.length = length;

        return this;
    }

    /**
     * Returns the length of the bitmap.
     *
     * @return The length of the bitmap.
     */
    public long getLength() {
        return length;
    }

    /**
     * Represents a segment in the bitmap, consisting of a type and length.
     */
    record Segment (SegmentType type, int length) {

        /**
         * Converts a 16-bit integer into a Segment based on its value.
         *
         * @param input The 16-bit integer to convert.
         * @return The corresponding Segment.
         */
        static Segment uint16ToSegment(int input) {
            if (input < 0x40) {
                return new Segment(TRANSPARENT, input);
            } else if (input < 0x80) {
                return new Segment(COLOR, input - 0x40);
            } else if (input < 0xC0) {
                return new Segment(PLAYER_COLOR, input - 0x80);
            } else {
                return new Segment(COMPRESSED, input - 0xC0);
            }
        }
    }

    enum SegmentType {
        TRANSPARENT,
        COLOR,
        PLAYER_COLOR,
        COMPRESSED
    }
}