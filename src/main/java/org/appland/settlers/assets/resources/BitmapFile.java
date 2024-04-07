package org.appland.settlers.assets.resources;

import org.appland.settlers.assets.TextureFormat;

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
    private int colorUsed;
    private int clrimp;
    private short sourceBitsPerPixel;

    public BitmapFile(int width, int height, Palette palette, TextureFormat format) {
        super(width, height, palette, format);
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setReserved(long reserved) {
        this.reserved = reserved;
    }

    public void setPixelOffset(long pixelOffset) {
        this.pixelOffset = pixelOffset;
    }

    public void setHeaderSize(long headerSize) {
        this.headerSize = headerSize;
    }

    public void setPlanes(short planes) {
        this.planes = planes;
    }

    public void setCompression(long compression) {
        this.compression = compression;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setXPixelsPerM(int xPixelsPerM) {
        this.xPixelsPerM = xPixelsPerM;
    }

    public void setYPixelsPerM(int yPixelsPerM) {
        this.yPixelsPerM = yPixelsPerM;
    }

    public void setColorUsed(int colorUsed) {
        this.colorUsed = colorUsed;
    }

    public void setColorImp(int clrimp) {
        this.clrimp = clrimp;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getReserved() {
        return reserved;
    }

    public long getPixelOffset() {
        return pixelOffset;
    }

    public long getHeaderSize() {
        return headerSize;
    }

    public int getPlanes() {
        return planes;
    }

    public long getCompression() {
        return compression;
    }

    public long getSize() {
        return size;
    }

    public int getXPixelsPerM() {
        return xPixelsPerM;
    }

    public int getYPixelsPerM() {
        return yPixelsPerM;
    }

    public int getColorUsed() {
        return colorUsed;
    }

    public int getColorImp() {
        return clrimp;
    }

    @Override
    public String toString() {
        return "BitmapFile{" +
                "fileSize=" + fileSize +
                ", reserved=" + reserved +
                ", pixelOffset=" + pixelOffset +
                ", headerSize=" + headerSize +
                ", planes=" + planes +
                ", bytesPerPixel=" + getBytesPerPixel() +
                ", sourceBitsPerPixel=" + sourceBitsPerPixel +
                ", compression=" + compression +
                ", size=" + size +
                ", xPixelsPerM=" + xPixelsPerM +
                ", yPixelsPerM=" + yPixelsPerM +
                ", colorUsed=" + colorUsed +
                ", clrimp=" + clrimp +
                '}';
    }

    public void setSourceBitsPerPixel(short bitsPerPixel) {
        sourceBitsPerPixel = bitsPerPixel;
    }

    public short getSourceBitsPerPixel() {
        return sourceBitsPerPixel;
    }
}
