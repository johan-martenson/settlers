package org.appland.settlers.utils;

import org.appland.settlers.maps.ByteArray;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class ByteArrayReader implements ByteReader {
    private final ByteBuffer byteArray;
    private final Deque<ByteOrder> byteOrderStack = new ArrayDeque<>();

    private ByteOrder order;

    public ByteArrayReader(byte[] byteArray, ByteOrder byteOrder) {
        this.byteArray = ByteBuffer.wrap(byteArray).order(byteOrder);
        this.order = byteOrder;
    }

    @Override
    public short getUint8() {
        return (short)(byteArray.get() & (short)0xff);
    }

    @Override
    public byte getInt8() {
        return this.byteArray.get();
    }

    @Override
    public int getUint16() {
        return this.byteArray.getShort() & 0xffff;
    }

    @Override
    public int getUint16(ByteOrder endian) {
        ByteOrder previous = byteArray.order();
        byteArray.order(endian);

        int value = byteArray.getShort() & 0xFFFF;

        byteArray.order(previous);
        return value;
    }

    @Override
    public String getRemainingBytesAsString() {
        List<Byte> byteList = new ArrayList<>();

        while (byteArray.position() < byteArray.array().length) {
            byteList.add(byteArray.get());
        }

        byte[] bytes = new byte[byteList.size()];

        for (int i = 0; i < byteList.size(); i++) {
            bytes[i] = byteList.get(i);
        }

        return new String(bytes, StandardCharsets.US_ASCII);
    }

    @Override
    public short getInt16() {
        return this.byteArray.getShort();
    }

    @Override
    public long getUint32() {
        return this.byteArray.getInt() & 0xffffffffL;
    }

    @Override
    public long getUint32(ByteOrder byteOrder) {
        ByteOrder previous = byteArray.order();
        byteArray.order(byteOrder);

        long value = byteArray.getInt() & 0xFFFFFFFFL;

        byteArray.order(previous);
        return value;
    }

    @Override
    public int getInt32() {
        return this.byteArray.getInt();
    }

    @Override
    public short[] getUint8Array(int length) {
        short[] result = new short[length];

        for (int i = 0; i < length; i++) {
            result[i] = getUint8();
        }

        return result;
    }

    @Override
    public long[] getUint32Array(int length) {
        long[] result = new long[length];

        for (int i = 0; i < length; i++) {
            result[i] = getUint32();
        }

        return result;
    }

    @Override
    public char[] getUint8ArrayAsChar(int length) {
        char[] chars = new char[length];

        for (int i = 0; i < length; i++) {
            chars[i] = (char) (this.byteArray.get() & 0xffff);
        }

        return chars;
    }

    @Override
    public void pushByteOrder(ByteOrder newOrder) {
        this.byteOrderStack.push(this.order);
        this.order = newOrder;
        byteArray.order(newOrder);
    }

    @Override
    public void popByteOrder() {
        this.order = this.byteOrderStack.pop();
        byteArray.order(this.order);
    }

    public ByteBuffer getByteArray() {
        return this.byteArray;
    }

    @Override
    public String getUint8ArrayAsString(int i) {
        byte[] bytes = getUint8ArrayAsBytes(i);
        return new String(bytes, StandardCharsets.US_ASCII);
    }

    @Override
    public byte[] getRemainingBytes() {
        byte[] bytes = new byte[byteArray.remaining()];
        byteArray.get(bytes);
        return bytes;
    }

    @Override
    public byte[] getUint8ArrayAsBytes(int length) {
        byte[] bytes = new byte[length];
        byteArray.get(bytes);
        return bytes;
    }

    @Override
    public int[] getUint16ArrayAsInts(int length) {
        int[] intArray = new int[length];

        for (int i = 0; i < length; i++) {
            intArray[i] = getUint16();
        }

        return intArray;
    }

    @Override
    public boolean isEof() {
        return !byteArray.hasRemaining();
    }

    @Override
    public ByteArray getUint8ArrayAsByteArray(int length) {
        return new ByteArray(getUint8ArrayAsBytes(length), order);
    }

    @Override
    public int getPosition() {
        return byteArray.position();
    }

    @Override
    public void skip(int length) {
        byteArray.position(byteArray.position() + length);
    }

    @Override
    public void setPosition(int position) {
        byteArray.position(position);
    }

    @Override
    public int length() {
        return byteArray.limit();
    }

    @Override
    public String getUint8ArrayAsNullTerminatedString(int maxLength) {
        byte[] bytes = getUint8ArrayAsBytes(maxLength);

        int end = 0;
        while (end < bytes.length && bytes[end] != 0) {
            end++;
        }

        return new String(bytes, 0, end, StandardCharsets.US_ASCII);
    }

    @Override
    public void close() throws Exception {

    }
}
