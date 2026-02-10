package org.appland.settlers.utils;

import org.appland.settlers.maps.ByteArray;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;

import static java.lang.String.format;

public class StreamReader implements ByteReader, AutoCloseable {
    public static final int SIZE_OF_UINT32 = 4;
    public static final int SIZE_OF_UINT16 = 2;
    public static final int SIZE_OF_UINT8 = 1;
    public static final int EOF = -1;

    private final InputStream inputStream;
    private final Deque<ByteOrder> byteOrderStack = new ArrayDeque<>();

    private ByteOrder order;
    private long offset = 0;
    private boolean isEof = false;

    public StreamReader(InputStream inputStream, ByteOrder byteOrder) {
        this.inputStream = inputStream;
        this.order = byteOrder;
    }

    // ------------------------------------------------------------
    // Core safe read helper
    // ------------------------------------------------------------

    private byte[] readFully(int length) throws IOException {
        byte[] bytes = inputStream.readNBytes(length);
        if (bytes.length != length) {
            isEof = true;
            throw new EOFException(format("Expected %d bytes, got %d", length, bytes.length));
        }
        offset += length;
        return bytes;
    }

    // ------------------------------------------------------------
    // Unsigned reads
    // ------------------------------------------------------------

    public short getUint8() throws IOException {
        return (short) (readFully(1)[0] & 0xff);
    }

    public int getUint16() throws IOException {
        return getUint16(this.order);
    }

    @Override
    public int getUint16(ByteOrder endian) throws IOException {
        byte[] b = readFully(2);
        return endian == ByteOrder.LITTLE_ENDIAN
                ? (b[0] & 0xff) | ((b[1] & 0xff) << 8)
                : ((b[0] & 0xff) << 8) | (b[1] & 0xff);
    }

    public long getUint32() throws IOException {
        return getUint32(this.order);
    }

    @Override
    public long getUint32(ByteOrder endian) throws IOException {
        byte[] b = readFully(4);
        int value = endian == ByteOrder.LITTLE_ENDIAN
                ? (b[0] & 0xff)
                | ((b[1] & 0xff) << 8)
                | ((b[2] & 0xff) << 16)
                | ((b[3] & 0xff) << 24)
                : ((b[0] & 0xff) << 24)
                | ((b[1] & 0xff) << 16)
                | ((b[2] & 0xff) << 8)
                | (b[3] & 0xff);

        return value & 0xffffffffL;
    }

    // ------------------------------------------------------------
    // Signed reads
    // ------------------------------------------------------------

    public byte getInt8() throws IOException {
        return readFully(1)[0];
    }

    public short getInt16() throws IOException {
        byte[] b = readFully(2);
        return (short) (order == ByteOrder.LITTLE_ENDIAN
                ? (b[0] & 0xff) | ((b[1] & 0xff) << 8)
                : ((b[0] & 0xff) << 8) | (b[1] & 0xff));
    }

    public int getInt32() throws IOException {
        byte[] b = readFully(4);
        return order == ByteOrder.LITTLE_ENDIAN
                ? (b[0] & 0xff)
                | ((b[1] & 0xff) << 8)
                | ((b[2] & 0xff) << 16)
                | ((b[3] & 0xff) << 24)
                : ((b[0] & 0xff) << 24)
                | ((b[1] & 0xff) << 16)
                | ((b[2] & 0xff) << 8)
                | (b[3] & 0xff);
    }

    // ------------------------------------------------------------
    // Arrays
    // ------------------------------------------------------------

    public short[] getUint8Array(int length) throws IOException {
        short[] arr = new short[length];
        for (int i = 0; i < length; i++) arr[i] = getUint8();
        return arr;
    }

    public int[] getUint16ArrayAsInts(int length) throws IOException {
        int[] arr = new int[length];
        for (int i = 0; i < length; i++) arr[i] = getUint16();
        return arr;
    }

    public long[] getUint32Array(int length) throws IOException {
        long[] arr = new long[length];
        for (int i = 0; i < length; i++) arr[i] = getUint32();
        return arr;
    }

