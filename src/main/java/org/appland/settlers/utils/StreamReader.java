package org.appland.settlers.utils;

import org.appland.settlers.maps.ByteArray;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Stack;

/**
 * A utility class to read common data types from an input stream with specific byte order handling.
 */
public class StreamReader implements ByteReader {
    public static final int SIZE_OF_UINT32 = 4;
    public static final int SIZE_OF_UINT16 = 2;
    public static final int SIZE_OF_UINT8 = 1;
    public static final int EOF = -1;

    private final InputStream inputStream;
    private final Stack<ByteOrder> byteOrderStack = new Stack<>();

    private ByteOrder order;
    private long offset = 0;
    private boolean isEof = false;

    /**
     * Constructs a StreamReader with the specified input stream and byte order.
     *
     * @param inputStream the input stream to read from
     * @param byteOrder   the byte order to use
     */
    public StreamReader(InputStream inputStream, ByteOrder byteOrder) {
        this.inputStream = inputStream;
        this.order = byteOrder;
    }

    /**
     * Reads an unsigned 16-bit integer.
     *
     * @return the unsigned 16-bit integer
     * @throws IOException if an I/O error occurs
     */
    public int getUint16() throws IOException {
        return getUint16(this.order);
    }

    @Override
    public int getUint16(ByteOrder endian) throws IOException {
        byte[] bytes = new byte[SIZE_OF_UINT16];

        if (inputStream.read(bytes) == EOF) {
            isEof = true;
        }

        offset += SIZE_OF_UINT16;
        return ByteBuffer.wrap(bytes).order(endian).getShort() & 0xffff;
    }

    /**
     * Reads data into the specified buffer.
     *
     * @param buffer the buffer to read data into
     * @param offset the start offset in the buffer
     * @param length the maximum number of bytes to read
     * @throws IOException if an I/O error occurs
     */
    public void read(byte[] buffer, int offset, int length) throws IOException {
        int result = inputStream.read(buffer, offset, length);

        if (result == -1) {
            isEof = true;
        }

        this.offset += result;
    }

    /**
     * Reads an array of unsigned 8-bit integers.
     *
     * @param lengthInBytes the length of the array
     * @return an array of unsigned 8-bit integers
     * @throws IOException if an I/O error occurs
     */
    public short[] getUint8Array(int lengthInBytes) throws IOException {
        short[] shorts = new short[lengthInBytes];

        for (int i = 0; i < lengthInBytes; i++) {
            shorts[i] = getUint8();
        }

        return shorts;
    }

    /**
     * Reads an array of unsigned 32-bit integers.
     *
     * @param length the length of the array
     * @return an array of unsigned 32-bit integers
     * @throws IOException if an I/O error occurs
     */
    public long[] getUint32Array(int length) throws IOException {
        long[] uint32Array = new long[length];

        for (int i = 0; i < length; i++) {
            uint32Array[i] = getUint32();
        }

        return uint32Array;
    }

    /**
     * Reads an unsigned 32-bit integer.
     *
     * @return the unsigned 32-bit integer
     * @throws IOException if an I/O error occurs
     */
    public long getUint32() throws IOException {
        return getUint32(this.order);
    }

    @Override
    public long getUint32(ByteOrder byteOrder) throws IOException {
        byte[] bytes = new byte[SIZE_OF_UINT32];

        if (inputStream.read(bytes) == EOF) {
            isEof = true;
        }

        offset += SIZE_OF_UINT32;

        return ByteBuffer.wrap(bytes).order(byteOrder).getInt() & 0xffffffffL;
    }

    /**
     * Reads an array of unsigned 8-bit integers as characters.
     *
     * @param lengthInBytes the length of the array
     * @return an array of characters
     * @throws IOException if an I/O error occurs
     */
    public char[] getUint8ArrayAsChar(int lengthInBytes) throws IOException {
        char[] charArray = new char[lengthInBytes];

        for (int i = 0; i < lengthInBytes; i++) {
            charArray[i] = (char)getUint8();
        }

        return charArray;
    }

    /**
     * Pushes the current byte order onto the stack and sets a new byte order.
     *
     * @param order the new byte order to set
     */
    public void pushByteOrder(ByteOrder order) {
        this.byteOrderStack.push(this.order);
        this.order = order;
    }

    /**
     * Reads a signed 16-bit integer.
     *
     * @return the signed 16-bit integer
     * @throws IOException if an I/O error occurs
     */
    public short getInt16() throws IOException {
        byte[] bytes = new byte[SIZE_OF_UINT16];
        if (inputStream.read(bytes) == EOF) {
            isEof = true;
        }
        offset += SIZE_OF_UINT16;
        return ByteBuffer.wrap(bytes).order(this.order).getShort();
    }

    /**
     * Pops the last byte order from the stack and sets it as the current byte order.
     */
    public void popByteOrder() {
        this.order = this.byteOrderStack.pop();
    }

    public InputStream getInputStream() {
        return this.inputStream;
    }

    /**
     * Skips the specified number of bytes in the input stream.
     *
     * @param bytesToSkip the number of bytes to skip
     * @throws IOException if an I/O error occurs
     */
    public void skip(int bytesToSkip) throws IOException {
        long skipped = inputStream.skip(bytesToSkip);

        if (skipped < bytesToSkip) {
            isEof = true;
        }

        offset = offset + skipped;
    }

    @Override
    public void setPosition(int position) throws IOException {
        if (position > offset) {
            skip(position - (int) offset);
        } else {
            throw new RuntimeException("Can't set a position backwards in the stream reader.");
        }
    }

