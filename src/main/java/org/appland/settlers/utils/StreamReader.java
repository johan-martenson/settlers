package org.appland.settlers.utils;

import org.appland.settlers.maps.ByteArray;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class StreamReader implements ByteReader {
    public static final int SIZE_OF_UINT32 = 4;

    private final InputStream inputStream;
    private final Stack<ByteOrder> byteOrderStack;

    private ByteOrder order;
    private long offset;
    private boolean isEof;

    public StreamReader(InputStream inputStream, ByteOrder byteOrder) {
        this.inputStream = inputStream;
        this.order = byteOrder;

        byteOrderStack = new Stack<>();
        offset = 0;
        isEof = false;
    }

    public int getUint16() throws IOException {
        byte[] bytes = new byte[2];

        int result = inputStream.read(bytes, 0, 2);

        if (result == -1) {
            isEof = true;
        }

        offset = offset + 2;

        return (short)(ByteBuffer.wrap(bytes).order(this.order).getShort() & 0xffff);
    }

    @Override
    public int getUint16(ByteOrder endian) throws IOException {
        byte[] bytes = new byte[2];

        int result = inputStream.read(bytes, 0, 2);

        if (result == -1) {
            isEof = true;
        }

        offset = offset + 2;

        return (short)(ByteBuffer.wrap(bytes).order(endian).getShort() & 0xffff);

    }

    public void read(byte[] buffer, int offset, int length) throws IOException {
        int result = inputStream.read(buffer, offset, length);

        if (result == -1) {
            isEof = true;
        }

        this.offset = this.offset + length;
    }

    public short[] getUint8Array(int lengthInBytes) throws IOException {
        short[] shorts = new short[lengthInBytes];

        for (int i = 0; i < lengthInBytes; i++) {
            shorts[i] = getUint8();
        }

        return shorts;
    }

    public long[] getUint32Array(int length) throws IOException {
        long[] uint32Array = new long[length];

        for (int i = 0; i < length; i++) {
            uint32Array[i] = getUint32();
        }

        return uint32Array;
    }

    public List<Long> getUint32ArrayAsList(int length) throws IOException {
        long[] uint32Array = getUint32Array(length);

        List<Long> uint32List = new ArrayList<>();

        for (long l : uint32Array) {
            uint32List.add(l);
        }

        return uint32List;
    }

    public long getUint32() throws IOException {
        byte[] bytes = new byte[4];

        int result = inputStream.read(bytes, 0, 4);

        if (result == -1) {
            isEof = true;
        }

        offset = offset + 4;

        return (long) ByteBuffer.wrap(bytes).order(order).getInt() & 0xffffffffL;
    }

    @Override
    public long getUint32(ByteOrder byteOrder) throws IOException {
        byte[] bytes = new byte[4];

        int result = inputStream.read(bytes, 0, 4);

        if (result == -1) {
            isEof = true;
        }

        offset = offset + 4;

        return (long) ByteBuffer.wrap(bytes).order(byteOrder).getInt() & 0xffffffffL;
    }

    public char[] getUint8ArrayAsChar(int lengthInBytes) throws IOException {
        char[] charArray = new char[lengthInBytes];

        for (int i = 0; i < lengthInBytes; i++) {
            charArray[i] = (char)getUint8();
        }

        return charArray;
    }

    public void pushByteOrder(ByteOrder order) {
        this.byteOrderStack.push(this.order);

        this.order = order;
    }

    public short getInt16() throws IOException {
        byte[] bytes = new byte[2];

        int result = this.inputStream.read(bytes, 0, 2);

        if (result == -1) {
            isEof = true;
        }

        offset = offset + 2;

        return ByteBuffer.wrap(bytes).order(this.order).getShort();
    }

    public void popByteOrder() {
        this.order = this.byteOrderStack.pop();
    }

    public InputStream getInputStream() {
        return this.inputStream;
    }

    public void skip(int i) throws IOException {
        long skipped = inputStream.skip(i);

        if (skipped < i) {
            isEof = true;
        }

        if (skipped > -1) {
            offset = offset + skipped;
        }
    }

    @Override
    public void setPosition(int position) throws IOException {
        if (position > offset) {
            skip((int) (position - offset));
        }

        throw new RuntimeException("Can't set a position backwards in the stream reader.");
    }

    public short getUint8() throws IOException {
        byte[] bytes = new byte[1];

        int result = inputStream.read(bytes, 0, 1);

        if (result == -1) {
            isEof = true;
        }

        offset = offset + 1;

        return (short)(ByteBuffer.wrap(bytes).order(order).get() & (short)0xff);
    }

    public String getUint8ArrayAsString(int i) throws IOException {
        byte[] bytes = new byte[i];

        int result = inputStream.read(bytes, 0, i);

        if (result == -1) {
            isEof = true;
        }

        String string = new String(bytes, StandardCharsets.US_ASCII);

        offset = offset + i;

        return string;
    }

    public byte[] getRemainingBytes() throws IOException {
        isEof = true;

        return inputStream.readAllBytes();
    }

    public byte[] getUint8ArrayAsBytes(int length) throws IOException {
        byte[] bytes = new byte[length];

        int result = inputStream.read(bytes, 0, length);

        if (result == -1) {
            isEof = true;
        }

        offset = offset + length;

        return bytes;
    }

    public int[] getUint16ArrayAsInts(int length) throws IOException {
        int[] intArray = new int[length];

        for (int i = 0; i < length; i++) {
            intArray[i] = getUint16();
        }

        return intArray;
    }

    public int getInt32() throws IOException {
        byte[] bytes = new byte[4];

        int result = inputStream.read(bytes, 0, 4);

        if (result == -1) {
            isEof = true;
        }

        offset = offset + 4;

        return ByteBuffer.wrap(bytes).order(this.order).getInt();
    }

    public void setPosition(long pixelOffset) throws IOException {
        long skipped = inputStream.skip(pixelOffset - offset);

        if (skipped < pixelOffset - offset) {
            isEof = true;
        }

        if (skipped > 0) {
            offset = offset + skipped;
        }
    }

    public byte getInt8() throws IOException {
        byte[] oneByte = new byte[1];

        int result = inputStream.read(oneByte, 0, 1);

        if (result == -1) {
            isEof = true;
        }

        offset = offset + 1;

        return oneByte[0];
    }

    public boolean isEof() {
        return isEof;
    }

    public String getRemainingBytesAsString() throws IOException {
        isEof = true;

        byte[] bytes = inputStream.readAllBytes();

        String string = new String(bytes, StandardCharsets.US_ASCII);

        offset = offset + bytes.length;

        return string;
    }

    public String getUint8ArrayAsNullTerminatedString(int length) throws IOException {
        byte[] bytes = new byte[length];

        int result = inputStream.read(bytes, 0, length);

        if (result == -1) {
            isEof = true;
        }

        offset = offset + length;

        /* Find the null termination byte if there is one */
        int indexOfZero = -1;
        for (int i = 0; i < length; i++) {
            if (bytes[i] == 0) {
                indexOfZero = i;

                break;
            }
        }

        String string = new String(bytes, 0, indexOfZero, StandardCharsets.US_ASCII);

        return string;
    }

    public ByteArray getUint8ArrayAsByteArray(int length) throws IOException {
        byte[] bytes = new byte[length];

        int result = inputStream.read(bytes, 0, length);

        if (result == -1) {
            isEof = true;
        }

        offset = offset + length;

        return new ByteArray(bytes, this.order);
    }

    @Override
    public int getPosition() {
        return (int) offset;
    }

    public void close() throws IOException {
        this.inputStream.close();
    }
}
