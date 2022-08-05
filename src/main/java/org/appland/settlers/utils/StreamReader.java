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

public class StreamReader {
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

        return getUint16(ByteBuffer.wrap(bytes).order(this.order));
    }

    private short getUint16(ByteBuffer byteBuffer) {
        return (short)(byteBuffer.getShort() & 0xffff);
    }

    public void read(byte[] buffer, int offset, int length) throws IOException {
        int result = inputStream.read(buffer, offset, length);

        if (result == -1) {
            isEof = true;
        }

        this.offset = this.offset + length;
    }

    public short[] getUint8Array(int lengthInBytes) throws IOException {
        byte[] bytes = new byte[lengthInBytes];

        int result = inputStream.read(bytes, 0, lengthInBytes);

        if (result == -1) {
            isEof = true;
        }

        short[] shorts = new short[lengthInBytes];

        for (int i = 0; i < bytes.length; i++) {
            shorts[i] = getUint8(ByteBuffer.wrap(bytes));
        }

        offset = offset + lengthInBytes;

        return shorts;
    }

    private short getUint8(ByteBuffer byteBuffer) {
        return ((short)(byteBuffer.get() & 0xff));
    }

    public long[] getUint32Array(int length) throws IOException {
        byte[] bytes = new byte[length * 4];

        int result = inputStream.read(bytes, 0, length * 4);

        if (result == -1) {
            isEof = true;
        }

        long[] longArray = new long[length];

        for (int i = 0; i < bytes.length; i++) {
            longArray[i] = ((long)ByteBuffer.wrap(bytes).order(order).getInt(i * 4) & 0xffffffffL);
        }

        offset = offset + length * 4L;

        return longArray;
    }

    public List<Long> getUint32ArrayAsList(int length) throws IOException {
        long[] array = getUint32Array(length);

        List<Long> list = new ArrayList<>();

        for (long l : array) {
            list.add(l);
        }

        return list;
    }

    public long getUint32() throws IOException {
        byte[] bytes = new byte[4];

        int result = inputStream.read(bytes, 0, 4);

        if (result == -1) {
            isEof = true;
        }

        offset = offset + 4;

        return ((long) ByteBuffer.wrap(bytes).order(order).getInt() & 0xffffffffL);
    }

    public char[] getUint8ArrayAsChar(int lengthInBytes) throws IOException {
        byte[] bytes = new byte[lengthInBytes];

        int result = inputStream.read(bytes, 0, lengthInBytes);

        if (result == -1) {
            isEof = true;
        }

        char[] chars = new char[lengthInBytes];

        for (int i = 0; i < bytes.length; i++) {
            chars[i] = (char)(ByteBuffer.wrap(bytes).order(order).getShort(i) & 0xffff);
        }

        offset = offset + lengthInBytes;

        return chars;
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
        byte[] bytes = new byte[length * 2];

        int[] intArray = new int[length];

        int result = inputStream.read(bytes, 0, length * 2);

        if (result == -1) {
            isEof = true;
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes).order(this.order);

        for (int i = 0; i < length; i++) {
            intArray[i] = (byteBuffer.getShort(i * 2) & 0xffff);
        }

        offset = offset + length * 2L;

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
        byte[] b = new byte[1];

        int result = inputStream.read(b, 0, 1);

        if (result == -1) {
            isEof = true;
        }

        offset = offset + 1;

        return b[0];
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
}
