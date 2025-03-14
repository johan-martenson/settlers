package org.appland.settlers.assets.resources;

import org.appland.settlers.assets.Area;
import org.appland.settlers.assets.RGBColor;
import org.appland.settlers.assets.TextureFormat;

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static java.lang.String.format;
import static org.appland.settlers.assets.TextureFormat.BGRA;
import static org.appland.settlers.assets.TextureFormat.PALETTED;

/**
 * Represents a bitmap image that can be manipulated and saved.
 */
public class Bitmap {
    protected final int height;
    protected final int width;
    protected final TextureFormat format;

    protected byte[] imageData; // uint 8

    private Palette palette;
    private boolean debug = false;

    int nx;
    int ny;

    /**
     * Creates a Bitmap with the specified width, height, palette, and format.
     *
     * @param width The width of the bitmap.
     * @param height The height of the bitmap.
     * @param palette The color palette to be used.
     * @param format The texture format.
     */
    public Bitmap(int width, int height, Palette palette, TextureFormat format) {
        this(width, height, 0, 0, palette, format);
    }

    /**
     * Creates a Bitmap with the specified dimensions, origin, palette, and format.
     *
     * @param width The width of the bitmap.
     * @param height The height of the bitmap.
     * @param nx The x-coordinate of the origin.
     * @param ny The y-coordinate of the origin.
     * @param palette The color palette to be used.
     * @param format The texture format.
     */
    public Bitmap(int width, int height, int nx, int ny, Palette palette, TextureFormat format) {
        this.width = width;
        this.height = height;
        this.palette = palette;
        this.format = format;
        this.nx = nx;
        this.ny = ny;

        imageData = new byte[width * height * getBytesPerPixel()];

        if (debug) {
            System.out.println("    ++++ Image size is: " + imageData.length);
        }
    }

    public void setPixelByColorIndex(int x, int y, short colorIndex) {
        switch (format) {
            case PALETTED -> imageData[(y * width + x)] = (byte)(colorIndex & 0xFF);
            case BGRA -> {
                if (palette.isColorIndexTransparent(colorIndex)) {
                    imageData[(y * width + x) * 4 + 3] = 0;
                } else {
                    RGBColor colorRGB = palette.getColorForIndex(colorIndex);

                    imageData[(y * width + x) * 4] = colorRGB.blue();
                    imageData[(y * width + x) * 4 + 1] = colorRGB.green();
                    imageData[(y * width + x) * 4 + 2] = colorRGB.red();
                    imageData[(y * width + x) * 4 + 3] = (byte) 0xFF;
                }
            }
            case BGR -> {
                RGBColor colorRGB = palette.getColorForIndex(colorIndex);

                imageData[(y * width + x) * 3] = colorRGB.blue();
                imageData[(y * width + x) * 3 + 1] = colorRGB.green();
                imageData[(y * width + x) * 3 + 2] = colorRGB.red();
            }
            case ORIGINAL -> throw new RuntimeException(format("Cannot set pixel in format %s", format));
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void writeToFile(Path path) throws IOException {
        writeToFile(path.toString());
    }

    public void writeToFile(String filename) throws IOException {
        if (debug) {
            System.out.printf("""
 - Dimensions: %d x %d
 - Data size (*4): %d
 - Data length: %d
""", width, height, width * height * 4, imageData.length);
        }

        WritableRaster raster;
        ColorModel colorModel;

        if (format == PALETTED) {
            var rgbArray = new byte[width * height * 4];

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    var colorIndex = imageData[y * width + x] & 0xFF;
                    var color = palette.getColorForIndex(colorIndex);

                    rgbArray[(y * width + x) * 4] = color.red();
                    rgbArray[(y * width + x) * 4 + 1] = color.green();
                    rgbArray[(y * width + x) * 4 + 2] = color.blue();
                    rgbArray[(y * width + x) * 4 + 3] = palette.getTransparentIndex() == colorIndex ? 0 : Byte.MAX_VALUE;
                }
            }

            int samplesPerPixel = 4;
            int[] bandOffsets = {0, 1, 2, 3};

            DataBuffer buffer = new DataBufferByte(rgbArray, rgbArray.length);
            raster = Raster.createInterleavedRaster(buffer, width, height, samplesPerPixel * width, samplesPerPixel, bandOffsets, null);
            colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
        } else if (format == BGRA){
            int samplesPerPixel = 4;
            int[] bandOffsets = {2, 1, 0, 3};

            DataBuffer buffer = new DataBufferByte(imageData, imageData.length);
            raster = Raster.createInterleavedRaster(buffer, width, height, samplesPerPixel * width, samplesPerPixel, bandOffsets, null);
            colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
        } else {
            throw new RuntimeException(format("Can't write file with format %s", format.name()));
        }

        BufferedImage image = new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);

