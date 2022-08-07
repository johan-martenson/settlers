package org.appland.settlers.assets;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PlayerBitmap extends Bitmap {

    private final PalettedPixelBuffer texturePixelData;

    private boolean debug = false;
    private long length;

    public PlayerBitmap(int width, int height, Palette palette, TextureFormat format) {
        super(width, height, palette, format);

        texturePixelData = new PalettedPixelBuffer(width, height, Palette.DEFAULT_TRANSPARENT_INDEX);
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
            System.out.println("       - New height from startlist.size: " + height);
        }

        ByteBuffer sourceByteBuffer = ByteBuffer.wrap(sourceData).order(ByteOrder.LITTLE_ENDIAN);

        // y: uint 16
        for (int y = 0; y < height; y++) {
            int x = 0;  // uint 16

            int position = starts[y]; // uint 32 - change to int 32 for now as input is uint 16

            if (!absoluteStarts) {
                position = position - height * 2; // height * sizeof(uint16)
            }

            if (debug) {
                System.out.println("       - Reading row of pixels");
                System.out.println("       - Updated: position before row: " + position);
                System.out.println("       - Position before update: " + position);
                System.out.println("       - X start at: " + x);
            }

            while (x < width) {
                short shift = Unsigned.getUnsignedByte(sourceByteBuffer, position);

                position = position + 1;

                // Handle transparent pixel
                if (shift < 0x40) {
                    x = x + shift;

                // Handle colored pixel
                } else if (shift < 0x80) {
                    shift = (short)(shift - 0x40); // uint 8 = uint 8 - 0x40

                    for (int i = 0; i < shift; i++, x++) {
                        setPixelByColorIndex(x, y, Unsigned.getUnsignedByte(sourceByteBuffer, position));

                        position = position + 1;
                    }

                // Handle pixel with player color
                } else if (shift < 0xC0) {
                    shift = (short)(shift - 0x80);

                    // Set pixel to player color
                    for (int i = 0; i < shift; i++, x++) {
                        texturePixelData.set(x, y, sourceData[position]); // TODO: verify that this is correct

                        setPixelByColorIndex(x, y, (short)(Unsigned.getUnsignedByte(sourceByteBuffer, position) + 128));
                    }

                    position = position + 1;

                // Set compressed pixel
                } else {
                    shift = (short)(shift - 0xC0);

                    for (int i = 0; i < shift; i++, x++) {
                        setPixelByColorIndex(x, y, Unsigned.getUnsignedByte(sourceByteBuffer, position));
                    }

                    position = position + 1;
                }
            }
        }
    }

    public PalettedPixelBuffer getTextureBitmap() {
        return this.texturePixelData;
    }

    public PlayerBitmap setLength(long length) {
        this.length = length;

        return this;
    }

    public long getLength() {
        return length;
    }
}