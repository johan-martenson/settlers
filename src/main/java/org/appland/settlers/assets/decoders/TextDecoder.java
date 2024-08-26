package org.appland.settlers.assets.decoders;

import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.Utils;
import org.appland.settlers.utils.ByteReader;
import org.appland.settlers.utils.StreamReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.nio.ByteOrder.LITTLE_ENDIAN;
import static org.appland.settlers.utils.StreamReader.SIZE_OF_UINT32;

public class TextDecoder {
    private static final int TEXT_FILE_HEADER_SIZE = 2 + 2 + 2 + 4;

    public static List<String> loadTextFile(String filename) throws IOException, InvalidFormatException {
        return loadTextFromStream(new StreamReader(new FileInputStream(filename), LITTLE_ENDIAN));
    }

    public static List<String> loadTextFromStream(ByteReader streamReader) throws IOException, InvalidFormatException {

        List<String> textsLoaded = new ArrayList<>();

        byte[] header = streamReader.getUint8ArrayAsBytes(2);

        /* Handle straight text data */
        if (header[1] != (byte)0xE7 && header[0] != (byte)0xFD) {

            /* Get the remaining text data */
            byte[] remainingBytes = streamReader.getRemainingBytes();

            /* In this case, the first two bytes were part of the text and not a special header */
            byte[] fullTextAsBytes = new byte[remainingBytes.length + 2];

            fullTextAsBytes[0] = header[0];
            fullTextAsBytes[1] = header[1];

            System.arraycopy(remainingBytes, 0, fullTextAsBytes, 2, remainingBytes.length);

            String text = Utils.nullTerminatedByteArrayToString(fullTextAsBytes);

            textsLoaded.add(text);

            /* Load "archived" text */
        } else {
            int count = streamReader.getUint16();
            int unknown = streamReader.getUint16();
            long size = streamReader.getUint32();

            if (size == 0) {
                // size = fileSize - headerSize
            }

            if (size < (long) count * SIZE_OF_UINT32) {
                throw new InvalidFormatException("Size must be less that count * 4 (" + count * SIZE_OF_UINT32 + "). Not " + size);
            }

            List<Long> starts = Arrays.stream(streamReader.getUint32Array(count))
                    .boxed()
                    .toList();

            long lastStart = 0;

            /* Verify that each start offset is correct */
            for (int x = 0; x < count; x++) {
                if (starts.get(x) != 0) {

                    if (starts.get(x) < lastStart || starts.get(x) < count * SIZE_OF_UINT32) {
                        throw new InvalidFormatException("Start value is wrong. Cannot be " + starts.get(x));
                    }

                    lastStart = starts.get(x);
                    starts.set(x, starts.get(x) + TEXT_FILE_HEADER_SIZE);
                }
            }

            starts.add(size + TEXT_FILE_HEADER_SIZE);

            /* Read each text item */
            for (int x = 0; x < count; x++) {
                long itemPosition = starts.get(x);

                if (itemPosition != 0) {
                    long itemSize = 0;

                    for (int j = x + 1; j <= count; j++) {

                        if (starts.get(j) != 0) {
                            itemSize = starts.get(j) - itemPosition;

                            break;
                        }
                    }

                    streamReader.setPosition((int) itemPosition);

                    if (itemSize > 0) {
                        String textItem = streamReader.getUint8ArrayAsString((int) itemSize);

                        textsLoaded.add(textItem);
                    } else {
                        String textItem = streamReader.getRemainingBytesAsString();

                        textsLoaded.add(textItem);
                    }
                }
            }
        }

        return textsLoaded;
    }
}
