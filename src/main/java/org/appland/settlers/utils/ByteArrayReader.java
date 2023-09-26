package org.appland.settlers.utils;

import org.appland.settlers.maps.ByteArray;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ByteArrayReader implements ByteReader {

    private final ByteBuffer byteArray;
    private final Stack<ByteOrder> byteOrderStack;

    private ByteOrder order;
    private long offset;
    private boolean isEof;

    public ByteArrayReader(byte[] byteArray, ByteOrder byteOrder) {
        this.byteArray = ByteBuffer.wrap(byteArray).order(byteOrder);
        this.order = byteOrder;

        byteOrderStack = new Stack<>();
        offset = 0;
        isEof = false;
    }

    @Override
    public short getUint8() {
        offset = offset + 1;

        return (short)(this.byteArray.order(order).get() & (short)0xff);
    }

    @Override
    public byte getInt8() {
        offset = offset + 1;

        return this.byteArray.order(order).get();
    }

    @Override
    public int getUint16() {
        this.offset = this.offset + 2;

        return (short)(this.byteArray.order(order).getShort() & 0xffff);
    }

    @Override
    public int getUint16(ByteOrder endian) {
        this.offset = this.offset + 2;

        return (short)(this.byteArray.order(endian).getShort() & 0xffff);
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

        offset = offset + bytes.length;

        return new String(bytes, StandardCharsets.US_ASCII);
    }

    @Override
    public short getInt16() {
        offset = offset + 2;

        return this.byteArray.order(order).getShort();
    }

    @Override
    public long getUint32() {
        offset = offset + 4;

        return (long) this.byteArray.order(order).getInt() & 0xffffffffL;
    }

    @Override
    public long getUint32(ByteOrder byteOrder) {
        offset = offset + 4;

        return (long) this.byteArray.order(byteOrder).getInt() & 0xffffffffL;
    }

    @Override
    public int getInt32() {
        offset = offset + 4;

        return this.byteArray.order(this.order).getInt();
    }

    @Override
    public short[] getUint8Array(int lengthInBytes) {
        short[] shorts = new short[lengthInBytes];

        for (int i = 0; i < lengthInBytes; i++) {
            shorts[i] = getUint8();
        }

        return shorts;
    }

    @Override
    public long[] getUint32Array(int length) {
        long[] longArray = new long[length];

        for (int i = 0; i < length; i++) {
            longArray[i] = getUint32();
        }

        return longArray;
    }

    @Override
    public List<Long> getUint32ArrayAsList(int length) {
        long[] array = getUint32Array(length);

        List<Long> list = new ArrayList<>();

        for (long l : array) {
            list.add(l);
        }

        return list;
    }

    @Override
    public char[] getUint8ArrayAsChar(int lengthInBytes) {
        char[] chars = new char[lengthInBytes];

        for (int i = 0; i < lengthInBytes; i++) {
            chars[i] = (char)(this.byteArray.order(order).getShort(i) & 0xffff);
        }

        offset = offset + lengthInBytes;

        return chars;
    }

    @Override
    public void pushByteOrder(ByteOrder order) {
        this.byteOrderStack.push(this.order);

        this.order = order;
    }

    @Override
    public void popByteOrder() {
        this.order = this.byteOrderStack.pop();
    }

    public ByteBuffer getByteArray() {
        return this.byteArray;
    }

    @Override
    public String getUint8ArrayAsString(int i) {
        byte[] bytes = getUint8ArrayAsBytes(i);

        String string = new String(bytes, StandardCharsets.US_ASCII);

        offset = offset + i;

        return string;
    }

    @Override
    public byte[] getRemainingBytes() {
        List<Byte> byteList = new ArrayList<>();

        while (byteArray.position() < byteArray.array().length) {
            byteList.add(byteArray.get());
        }

        byte[] bytes = new byte[byteList.size()];

        for (int i = 0; i < byteList.size(); i++) {
            bytes[i] = byteList.get(i);
        }

        offset = offset + bytes.length;

        return bytes;
    }

    @Override
    public byte[] getUint8ArrayAsBytes(int length) {
        byte[] bytes = new byte[length];

        for (int i = 0; i < length; i++) {
            bytes[i] = this.byteArray.order(order).get();
        }

        offset = offset + length;

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
        return isEof;
    }

    @Override
    public ByteArray getUint8ArrayAsByteArray(int length) {
        byte[] bytes =  getUint8ArrayAsBytes(length);

        return new ByteArray(bytes, this.order);
    }

    @Override
    public int getPosition() {
        return byteArray.position();
    }

    @Override
    public void skip(int length) {
        for (int i = 0; i < length; i++) {
            byteArray.get();
        }
    }

    @Override
    public void setPosition(int position) {
        byteArray.position(position);
    }
}
