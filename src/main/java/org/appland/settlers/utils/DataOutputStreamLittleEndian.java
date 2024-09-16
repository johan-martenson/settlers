package org.appland.settlers.utils;


import java.io.DataOutputStream;
import java.io.IOException;

public class DataOutputStreamLittleEndian implements AutoCloseable {
    private final DataOutputStream systemStream;

    public DataOutputStreamLittleEndian(DataOutputStream systemStream) {
        this.systemStream = systemStream;
    }

    @Override
    public void close() throws IOException {
        this.systemStream.close();
    }

    public void writeString(String stringToWrite) throws IOException {
        this.systemStream.writeBytes(stringToWrite);
    }

    public void writeBytes(byte[] bytesToWrite) throws IOException {
        this.systemStream.write(bytesToWrite, 0, bytesToWrite.length);
    }

    public void writeInt(int intToWrite) throws IOException {
        byte[] intToWriteAsBytesLittleEndian = new byte[]
                {
                        (byte) (intToWrite & 0xFF),
                        (byte) ((intToWrite >> 8) & 0xFF),
                        (byte) ((intToWrite >> 16) & 0xFF),
                        (byte) ((intToWrite >> 24) & 0xFF),
                };

        this.systemStream.write(intToWriteAsBytesLittleEndian, 0, 4);
    }

    public void writeShort(short shortToWrite) throws IOException {
        byte[] shortToWriteAsBytesLittleEndian = new byte[]
                {
                        (byte) shortToWrite,
                        (byte) (shortToWrite >>> 8 & 0xFF),
                };

        this.systemStream.write(shortToWriteAsBytesLittleEndian, 0, 2);
    }
}