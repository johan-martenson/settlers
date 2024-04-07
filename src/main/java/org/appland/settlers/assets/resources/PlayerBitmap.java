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

    public PlayerBitmap(int width, int height, Palette palette, TextureFormat format) {
        super(width, height, palette, format);

        playerTexture = new Bitmap(width, height, palette, TextureFormat.PALETTED);
    }

    public static PlayerBitmap loadFrom(int nx, short ny, int width, ColorBlock colorBlock, int[] starts, boolean absoluteStarts, Palette palette, TextureFormat format) {
        int height = starts.length;

        PlayerBitmap playerBitmap = new PlayerBitmap(width, height, palette, format);

        playerBitmap.nx = nx;
        playerBitmap.ny = ny;

        playerBitmap.loadImageFromData(colorBlock.pixels, starts, absoluteStarts);

        return playerBitmap;
    }

    public void loadImageFromData(byte[] sourceData, int[] starts, boolean absoluteStarts) {
        if (debug) {
            System.out.println("       - Height based on start list size: " + height);
        }

        ByteBuffer sourceByteBuffer = ByteBuffer.wrap(sourceData).order(ByteOrder.LITTLE_ENDIAN);

        // Build the image, line by line
        for (int y = 0; y < height; y++) {
            int x = 0;

            // Look up where the first non-transparent pixel is on the line
            int position = starts[y];

            if (!absoluteStarts) {
                position = position - height * 2;
            }

            if (debug) {
                System.out.println("       - Reading row of pixels");
                System.out.println("       - Updated: position before row: " + position);
                System.out.println("       - Position before update: " + position);
                System.out.println("       - X start at: " + x);
            }

            // Read the line
            while (x < width) {
                short typeAndLength = Unsigned.getUnsignedByte(sourceByteBuffer, position);
                var segment = Segment.uint16ToSegment(typeAndLength);

                position = position + 1;

                switch (segment.type) {
                    case TRANSPARENT -> {
                        x = x + segment.length;
                    }
                    case COLOR -> {
                        for (int i = 0; i < segment.length; i++, x++) {
                            setPixelByColorIndex(x, y, Unsigned.getUnsignedByte(sourceByteBuffer, position));

                            position = position + 1;
                        }
                    }
                    case PLAYER_COLOR -> {
                        for (int i = 0; i < segment.length; i++, x++) {
                            playerTexture.setPixelByColorIndex(x, y, (short)(Unsigned.getUnsignedByte(sourceByteBuffer, position)));

                            setPixelByColorIndex(x, y, (short)(Unsigned.getUnsignedByte(sourceByteBuffer, position) + 128));
                            //setPixelByColorIndex(x, y, (short)5);
                        }

                        position = position + 1;
                    }
                    case COMPRESSED -> {
                        for (int i = 0; i < segment.length; i++, x++) {
                            setPixelByColorIndex(x, y, Unsigned.getUnsignedByte(sourceByteBuffer, position));
                        }

                        position = position + 1;
                    }
                }
            }
        }
    }

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

    public Bitmap getTextureBitmap() {
        return this.playerTexture;
    }

    public PlayerBitmap setLength(long length) {
        this.length = length;

        return this;
    }

    public long getLength() {
        return length;
    }

    static class Segment {
        private final SegmentType type;
        private final int length;

        Segment(SegmentType type, int length) {
            this.type = type;
            this.length = length;
        }

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
        COMPRESSED;
    }
}