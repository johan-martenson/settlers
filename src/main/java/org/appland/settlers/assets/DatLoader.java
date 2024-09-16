package org.appland.settlers.assets;

import org.appland.settlers.utils.StreamReader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import static java.nio.ByteOrder.BIG_ENDIAN;

/**
 * DatLoader is responsible for loading and parsing binary data chunks from a file or byte stream.
 */
public class DatLoader {
    static final Set<String> HAS_CHILDREN = Set.of("FORM", "CAT ", "LIST");
    private static final Set<String> TYPE_ID_ONLY = Set.of("XDIR", "XMID");

    // Debug flag for verbose output
    private boolean debug = false;

    /**
     * Loads and parses a file containing data chunks.
     *
     * @param assetFilename the file to load
     * @throws IOException if an I/O error occurs
     */
    public void load(String assetFilename) throws IOException {
        try (StreamReader streamReader = new StreamReader(Files.newInputStream(Path.of(assetFilename)), ByteOrder.LITTLE_ENDIAN)) {
            List<Chunk> chunks = new ArrayList<>();

            while (!streamReader.isEof()) {
                chunks.add(loadChunkFromStream(streamReader));
            }

            streamReader.getInputStream().close();

            if (debug) {
                System.out.println("All chunks loaded");
                System.out.println(".....");

                printChunks(chunks);
            }
        }
    }

    // Prints all loaded chunks in a hierarchical structure
    private void printChunks(List<Chunk> chunks) {
        Stack<Chunk> toPrint = new Stack<>();
        Map<Chunk, Integer> depth = new HashMap<>();

        Collections.reverse(chunks); // Reverse for correct order

        chunks.forEach(chunk -> {
            toPrint.push(chunk);
            depth.put(chunk, 0);
        });

        // Process and print each chunk and its children
        while (!toPrint.isEmpty()) {
            Chunk chunk = toPrint.pop();
            int chunkDepth = depth.get(chunk);

            // Indentation for nested chunks
            System.out.printf("%s%s (%d)%n", " ".repeat(chunkDepth), chunk.getTypeId(), chunk.getTotalSize());

            List<Chunk> children = new ArrayList<>(chunk.getChildren());
            Collections.reverse(children); // Reverse for correct order

            // Add children to the stack
            children.forEach(child -> {
                toPrint.push(child);
                depth.put(child, chunkDepth + 1);
            });
        }
    }

    private Chunk loadChunkFromByteArray(byte[] data, int offset) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(data, offset + 4, data.length - offset - 4);

        // Get the type id
        String typeId = new String(data, offset, 4, StandardCharsets.US_ASCII);

        if (debug) {
            System.out.println("\nNew chunk. (byte array)");

            System.out.printf("Offset: %d%n", offset);
            System.out.printf("Type id: %s%n", typeId);
        }

        Chunk chunk = new Chunk(typeId);

        // Set the size if there is no content
        if (TYPE_ID_ONLY.contains(typeId)) {
            chunk.setTotalSize(4);

            // Handle chunks that can contain content
        } else {
            if (debug) {
                System.out.println("Has content");
            }

            // Get the size of the data (as uint32)
            long contentSize = (long) byteBuffer.order(BIG_ENDIAN).getInt() & 0xffffffffL;

            // Align to even size
            contentSize += (contentSize & 1) != 0 ? 1 : 0;

            if (debug) {
                System.out.printf("Defined size of data: %d%n", contentSize);
            }

            byte[] content = new byte[(int) contentSize];
            byteBuffer.get(content, 0, (int) contentSize);

            if (debug) {
                System.out.printf("Actual size of data: %d%n", content.length);
            }

            chunk.setContent(content);
            chunk.setTotalSize(8 + contentSize);
        }

        // Load children if this chunk has children
        if (HAS_CHILDREN.contains(chunk.getTypeId())) {
            if (debug) {
                System.out.println("Loading children");
            }

            long bytesRead = 0;

            while (bytesRead < chunk.getContent().length) {
                Chunk childChunk = loadChunkFromByteArray(chunk.getContent(), (int)bytesRead);

                chunk.addChild(childChunk);

                bytesRead = bytesRead + childChunk.getTotalSize();
            }
        }

        if (debug) {
            System.out.println("\n");
        }

        return chunk;
    }

    private Chunk loadChunkFromStream(StreamReader streamReader) throws IOException {

        // Get the type id
        String typeId = streamReader.getUint8ArrayAsString(4);

        if (debug) {
            System.out.println("\nNew chunk. (stream)");
            System.out.printf("Type id: %s%n", typeId);
        }

        Chunk chunk = new Chunk(typeId);

        // Set the size if there is no content
        if (TYPE_ID_ONLY.contains(typeId)) {
            chunk.setTotalSize(4);

        // Handle chunks that can contain content
        } else {
            if (debug) {
                System.out.println("Has content");
            }

            // Get the size of the data (as uint32)
            streamReader.pushByteOrder(BIG_ENDIAN);
            long contentSize = (long)streamReader.getInt32() & 0xffffffffL;
            streamReader.popByteOrder();

            // Align to even size
            contentSize += (contentSize & 1) != 0 ? 1 : 0;

            if (debug) {
                System.out.printf("Defined size of data: %d%n", contentSize);
            }

            byte[] content = streamReader.getUint8ArrayAsBytes((int) contentSize);

            if (debug) {
                System.out.printf("Actual size of data: %d%n", content.length);
            }

            chunk.setContent(content);
            chunk.setTotalSize(8 + contentSize);
        }

        // Load children if this chunk has children
        if (HAS_CHILDREN.contains(chunk.getTypeId())) {
            long bytesRead = 0;

            while (bytesRead < chunk.getContent().length) {
                Chunk childChunk = loadChunkFromByteArray(chunk.getContent(), (int)bytesRead);
                chunk.addChild(childChunk);
                bytesRead += childChunk.getTotalSize();

            }
        }

        if (debug) {
            System.out.println("\n");
        }

        return chunk;
    }
}