        if (debug) {
            System.out.println("image: " + image);
        }

        ImageIO.write(image, "PNG", new File(filename));
    }

    public void setImageDataFromBuffer(byte[] data) {
        this.imageData = data;
    }

    public void setPalette(Palette palette) {
        this.palette = palette;
    }

    public void setPixelValue(int x, int y, byte red, byte green, byte blue, byte transparency) {
        switch (format) {
            case BGRA -> {
                this.imageData[(y * width + x) * 4] = blue;
                this.imageData[(y * width + x) * 4 + 1] = green;
                this.imageData[(y * width + x) * 4 + 2] = red;
                this.imageData[(y * width + x) * 4 + 3] = transparency;
            }
            case BGR -> {
                this.imageData[(y * width + x) * 3] = blue;
                this.imageData[(y * width + x) * 3 + 1] = green;
                this.imageData[(y * width + x) * 3 + 2] = red;
            }
            default -> throw new RuntimeException("Can't set pixel value for format: " + format);
        }
    }

    /**
     * Makes this bitmap transparent
     */
    public void makeTransparent() {
        switch (format) {
            case BGRA -> {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        setPixelValue(x, y, (byte) 0, (byte)0, (byte)0, (byte)0);
                    }
                }
            }
            case PALETTED -> {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        setPixelByColorIndex(x, y, (short)palette.getTransparentIndex());
                    }
                }
            }
            default -> throw new RuntimeException("Can't make transparent for format: " + format);
        }
    }

    public byte[] getImageData() {
        return this.imageData;
    }

    public int getBytesPerPixel() {
        return switch (format) {
            case BGRA -> 4;
            case BGR -> 3;
            case PALETTED, ORIGINAL -> 1;
        };
    }

    public TextureFormat getFormat() {
        return format;
    }

    public Palette getPalette() {
        return palette;
    }

    public byte getBlueAsByte(int x, int y) {
        return switch (format) {
            case BGRA -> imageData[(y * width + x) * 4];
            case BGR -> imageData[(y * width + x) * 3];
            case PALETTED -> palette.getBlueAsByte(imageData[y * width + x]);
            case ORIGINAL -> throw new RuntimeException(format("Can't manage format %s", format));
        };
    }

    public byte getGreenAsByte(int x, int y) {
        return switch (format) {
            case BGRA -> imageData[(y * width + x) * 4 + 1];
            case BGR -> imageData[(y * width + x) * 3 + 1];
            case PALETTED -> palette.getGreenAsByte(imageData[y * width + x]);
            case ORIGINAL -> throw new RuntimeException(format("Can't manage format %s", format));
        };
    }

    public byte getRedAsByte(int x, int y) {
        return switch (format) {
            case BGRA -> imageData[(y * width + x) * 4 + 2];
            case BGR -> imageData[(y * width + x) * 3 + 2];
            case PALETTED -> palette.getRedAsByte(imageData[y * width + x]);
            case ORIGINAL -> throw new RuntimeException(format("Can't manage format %s", format));
        };
    }

    public byte getAlphaAsByte(int x, int y) {
        return switch (format) {
            case BGRA -> imageData[(y * width + x) * 4 + 3];
            case PALETTED, BGR -> (byte)0xFF;
            case ORIGINAL -> throw new RuntimeException(format("Can't manage format %s", format));
        };
    }

    public Bitmap getSubBitmap(int x0, int y0, int x1, int y1) {
        int subImageWidth = x1 - x0;
        int subImageHeight = y1 - y0;
        byte[] subImage = new byte[subImageWidth * subImageHeight * 4];

        for (int y = 0; y < subImageHeight; y++) {
            for (int x = 0; x < subImageWidth; x++) {
                subImage[(y * subImageWidth + x) * 4] = getBlueAsByte(x0 + x, y0 + y);
                subImage[(y * subImageWidth + x) * 4 + 1] = getGreenAsByte(x0 + x, y0 + y);
                subImage[(y * subImageWidth + x) * 4 + 2] = getRedAsByte(x0 + x, y0 + y);
                subImage[(y * subImageWidth + x) * 4 + 3] = getAlphaAsByte(x0 + x, y0 + y);
            }
        }

        Bitmap subBitmap = new Bitmap(subImageWidth, subImageHeight, palette, TextureFormat.BGRA);
        subBitmap.setImageDataFromBuffer(subImage);

        return subBitmap;
    }

    public Bitmap getDiagonalSubBitmap(int x0, int y0, int x1, int y1) {
        int destinationWidth = (x1 - x0) / 2;
        int destinationHeight = (y1 - y0) / 2;
        int sourceMiddleX = x0 + destinationWidth;
        int sourceMiddleY = y0 + destinationHeight;

        byte[] subImage = new byte[destinationWidth * destinationHeight * 4];

        int dy = 0;

        int sourceStartY = sourceMiddleY;

        /* Walk down the diagonal line left-middle, to bottom-middle */
        for (int sourceStartX = x0; sourceStartX < sourceMiddleX; sourceStartX++) {
            int sx = sourceStartX;
            int sy = sourceStartY;

            int dx = 0;

            /* Walk diagonally up-right */
            for (int i = 0; i < destinationWidth; i++) {
                byte blue = getBlueAsByte(sx, sy);
                byte green = getGreenAsByte(sx, sy);
                byte red = getRedAsByte(sx, sy);
                byte alpha = getAlphaAsByte(sx, sy);

                subImage[(dy * destinationWidth + dx) * 4] = blue;
                subImage[(dy * destinationWidth + dx) * 4 + 1] = green;
                subImage[(dy * destinationWidth + dx) * 4 + 2] = red;
                subImage[(dy * destinationWidth + dx) * 4 + 3] = alpha;

                dx = dx + 1;
                sx = sx + 1;
                sy = sy + 1;
            }

            sourceStartY = sourceStartY - 1;
            dy = dy + 1;
        }

        Bitmap subBitmap = new Bitmap(destinationWidth, destinationHeight, palette, TextureFormat.BGRA);

        subBitmap.setImageDataFromBuffer(subImage);

        return subBitmap;
    }

    public void copyNonTransparentPixels(Bitmap bitmap, Point toUpperLeft, Point fromUpperLeft, Dimension fromSize) {
        Point toIterator = new Point(toUpperLeft.x, toUpperLeft.y);

        for (int fromY = fromUpperLeft.y; fromY < fromSize.height + fromUpperLeft.y; fromY++) {
            for (int fromX = fromUpperLeft.x; fromX < fromSize.width + fromUpperLeft.x; fromX++) {

                if (format == PALETTED) {
                    setPixelByColorIndex(toIterator.x, toIterator.y, bitmap.getColorIndex(fromX, fromY));
                } else {
                    byte blue = bitmap.getBlueAsByte(fromX, fromY);
                    byte green = bitmap.getGreenAsByte(fromX, fromY);
                    byte red = bitmap.getRedAsByte(fromX, fromY);
                    byte alpha = bitmap.getAlphaAsByte(fromX, fromY);

                    if (alpha != 0) {
                        setPixelValue(toIterator.x, toIterator.y, red, green, blue, alpha);
                    }
                }

                toIterator.x = toIterator.x + 1;
            }

            toIterator.y = toIterator.y + 1;
            toIterator.x = toUpperLeft.x;
        }
    }

    public Area getVisibleArea() {

        // Find first non-transparent pixel from top
        int firstVisibleY = -1;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (!isTransparent(x, y)) {
                    firstVisibleY = y;
                    break;
                }
            }

            if (firstVisibleY != -1) {
                break;
            }
        }

        // No non-transparent pixels in whole image
        if (firstVisibleY == -1) {
            return new Area(0, 0, 0, 0);
        }

        // Find first non-transparent pixel from bottom
        int lastVisibleY = firstVisibleY;
        for (int y = height - 1; y > firstVisibleY; y--) {
            for (int x = 0; x < width; x++) {
                if (!isTransparent(x, y)) {
                    lastVisibleY = y;
                    break;
                }
            }

            if (lastVisibleY != firstVisibleY) {
                break;
            }
        }

        // Find first non-transparent pixel from left
        int firstVisibleX = 0;
        for (int x = 0; x < width; x++) {
            for (int y = firstVisibleY; y <= lastVisibleY; y++) {
                if (!isTransparent(x, y)) {
                    firstVisibleX = x;
                    break;
                }
            }

            if (firstVisibleX != 0) {
                break;
            }
        }

        // Find first non-transparent pixel from right
        int lastVisibleX = firstVisibleX;
        for (int x = width - 1; x > firstVisibleX; x--) {
            for (int y = firstVisibleY; y <= lastVisibleY; y++) {
                if (!isTransparent(x, y)) {
                    lastVisibleX = x;
                    break;
                }
            }

            if (lastVisibleX != firstVisibleX) {
                break;
            }
        }

        return new Area(
                firstVisibleX,
                firstVisibleY,
                lastVisibleX + 1 - firstVisibleX,
                lastVisibleY + 1 - firstVisibleY
        );
    }

    public Point getOrigin() {
        Area visibleArea = getVisibleArea();
        int originX = this.nx - visibleArea.x();
        int originY = this.ny - visibleArea.y();

        return new Point(originX, originY);
    }

    public boolean isTransparent(int x, int y) {
        return switch (format) {
            case PALETTED -> imageData[y * width + x] == palette.getTransparentIndexAsByte();
            case BGRA -> getAlphaAsByte(x, y) == 0;
            default -> false;
        };
    }

    public Dimension getDimension() {
        return new Dimension(width, height);
    }

    public void setNx(int nx) {
        this.nx = nx;
    }

    public void setNy(int ny) {
        this.ny = ny;
    }

    public int getNx() {
        return nx;
    }

    public int getNy() {
        return ny;
    }

    public Bitmap getMirror() {
        Bitmap mirror = new Bitmap(width, height, width - nx, ny, palette, format);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                byte red = getRedAsByte(x, y);
                byte green = getGreenAsByte(x, y);
                byte blue = getBlueAsByte(x, y);
                byte alpha = getAlphaAsByte(x, y);

                mirror.setPixelValue(width - x - 1, y, red, green, blue, alpha);
            }
        }

        return mirror;
    }

    protected void copyPixelFrom(int x, int y, Bitmap bitmap) {
        setPixelValue(
                x,
                y,
                bitmap.getRedAsByte(x, y),
                bitmap.getGreenAsByte(x, y),
                bitmap.getBlueAsByte(x, y),
                bitmap.getAlphaAsByte(x, y)
        );
    }

    protected short getColorIndex(int x, int y) {
        if (format == PALETTED) {
            return imageData[y * width + x];
        } else {
            throw new RuntimeException("Can't get color for index in non-paletted bitmap");
        }
    }

    public interface PixelAction {
        void apply(int x, int y, byte red, byte green, byte blue, byte alpha);
    }

    public void forEachPixel(PixelAction fun) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                fun.apply(
                        x,
                        y,
                        getRedAsByte(x, y),
                        getGreenAsByte(x, y),
                        getBlueAsByte(x, y),
                        getAlphaAsByte(x, y)
                );
            }
        }
    }
}
