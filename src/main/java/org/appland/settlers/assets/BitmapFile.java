package org.appland.settlers.assets;

public class BitmapFile extends Bitmap {

    private long fileSize;
    private long reserved;
    private long pixelOffset;
    private long headerSize;
    private short planes;
    private short bitsPerPixel;
    private long compression;
    private long size;
    private int xPixelsPerM;
    private int yPixelsPerM;
    private int colorUsed;
    private int clrimp;

    public BitmapFile(int width, int height, Palette palette, TextureFormat format) {
        super(width, height, palette, format);
    }

    public BitmapFile setFileSize(long fileSize) {
        this.fileSize = fileSize;

        return this;
    }

    public BitmapFile setReserved(long reserved) {
        this.reserved = reserved;

        return this;
    }

    public BitmapFile setPixelOffset(long pixelOffset) {
        this.pixelOffset = pixelOffset;

        return this;
    }

    public BitmapFile setHeaderSize(long headerSize) {
        this.headerSize = headerSize;

        return this;
    }

    public BitmapFile setPlanes(short planes) {
        this.planes = planes;

        return this;
    }

    public BitmapFile setBitsPerPixel(short bitsPerPixel) {
        this.bitsPerPixel = bitsPerPixel;

        return this;
    }

    public BitmapFile setCompression(long compression) {
        this.compression = compression;

        return this;
    }

    public BitmapFile setSize(long size) {
        this.size = size;

        return this;
    }

    public BitmapFile setXPixelsPerM(int xPixelsPerM) {
        this.xPixelsPerM = xPixelsPerM;

        return this;
    }

    public BitmapFile setYPixelsPerM(int yPixelsPerM) {
        this.yPixelsPerM = yPixelsPerM;

        return this;
    }

    public BitmapFile setColorUsed(int colorUsed) {
        this.colorUsed = colorUsed;

        return this;
    }

    public BitmapFile setColorImp(int clrimp) {
        this.clrimp = clrimp;

        return this;
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

    public int getBitsPerPixel() {
        return bitsPerPixel;
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
                ", bitsPerPixel=" + bitsPerPixel +
                ", compression=" + compression +
                ", size=" + size +
                ", xPixelsPerM=" + xPixelsPerM +
                ", yPixelsPerM=" + yPixelsPerM +
                ", colorUsed=" + colorUsed +
                ", clrimp=" + clrimp +
                '}';
    }
}
