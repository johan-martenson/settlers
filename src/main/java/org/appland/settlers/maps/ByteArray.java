package org.appland.settlers.maps;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class ByteArray {
    private final ByteBuffer bytes;

    public ByteArray(byte[] bytes, ByteOrder order) {
        this.bytes = ByteBuffer.wrap(bytes).order(order);
    }

    public int getUint16(int position) {
        return bytes.getShort(position) & 0xffff;
    }

    public String getNullTerminatedString(int length) {
        ByteBuffer tempViewToFindNullChar = bytes.asReadOnlyBuffer();

        byte[] stringAsBytes = new byte[length];
        bytes.asReadOnlyBuffer().get(stringAsBytes, 0, length);

        /* Find the null termination byte if there is one */
        int indexOfZero = -1;
        for (int i = 0; i < length; i++) {
            if (tempViewToFindNullChar.get(i) == 0) {
                indexOfZero = i;

                break;
            }
        }

        if (indexOfZero == -1) {
            indexOfZero = length;
        }

        String string = new String(stringAsBytes, 0, indexOfZero, StandardCharsets.US_ASCII);

        return string;
    }
}
