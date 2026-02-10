package org.appland.settlers.assets.test;

import org.appland.settlers.utils.ByteArrayReader;
import org.appland.settlers.utils.ByteReader;
import org.appland.settlers.utils.StreamReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.nio.ByteOrder;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StreamReader implementations")
class StreamReaderTest {

    // ---------------------------------------------------------------------
    // Reader factories
    // ---------------------------------------------------------------------

    private static Stream<BiFunction<byte[], ByteOrder, ByteReader>> readers() {
        return Stream.of(
                (data, order) -> new StreamReader(new ByteArrayInputStream(data), order),
                ByteArrayReader::new
        );
    }

    // ---------------------------------------------------------------------
    // Primitive reads
    // ---------------------------------------------------------------------

    @ParameterizedTest(name = "{index} → {0}")
    @MethodSource("readers")
    void testUint8(BiFunction<byte[], ByteOrder, ByteReader> readerFactory) throws Exception {
        try (var r = readerFactory.apply(new byte[]{(byte) 0xff}, ByteOrder.LITTLE_ENDIAN)) {
            assertEquals(255, r.getUint8());
            assertEquals(1, r.getPosition());
        }
    }

    @ParameterizedTest
    @MethodSource("readers")
    void testInt8(BiFunction<byte[], ByteOrder, ByteReader> readerFactory) throws Exception {
        try (var r = readerFactory.apply(new byte[]{(byte) 0x80}, ByteOrder.LITTLE_ENDIAN)) {
            assertEquals((byte) 0x80, r.getInt8());
        }
    }

    @ParameterizedTest
    @MethodSource("readers")
    void testUint16LittleEndian(BiFunction<byte[], ByteOrder, ByteReader> readerFactory) throws Exception {
        try (var r = readerFactory.apply(new byte[]{0x34, 0x12}, ByteOrder.LITTLE_ENDIAN)) {
            assertEquals(0x1234, r.getUint16());
        }
    }

    @ParameterizedTest
    @MethodSource("readers")
    void testUint16BigEndian(BiFunction<byte[], ByteOrder, ByteReader> readerFactory) throws Exception {
        try (var r = readerFactory.apply(new byte[]{0x12, 0x34}, ByteOrder.BIG_ENDIAN)) {
            assertEquals(0x1234, r.getUint16());
        }
    }

    @ParameterizedTest
    @MethodSource("readers")
    void testInt16(BiFunction<byte[], ByteOrder, ByteReader> readerFactory) throws Exception {
        try (var r = readerFactory.apply(new byte[]{(byte) 0xff, (byte) 0xff}, ByteOrder.LITTLE_ENDIAN)) {
            assertEquals(-1, r.getInt16());
        }
    }

    @ParameterizedTest
    @MethodSource("readers")
    void testUint32LittleEndian(BiFunction<byte[], ByteOrder, ByteReader> readerFactory) throws Exception {
        try (var r = readerFactory.apply(new byte[]{1, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN)) {
            assertEquals(1L, r.getUint32());
        }
    }

    @ParameterizedTest
    @MethodSource("readers")
    void testUint32BigEndian(BiFunction<byte[], ByteOrder, ByteReader> readerFactory) throws Exception {
        try (var r = readerFactory.apply(new byte[]{0, 0, 0, 1}, ByteOrder.BIG_ENDIAN)) {
            assertEquals(1L, r.getUint32());
        }
    }

    @ParameterizedTest
    @MethodSource("readers")
    void testInt32(BiFunction<byte[], ByteOrder, ByteReader> readerFactory) throws Exception {
        try (var r = readerFactory.apply(
                new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff},
                ByteOrder.LITTLE_ENDIAN
        )) {
            assertEquals(-1, r.getInt32());
        }
    }

    // ---------------------------------------------------------------------
    // Arrays
    // ---------------------------------------------------------------------

    @ParameterizedTest
    @MethodSource("readers")
    void testUint8Array(BiFunction<byte[], ByteOrder, ByteReader> readerFactory) throws Exception {
        try (var r = readerFactory.apply(new byte[]{1, 2, 3}, ByteOrder.LITTLE_ENDIAN)) {
            assertArrayEquals(new short[]{1, 2, 3}, r.getUint8Array(3));
        }
    }

    @ParameterizedTest
    @MethodSource("readers")
    void testUint16Array(BiFunction<byte[], ByteOrder, ByteReader> readerFactory) throws Exception {
        try (var r = readerFactory.apply(new byte[]{1, 0, 2, 0}, ByteOrder.LITTLE_ENDIAN)) {
            assertArrayEquals(new int[]{1, 2}, r.getUint16ArrayAsInts(2));
        }
    }

