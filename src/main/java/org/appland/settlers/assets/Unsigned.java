package org.appland.settlers.assets;

import java.nio.ByteBuffer;

/**
 * Utility class for handling unsigned values in ByteBuffer.
 */
public class Unsigned {

    /**
     * Reads an unsigned byte from the ByteBuffer.
     *
     * @param bb The ByteBuffer to read from.
     * @return The unsigned byte value.
     */
    public static short getUnsignedByte(ByteBuffer bb) {
        return (short) (bb.get() & 0xff);
    }

    /**
     * Writes an unsigned byte to the ByteBuffer.
     *
     * @param bb The ByteBuffer to write to.
     * @param value The unsigned byte value to write.
     */
    public static void putUnsignedByte(ByteBuffer bb, int value) {
        bb.put((byte) (value & 0xff));
    }

    /**
     * Reads an unsigned byte from the ByteBuffer at the specified position.
     *
     * @param bb The ByteBuffer to read from.
     * @param position The position in the ByteBuffer.
     * @return The unsigned byte value.
     */
    public static short getUnsignedByte(ByteBuffer bb, int position) {
        return (short) (bb.get(position) & (short)0xff);
    }

    /**
     * Writes an unsigned byte to the ByteBuffer at the specified position.
     *
     * @param bb The ByteBuffer to write to.
     * @param position The position in the ByteBuffer.
     * @param value The unsigned byte value to write.
     */
    public static void putUnsignedByte(ByteBuffer bb, int position, int value) {
        bb.put(position, (byte) (value & 0xff));
    }

    // ---------------------------------------------------------------

    /**
     * Reads an unsigned short from the ByteBuffer.
     *
     * @param bb The ByteBuffer to read from.
     * @return The unsigned short value.
     */
    public static int getUnsignedShort(ByteBuffer bb) {
        return bb.getShort() & 0xffff;
    }

    /**
     * Writes an unsigned short to the ByteBuffer.
     *
     * @param bb The ByteBuffer to write to.
     * @param value The unsigned short value to write.
     */
    public static void putUnsignedShort(ByteBuffer bb, int value) {
        bb.putShort((short) (value & 0xffff));
    }

    /**
     * Reads an unsigned short from the ByteBuffer at the specified position.
     *
     * @param bb The ByteBuffer to read from.
     * @param position The position in the ByteBuffer.
     * @return The unsigned short value.
     */
    public static int getUnsignedShort(ByteBuffer bb, int position) {
        return bb.getShort(position) & 0xffff;
    }

    /**
     * Writes an unsigned short to the ByteBuffer at the specified position.
     *
     * @param bb The ByteBuffer to write to.
     * @param position The position in the ByteBuffer.
     * @param value The unsigned short value to write.
     */
    public static void putUnsignedShort(ByteBuffer bb, int position, int value) {
        bb.putShort(position, (short) (value & 0xffff));
    }

    // ---------------------------------------------------------------

    /**
     * Reads an unsigned int from the ByteBuffer.
     *
     * @param bb The ByteBuffer to read from.
     * @return The unsigned int value.
     */
    public static long getUnsignedInt(ByteBuffer bb) {
        return (long)bb.getInt() & 0xffffffffL;
    }

    /**
     * Writes an unsigned int to the ByteBuffer.
     *
     * @param bb The ByteBuffer to write to.
     * @param value The unsigned int value to write.
     */
    public static void putUnsignedInt(ByteBuffer bb, long value) {
        bb.putInt((int) (value & 0xffffffffL));
    }

    /**
     * Reads an unsigned int from the ByteBuffer at the specified position.
     *
     * @param bb The ByteBuffer to read from.
     * @param position The position in the ByteBuffer.
     * @return The unsigned int value.
     */
    public static long getUnsignedInt(ByteBuffer bb, int position) {
        return (long)bb.getInt(position) & 0xffffffffL;
    }

    /**
     * Writes an unsigned int to the ByteBuffer at the specified position.
     *
     * @param bb The ByteBuffer to write to.
     * @param position The position in the ByteBuffer.
     * @param value The unsigned int value to write.
     */
    public static void putUnsignedInt(ByteBuffer bb, int position, long value) {
        bb.putInt(position, (int) (value & 0xffffffffL));
    }
}