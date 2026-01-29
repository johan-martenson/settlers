package org.appland.settlers.assets.test;

import org.appland.settlers.utils.StreamReader;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteOrder;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class TestStreamReader {

    private StreamReader reader(byte[] data, ByteOrder order) {
        return new StreamReader(new ByteArrayInputStream(data), order);
    }

    // ------------------------------------------------------------
    // Primitive reads
    // ------------------------------------------------------------

    @Test
    public void testUint8() throws Exception {
        try (var r = reader(new byte[]{(byte) 0xff}, ByteOrder.LITTLE_ENDIAN)) {
            assertEquals(255, r.getUint8());
            assertEquals(1, r.getPosition());
        }
    }

    @Test
    public void testInt8() throws Exception {
        try (var r = reader(new byte[]{(byte) 0x80}, ByteOrder.LITTLE_ENDIAN)) {
            assertEquals((byte) 0x80, r.getInt8());
        }
    }

    @Test
    public void testUint16LittleEndian() throws Exception {
        try (var r = reader(new byte[]{0x34, 0x12}, ByteOrder.LITTLE_ENDIAN)) {
            assertEquals(0x1234, r.getUint16());
        }
    }

    @Test
    public void testUint16BigEndian() throws Exception {
        try (var r = reader(new byte[]{0x12, 0x34}, ByteOrder.BIG_ENDIAN)) {
            assertEquals(0x1234, r.getUint16());
        }
    }

    @Test
    public void testInt16() throws Exception {
        try (var r = reader(new byte[]{(byte) 0xff, (byte) 0xff}, ByteOrder.LITTLE_ENDIAN)) {
            assertEquals(-1, r.getInt16());
        }
    }

    @Test
    public void testUint32LittleEndian() throws Exception {
        try (var r = reader(new byte[]{1, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN)) {
            assertEquals(1L, r.getUint32());
        }
    }

    @Test
    public void testUint32BigEndian() throws Exception {
        try (var r = reader(new byte[]{0, 0, 0, 1}, ByteOrder.BIG_ENDIAN)) {
            assertEquals(1L, r.getUint32());
        }
    }

    @Test
    public void testInt32() throws Exception {
        try (var r = reader(new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff}, ByteOrder.LITTLE_ENDIAN)) {
            assertEquals(-1, r.getInt32());
        }
    }

    // ------------------------------------------------------------
    // Arrays
    // ------------------------------------------------------------

    @Test
    public void testUint8Array() throws Exception {
        try (var r = reader(new byte[]{1, 2, 3}, ByteOrder.LITTLE_ENDIAN)) {
            assertArrayEquals(new short[]{1, 2, 3}, r.getUint8Array(3));
        }
    }

    @Test
    public void testUint16Array() throws Exception {
        try (var r = reader(new byte[]{1, 0, 2, 0}, ByteOrder.LITTLE_ENDIAN)) {
            assertArrayEquals(new int[]{1, 2}, r.getUint16ArrayAsInts(2));
        }
    }

    @Test
    public void testUint32Array() throws Exception {
        try (var r = reader(new byte[]{
                1, 0, 0, 0,
                2, 0, 0, 0
        }, ByteOrder.LITTLE_ENDIAN)) {
            assertArrayEquals(new long[]{1L, 2L}, r.getUint32Array(2));
        }
    }

    // ------------------------------------------------------------
    // Strings
    // ------------------------------------------------------------

    @Test
    public void testAsciiString() throws Exception {
        try (var r = reader("HELLO".getBytes(), ByteOrder.LITTLE_ENDIAN)) {
            assertEquals("HELLO", r.getUint8ArrayAsString(5));
        }
    }

    @Test
    public void testNullTerminatedString() throws Exception {
        try (var r = reader(new byte[]{'A', 'B', 0, 'C'}, ByteOrder.LITTLE_ENDIAN)) {
            assertEquals("AB", r.getUint8ArrayAsNullTerminatedString(4));
        }
    }

    // ------------------------------------------------------------
    // Positioning
    // ------------------------------------------------------------

    @Test
    public void testSkip() throws Exception {
        try (var r = reader(new byte[]{1, 2, 3, 4}, ByteOrder.LITTLE_ENDIAN)) {
            r.skip(2);
            assertEquals(3, r.getUint8());
        }
    }

    @Test
    public void testSetPositionForward() throws Exception {
        try (var r = reader(new byte[]{10, 20, 30}, ByteOrder.LITTLE_ENDIAN)) {
            r.setPosition(2);
            assertEquals(30, r.getUint8());
        }
    }

    @Test
    public void testSetPositionBackwardsFails() {
        assertThrows(IOException.class, () -> {
            try (var r = reader(new byte[]{1, 2}, ByteOrder.LITTLE_ENDIAN)) {
                r.getUint8();
                r.setPosition(0);
            }
        });
    }

    // ------------------------------------------------------------
    // Byte order stack
    // ------------------------------------------------------------

    @Test
    public void testPushPopByteOrder() throws Exception {
        byte[] data = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06};

        try (var r = reader(data, ByteOrder.BIG_ENDIAN)) {
            assertEquals(0x0102, r.getUint16());

            r.pushByteOrder(ByteOrder.LITTLE_ENDIAN);

            assertEquals(0x0403, r.getUint16());

            r.popByteOrder();

            assertEquals(0x0506, r.getUint16());
        }
    }

    // ------------------------------------------------------------
    // EOF behavior
    // ------------------------------------------------------------

    @Test
    public void testEofThrows() {
        assertThrows(EOFException.class, () -> {
            try (var r = reader(new byte[]{1}, ByteOrder.LITTLE_ENDIAN)) {
                r.getUint32();
            }
        });
    }

    @Test
    public void testRemainingBytes() throws Exception {
        try (var r = reader(new byte[]{1, 2, 3}, ByteOrder.LITTLE_ENDIAN)) {
            byte[] remaining = r.getRemainingBytes();
            assertArrayEquals(new byte[]{1, 2, 3}, remaining);
            assertTrue(r.isEof());
        }
    }

    @Test
    public void testRemainingBytesAsString() throws Exception {
        try (var r = reader("TEST".getBytes(), ByteOrder.LITTLE_ENDIAN)) {
            assertEquals("TEST", r.getRemainingBytesAsString());
        }
    }
}
