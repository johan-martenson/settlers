package org.appland.settlers.assets.resources;

import org.appland.settlers.assets.TextureFormat;

/**
 * Represents a bitmap file with additional file-related metadata.
 */
public class BitmapFile extends Bitmap {
    private long fileSize;
    private long reserved;
    private long pixelOffset;
    private long headerSize;
    private short planes;
    private long compression;
    private long size;
    private int xPixelsPerM;
    private int yPixelsPerM;
    private int colorsUsed;
    private int importantColors;
    private short sourceBitsPerPixel;

    /**
     * Constructs a BitmapFile with the specified dimensions, palette, and texture format.
     *
     * @param width   The width of the bitmap.
     * @param height  The height of the bitmap.
     * @param palette The color palette for the bitmap.
     * @param format  The texture format of the bitmap.
     */
    public BitmapFile(int width, int height, Palette palette, TextureFormat format) {
        super(width, height, palette, format);
    }

    /**
     * Sets the file size for the bitmap file.
     *
     * @param fileSize The file size to set.
     */
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * Sets the reserved field for the bitmap file.
     *
     * @param reserved The reserved field to set.
     */
    public void setReserved(long reserved) {
        this.reserved = reserved;
    }

    /**
     * Sets the pixel offset for the bitmap file.
     *
     * @param pixelOffset The pixel offset to set.
     */
    public void setPixelOffset(long pixelOffset) {
        this.pixelOffset = pixelOffset;
    }

    /**
     * Sets the header size for the bitmap file.
     *
     * @param headerSize The header size to set.
     */
    public void setHeaderSize(long headerSize) {
        this.headerSize = headerSize;
    }

    /**
     * Sets the number of planes for the bitmap file.
     *
     * @param planes The number of planes to set.
     */
    public void setPlanes(short planes) {
        this.planes = planes;
    }

    /**
     * Sets the compression type for the bitmap file.
     *
     * @param compression The compression type to set.
     */
    public void setCompression(long compression) {
        this.compression = compression;
    }

    /**
     * Sets the image size in the bitmap file.
     *
     * @param size The image size to set.
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * Sets the horizontal pixels per meter value for the bitmap file.
     *
     * @param xPixelsPerM The horizontal pixels per meter to set.
     */
    public void setXPixelsPerM(int xPixelsPerM) {
        this.xPixelsPerM = xPixelsPerM;
    }

    /**
     * Sets the vertical pixels per meter value for the bitmap file.
     *
     * @param yPixelsPerM The vertical pixels per meter to set.
     */
    public void setYPixelsPerM(int yPixelsPerM) {
        this.yPixelsPerM = yPixelsPerM;
    }

    /**
     * Sets the number of colors used in the bitmap file.
     *
     * @param colorUsed The number of colors used to set.
     */
    public void setColorsUsed(int colorUsed) {
        this.colorsUsed = colorUsed;
    }

    /**
     * Sets the color importance field for the bitmap file.
     *
     * @param importantColors The color importance value to set.
     */
    public void setImportantColors(int importantColors) {
        this.importantColors = importantColors;
    }

    /**
     * Gets the file size of the bitmap file.
     *
     * @return The file size.
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * Gets the reserved field of the bitmap file.
     *
     * @return The reserved field.
     */
    public long getReserved() {
        return reserved;
    }

    /**
     * Gets the pixel offset of the bitmap file.
     *
     * @return The pixel offset.
     */
    public long getPixelOffset() {
        return pixelOffset;
    }

    /**
     * Gets the header size of the bitmap file.
     *
     * @return The header size.
     */
    public long getHeaderSize() {
        return headerSize;
    }

    /**
     * Gets the number of planes in the bitmap file.
     *
     * @return The number of planes.
     */
    public int getPlanes() {
        return planes;
    }

    /**
     * Gets the compression type of the bitmap file.
     *
     * @return The compression type.
     */
    public long getCompression() {
        return compression;
    }

    /**
     * Gets the image size of the bitmap file.
     *
     * @return The image size.
     */
    public long getSize() {
        return size;
    }

    /**
     * Gets the horizontal pixels per meter value in the bitmap file.
     *
     * @return The horizontal pixels per meter.
     */
    public int getXPixelsPerM() {
        return xPixelsPerM;
    }

    /**
     * Gets the vertical pixels per meter value in the bitmap file.
     *
     * @return The vertical pixels per meter.
     */
    public int getYPixelsPerM() {
        return yPixelsPerM;
    }

    /**
     * Gets the number of colors used in the bitmap file.
     *
     * @return The number of colors used.
     */
    public int getColorsUsed() {
        return colorsUsed;
    }

    /**
     * Gets the color importance value of the bitmap file.
     *
     * @return The color importance value.
     */
    public int getImportantColors() {
        return importantColors;
    }

    @Override
    public String toString() {
        return String.format("""
            BitmapFile {
                fileSize=%d,
                reserved=%d,
                pixelOffset=%d,
                headerSize=%d,
                planes=%d,
                bytesPerPixel=%d,
                sourceBitsPerPixel=%d,
                compression=%d,
                size=%d,
                xPixelsPerM=%d,
                yPixelsPerM=%d,
                colorsUsed=%d,
                importantColors=%d
            }""",
                fileSize, reserved, pixelOffset, headerSize, planes, getBytesPerPixel(),
                sourceBitsPerPixel, compression, size, xPixelsPerM, yPixelsPerM,
                colorsUsed, importantColors
        );
    }

    /**
     * Sets the source bits per pixel for the bitmap file.
     *
     * @param bitsPerPixel The bits per pixel to set.
     */
    public void setSourceBitsPerPixel(short bitsPerPixel) {
        sourceBitsPerPixel = bitsPerPixel;
    }

    /**
     * Gets the source bits per pixel value of the bitmap file.
     *
     * @return The source bits per pixel value.
     */
    public short getSourceBitsPerPixel() {
        return sourceBitsPerPixel;
    }
}