    public byte[] getUint8ArrayAsBytes(int length) throws IOException {
        return readFully(length);
    }

    public ByteArray getUint8ArrayAsByteArray(int length) throws IOException {
        return new ByteArray(readFully(length), order);
    }

    // ------------------------------------------------------------
    // Strings
    // ------------------------------------------------------------

    public String getUint8ArrayAsString(int length) throws IOException {
        byte[] bytes = readFully(length);
        int nullPos = -1;

        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == 0) {
                nullPos = i;
                break;
            }
        }

        return new String(bytes, 0, nullPos == -1 ? bytes.length : nullPos, StandardCharsets.US_ASCII);
    }

    public String getUint8ArrayAsNullTerminatedString(int maxLength) throws IOException {
            byte[] bytes = getUint8ArrayAsBytes(maxLength);

            int end = 0;
            while (end < bytes.length && bytes[end] != 0) {
                end++;
            }

            return new String(bytes, 0, end, StandardCharsets.US_ASCII);
    }

    public String getRemainingBytesAsString() throws IOException {
        byte[] bytes = inputStream.readAllBytes();
        offset += bytes.length;
        isEof = true;
        return new String(bytes, StandardCharsets.US_ASCII);
    }

    public byte[] getRemainingBytes() throws IOException {
        byte[] bytes = inputStream.readAllBytes();
        offset += bytes.length;
        isEof = true;
        return bytes;
    }

    // ------------------------------------------------------------
    // Positioning
    // ------------------------------------------------------------

    public void skip(int bytesToSkip) throws IOException {
        setPosition(offset + bytesToSkip);
    }

    @Override
    public void setPosition(int position) throws IOException {
        setPosition((long) position);
    }

    public void setPosition(long target) throws IOException {
        if (target < offset) {
            throw new IOException("Cannot seek backwards in forward-only StreamReader");
        }

        while (offset < target) {
            long skipped = inputStream.skip(target - offset);
            if (skipped <= 0) {
                isEof = true;
                throw new EOFException("Unable to skip further, reached EOF");
            }
            offset += skipped;
        }
    }

    // ------------------------------------------------------------
    // Byte order stack
    // ------------------------------------------------------------

    public void pushByteOrder(ByteOrder order) {
        byteOrderStack.push(this.order);
        this.order = order;
    }

    public void popByteOrder() {
        this.order = byteOrderStack.pop();
    }

    // ------------------------------------------------------------
    // Misc
    // ------------------------------------------------------------

    public InputStream getInputStream() {
        return inputStream;
    }

    public boolean isEof() {
        if (isEof) {
            return true;
        }

        if (!inputStream.markSupported()) {
            System.out.println("Mark not supported");

            return false;
        }

        try {
            inputStream.mark(1);
            int b = inputStream.read();
            if (b == -1) {
                isEof = true;
            }
            inputStream.reset();
        } catch (IOException e) {
            isEof = true;
        }

        return isEof;
    }

    @Override
    public int getPosition() {
        return (int) Math.min(offset, Integer.MAX_VALUE);
    }

    @Override
    public int length() {
        throw new UnsupportedOperationException("StreamReader is forward-only");
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    public char[] getUint8ArrayAsChar(int lengthInBytes) throws IOException {
        byte[] bytes = readFully(lengthInBytes);
        char[] chars = new char[lengthInBytes];

        for (int i = 0; i < lengthInBytes; i++) {
            chars[i] = (char) (bytes[i] & 0xff);
        }

        return chars;
    }

    public void read(byte[] buffer, int offset, int length) throws IOException {
        int totalRead = 0;

        while (totalRead < length) {
            int read = inputStream.read(buffer, offset + totalRead, length - totalRead);

            if (read == -1) {
                isEof = true;
                throw new EOFException("Expected " + length + " bytes, got " + totalRead);
            }

            totalRead += read;
        }

        this.offset += length;
    }
}