    /**
     * Reads an unsigned 8-bit integer.
     *
     * @return the unsigned 8-bit integer
     * @throws IOException if an I/O error occurs
     */
    public short getUint8() throws IOException {
        byte[] bytes = new byte[SIZE_OF_UINT8];

        if (inputStream.read(bytes) == EOF) {
            isEof = true;
        }

        offset += SIZE_OF_UINT8;

        return (short) (ByteBuffer.wrap(bytes).order(order).get() & 0xff);
    }

    /**
     * Reads an array of unsigned 8-bit integers as a string.
     *
     * @param length the length of the array
     * @return a string representation of the array
     * @throws IOException if an I/O error occurs
     */
    public String getUint8ArrayAsString(int length) throws IOException {
        byte[] bytes = new byte[length];

        int result = inputStream.read(bytes);

        if (result == EOF) {
            isEof = true;
        }

        offset += result;

        int nullTerminatorAt = -1;
        for (int j = 0; j < bytes.length; j++) {
            if (bytes[j] == 0) {
                nullTerminatorAt = j;
                break;
            }
        }

        return nullTerminatorAt != -1
                ? new String(bytes, 0, nullTerminatorAt, StandardCharsets.US_ASCII)
                : new String(bytes, StandardCharsets.US_ASCII);
    }

    /**
     * Reads the remaining bytes from the input stream.
     *
     * @return a byte array containing the remaining bytes
     * @throws IOException if an I/O error occurs
     */
    public byte[] getRemainingBytes() throws IOException {
        isEof = true;

        return inputStream.readAllBytes();
    }

    /**
     * Reads an array of unsigned 8-bit integers as a byte array.
     *
     * @param length the length of the array
     * @return a byte array containing the unsigned 8-bit integers
     * @throws IOException if an I/O error occurs
     */
    public byte[] getUint8ArrayAsBytes(int length) throws IOException {
        byte[] bytes = new byte[length];

        int result = inputStream.read(bytes, 0, length);

        if (result == -1) {
            isEof = true;
        }

        offset = offset + result;

        return bytes;
    }

    /**
     * Reads an array of unsigned 16-bit integers as an array of ints.
     *
     * @param length the length of the array
     * @return an array of integers
     * @throws IOException if an I/O error occurs
     */
    public int[] getUint16ArrayAsInts(int length) throws IOException {
        int[] intArray = new int[length];

        for (int i = 0; i < length; i++) {
            intArray[i] = getUint16();
        }

        return intArray;
    }

    /**
     * Reads a signed 32-bit integer.
     *
     * @return the signed 32-bit integer
     * @throws IOException if an I/O error occurs
     */
    public int getInt32() throws IOException {
        byte[] bytes = new byte[SIZE_OF_UINT32];

        if (inputStream.read(bytes) == EOF) {
            isEof = true;
        }

        offset += SIZE_OF_UINT32;

        return ByteBuffer.wrap(bytes).order(this.order).getInt();
    }

    /**
     * Sets the position of the stream to the specified pixel offset.
     *
     * @param pixelOffset the pixel offset to set
     * @throws IOException if an I/O error occurs
     */
    public void setPosition(long pixelOffset) throws IOException {
        long skipped = inputStream.skip(pixelOffset - offset);

        if (skipped < pixelOffset - offset) {
            isEof = true;
        } else {
            offset += skipped;
        }
    }

    /**
     * Reads a signed 8-bit integer.
     *
     * @return the signed 8-bit integer
     * @throws IOException if an I/O error occurs
     */
    public byte getInt8() throws IOException {
        byte[] oneByte = new byte[SIZE_OF_UINT8];

        if (inputStream.read(oneByte) == EOF) {
            isEof = true;
        }

        offset += SIZE_OF_UINT8;

        return oneByte[0];
    }

    /**
     * Checks if the end of the file has been reached.
     *
     * @return true if the end of the file has been reached, false otherwise
     */
    public boolean isEof() {
        return isEof;
    }

    /**
     * Reads the remaining bytes from the input stream as a string.
     *
     * @return the remaining bytes as a string
     * @throws IOException if an I/O error occurs
     */
    public String getRemainingBytesAsString() throws IOException {
        isEof = true;
        byte[] bytes = inputStream.readAllBytes();
        offset += bytes.length;

        return new String(bytes, StandardCharsets.US_ASCII);
    }

    /**
     * Reads an array of unsigned 8-bit integers as a null-terminated string.
     *
     * @param length the length of the array
     * @return a string representation of the array
     * @throws IOException if an I/O error occurs
     */
    public String getUint8ArrayAsNullTerminatedString(int length) throws IOException {
        byte[] bytes = new byte[length];

        int result = inputStream.read(bytes, 0, length);

        if (result == -1) {
            isEof = true;
        }

        offset = offset + result;

        int indexOfZero = -1;
        for (int i = 0; i < result; i++) {
            if (bytes[i] == 0) {
                indexOfZero = i;

                break;
            }
        }

        return new String(bytes, 0, indexOfZero, StandardCharsets.US_ASCII);
    }

    /**
     * Reads an array of unsigned 8-bit integers as a ByteArray.
     *
     * @param length the length of the array
     * @return a ByteArray containing the unsigned 8-bit integers
     * @throws IOException if an I/O error occurs
     */
    public ByteArray getUint8ArrayAsByteArray(int length) throws IOException {
        byte[] bytes = new byte[length];
        if (inputStream.read(bytes) == EOF) {
            isEof = true;
        }
        offset += length;
        return new ByteArray(bytes, this.order);
    }

    @Override
    public int getPosition() {
        return (int) offset;
    }

    /**
     * Closes the input stream.
     *
     * @throws IOException if an I/O error occurs
     */
    public void close() throws IOException {
        this.inputStream.close();
    }
}
