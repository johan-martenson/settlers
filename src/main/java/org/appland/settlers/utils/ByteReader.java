package org.appland.settlers.utils;

import org.appland.settlers.maps.ByteArray;

import java.io.IOException;
import java.nio.ByteOrder;

public interface ByteReader {
    short getUint8() throws IOException;

    byte getInt8() throws IOException;

    int getUint16() throws IOException;

    short getInt16() throws IOException;

    long getUint32() throws IOException;

    int getInt32() throws IOException;

    short[] getUint8Array(int lengthInBytes) throws IOException;

    long[] getUint32Array(int length) throws IOException;

    char[] getUint8ArrayAsChar(int lengthInBytes) throws IOException;

    void pushByteOrder(ByteOrder order);

    void popByteOrder();

    String getUint8ArrayAsString(int i) throws IOException;

    byte[] getRemainingBytes() throws IOException;

    byte[] getUint8ArrayAsBytes(int length) throws IOException;

    int[] getUint16ArrayAsInts(int length) throws IOException;

    boolean isEof();

    ByteArray getUint8ArrayAsByteArray(int length) throws IOException;

    int getPosition();

    void skip(int length) throws IOException;

    void setPosition(int position) throws IOException;

    long getUint32(ByteOrder byteOrder) throws IOException;

    int getUint16(ByteOrder endian) throws IOException;

    String getRemainingBytesAsString() throws IOException;

    int length();
}