    @ParameterizedTest
    @MethodSource("readers")
    void testUint32Array(BiFunction<byte[], ByteOrder, ByteReader> readerFactory) throws Exception {
        try (var r = readerFactory.apply(new byte[]{
                1, 0, 0, 0,
                2, 0, 0, 0
        }, ByteOrder.LITTLE_ENDIAN)) {
            assertArrayEquals(new long[]{1L, 2L}, r.getUint32Array(2));
        }
    }

    // ---------------------------------------------------------------------
    // Strings
    // ---------------------------------------------------------------------

    @ParameterizedTest
    @MethodSource("readers")
    void testAsciiString(BiFunction<byte[], ByteOrder, ByteReader> readerFactory) throws Exception {
        try (var r = readerFactory.apply("HELLO".getBytes(), ByteOrder.LITTLE_ENDIAN)) {
            assertEquals("HELLO", r.getUint8ArrayAsString(5));
        }
    }

    @ParameterizedTest
    @MethodSource("readers")
    void testNullTerminatedString(BiFunction<byte[], ByteOrder, ByteReader> readerFactory) throws Exception {
        try (var r = readerFactory.apply(new byte[]{'A', 'B', 0, 'C'}, ByteOrder.LITTLE_ENDIAN)) {
            assertEquals("AB", r.getUint8ArrayAsNullTerminatedString(4));
        }
    }

    // ---------------------------------------------------------------------
    // Positioning
    // ---------------------------------------------------------------------

    @ParameterizedTest
    @MethodSource("readers")
    void testSkip(BiFunction<byte[], ByteOrder, ByteReader> readerFactory) throws Exception {
        try (var r = readerFactory.apply(new byte[]{1, 2, 3, 4}, ByteOrder.LITTLE_ENDIAN)) {
            r.skip(2);
            assertEquals(3, r.getUint8());
        }
    }

    @ParameterizedTest
    @MethodSource("readers")
    void testSetPositionForward(BiFunction<byte[], ByteOrder, ByteReader> readerFactory) throws Exception {
        try (var r = readerFactory.apply(new byte[]{10, 20, 30}, ByteOrder.LITTLE_ENDIAN)) {
            r.setPosition(2);
            assertEquals(30, r.getUint8());
        }
    }

    /*@Ignore()
    @ParameterizedTest
    @MethodSource("readers")
    void testSetPositionBackwardsFails(BiFunction<byte[], ByteOrder, ByteReader> readerFactory) {
        assertThrows(IOException.class, () -> {
            try (var r = readerFactory.apply(new byte[]{1, 2}, ByteOrder.LITTLE_ENDIAN)) {
                r.getUint8();
                r.setPosition(0);
            }
        });
    }*/

    // ---------------------------------------------------------------------
    // Byte order stack
    // ---------------------------------------------------------------------

    @ParameterizedTest
    @MethodSource("readers")
    void testPushPopByteOrder(BiFunction<byte[], ByteOrder, ByteReader> readerFactory) throws Exception {
        byte[] data = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06};

        try (var r = readerFactory.apply(data, ByteOrder.BIG_ENDIAN)) {
            assertEquals(0x0102, r.getUint16());

            r.pushByteOrder(ByteOrder.LITTLE_ENDIAN);
            assertEquals(0x0403, r.getUint16());

            r.popByteOrder();
            assertEquals(0x0506, r.getUint16());
        }
    }

    // ---------------------------------------------------------------------
    // EOF behavior
    // ---------------------------------------------------------------------

    @ParameterizedTest
    @MethodSource("readers")
    void testEofThrows(BiFunction<byte[], ByteOrder, ByteReader> readerFactory) {
        assertThrows(Exception.class, () -> {
            try (var r = readerFactory.apply(new byte[]{1}, ByteOrder.LITTLE_ENDIAN)) {
                r.getUint32();
            }
        });
    }

    @ParameterizedTest
    @MethodSource("readers")
    void testRemainingBytes(BiFunction<byte[], ByteOrder, ByteReader> readerFactory) throws Exception {
        try (var r = readerFactory.apply(new byte[]{1, 2, 3}, ByteOrder.LITTLE_ENDIAN)) {
            assertArrayEquals(new byte[]{1, 2, 3}, r.getRemainingBytes());
            assertTrue(r.isEof());
        }
    }

    @ParameterizedTest
    @MethodSource("readers")
    void testRemainingBytesAsString(BiFunction<byte[], ByteOrder, ByteReader> readerFactory) throws Exception {
        try (var r = readerFactory.apply("TEST".getBytes(), ByteOrder.LITTLE_ENDIAN)) {
            assertEquals("TEST", r.getRemainingBytesAsString());
        }
    }
}
